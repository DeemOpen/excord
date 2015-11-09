$(document).ready(function () {

    var testplanId = $("#testplanId").val();

    $('#testcase-ele').DataTable({
        columnDefs: [{orderable: false, targets: 0}]
    });

    $("#testplan_delete").click(function (event) {
        event.preventDefault();
        var link = $(this).attr("href");
        bootbox.confirm("All Test Runs associated with this Test Plan will be deleted! Are you sure you want to delete this test plan?", function (result) {
            if (result) {
                document.location.href = link;
            }
        });
    });

    $("#checkAll").click(function (event) {
        $('input:checkbox').not(this).prop('checked', this.checked);
    });

    $("#testplan_clone").click(function (event) {
        var link = $(this).attr("href");
        bootbox.confirm("Are you sure you want to clone the Test Plan?", function (result) {
            if (result) {
                document.location.href = link;
            }
        });
        event.preventDefault();
    });

    $("#unlinkTestcaseTestplan").click(function (event) {
        event.preventDefault();
        if ($('input[name="testcaseChk"]:checked').length <= 0) {
            bootbox.alert("Please check a testcase!");
        } else {
            bootbox.confirm("Are you sure that you want to remove these testcases from Test Plan?", function (result) {
                if (result) {
                    $("#testplanForm").attr("action", "/testcase_testplan_map_remove");
                    $("#testplanForm").submit();
                }
            });
        }

    });


    $("#testcase_assign").click(function (event) {
        event.preventDefault();
        if ($('input[name="testcaseChk"]:checked').length <= 0) {
            bootbox.alert("Please check a testcase!");
        } else {
            $("#testplanForm").attr("action", "/testplan_testcase_assign");
            $("#testplanForm").submit();
        }
    });


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

    $.getJSON("/rest/testplan-data?testplanId=" + testplanId, function (json) {
        lineChartOptions.xAxis.categories = json[0]['data'];
        lineChartOptions.series[0] = json[1];
        lineChartOptions.series[1] = json[2];
        lineChartOptions.series[2] = json[3];
        chart = new Highcharts.Chart(lineChartOptions);
    });


}); 