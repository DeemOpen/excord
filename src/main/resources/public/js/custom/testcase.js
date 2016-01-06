$(document).ready(function () {

    $(".teststepsTable").hide();

    $("#checkAll").click(function (event) {
        $('input:checkbox').not(this).prop('checked', this.checked);
    });

    $(".teststepShow").click(function (event) {
        event.preventDefault();
        var id = $(this).attr("id");
        $("#teststeps_" + id).toggle();
    });


    $("#addFolder").click(function (event) {
        event.preventDefault();

        var folderPath = $("#breadCrumbPath").val();
        bootbox.prompt("Parent folder path: " + folderPath + ",<br/> Please enter folder name:", function (result) {
            if (result !== null) {
                $("#folderName").val(result);
                $("#testcaseForm").attr("action", "/testcase_addfolder");
                $("#testcaseForm").submit();
            }
        });
    });

    $("#linkTestcaseTestplan").click(function (event) {
        event.preventDefault();
        if ($('input[name="testcaseChk"]:checked').length > 0) {
            $("#testcaseForm").attr("action", "/testcase_testplan_link");
            $("#testcaseForm").submit();
        } else {
            bootbox.alert("Please check a testcase!");
        }
    });

    $("#enableTestcase").click(function (event) {
        event.preventDefault();
        if ($('input[name="testcaseChk"]:checked').length > 0) {
            $("#testcaseForm").attr("action", "/testcase_enable");
            $("#testcaseForm").submit();
        } else {
            bootbox.alert("Please check a testcase!");
        }
    });

    $("#disableTestcase").click(function (event) {
        event.preventDefault();
        if ($('input[name="testcaseChk"]:checked').length > 0) {
            $("#testcaseForm").attr("action", "/testcase_disable");
            $("#testcaseForm").submit();
        } else {
            bootbox.alert("Please check a testcase!");
        }
    });

    $("#deleteTestcase").click(function (event) {
        event.preventDefault();
        if ($('input[name="testcaseChk"]:checked').length > 0) {
            bootbox.confirm("Are you sure about physically deleting testcases?", function (result) {
                if (result) {
                    $("#testcaseForm").attr("action", "/testcase_delete");
                    $("#testcaseForm").submit();
                }
            });
        } else {
            bootbox.alert("Please check a testcase!");
        }
    });

    $("#deleteFolder").click(function (event) {
        event.preventDefault();
        bootbox.confirm("Are you sure about physically deleting test folder?", function (result) {
            if (result) {
                $("#testcaseForm").attr("action", "/testcase_deletefolder");
                $("#testcaseForm").submit();
            }
        });
    });

    $("#bulkEdit").click(function (event) {
        event.preventDefault();
        if ($('input[name="testcaseChk"]:checked').length > 0) {
            $("#testcaseForm").attr("action", "/testcase_bulkedit");
            $("#testcaseForm").submit();
        } else {
            bootbox.alert("Please check a testcase!");
        }
    });

    $("#cutTestcase").click(function (event) {
        event.preventDefault();
        if ($('input[name="testcaseChk"]:checked').length > 0) {
            $("#testcaseForm").attr("action", "/testcase_cut");
            $("#testcaseForm").submit();
        } else {
            bootbox.alert("Please check a testcase!");
        }
    });

    $("#pasteTestcase").click(function (event) {
        $("#testcaseForm").attr("action", "/testcase_paste");
        $("#testcaseForm").submit();
    });

    $("#renameFolder").click(function (event) {
        event.preventDefault();
        bootbox.prompt({
            title: "Enter folder name",
            callback: function (result) {
                if (result !== null) {
                    $("#newNodeName").val(result);
                    $("#testcaseForm").attr("action", "/testcase_renamefolder");
                    $("#testcaseForm").submit();
                }
            }
        });

    });

    $("#cloneTestcase").click(function (event) {
        event.preventDefault();
        if ($('input[name="testcaseChk"]:checked').length > 0) {
            $("#testcaseForm").attr("action", "/testcase_clone");
            $("#testcaseForm").submit();
        } else {
            bootbox.alert("Please check a testcase!");
        }
    });

    $("#exportTestcases").click(function (event) {
        event.preventDefault();
        if ($('input[name="testcaseChk"]:checked').length > 0) {
            $("#testcaseForm").attr("action", "/testcase_export");
            $("#testcaseForm").submit();
        } else {
            bootbox.alert("Please check a testcase!");
        }
    });

});

