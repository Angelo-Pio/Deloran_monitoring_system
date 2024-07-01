//List of CustomChart
var chart_list = [];
var interval_id = null;

$(document).ready(function () {
    $('#packet_data').submit(function (event) {
        event.preventDefault();


        clearInterval(interval_id);
        generateGraphs();
        interval_id = setInterval(generateGraphs, 5000);
    });
});
function generateGraphs() {

    var gateway_id_list = [];

    $('input[type="checkbox"]:checked').each(function () {
        gateway_id_list.push($(this).val()); // Add the value to the array
    });

    $.ajax({
        url: '/packets/getAll',
        type: 'GET',
        data: {
            id: gateway_id_list,
            start: null,
            end: null
        },
        success: function (data) {
            // Assuming data is an array of objects
            console.log(JSON.parse(data[0].packet));
            if (data.length !== 0) {
                handleChartCreationAndUpdate("PACKETS_TRAFFIC", data, "packets",true);
                disableAnimation();
            }else{
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
