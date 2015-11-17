$(document).ready(function () {
    autosize(document.querySelectorAll('textarea'));

    var t = $('#testStepTable').DataTable({"paging": false, "ordering": false, "info": false, "bFilter": false});

    $("#addTestStep").click(function (event) {
        event.preventDefault();
        var stepCount = Number($("#tstepCount").val().trim());
        stepCount = stepCount + 1;
        $("#tstepCount").val(stepCount);
        var testStep = "<textarea id=\"testStep_" + stepCount + "\" name=\"testStep_" + stepCount + "\" required></textarea>";
        var testResult = "<textarea id=\"testExpected_" + stepCount + "\" name=\"testExpected_" + stepCount + "\" required></textarea>";
        t.row.add([stepCount, testStep, testResult]).draw(false);
        autosize(document.querySelectorAll('textarea'));
    });



//    $("#testcase_save").click(function (event) {
//        var stepCount = Number($("#tstepCount").val().trim());
//        for (i = 1; i <= stepCount; i++) {
//            if (jQuery.isEmptyObject($("#testStep_" + i).val()) || jQuery.isEmptyObject($("#testExpected_" + i).val())) {
//                event.preventDefault();
//                bootbox.alert("Test steps cant be empty!");
//                break;
//            }
//        }
//    });

}); 