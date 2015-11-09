$(document).ready(function () {

    $(".teststepsTable").hide();

    $("#checkAll").click(function (event) {
        $('input:checkbox').not(this).prop('checked', this.checked);
    });


    $("#testplanRunForm").submit(function (event) {
        if ($('input[name="testcaseChk"]:checked').length <= 0) {
            bootbox.alert("Please check a testcase!");
            event.preventDefault();
        }
    });

    $(".teststepShow").click(function (event) {
        event.preventDefault();

        var id = $(this).attr("id");
        $("#teststeps_" + id).toggle();
    });

}); 