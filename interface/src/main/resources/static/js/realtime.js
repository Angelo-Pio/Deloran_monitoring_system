
//List of CustomChart
var chart_list = [];

$(document).ready(function () {


    function generateGraphs() {
        $.ajax({
            url: '/getAllById&Timestamp',
            type: 'GET',
            data: {
                id: null,
                start: null,
                end: null
            },
            success: function (data) {
                // Assuming data is an array of objects
                sys_data = data;
                if (data.length !== 0) {
                    handleChartCreationAndUpdate("CPU_Usage", data, "cpu_usage");
                    handleChartCreationAndUpdate("RAM_Usage", data, "ram_usage");
                    handleChartCreationAndUpdate("NET_Usage", data, "net");
                    disableAnimation();
                }

            },
            error: function () {
                alert('Error fetching data');
            }
        });
    }

    generateGraphs();
    setInterval(generateGraphs,5000)
})

function disableAnimation(){
    chart_list.forEach(c => {
        c.chart.options.animation.duration = 0;
        c.chart.update();
    })
}