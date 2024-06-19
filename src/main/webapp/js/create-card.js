const form = document.getElementById("form");

window.onload = function() {
    checkLoggedIn();
}

form.addEventListener("submit", function(event) {
    event.preventDefault();

    const data = new FormData(form);
    let dataObject = {};
    dataObject.personId = 123;

    for (const [key, value] of data.entries()) {
        dataObject[key] = value;
    }

    fetch("/notebridge/api/posts", {
        method: "POST",
        body: JSON.stringify(dataObject),
        headers: {
            "Content-type": "application/json"
        }
    }).then(r => {
        window.location.href = "cards.html";
    });
})
