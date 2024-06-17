const carousel = document.getElementById("carousel");
const leftContentTitle = document.getElementById("left-content-title");
const leftContentDescription = document.getElementById("left-content-description");
const btn1 = document.getElementById("btn1");
const btn2 = document.getElementById("btn2");
const btn3 = document.getElementById("btn3");

btn1.addEventListener("click", function() {
    leftContentTitle.innerHTML = `Discover multiple musical events`;
    leftContentDescription.innerHTML = `Connect with other musicians to have jam sessions together!`;
})

btn2.addEventListener("click", function() {
    leftContentTitle.innerHTML = `Are you looking for new band members?`;
    leftContentDescription.innerHTML = `Get yourself ready to be connected with musicians from all over the world!`;
})

btn3.addEventListener("click", function() {
    leftContentTitle.innerHTML = `Sell or buy instruments`;
    leftContentDescription.innerHTML = `Are you interested to buy an instrument or do you want to sell one? Check the posts out!`;
})