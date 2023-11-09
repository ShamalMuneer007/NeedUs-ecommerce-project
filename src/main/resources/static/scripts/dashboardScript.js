
var combinedChart;
$(document).ready(function() {
    week();
});
const currentDate = new Date();


const monthNames = [
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
];
const currentMonth = monthNames[currentDate.getMonth()];
document.getElementById('currentMonth').textContent = currentMonth;
function graph(amount, quantity, xAxis) {
    var data = {
        labels: xAxis,
        datasets: [
            {
                label: 'Revenue',
                data: amount,
                yAxisID: 'amountAxis',
                fill: true,
                borderColor: '#ff6384',
                backgroundColor: 'rgba(255, 99, 132, 0.2)',
                borderWidth: 2,
                lineTension: 0.2
            },
            {
                label: 'Quantity',
                data: quantity,
                yAxisID: 'quantityAxis',
                fill: true,
                borderColor: '#3ff09f',
                backgroundColor: 'rgba(0, 143, 255, 0.2)',
                borderWidth: 2,
                lineTension: 0.2
            }
        ]
    };

    $('#downloadGraph').click(function() {
        downloadCSV(data);
    });

    if (combinedChart) {
        combinedChart.destroy();
    }

    combinedChart = new Chart(document.getElementById('combinedChart').getContext('2d'), {
        type: 'bar',
        data: data,
        options: {
            scales: {
                amountAxis: {
                    type: 'linear',
                    position: 'left',
                    grid: {
                        display: true,
                    }
                },
                quantityAxis: {
                    type: 'linear',
                    position: 'right',
                    grid: {
                        display: true,
                    }
                }
            },
            plugins: {
                legend: {
                    labels: {
                        font: {
                            size: 14,
                            weight: 'bold'
                        }
                    }
                },
            }
        }
    });
}
function month() {
    $.ajax({
        url: "/admin/dashboard/sales-report/monthly",
        type: "GET",
        dataType: "json",
        success: function(responseData) {
            let amount = responseData.amount;
            let quantity = responseData.quantity;
            let xAxis = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September','October', 'November', 'December'];
            graph(amount,quantity,xAxis);
            $("#graph-message").text("The displayed result represents the data of the months of the current year.");
            console.log(responseData);
        },
        error: function(xhr, status, error) {
            console.error("Request failed with status: " + xhr.status);
        }
    });
}
function week() {
    $.ajax({
        url: "/admin/dashboard/sales-report/weekly",
        type: "GET",
        dataType: "json",
        success: function(responseData) {
            let amount = responseData.amount;
            let quantity = responseData.quantity;
            let totalAmount = 0;
            let totalQuantity = 0;
            amount.forEach(function (amount) {
                totalAmount = totalAmount + amount;
            })
            quantity.forEach(function (quantity) {
                totalQuantity = totalQuantity + quantity;
            })
            document.getElementById('total-revenue').textContent = totalAmount;
            document.getElementById('quantity').textContent = totalQuantity;
            let xAxis = ['Week 1','Week 2','Week 3','Week 4','Week 5','Week 6'];
            graph(amount,quantity,xAxis);
            $("#graph-message").text("The displayed result represents the data of the weeks of the current month.");
            console.log(responseData.quantity);
            console.log(responseData.amount);
        },
        error: function(xhr, status, error) {
            console.error("Request failed with status: " + xhr.status);
        }
    });
}


function year() {
    $.ajax({
        url: "/admin/dashboard/sales-report/yearly",
        type: "GET",
        dataType: "json",
        success: function(responseData) {
            let amount = responseData.amount;
            let quantity = responseData.quantity;
            let xAxis = ['2023','2024','2025','2026','2027','2028','2029','2030','2031','2032','2033'];
            graph(amount,quantity,xAxis);
            $("#graph-message").text("");
            console.log(responseData);
        },
        error: function(xhr, status, error) {
            console.error("Request failed with status: " + xhr.status);
        }
    });
}
 function convertToCSV(data) {
            var csv = [];
            csv.push('Label,Revenue');

            for (var i = 0; i < data.labels.length; i++) {
                csv.push(data.labels[i] + ',' + data.datasets[0].data[i]);
            }

            return csv.join('\n');
        }

        // Function to download CSV file
        function downloadCSV(data){
            var csvContent = convertToCSV(data);
            var blob = new Blob([csvContent], { type: 'text/csv' });
            var url = window.URL.createObjectURL(blob);
            var a = document.createElement('a');
            a.href = url;
            a.download = 'data.csv';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
        }
document.getElementById('downloadPdf').addEventListener('click', function() {

    var element = document.getElementById('myTable');
    element.style.visibility='visible';
    element.style.position='static';
    html2canvas(element, {
        scrollY: -window.scrollY,
        scale: 2
    }).then(function(canvas) {
        var imgData = canvas.toDataURL('image/png');
        var pdf = new jsPDF('p', 'mm', 'a4');
        pdf.addImage(imgData, 'PNG', 10, 10, 190, 0);
        pdf.save('order_summary.pdf');
    });
    element.style.visibility='hidden';
    element.style.position='absolute';
});