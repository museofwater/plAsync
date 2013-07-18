var usernameAvailable = false;

$(function () {
    // Do an initial check in case their is data in the input (user hit the back button)
    checkUsername();

    $('#username-input').keyup(function () {
        checkUsername();
    });

    // Do the updates on stop so we only process the last request
    $(document).ajaxStop(function() {
        if (usernameAvailable) {
            $('.checkAvailable').html('<img src="/assets/images/accept.png" /> Username Avaliable');
            $(".checkAvailable").removeClass("red");
            $('.check').addClass("green");
            $("#username-input").removeClass("yellow");
            $("#username-input").addClass("white");
            enableSubmit();
        } else {
            $('.checkAvailable').html('<img src="/assets/images/error.png" /> Username Taken');
            $(".checkAvailable").removeClass("green");
            $('.checkAvailable').addClass("red")
            $("#username-input").removeClass("white");
            $("#username-input").addClass("yellow");
            disableSubmit();
        }
    });
});

function getUsernameInputValue() {
    var checkname = $("#username-input").val();
    return remove_whitespaces(checkname);
}

function checkUsername() {
    // Initially disable the Submit
    disableSubmit();
    var availname = getUsernameInputValue();
    if (availname != '') {
        $('.checkAvailable').show();
        $('.checkAvailable').fadeIn(400).html('<img src="/assets/images/ajax-loading.gif" /> ');

        $.ajax({
            type: "GET",
            url: "/user/" + availname + "/available",
            cache: false,
            success: function (result) {
                usernameAvailable = result;
            }
        });
    }
    else {
        $('.checkAvailable').html('');
    }
    return usernameAvailable
}

function enableSubmit() {
    $("#username-submit").prop("disabled", false);
}

function disableSubmit() {
    $("#username-submit").prop("disabled", true);
}

function remove_whitespaces(str){
    return $.trim(str);



}
