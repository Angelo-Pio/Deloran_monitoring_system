
var sys_data;

$(document).ready(function() {
    $('#gateway_data').submit(function(event) {
        event.preventDefault();

        var gateway_id_list = [];
        var start_timestamp = $('#start_time').val();
        var end_timestamp = $('#end_time').val();


        $('input[type="checkbox"]:checked').each(function() {
            gateway_id_list.push($(this).val()); // Add the value to the array
        });

        $.ajax({
            url: '/getAllById&Timestamp',
            type: 'GET',
            data:{
                id: gateway_id_list,
                start : start_timestamp,
                end: end_timestamp
            },
            success: function(data) {
                // Assuming data is an array of objects
                console.log(data);
                sys_data = data;
                addData(chart,"Ram_usage",data.map(r => r.ram_usage))
            },
            error: function() {
                alert('Error fetching data');
            }
        });
    });


});


