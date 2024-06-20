const leftContentTitle = document.getElementById("left-content-title");
const leftContentDescription = document.getElementById("left-content-description");
const rightContent = document.getElementById("right-content");
const btn1 = document.getElementById("btn1");
const btn2 = document.getElementById("btn2");
const btn3 = document.getElementById("btn3");

window.onload = function() {
    checkLoggedIn();
}

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

function addParagraphTag(text) {
    return `<p class="fade-in-text">` + text + `</p>`
}

function addImgTag(img) {
    return `<img src=` + img + ` class="d-block fade-in-image w-100 rounded-3">`;
}
