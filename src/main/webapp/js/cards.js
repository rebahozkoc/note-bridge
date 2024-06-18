let cards = document.querySelector("#cards");
let cardsList = {};
const cardTitle = document.querySelector("#title");
const description = document.getElementById("description");

fetchPosts();

function fetchPosts() {
    fetch('/notebridge/api/posts')
        .then(res => res.json())
        .then(data => {
            cardsList = data;
            displayAllCards();
        })
        .catch(err => {
            console.error(`Unable to fetch cards: ${err.status}`);
            console.error(err);
        });
}

function displayAllCards() {
    cardsList.data.reverse();
    cards.innerHTML = `
        ${cardsList.data.map(card => `${displayCard(card)}`).join("\n")}
    `;
}

function displayCard(card) {
    return `
    <div class="card" data-card-id="${card.id}" onclick="document.getElementById('test').innerHTML = 'new title'; window.location.href = 'card-details.html'" style="width: 20rem; height: 25rem; margin: 35px 15px 15px;">
        <img src="" height="250" class="card-img-top"  alt="card image">
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
    sendRequestGetCard(cardId);
}

function sendRequestGetCard(cardId) {
    fetch('/notebridge/api/posts/' + cardId)
        .then(res => res.json())
        .then(data => {
            console.log(data.title);
            cardTitle.innerHTML = `${data.title}`;
            description.innerHTML = `${data.title}`;
        })
        .catch(err => {
            console.error(`Unable to fetch cards: ${err.status}`);
            console.error(err);
        });
}
