$(document).ready(function () {

    var lineChartOptions = {
        chart: {
            renderTo: 'linechartContainer'
        },
        title: {
            text: 'Test Plan Run Status',
            x: -20 //center
        },
        subtitle: {
            text: 'Day',
            x: -20
        },
        xAxis: {
            categories: []
        },
        yAxis: {
            title: {
                text: 'Count'
            },
            plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
        },
        tooltip: {
            formatter: function () {
                return '<b>' + this.series.name + '</b><br/>' +
                        this.x + ': ' + this.y;
            }
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        colors: ['green', 'red', 'blue'],
        series: []
    };

    $.getJSON("/rest/monthlyexe-data", function (json) {
        lineChartOptions.xAxis.categories = json[0]['data'];
        lineChartOptions.series[0] = json[1];
        chart = new Highcharts.Chart(lineChartOptions);
    });
});

