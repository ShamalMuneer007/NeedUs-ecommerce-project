const fileInput = document.getElementById('imageInput');
const imagePreview = document.getElementById('imagePreview');

// Event listener for file input change
fileInput.addEventListener('change', function() {
    // Get the selected files
    const files = fileInput.files;

    // Clear previous previews
    imagePreview.innerHTML = '';

    // Loop through the selected files and display previews
    for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const reader = new FileReader();

        // Closure to capture the file information
        reader.onload = (function(file) {
            return function(e) {
                // Create an image element for the preview
                const imgElement = document.createElement('img');
                imgElement.className = 'preview-image';
                imgElement.src = e.target.result;

                // Append the image element to the preview container
                imagePreview.appendChild(imgElement);
            };
        })(file);

        // Read in the image file as a data URL
        reader.readAsDataURL(file);
    }
})