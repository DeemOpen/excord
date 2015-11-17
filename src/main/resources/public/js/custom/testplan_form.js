var curYear = (new Date).getFullYear();
var nextYear = curYear + 1;
$(document).ready(function () {

    $("#tstartdt").datepicker({
        format: 'mm-dd-yyyy',
        startDate: "01-01-" + curYear,
        endDate: "12-31-" + nextYear,
        autoclose: true
    });

    $("#tenddt").datepicker({
        format: 'mm-dd-yyyy',
        startDate: "01-01-" + curYear,
        endDate: "12-31-" + nextYear,
        autoclose: true
    });

    $("#testplan_save").click(function (event) {
        if (jQuery.isEmptyObject($("#tstartdt").val()) || jQuery.isEmptyObject($("#tenddt").val())) {
            event.preventDefault();
            bootbox.alert("Please fill mandatory fields!");
        }
    });

});


