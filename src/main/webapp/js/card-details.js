let cardId = GetURLParameter('id');

const cardTitle = document.getElementById("title");
const description = document.getElementById("description");
const eventType = document.getElementById("event-type");
const eventLocation = document.getElementById("location");

window.onload = function() {
    updatePage(cardId);
    checkLoggedIn();
}

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

function updatePage(cardId) {
    fetch('/notebridge/api/posts/' + cardId)
        .then(res => res.json())
        .then(data => {
            cardTitle.innerHTML = `<h3>${data.title}</h3>`;
            description.innerHTML = `<h5>${data.description}</h5>`;
            eventType.innerHTML = `${data.eventType}`;
            eventLocation.innerHTML = `${data.location}`;
        })
        .catch(err => {
            console.error(`Unable to fetch cards: ${err.status}`);
            console.error(err);
        });
}

