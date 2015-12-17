$(document).ready(function () {
    
    $('#testmetric-ele').DataTable({
        
    });

    var testplanId = getUrlParameter('testplanId');

    var automatedPieChart = {
        chart: {
            renderTo: 'automatedPieChartContainer',
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        colors: ['green', 'red'],
        title: {
            text: 'Automation Percentage'
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

    $.getJSON("/rest/automated-data/" + testplanId, function (json) {
        automatedPieChart.series[0].data = json;
        chart = new Highcharts.Chart(automatedPieChart);
    });

    var testtypePieChart = {
        chart: {
            renderTo: 'testtypePieChartContainer',
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: 'Testcase Type'
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

    $.getJSON("/rest/testtype-data/" + testplanId, function (json) {
        testtypePieChart.series[0].data = json;
        chart = new Highcharts.Chart(testtypePieChart);

    });

    var priorityPieChart = {
        chart: {
            renderTo: 'priorityPieChartContainer',
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        colors: ['#ED561B', '#DDDF00', '#24CBE5', '#64E572', '#FF9655', '#FFF263', '#6AF9C4'],
        title: {
            text: 'Testcase Priority'
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

    $.getJSON("/rest/priority-data/" + testplanId, function (json) {
        priorityPieChart.series[0].data = json;
        chart = new Highcharts.Chart(priorityPieChart);
    });

});

var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)), sURLVariables = sPageURL.split('&'), sParameterName, i;
    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};