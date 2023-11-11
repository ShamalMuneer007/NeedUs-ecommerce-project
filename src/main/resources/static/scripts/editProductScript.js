function previewImage(inputId,imgId,labelId,deleteButtonId){
    const file = document.getElementById(inputId);
    const image = document.getElementById(imgId);
    const label = document.getElementById(labelId);
    const dltButton = document.getElementById(deleteButtonId);
    if(file.files && file.files[0]){
        const reader = new FileReader();
        reader.onload = function (e){
            image.src = e.target.result;
            image.style.height = "125px";
            image.style.width = "125px";
            label.style.display = "none";
            dltButton.style.display = "flex";
            dltButton.style.alignItems = "center";
            dltButton.style.justifyContent = "center";
        };
        reader.readAsDataURL(file.files[0]);
    }
    else{
        image.src="";
    }
}
function deleteImage(inputId, imgId,labelId,deleteButtonId) {
    const fileInput = document.getElementById(inputId);
    const image = document.getElementById(imgId);
    const label = document.getElementById(labelId);
    const dltButton = document.getElementById(deleteButtonId);
    console.log('deleting image..')
    if(dltButton.getAttribute('value') !== null){
        console.log(dltButton.getAttribute('value'))
        $.ajax({
            url: '/admin/products/delete-image',
            contentType: 'application/json',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify({'imageId' : dltButton.getAttribute('value')}),
            success: (response) =>{
                fileInput.setAttribute('required','');
                dltButton.removeAttribute('value');
            },
            error:{

            }
        });
    }
    fileInput.value = "";
    image.src = "";
    image.style.height = "0";
    image.style.width = "0";
    dltButton.style.display = "none";
    label.style.display = "inline-block";
}