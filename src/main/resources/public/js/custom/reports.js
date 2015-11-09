$(function () {


    var pieChartOptions = {
        chart: {
            renderTo: 'piechartContainer',
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: 'Pie Chart'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                showInLegend: true
            }
        },
        series: [{
                type: 'pie',
                name: 'Browser share',
                data: []
            }]
    }

    var columnChartOptions = {
        chart: {
            renderTo: 'columnchartContainer',
            type: 'column',
            marginRight: 130,
            marginBottom: 25
        },
        title: {
            text: 'Column Chart',
            x: -20 //center
        },
        subtitle: {
            text: '',
            x: -20
        },
        xAxis: {
            categories: []
        },
        yAxis: {
            title: {
                text: 'Requests'
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
            verticalAlign: 'top',
            x: -10,
            y: 100,
            borderWidth: 0
        },
        series: []
    };


    var lineChartOptions = {
        chart: {
            renderTo: 'linechartContainer'
        },
        title: {
            text: 'Line Chart',
            x: -20 //center
        },
        subtitle: {
            text: 'Subtitle',
            x: -20
        },
        xAxis: {
            categories: []
        },
        yAxis: {
            title: {
                text: 'Requests'
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
        series: []
    };


    var areaChartOptions = {
        chart: {
            type: 'area',
            renderTo: 'areachartContainer'
        },
        title: {
            text: 'Area Chart'
        },
        subtitle: {
            text: 'Subtitle Text'
        },
        xAxis: {
            categories: []
        },
        yAxis: {
            title: {
                text: 'Requests'
            },
            labels: {
                formatter: function () {
                    return this.value / 1000 + 'k';
                }
            }
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
            verticalAlign: 'top',
            x: -10,
            y: 100,
            borderWidth: 0
        },
        series: []
    };

    $.getJSON("/rest/pie-data", function (json) {
        pieChartOptions.series[0].data = json;
        chart = new Highcharts.Chart(pieChartOptions);
    });

    $.getJSON("/rest/column-data", function (json) {
        columnChartOptions.xAxis.categories = json[0]['data'];
        columnChartOptions.series[0] = json[1];
        columnChartOptions.series[1] = json[2];
        columnChartOptions.series[2] = json[3];
        chart = new Highcharts.Chart(columnChartOptions);
    });

    $.getJSON("/rest/column-data", function (json) {
        lineChartOptions.xAxis.categories = json[0]['data'];
        lineChartOptions.series[0] = json[1];
        lineChartOptions.series[1] = json[2];
        lineChartOptions.series[2] = json[3];
        chart = new Highcharts.Chart(lineChartOptions);
    });

    $.getJSON("/rest/column-data", function (json) {
        areaChartOptions.xAxis.categories = json[0]['data'];
        areaChartOptions.series[0] = json[1];
        areaChartOptions.series[1] = json[2];
        areaChartOptions.series[2] = json[3];
        chart = new Highcharts.Chart(areaChartOptions);
    });

});