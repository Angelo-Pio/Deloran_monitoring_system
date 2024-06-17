
var chart_list = [];

$(document).ready(function () {

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
            sys_data = data;
            if(data.length !== 0) {
                handleChartCreationAndUpdate("CPU_Usage", data, "cpu_usage");
                handleChartCreationAndUpdate("RAM_Usage", data, "ram_usage");
                handleChartCreationAndUpdate("NET_Usage", data, "net");
            }

        },
        error: function () {
            alert('Error fetching data');
        }
    });

})