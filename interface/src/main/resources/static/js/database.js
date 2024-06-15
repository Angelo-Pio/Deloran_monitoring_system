var sys_data;
var ch = null;

$('#reset').click(function (event){event.preventDefault(); if(ch !=null){removeData(ch);}})

$(document).ready(function () {
    $('#gateway_data').submit(function (event) {
        event.preventDefault();

        var gateway_id_list = [];
        var start_timestamp = $('#start_time').val();
        var end_timestamp = $('#end_time').val();


        $('input[type="checkbox"]:checked').each(function () {
            gateway_id_list.push($(this).val()); // Add the value to the array
        });

        $.ajax({
            url: '/getAllById&Timestamp',
            type: 'GET',
            data: {
                id: gateway_id_list,
                start: start_timestamp,
                end: end_timestamp
            },
            success: function (data) {
                // Assuming data is an array of objects
                var dataset = createDataset(data,'cpu_usage');
                sys_data = data;
                createChart(sys_data,dataset);

                /*if ($('#resourceChart').hasClass("0")) {
                    ch = createChart(sys_data);

                    $('#resourceChart').attr("class", "1");

                } else {
                    removeData(ch);

                    var ds_ata = sys_data.map(row => (
                        {
                            y: parseFloat(row.ram_usage.replace('%', '')),
                            x: row.timestamp
                        }));
                    addData(ch,sys_data.map(r=>r.timestamp),ds_ata);
                }*/
            },
            error: function () {
                alert('Error fetching data');
            }
        });
    });


});


function createChart(sys_data,dataset) {
    var elem = document.getElementById("resourceChart");



    var chart = new Chart(elem,
        {
            type: 'line',
            data: {
                labels: sys_data.map(row => row.timestamp),
                datasets: [
                    /*{
                        label: "Cpu Usage",
                        data: sys_data.map(row => (
                            {
                                y: parseFloat(row.cpu_usage.replace('%', '')),
                                x: row.timestamp
                            }
                        )),
                        fill: false,
                        borderColor: 'rgb(192,75,75)',
                        tension: 0.1,
                        showLine: true

                    }*/
                ]

            }
        });
    chart.data.datasets = dataset;
    chart.update();

    return chart;
}

function addData(chart, label, newData) {
    chart.data.labels.push(label);
    chart.data.datasets.forEach((dataset) => {
        dataset.data.push(newData);
    });
    chart.update();
}

function removeData(chart) {
    chart.data.labels.pop();
    chart.data.datasets.forEach((dataset) => {
        dataset.data.pop();
    });
    chart.update();
}

function getRandomRgbColor() {
    const r = Math.floor(Math.random() * 256); // Random value between 0 and 255
    const g = Math.floor(Math.random() * 256); // Random value between 0 and 255
    const b = Math.floor(Math.random() * 256); // Random value between 0 and 255
    return `rgb(${r},${g},${b})`;
}

/* Gateway === Dataset
    192.168.1.12 -> sys_data: tutti i dati relativi alla ram usage
 */
function Gateway(id,sys_data) {
    this.label= id;
    this.data= sys_data

    this.fill= false;
    this.borderColor= getRandomRgbColor();
    this.tension= 0.1;
    this.showLine= true;
}

function createDataset(db_data,property){

    var dataset = [];
    const map = new Map();

    // Per ogni gateway dal db, crea una mappa per raggruppare le info per gateway
    db_data.forEach(o => {
        if(!map.has(o.id)){
            map.set(o.id,[]);
        }
        map.get(o.id).push(
            {
                y : o[property].replace('%', ''),
                x : o.timestamp
            }
        );
    });
    map.forEach((value, key) => dataset.push(new Gateway(key,value)));
    console.log(dataset);
    return dataset;

}


