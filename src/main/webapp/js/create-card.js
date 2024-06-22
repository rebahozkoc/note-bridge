const createPostForm = document.getElementById("create-post-form");

window.onload = function() {
    checkLoggedIn();
}

createPostForm.addEventListener("submit", function(event) {
    event.preventDefault();

    const formDataPostInformation = new FormData(createPostForm);
    let dataObject = {};

    for (const [key, value] of formDataPostInformation.entries()) {
        if(key !== "images") {
            dataObject[key] = value;
        }
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
    }).then(res => res.json())
        .then(data => {
            sendRequestAddImagesToPost(data.id);
            window.location.href = "cards.html";
    });
}

function sendRequestAddImagesToPost(postId) {
    let formData = new FormData();
    let images = document.getElementById("images").files[0];
    formData.append("images", images);

    if(images !== undefined) {
        fetch("/notebridge/api/posts/" + postId + "/images", {
            method: "POST",
            body: formData
        })
    }
}
