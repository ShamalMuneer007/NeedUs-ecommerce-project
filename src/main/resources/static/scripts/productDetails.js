 var options = {
        width: 400,
        zoomWidth: 500,
        offset: {vertical: 200, horizontal: -100}
    };
    new ImageZoom(document.getElementById("img-container"), options);
       function updateMainImage(clickedImage) {
            const mainImage = document.getElementById('main-image')
            const newSrc = clickedImage.getAttribute('data-src')

            mainImage.setAttribute('src', newSrc);
        }