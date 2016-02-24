$(document).ready(function () {

    $('#testplanrun-ele').DataTable({
        "bPaginate": false,
        "aoColumnDefs": [
            {'bSortable': false, aTargets: [ '_all' ]}
        ],
        "paging":   false,
        "info":     false
    });

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
        var testcaseId = $(this).attr("id");
        if ($("#tcstep_" + testcaseId).html().length > 0) {
            $("#tcstep_" + testcaseId).html("");
        } else {
            $.ajax({url: "/testcase_view?testcaseId=" + testcaseId, success: function (result) {
                    $("#tcstep_" + testcaseId).html(result);
                }
            });
        }
    });

}); 