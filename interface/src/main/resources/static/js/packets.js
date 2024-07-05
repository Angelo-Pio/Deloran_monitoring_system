//List of CustomChart
var chart_list = [];
var interval_id = null;
var packets_db = null;
var payload_map = new Map();

$(document).ready(function () {
    $('#packet_data').submit(function (event) {
        event.preventDefault();


        clearInterval(interval_id);
        generateGraphs();
        interval_id = setInterval(generateGraphs, 5000);
    });

    $('#packet_pause').click(function (event) {
        event.preventDefault();
        clearInterval(interval_id);
        console.log("GRAPH PAUSED");
        generateTable(packets_db);
    })



});

function generateGraphs() {

    var gateway_id_list = [];

    $('input[type="checkbox"]:checked').each(function () {
        gateway_id_list.push($(this).val()); // Add the value to the array
    });

    $.ajax({
        url: '/packets/getAllLastFiveMinutesPackets',
        type: 'GET',
        data: {
            id: gateway_id_list,
            start: null,
            end: null
        },
        success: function (data) {
            // Assuming data is an array of objects
            console.log(data);
            if (data.length !== 0) {
                packets_db = data;
                handleChartCreationAndUpdate("PACKETS_TRAFFIC", data, "packets", true);
                disableAnimation();
            } else {
                destroyCharts();
            }

        },
        error: function () {
            alert('Error fetching data');
        }
    });
}

function disableAnimation() {
    chart_list.forEach(c => {
        c.chart.options.animation.duration = 0;
        c.chart.update();
    })
}

// Reset graphs
$('#reset').click(function (event) {
    event.preventDefault();
    chart_list.forEach(c => {
        $('#'.concat(c.canva_name)).attr("class", "0");
        c.chart.destroy();
    })
    chart_list = [];
    clearInterval(interval_id);
})

function generateTable(packets_db) {
    let graph_data = chart_list[0].chart.data.datasets[0].data;
    console.log(packets_db);

    // if (graph_data != null) {
    //     graph_data.forEach(d => {
    //
    //         var string = "<tr>\n" +
    //             "            <td id=\"table_time\">" + d.x +"</td>\n" +
    //         "            <td id=\"table_quantity\"> " + d.y +  "</td>\n" +
    //         "            <td id=\"table_type\">type</td>\n" +
    //         "            <td id=\"table_payload\">payload</td>\n" +
    //         "        </tr>";
    //         $('#packet_table').append(string)
    //     });
    //
    //     $('#table_time').text(graph_data[0].x);
    //     $('#table_payload').text(graph_data[0].y);
    // }

    var i = 0;
    packets_db.forEach(p => {

        var packet = JSON.parse(p.packet);

        var table_payload = "table_payload_" + i;
        let packet_json = JSON.stringify(packet,null,2).toString();
        payload_map.set(table_payload, packet_json);
        var timestamp =  new Date (Date.parse(p.timestamp));

        var timestamp_string = timestamp.getHours() + "h " + timestamp.getMinutes() + "m " + timestamp.getSeconds()+"s" ;

        var string = "<tr>\n" +
            "            <td id=\"table_time\">" + timestamp_string + "</td>\n" +
            "            <td id=\"table_type\">" + packet.mhdr.mtype + "</td>\n" +
            "            <td>" + " <button class=\"rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 m-4 p-2 \" id=" + table_payload + "> Show JSON  </button> </td>\n" +
            "        </tr>";

        string = "<tbody class='bg-slate-100' >"+string+"</tbody>";
        $('#packet_table').append(string);

        i++;

    });

    function handleClick(event,json) {

        Swal.fire({
            html:  '<div class="text-justify"><pre>' + json + '</pre></div>',
            confirmButtonText: 'OK'
        });

        // $('#json').text(json);
    }

    payload_map.forEach((v, k) => {
        $("#" + k).on('click', function (event) {
            handleClick(event,v);
        });
        console.log("ok");
    });

}


