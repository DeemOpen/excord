$(document).ready(function () {
    autosize(document.querySelectorAll('textarea'));

    var stepCount = Number($("#tstepCount").val().trim());
    for (i = 1; i <= stepCount; i++) {
        if ($("#testStep_" + i).val() === undefined) {
            if (i > 2) {
                $("#tstepCount").val(i - 1);
            } else {
                $("#tstepCount").val(2);
            }
            break;
        }
    }

    $("#addTestStep").click(function (event) {
        event.preventDefault();
        var stepCount = Number($("#tstepCount").val().trim());
        stepCount = stepCount + 1;
        $("#tstepCount").val(stepCount);
        var testStep = "<textarea id=\"testStep_" + stepCount + "\" name=\"testStep_" + stepCount + "\" required></textarea>";
        var testResult = "<textarea id=\"testExpected_" + stepCount + "\" name=\"testExpected_" + stepCount + "\" required></textarea>";
        $('#testStepTable tr:last').after("<tr><td>" + stepCount + "</td><td>" + testStep + "</td><td>" + testResult + "</td></tr>");
        autosize(document.querySelectorAll('textarea'));
    });

    $("#delTestStep").click(function (event) {
        event.preventDefault();
        var stepCount = Number($("#tstepCount").val().trim());
        stepCount = stepCount - 1;
        $("#tstepCount").val(stepCount);
        $('#testStepTable tr:last').remove();
    });

}); 