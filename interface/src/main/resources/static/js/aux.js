var color_map = new Map();

function CustomChart(canva_name, chart) {
    this.canva_name = canva_name;
    this.chart = chart;
}

function destroyCharts() {
    chart_list.forEach(c => {
        $('#'.concat(c.canva_name)).attr("class", "0");
        c.chart.destroy();
    })
    chart_list = [];
}

/*Funzione per gestire creazione o aggiornamento del grafico
* in base ad un flag impostato sull'html
* se property == packets allora db_data è un array costituito da oggetti {timestamp,packet} dove packet deve essere deserializzato con JSON.parse()
* */
function handleChartCreationAndUpdate(chartName, db_data, property, realtime = false) {

    var chart = $('#'.concat(chartName));
    if (chart.hasClass("0")) {

        chart_list.push(createChart(db_data, createDataset(db_data, property, realtime), chartName));
        chart.attr("class", "1");

    } else {

        chart_list.forEach(c => {
            if (c.canva_name === chartName) {
                removeData(c.chart);
                addData(c.chart, db_data.map(r => r.timestamp), createDataset(db_data, property, realtime));
            }
        })

    }
}

/*Crea il grafico*/
function createChart(sys_data, dataset, title) {
    var elem = document.getElementById(title);


    var chart = new Chart(elem,
        {
            type: 'line',
            data: {
                datasets: []
            },
            options: {
                plugins: {
                    title: {
                        display: true,
                        text: title,
                        fullSize: true
                    }
                }
            }

        });
    chart.data.datasets = dataset;
    chart.update();

    return new CustomChart(title, chart);
}

/*Aggiunge dati al grafico*/
function addData(chart, label, newData) {
    // chart.data.labels = label;
    chart.data.datasets = newData;
    chart.update();

}/*Rimuove dati dal grafico*/
function removeData(chart) {

    chart.data.labels = [];
    chart.data.datasets = [];
    chart.update();
}

function getRandomRgbColor() {
    const r = Math.floor(Math.random() * 256); // Random value between 0 and 255
    const g = Math.floor(Math.random() * 256); // Random value between 0 and 255
    const b = Math.floor(Math.random() * 256); // Random value between 0 and 255
    return `rgb(${r},${g},${b})`;
}

function getBorderColor(id) {
    return color_map.get(id);
}


function Dataset(id, sys_data) {
    this.label = id;
    this.data = sys_data

    this.fill = false;
    this.tension = 0.1;
    this.showLine = true;
    this.borderColor = getBorderColor(id);
}


function resourcesDatasetGeneration(map, db_data, property, realtime) {
    function fillMapAndReplace(obj, id, property, toReplace) {
        if (!map.has(id)) {
            map.set(id, []);
        }


        map.get(id).push(
            {
                y: obj[property].replace(toReplace, ''),
                x: obj.timestamp
            }
        );

        if (!color_map.has(id)) {
            color_map.set(id, getRandomRgbColor());
        }
    }

// Per ogni gateway dal db, crea una mappa per raggruppare le info per gateway
    db_data.forEach(o => {

        if (property == "cpu_usage" || property == "ram_usage") {
            fillMapAndReplace(o, o.id, property, '%');
        }
        if (property == "net") {

            fillMapAndReplace(o, o.id + "rx", "net_rx", ' bytes');
            fillMapAndReplace(o, o.id + "tx", "net_tx", ' bytes');
        }

    });


    return transform(map, property, realtime);
}

/*
* Crea un array pieno dei dati necessari al grafico
* */
function createDataset(db_data, property, realtime) {

    var dataset = [];
    var map = new Map();


    if (property == "packets") {
        map = packetsDatasetGeneration(map, db_data);
    } else {
        map = resourcesDatasetGeneration(map, db_data, property, realtime);
    }


    map.forEach((value, key) => dataset.push(new Dataset(key, value)));
    console.log(dataset);
    return dataset;

}

/*
* Questa funzione ha due funzioni:
* per i grafici realtime trasforma i timestamp in secondi
* per i grafici sull'utilizzo di rete trasforma i dati affinchè
* mostrino la differenza di traffico e non i bytes effettivamente ricevuti e trasmessi
*
* */
function transform(map, property, realtime) {
    const arr = Array.from(map);
    console.log(arr);
    var newMap = new Map();

    // Iterate through all the map {id: "id", data_Arr : [{x,y}]}
    for (let i = 0; i < arr.length; i++) {

        var [id, data_arr] = arr[i];

        if (!newMap.has(id)) {
            newMap.set(id, []);
        }
        var firstDate;

        //Do it again
        for (let j = 0; j < data_arr.length; j++) {

            var obj1;
            var obj2;

            //If j is last item
            if (j == data_arr.length - 1) {
                obj1 = data_arr[j - 1];
                obj2 = data_arr[j];
            } else {
                obj1 = data_arr[j];
                obj2 = data_arr[j + 1];
            }



            var newY = data_arr[j].y;
            //Calculate Delta between bytes
            if (property == "net") {

                if (obj2.y >= obj1.y) {
                    newY = obj2.y - obj1.y;
                } else {
                    newY = obj1.y - obj2.y;
                }
            }

            var newX = data_arr[j].x;
            if (realtime) {
                var referenceDate = new Date(obj2.x);
                if (j == 0) {
                    firstDate = referenceDate;
                }
                newX = Math.abs((referenceDate - firstDate) / 1000);
            }
                // ! IMPORTANTE
                newX = newX.toString() + "s";

            const newVal = {
                x: newX,
                y: newY
            }

            newMap.get(id).push(newVal);
        }

    }
    return newMap;
}


/*
*  Lista di packets, ognuno con timestamp e vari dati del packet
*  Interesse: numero di packets nell'intervallo di 5 secondi
* Map :{ id , [{x:timestamp,y:valore}] }
* List<Packet> = [{timestamp,packet}]
* Usare data come un insieme su cui ragionare per svuotamento
*  */
function packetsDatasetGeneration(map, data) {

    // Crea una mappa: timestamp -> packet
    var packets = new Map(data.map(i => [new Date(i.timestamp), JSON.parse(i.packet)]));

    var graphPackets = new Map();

    var baseTime = null;
    var rangeTime = null;
    var counter = 0;

    //Count how many packets are received in a time slot of 5 seconds
    packets.forEach(
        (v, k) => {
            if (baseTime != null && rangeTime != null) {

                if (baseTime <= k && k <= rangeTime) {
                    counter++;
                } else {
                    baseTime = k;
                    rangeTime = new Date(baseTime.getTime() + 5000);
                    graphPackets.set(k, counter);
                }
            } else {
                baseTime = k;
                rangeTime = new Date(baseTime.getTime() + 5000);
                graphPackets.set(k, counter);
            }
        }
    )
    var res = new Map();
    res.set("mock",[]);

    var arr = Array.from(graphPackets);

    var firstDate;
    var newX;
    var newY;

    for (let i = 0; i < arr.length; i++) {

        var [date1,count] = arr[i];
        var date2;

        if(i+1 < arr.length){
            date2 = arr[i+1][0];
        }else{
            date2 = arr[i][0];
            date1 = arr[i-1];
        }

        if(i == 0){
            firstDate = date1;
        }
        var referenceDate = date2;


        newX = (referenceDate - firstDate )/1000;
        newX = newX.toString()+"s";

        newY = count;



        res.get("mock").push({
            x: newX,
            y: newY
        });

    }

    console.log(res);


    return res;
}
