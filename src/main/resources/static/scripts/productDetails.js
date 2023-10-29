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
 let currentRating = 1;
 const stars = document.querySelectorAll('.star');
 const ratingInput = document.getElementById('rating-input');
 const ratingMessage = document.getElementById('rating-message');

 stars.forEach(star => {
   star.addEventListener('click', () => {
     const rating = parseInt(star.getAttribute('data-rating'));
     ratingInput.value = rating;
     setRating(rating);
   });

   star.addEventListener('mouseover', () => {
     const rating = parseInt(star.getAttribute('data-rating'));
     highlightStars(rating);
   });

   star.addEventListener('mouseout', () => {
     currentRating = parseInt(ratingInput.value);
     highlightStars(currentRating); // Highlight stars based on the current rating
   });
 });

 function highlightStars(rating) {
   stars.forEach(star => {
     const starRating = parseInt(star.getAttribute('data-rating'));
     if (starRating <= rating) {
       star.classList.add('active');
     } else {
       star.classList.remove('active');
     }
   });
 }