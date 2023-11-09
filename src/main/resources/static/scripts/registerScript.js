$(document).ready(function () {
            $("#validate").click(function (e) {
                e.preventDefault();
                var username = $("#username").val();
                var email = $("#email").val();
                var mobileInputStr = $("#mobile").val();
                var passInputStr = $("#password").val();
                var confirmInputStr = $("#confirm-password").val();
                let emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                $("#error_msg").html("");
                $("#pass_error_msg").html("");
                $("#confirm_error_msg").html("");
                $("#user_error_msg").html("");
                $("#email_error_msg").html("");
                let flag = true;
                if (username.length === 0) {
                    $("#user_error_msg").html("Please enter your username");
                    flag = false;
                }
                if(email.length === 0){
                    $("#email_error_msg").html("Please enter your email here");
                    flag = false;
                }
                else if (!emailPattern.test(email)) {
                    $("#email_error_msg").html("Invalid email address. Please enter a valid email address.");
                    flag = false;
                }
                if (passInputStr.length === 0) {
                    $("#pass_error_msg").html("Enter enter your email");
                    flag = false;
                }
                else if (passInputStr.length < 8) {
                    $("#pass_error_msg").html("Enter at least 8 characters for your password");
                    flag = false;
                }
                if(confirmInputStr.length === 0){
                    $("#confirm_error_msg").html("Please re-type your password here");
                    flag = false;
                }
                else if (confirmInputStr !== passInputStr) {
                    $("#confirm_error_msg").html("Your password does not match");
                    flag = false;
                }
                if(mobileInputStr.length === 0){
                    $("#error_msg").html("Enter your mobile number");
                    flag = false;
                }
                else if (mobileInputStr.length !== 10) {
                    $("#error_msg").html("Enter valid mobile number");
                    flag = false;
                }
                 if(flag === true){
                    $("#login-form").submit();
                document.querySelector('.overlay').style.display = 'block';
                }
            });
});