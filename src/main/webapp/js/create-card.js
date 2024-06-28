const createPostForm = document.getElementById("create-post-form");

window.onload = function() {
    checkLoggedIn();
}

/**
 * When the form for creating a post is submitted, this function is called.
 */
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

/**
 * A request is sent to the server with the information the user entered in the form.
 * @param dataObject an object which contains the data entered in the form
 * @param userId the id of the person who created the post
 */
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

/**
 * Another request is sent to the server which contains the images uploaded by the user.
 * @param postId the id of the post which is created
 */
function sendRequestAddImagesToPost(postId) {
    let formData = new FormData();
    let images = document.getElementById("images").files;

    if(images !== undefined) {
        if(images.length === 1) {
            formData.append("images", images[0]);
        } else {
            for(let i= 0; i < images.length; i++) {
                formData.append("images", images[i]);
            }
        }

        fetch("/notebridge/api/posts/" + postId + "/images", {
            method: "POST",
            body: formData
        })
    }
}
