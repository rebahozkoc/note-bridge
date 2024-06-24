let cards = document.querySelector("#cards");
let cardsList = {};
const pageSize=12;
let pageNumber=1;
let totalNumberOfCards=0;


let searchValue = decodeURIComponent(GetURLParameter("search"));



const loadingScreen = document.getElementById("loading-screen");
const loadMoreButton = document.getElementById("load-more-btn");
const searchBtn=document.getElementById("search-btn");

searchBtn.addEventListener("click", setQueryParam);
loadMoreButton.addEventListener("click", loadMore);

function GetURLParameter(sParam) {
    const sPageURL = window.location.search.substring(1);
    const sURLVariables = sPageURL.split('&');
    for (let i = 0; i < sURLVariables.length; i++)
    {
        let sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam)
        {
            return sParameterName[1];
        }
    }
}

function setQueryParam(){
    const searchBarInput=searchBtn.parentNode.firstElementChild;
    const searchValue=searchBarInput.value;
    console.log(searchValue);
    window.location.href=`cards.html?search=${encodeURIComponent(searchValue)}`;
}

function loadMore(){
    pageNumber++;
    fetchPosts(pageSize,pageNumber);
}



window.onload = function() {

    fetchPosts(pageSize,pageNumber);
    checkLoggedIn();
}

function fetchPosts(pageSize,pageNumber) {
    requestedUrl = `/notebridge/api/posts?pageNumber=${pageNumber}&pageSize=${pageSize}`;
    if(window.location.href.includes("?")){
        requestedUrl+=`&${window.location.href.substring(window.location.href.indexOf("?")+1)}`;
    }

    fetch(requestedUrl)
        .then(res => res.json())
        .then(data => {
            totalNumberOfCards=data.meta.total;

            if(totalNumberOfCards===0){
                loadMoreButton.style.display="none";
            }
            cardsList = data;
            displayAllCards();
        })
        .catch(err => {
            if(err.status===500){
                loadMoreButton.style.display="none";
            }
            loadingScreen.style.display = "none";
            console.error(`Unable to fetch cards: ${err.status}`);
            console.error(err);
        });
}

function displayAllCards() {
    cardsList.data;
    cards.innerHTML += `
        ${cardsList.data.map(card => `${displayCard(card)}`).join("\n")}
    `;
    if(totalNumberOfCards<=pageSize*pageNumber){
        loadMoreButton.style.display="none";
    }
    if(pageNumber===1){
        loadingScreen.style.display = "none";

    }
}

function displayCard(card) {
    let imageSource;
    if(card.hasImage){
        console.log(card)
        imageSource="data:image/png;base64,";
        imageSource += card.image;
    }else{
        imageSource = "assets/images/placeholder.jpg";
    }
    return `
    <div class="card" data-card-id="${card.id}" onclick="selectCard(this)" style="width: 20rem; height: 25rem; margin: 35px 15px 15px;" id="displayed-card">
        <img src="${imageSource}" height="250" class="card-img-top"  alt="card image">
        <div class="card-body">
            <h5 class="card-title">${card.title}</h5>
            <p class="card-text">${card.description}</p>
            <p class="card-text">Event type: ${card.eventType}</p>
            <p class="card-text">Location: ${card.location}</p>
        </div>
    </div>
    `;
}

function selectCard(card) {
    const cardId = card.getAttribute("data-card-id");
    window.location.href = 'card-details.html?id=' + cardId;
}

function sortBy(element) {
    if(!window.location.href.includes("?") || window.location.href.includes("search")){
        window.location.href=`?sortBy=${element.dataset.sortBy}`;
    }else if(window.location.href.includes("sortBy")){
        let baseUrl = window.location.href.split("?")[0]; // Get the base URL without query parameters
        let queryParams = new URLSearchParams(window.location.search);
        queryParams.set('sortBy', element.dataset.sortBy); // Update sortBy parameter value
        window.location.href = `${baseUrl}?${queryParams.toString()}`;
    }else{
        window.location.href+=`&sortBy=${element.dataset.sortBy}`;
    }
}

function filterBy(element) {
    if(!window.location.href.includes("?")){
        window.location.href=`?filterBy=${element.dataset.filterBy}`;
    }else if(window.location.href.includes("filterBy")){
        let baseUrl = window.location.href.split("?")[0]; // Get the base URL without query parameters
        let queryParams = new URLSearchParams(window.location.search);
        queryParams.set('filterBy', element.dataset.filterBy); // Update filterBy parameter value
        window.location.href = `${baseUrl}?${queryParams.toString()}`;

    }else{
        window.location.href+=`&filterBy=${element.dataset.filterBy}`;
    }
}
