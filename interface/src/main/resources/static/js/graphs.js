
var chart = document.getElementById("resourceChart") ;
console.log(sys_data);


new Chart(chart,
    {
        type: 'line',
        data: {
            labels: sys_data.map(row => row.timestamp),
            datasets : [
                {
                    label: "Ram Usage",
                    data: sys_data.map(row => (
                        {
                            y: parseFloat(row.ram_usage.replace('%','')),
                            x: row.timestamp
                        }
                    )),
                    fill:false,
                    borderColor: 'rgb(75, 192, 192)',
                    tension: 0.1,
                    showLine: true
                },
                {
                    label: "Cpu Usage",
                    data: sys_data.map(row => (
                        {
                            y: parseFloat(row.cpu_usage.replace('%','')),
                            x: row.timestamp
                        }
                    )),
                    fill:false,
                    borderColor: 'rgb(192,75,75)',
                    tension: 0.1,
                    showLine: true

                }
            ]

        }
    });

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
