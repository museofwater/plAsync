var usernameAvailable = false;

$(function () {
    // Initially hide both alerts
    $("#usernameAvailable").hide();
    $("#usernameTaken").hide();

    // Do an initial check in case there is data in the input (user hit the back button)
    checkUsername();

    //  Do an initial load of the gravatar
    updateGravatar();

    $('#username-input').keyup(function () {
        checkUsername();
    });

    $('#gravatar-email-input').keyup(function () {
        if (isPotentialEmail(getGravatarEmailValue())) {
            updateGravatar();
        }
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

function getGravatarEmailValue() {
    var gravatarEmail = $('#gravatar-email-input').val();
    return remove_whitespaces(gravatarEmail);
}

function checkUsername() {
    // Initially disable the Submit
    disableSubmit();
    var availname = getUsernameInputValue();
    if (availname != "") {
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
        $("#usernameAvailable").hide();
        $("#usernameTaken").hide();
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

// This function just confirms that the string looks like an email.  Doesn't matter whether it is valid or not.
function isPotentialEmail(email) {
    var regex = /[\w-]+@([\w-]+\.)+[\w-]+/;
//    var regex = /^([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
    return regex.test(email);
}

function updateGravatar() {
   var gravatarUrl = getGravatarUrl(getGravatarEmailValue());
    $('#gravatar-img').attr('src', gravatarUrl);
}

function getGravatarUrl(gravatarEmail) {
    var gravatarBaseUrl = "http://www.gravatar.com/avatar/";
    if (remove_whitespaces(gravatarEmail) != "") {
        var hash = md5(gravatarEmail.toLowerCase());
        return gravatarBaseUrl + hash + "?d=identicon";
    }
    else {
        return gravatarBaseUrl + "?d=mm";
    }
}

