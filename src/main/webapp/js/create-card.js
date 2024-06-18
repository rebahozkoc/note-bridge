const form = document.querySelector("#create-post-form");

form.addEventListener("submit", (e) => {
    e.preventDefault();

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
