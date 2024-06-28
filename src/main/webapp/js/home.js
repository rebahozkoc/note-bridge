const leftContentTitle = document.getElementById("left-content-title");
const leftContentDescription = document.getElementById("left-content-description");
const rightContent = document.getElementById("right-content");
const btn1 = document.getElementById("btn1");
const btn2 = document.getElementById("btn2");
const btn3 = document.getElementById("btn3");

/**
 * When the page is opened or refreshed, the welcome message and the navigation bar are updated.
 */
window.onload = function() {
    checkLoggedIn();
}

/**
 * These functions are used to replace the images and paragraphs of text on the middle part of the Home page.
 */
btn1.addEventListener("click", function() {
    leftContentTitle.innerHTML = addParagraphTag(`Discover multiple musical events`);
    leftContentDescription.innerHTML = addParagraphTag(`Connect with other musicians to have jam sessions together!`);
    rightContent.innerHTML = addImgTag(`assets/images/main-image-1.jpg`);
})
btn2.addEventListener("click", function() {
    leftContentTitle.innerHTML = addParagraphTag(`Are you looking for new band members?`);
    leftContentDescription.innerHTML = addParagraphTag(`Get yourself ready to be connected with musicians from all over the world!`);
    rightContent.innerHTML = addImgTag(`assets/images/main-image-2.jpg`);
})
btn3.addEventListener("click", function() {
    leftContentTitle.innerHTML = addParagraphTag(`Sell or buy instruments`);
    leftContentDescription.innerHTML = addParagraphTag(`Are you interested to buy an instrument or do you want to sell one? Check the posts out!`);
    rightContent.innerHTML = addImgTag(`assets/images/main-image-3.jpg`);
})

/**
 * @param text a phrase which can be displayed on screen
 * @returns {string} a formatted string containing the 'text' which can be placed into the html file
 */
function addParagraphTag(text) {
    return `<p class="fade-in-text">` + text + `</p>`
}

/**
 * @param img the src of an image which can be displayed on screen
 * @returns {string} a formatted string containing the 'src', which can be placed into the html file
 */
function addImgTag(img) {
    return `<img src=` + img + ` class="d-block fade-in-image w-100 rounded-3">`;
}
