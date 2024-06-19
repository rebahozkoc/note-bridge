const form = document.getElementById("form");

window.onload = function() {
    checkLoggedIn();
}

form.addEventListener("submit", function(event) {
    event.preventDefault();

    const data = new FormData(form);
    let dataObject = {};

    for (const [key, value] of data.entries()) {
        dataObject[key] = value;
    }

    fetch("/notebridge/api/auth/status", {
        method: "GET"
    })
        .then(res => {
            if (res.status === 200) {
                return res.json().then(data => {
                    sendRequestCreatePost(dataObject, data.userId);
                });
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }
        })
})

function sendRequestCreatePost(dataObject, userId) {
    dataObject.personId = userId;
    fetch("/notebridge/api/posts", {
        method: "POST",
        body: JSON.stringify(dataObject),
        headers: {
            "Content-type": "application/json"
        }
    }).then(r => {
        window.location.href = "cards.html";
    });
}
