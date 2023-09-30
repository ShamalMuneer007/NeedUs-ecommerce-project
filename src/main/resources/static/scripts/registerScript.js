$(document).ready(function () {
            $("#validate").click(function (e) {
                e.preventDefault();
                var username = $("#username").val();
                var email = $("#email").val();
                var mobileInputStr = $("#mobile").val();
                var passInputStr = $("#password").val();
                var confirmInputStr = $("#confirm-password").val();

                $("#error_msg").html("");
                $("#pass_error_msg").html("");
                $("#confirm_error_msg").html("");
                $("#user_error_msg").html("");
                $("#email_error_msg").html("");

                if (username.length === 0) {
                    $("#user_error_msg").html("Please enter this field");
                }
                else if (passInputStr.length === 0) {
                    $("#email_error_msg").html("Enter enter this field");
                }
                else if (passInputStr.length < 8) {
                    $("#pass_error_msg").html("Enter at least 8 characters for your password");
                }
                else if (confirmInputStr !== passInputStr) {
                    $("#confirm_error_msg").html("Your password does not match");
                } else if (mobileInputStr.length !== 10) {
                    $("#error_msg").html("Enter valid mobile number");
                } else {
                    $("#login-form").submit();
                }
            });
});