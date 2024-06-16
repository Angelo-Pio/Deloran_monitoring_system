var sys_data;
var ch = null;

$('#reset').click(function (event) {
    event.preventDefault();
    if (ch != null) {
        ch.destroy();
        $('#resourceChart').attr("class", "0");
    }
})


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
                var dataset = createDataset(data, 'net');
                sys_data = data;

                handleChartCreationAndUpdate("resourceChart",data,"cpu_usage");


            },
            error: function () {
                alert('Error fetching data');
            }
        });
    });


});

function handleChartCreationAndUpdate(chartName, db_data, property) {
    if ($('#' + chartName).hasClass("0")) {

        ch = createChart(db_data, createDataset(db_data, property), chartName);
        $('#' + chartName).attr("class", "1");

    } else {
        removeData(ch);
        addData(ch, db_data.map(r => r.timestamp), createDataset(db_data, property));
    }
}

function createChart(sys_data, dataset, title) {
    var elem = document.getElementById("resourceChart");


    var chart = new Chart(elem,
        {
            type: 'line',
            data: {
                labels: sys_data.map(row => row.timestamp),
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

    return chart;
}

function addData(chart, label, newData) {
    chart.data.labels = label;
    chart.data.datasets = newData;
    chart.update();
}

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

/* Gateway === Dataset
    192.168.1.12 -> sys_data: tutti i dati relativi alla ram usage
 */
function Gateway(id, sys_data) {
    this.label = id;
    this.data = sys_data

    this.fill = false;
    this.borderColor = getRandomRgbColor();
    this.tension = 0.1;
    this.showLine = true;
}

function createDataset(db_data, property) {

    var dataset = [];
    const map = new Map();

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
    map.forEach((value, key) => dataset.push(new Gateway(key, value)));
    console.log(dataset);
    return dataset;

}


