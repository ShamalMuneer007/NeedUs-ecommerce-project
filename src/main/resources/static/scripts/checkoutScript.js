const paymentStart = () => {
            let amount = $("#amount").text();
            var paymentOptions = document.getElementsByName('payment');
            var selectedPaymentMethod;

            for (var i = 0; i < paymentOptions.length; i++) {
                if (paymentOptions[i].checked) {
                    selectedPaymentMethod = paymentOptions[i].value;
                    break;
                }
            }

            console.log('Selected payment method:', selectedPaymentMethod);

            if (selectedPaymentMethod === 'online') {
                $.ajax({
                    url: '/user/create-order',
                    data: JSON.stringify({ amount: amount }),
                    contentType: 'application/json',
                    type: 'POST',
                    dataType: 'json',
                    success: function (order) {
                        console.log(order);
                        let options = {
                            key: 'rzp_test_7sGRBU2Gye6g5s',
                            amount: order.amount,
                            currency: 'INR',
                            name: 'Needus',
                            description: 'Order payment',
                            order_id: order.id,
                            handler: function (response) {
                                console.log(response.razorpay_payment_id);
                                console.log(response.razorpay_order_id);
                                console.log(response.razorpay_signature);
                                console.log('payment_successful!!');
                                console.log("success");
                                verifyPayment(response, order)
                            },
                            prefill: {
                                name: "Needus",
                                email: "needus777@gmail.com",
                                contact: "9567211741"
                            },
                            notes: {
                                address: "Needus office, Kozhikode, Kerala"
                            },
                            theme: {
                                color: "#3399cc"
                            }
                        };
                        var rzp1 = new Razorpay(options);
                        rzp1.on('payment.failed', function (response) {
                            console.log(response.error.code);
                            console.log(response.error.description);
                            console.log(response.error.source);
                            console.log(response.error.step);
                            console.log(response.error.reason);
                            console.log(response.error.metadata.order_id);
                            console.log(response.error.metadata.payment_id);
                        });
                        rzp1.open();
                        e.preventDefault();
                    },
                    error: function (error) {
                        console.error(error);
                    }
                });
                function verifyPayment(payment, order) {
                    console.log('now in verifyPayment')
                    $.ajax({
                        url: '/user/verify-payment',
                        data: JSON.stringify({
                            payment,
                            order
                        }),
                        contentType: 'application/json',
                        method: 'post',
                        success: (response) => {
                            if (response.success) {
                                console.log('response got')
                                document.querySelector('.overlay').style.display = 'block';
                                document.getElementById("orderForm").submit();
                            } else {
                                console.log('response not get');
                                location.href = '*'
                            }
                        }
                    })
                }
            }
            else {
                document.querySelector('.overlay').style.display = 'block';
                document.getElementById("orderForm").submit();
            }
        };
        const applyCoupon = () =>{
            let coupon = $("#coupon").val();
             $.ajax({
                     url: '/user/checkout/apply-coupon',
                     data: JSON.stringify({
                            coupon
                     }),
                     contentType: 'application/json',
                     method: 'post',
                     success: (response) => {
                          if (response.success) {
                              console.log('response got')
                              location.reload()
                          }
                          else {
                             $("#coupon_error_msg").html("The total price of your products is not within the range of the selected coupon price limit");
                          }
                     }
             })
        }