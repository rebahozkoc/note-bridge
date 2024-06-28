let cards = document.querySelector("#cards");
let sidenavContent = document.querySelector("#sidenav-content");
let cardsList = {};
const pageSize=12;
let pageNumber=1;
let totalNumberOfCards=0;
let sponsoredCardsList = {};





const loadingScreen = document.getElementById("loading-screen");
const loadMoreButton = document.getElementById("load-more-btn");
const searchBar= document.getElementById("search-bar");
const createPostBtn = document.getElementById("createPostBtn");

loadMoreButton.addEventListener("click", loadMore);
//Getting status data and modifying URL

//hides create post button if user is a sponsor OR not logged in
hideCreatePostBtnIfSponsor();

function loadMore(){
    pageNumber++;
    fetchPosts(pageSize,pageNumber);
}



window.onload = function() {
    fetchPosts(pageSize,pageNumber);
    fetchSponsoredPosts();
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
        ${cardsList.data.map(card => `${displayCard(card, false)}`).join("\n")}
    `;
    if(totalNumberOfCards<=pageSize*pageNumber){
        loadMoreButton.style.display="none";
    }
    if(pageNumber===1){
        loadingScreen.style.display = "none";

    }
}

function displayCard(card, sponsoredCard) {
    let imageSource;
    if(card.hasImage){
        console.log(card)
        imageSource="data:image/png;base64,";
        imageSource += card.image;
    }else{
        imageSource = "assets/images/cards_placeholder.png";
    }

    if(sponsoredCard) {
        return `
        <div class="sponsored-card mt-1" data-card-id="${card.id}" onclick="selectCard(this)" style="width: 20rem; height: 30rem; margin: 35px 15px 15px;" id="displayed-card">
            <img src="${imageSource}" class="card-img-top"  alt="card image" style="height: 16rem">
            <div class="card-body" style="height: 14rem">
                <h5 class="sponsored-card-title fs-5">${card.title}</h5>
                <p class="sponsored-card-text ms-3">${card.description}</p>
                <p class="card-text ms-3">${card.eventType}</p>
            </div>
        </div>
        `;
    } else {
        return `
        <div class="card" data-card-id="${card.id}" onclick="selectCard(this)" style="width: 20rem; height: 25rem; margin: 35px 15px 15px;" id="displayed-card">
            <img src="${imageSource}" class="card-img-top" style="margin-top: 1.5vh" alt="card image">
            <div class="card-body" style="height: 12rem">
                <h5 class="card-title">${card.title}</h5>
                <p class="card-text">${card.description}</p>
                <p class="card-text">${card.eventType}</p>
            </div>
        </div>
        `;
    }
}

function selectCard(card) {
    const cardId = card.getAttribute("data-card-id");
    window.location.href = 'card-details.html?id=' + cardId;
}

function sortBy(element) {

    if(!window.location.href.includes("?") ){
        window.location.href=`?sortBy=${element.dataset.sortBy}`;
    }else if(window.location.href.includes("search")){
        let baseUrl = window.location.href.split("?")[0]; // Get the base URL without query parameters
        let queryParams = new URLSearchParams(window.location.search);
        queryParams.delete('search');
        queryParams.set('sortBy', element.dataset.sortBy);
        window.location.href = `${baseUrl}?${queryParams.toString()}`;

    } else if(window.location.href.includes("sortBy")){
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

function filterSponsored(){
    cards.innerHTML = `
        ${sponsoredCardsList.map(card => `${displayCard(card, false)}`).join("\n")}
    `;
}

function searchBy(){
    const searchInput=searchBar.value;
    console.log(searchInput)
    if(searchInput || !searchInput){
        if(!window.location.href.includes("?") ){
            window.location.href=`?search=${searchInput}`;
        }else if(window.location.href.includes("sortBy")){
            let baseUrl = window.location.href.split("?")[0]; // Get the base URL without query parameters
            let queryParams = new URLSearchParams(window.location.search);
            queryParams.delete('sortBy');
            queryParams.set('search', searchInput);
            window.location.href = `${baseUrl}?${queryParams.toString()}`;

        } else if(window.location.href.includes("search")){
            let baseUrl = window.location.href.split("?")[0]; // Get the base URL without query parameters
            let queryParams = new URLSearchParams(window.location.search);
            queryParams.set('search', searchInput); // Update sortBy parameter value
            window.location.href = `${baseUrl}?${queryParams.toString()}`;
        }else{
            window.location.href+=`&search=${searchInput}`;
        }
    }
}

function hideCreatePostBtnIfSponsor(){
    getStatus().then(data => {
        if(data.role==="sponsor"){
            createPostBtn.style.display="none";
        }
    }).catch(err => {
        createPostBtn.style.display="none";
        console.error(`Unable to fetch status: ${err.status}`);
    })
}
function fetchSponsoredPosts() {
    fetch("/notebridge/api/posts/sponsored")
        .then(res => res.json())
        .then(data => {
            sponsoredCardsList = data;
            displaySponsoredPosts();
        })
}

function displaySponsoredPosts() {
    if(sponsoredCardsList.length === 0) {
        sidenavContent.innerHTML = `<h6 class="text-white ms-3">There are no sponsored posts at this moment.</h6>`;
    } else if (sponsoredCardsList.length === 1) {
        sidenavContent.innerHTML = `${displayCard(sponsoredCardsList[0], true)}`;
    } else {
        let sponsoredCards = document.querySelector("#sponsored-cards");

        sponsoredCards.innerHTML = `
        <div class="carousel-item active">
            ${displayCard(sponsoredCardsList.shift(), true)}
        </div>
        `;

        sponsoredCards.innerHTML += `
        ${sponsoredCardsList.map(sponsoredCard => `<div class="carousel-item">${displayCard(sponsoredCard, true)}</div>`).join("\n")}
        `;
    }
}
