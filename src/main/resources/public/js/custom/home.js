$(document).ready(function () {

    var requirementPriorityChart = {
        chart: {
            renderTo: 'requirementPriorityChart',
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        colors: ['#DB7093', '#FFDEAD', '#7B68EE', '#2E8B57'],
        title: {
            text: 'Requirement By Priority'
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
                name: 'Percent',
                data: []
            }]
    }

    $.getJSON("/rest/req-priority-data", function (json) {
        requirementPriorityChart.series[0].data = json;
        chart = new Highcharts.Chart(requirementPriorityChart);
    });

    var requirementStatusChart = {
        chart: {
            renderTo: 'requirementStatusChart',
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        colors: ['#336633', '#A52A2A', '#FFD700'],
        title: {
            text: 'Requirement By Status'
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
                name: 'Percent',
                data: []
            }]
    }

    $.getJSON("/rest/req-status-data", function (json) {
        requirementStatusChart.series[0].data = json;
        chart = new Highcharts.Chart(requirementStatusChart);
    });

    var testcaseRunChart = {
        chart: {
            renderTo: 'testcaseRunChart',
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        colors: ['#FF9966', '#99CC66', '#660066', '#00CCFF', '#FF6699'],
        title: {
            text: 'Monthly Test Run By'
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
                name: 'Percent',
                data: []
            }]
    }

    $.getJSON("/rest/testcase-run-data", function (json) {
        testcaseRunChart.series[0].data = json;
        chart = new Highcharts.Chart(testcaseRunChart);
    });

    var runStatusChart = {
        chart: {
            renderTo: 'runStatusChart',
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: 'Monthly Test Run Status'
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
                name: 'Percent',
                data: []
            }]
    }

    $.getJSON("/rest/run-status-data", function (json) {
        runStatusChart.series[0].data = json;
        chart = new Highcharts.Chart(runStatusChart);
    });


});

