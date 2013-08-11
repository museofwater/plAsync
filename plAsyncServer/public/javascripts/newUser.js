var usernameAvailable = false;

$(function () {
    // Initially hide both alerts
    $("#usernameAvailable").hide();
    $("#usernameTaken").hide();

    // Do an initial check in case there is data in the input (user hit the back button)
    checkUsername();

    $('#username-input').keyup(function () {
        checkUsername();
    });

    // Do the updates on stop so we only process the last request
    $(document).ajaxStop(function() {
        if (usernameAvailable) {
            onUsernameAvailable();
        }
        else {
            onUsernameTaken();
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
                $('.checkAvailable').html('');
            }
        });
    }
    else {
        $('.checkAvailable').html('');
    }
    return usernameAvailable
}

function onUsernameAvailable() {
//    $('.checkAvailable').html('<img src="/assets/images/accept.png" /> Username Avaliable');
//    $(".checkAvailable").removeClass("red");
//    $('.check').addClass("green");
    $("#usernameAvailable").show();
    $("#usernameTaken").hide();
    $("#username-input").removeClass("yellow");
    $("#username-input").addClass("white");
    enableSubmit();
}

function onUsernameTaken() {
//    $('.checkAvailable').html('<img src="/assets/images/error.png" /> Username Taken');
//    $(".checkAvailable").removeClass("green");
//    $('.checkAvailable').addClass("red")
    $("#usernameTaken").show();
    $("#usernameAvailable").hide();
    $("#username-input").removeClass("white");
    $("#username-input").addClass("yellow");
    disableSubmit();
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
