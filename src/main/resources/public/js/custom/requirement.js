$(document).ready(function () {
    $("#req_delete").click(function (event) {
        event.preventDefault();
        bootbox.confirm("Are you sure about deleting this requirement?", function (result) {
            if (result) {
                $("#reqForm").attr("action", "/req_delete");
                $("#reqForm").submit();
            }
        });

    });
});

