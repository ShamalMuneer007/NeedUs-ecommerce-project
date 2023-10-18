var today = new Date();
var year = today.getFullYear();
var month = String(today.getMonth() + 1).padStart(2, '0');
var day = String(today.getDate()).padStart(2, '0');
var minDate = year + '-' + month + '-' + day;

document.getElementById("dateInput").min = minDate;