let cards = document.querySelector("#cards");
let sidenavContent = document.querySelector("#sidenav-content");
let sponsoredCards = document.querySelector("#sponsored-cards");
let cardsList = {};
const pageSize=12;
let pageNumber=1;
let totalNumberOfCards=0;
let sponsoredCardsList = {};
let likesPerCard = 0;
let likeMap = new Map()






const loadingScreen = document.getElementById("loading-screen");
const loadMoreButton = document.getElementById("load-more-btn");
const searchBar= document.getElementById("search-bar");
const createPostBtn = document.getElementById("createPostBtn");
const eventBadge = document.getElementById("card-event");

loadMoreButton.addEventListener("click", loadMore);
//Getting status data and modifying URL

//hides create post button if user is a sponsor OR not logged in
hideCreatePostBtnIfSponsor();

/**
 * Fetch more posts from the database and display them on the screen.
 */
function loadMore(){
    pageNumber++;
    fetchPosts(pageSize,pageNumber);
}


/**
 * When the page is opened or refreshed, it is filled with cards, sponsored cards and the navigation bar is updated.
 */
window.onload = function() {
    fetchPosts(pageSize,pageNumber);
    fetchSponsoredPosts();
    checkLoggedIn();
}

/**
 * Retrieve posts from the database.
 */
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

/**
 * Display on the screen the posts retrieved.
 */
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

/**
 * Display one card on the screen.
 * @param card the card information
 * @param sponsoredCard a boolean value which checks if the card is sponsored
 * @returns {string} a formatted string which can be placed in the html page.
 */
function displayCard(card, sponsoredCard) {
    let imageSource;
    if(card.hasImage){
        console.log(card)
        imageSource="data:image/png;base64,";
        imageSource += card.image;
    }else{
        imageSource = "assets/images/cards_placeholder.png";
    }

    let badge;
    if(card.eventType === "Jam Session") {
        badge = `
        <p class="badge text-bg-primary">Jam Session</p>
        `;
    }else if (card.eventType === "Live Event") {
        badge = `
        <p class="badge text-bg-success">Live Event</p>
        `;
    }else if (card.eventType === "Find Band Member") {
        badge = `
        <p class="badge text-bg-danger">Find Members</p>
        `;
    }else if (card.eventType === "Find Instrument") {
        badge = `
        <p class="badge text-bg-info">Find Instruments</p>
        `;
    }else if (card.eventType === "Music Discussion") {
        badge = `
        <p class="badge text-bg-light">Discussions</p>
        `;
    }

    let cardClass;
    if(sponsoredCard) {
        cardClass = "sponsored-card";
        badge += `
        <p class="badge text-bg-warning">Sponsored</p>
        `;
    } else {
        cardClass = "card";
    }


   countLikes(card.id);

    let likeCount = likeMap.get(card.id)

    return `
        <div class=${cardClass} data-card-id="${card.id}" onclick="selectCard(this)" id="displayed-card">
            <img src="${imageSource}" class="card-img-top" alt="card image">
            <div class="card-body">
                <h5 class="card-title">${card.title}</h5>
                ${badge}
                <p>Likes: ${likesPerCard}</p>
            </div>
        </div>
        `;
}

function countInterested(id) {
    let interested = [];

    fetch("/notebridge/api/posts/" + id + "/interestedusers")
        .then(res => {
            if (res.status === 200) {
                return res.json();
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }

        }).then(data => {

        data.forEach(user => {
            interested.push(user)
        })

        setInterestedCount(interested)


    }).catch(
        err => {
            console.error(err);
        }
    )

}

function setInterestedCount(interested){
    let interestedUsers = interested;
}


function countLikes(id) {

    fetch("/notebridge/api/posts/" + id + "/likes")
        .then(res => {
            if (res.status === 200) {
                return res.json();
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }

        }).then(data => {
           setLikeCount(id, data.totalLikes);

    }).catch(
        err => {
            console.error(err);
        }
    )

}

function setLikeCount(id, likes){
    likeMap.set(id, likes)
}


/**
 * When the user clicks on a card, this function is called.
 * @param card the card which has been clicked.
 */
function selectCard(card) {
    const cardId = card.getAttribute("data-card-id");
    window.location.href = 'card-details.html?id=' + cardId;
}

/**
 * When the user uses the sort functionality, this function is called.
 * @param element the type of sort selected
 */
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

/**
 * When the user uses the filter functionality, this function is called.
 * @param element the type of filter selected
 */
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

function isSponsored(element){
    if(!window.location.href.includes("?")){
        window.location.href=`?isSponsored=${element.dataset.filterBy}`;
    }else if(window.location.href.includes("isSponsored")){
        let baseUrl = window.location.href.split("?")[0]; // Get the base URL without query parameters
        let queryParams = new URLSearchParams(window.location.search);
        queryParams.set('isSponsored', element.dataset.filterBy); // Update filterBy parameter value
        window.location.href = `${baseUrl}?${queryParams.toString()}`;

    }else{
        window.location.href+=`&isSponsored=${element.dataset.filterBy}`;
    }

}

/**
 * Function called when the user performs a search.
 */
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

/**
 * If the user's role is 'sponsor', the button used to create posts is hidden.
 */
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

/**
 * Retrieve sponsored posts from the database.
 */
function fetchSponsoredPosts() {
    fetch("/notebridge/api/posts/sponsored")
        .then(res => res.json())
        .then(data => {
            sponsoredCardsList = data;
            displaySponsoredPosts();
        })
}

/**
 * The sponsored posts are displayed on the right side of the screen. If there are no sponsored posts, a corresponding
 * message is displayed on screen, and if there are more than 1 sponsored posts, these are displayed in a slideshow.
 */
function displaySponsoredPosts() {
    if(sponsoredCardsList.length === 0) {
        sidenavContent.innerHTML = `<h6 class="text-white ms-3">There are no sponsored posts at this moment.</h6>`;
    } else if (sponsoredCardsList.length === 1) {
        sponsoredCards.innerHTML = `
        <div class="carousel-item active">
            ${displayCard(sponsoredCardsList[0], true)}
        </div>
        `;
    } else {
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
