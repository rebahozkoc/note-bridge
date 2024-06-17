let cards = document.querySelector("#cards");
let cardsList = {};

window.onload = function() {
    fetchPosts();
}

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
    cards.innerHTML = `
        ${cardsList.data.map(card => `${displayCard(card)}`).join("\n")}
    `;
}

function displayCard(card) {
    return `
    <div class="card" onclick="location.href='../webapp/card-details.html'" style="width: 20rem; height: 25rem; margin: 35px 15px 15px;">
        <img src="" height="250" class="card-img-top"  alt="card image">
        <div class="card-body">
            <h5 class="card-title">${card.title}</h5>
            <p class="card-text">${card.description}</p>
        </div>
    </div>
    `;
}