const mainContent = document.querySelector("#main-content");
const createPostForm = document.querySelector("#createPostForm");
let posts = document.querySelector("#posts");
let cards = {};

fetchPosts();
displayAllCards();

function showCreatePostForm() {
    mainContent.innerHTML = `
    <form class="container main-content w-75 rounded-5 p-5" onsubmit="return sendRequestCreatePost()">
        <h1 class="fw-bold text-white py-2 fs-2">Create a post</h1>
        <div class="form-floating mb-3">
            <textarea class="form-control" placeholder="Title" id="title" name="title"></textarea>
            <label for="title">Title</label>
        </div>
        <div class="form-floating mb-3">
            <textarea class="form-control" placeholder="Description" id="description" name="description" style="height: 15vh"></textarea>
            <label for="description">Description</label>
        </div>
        <div class="row">
            <div class="col-6">
                <h6 class="text-white">Select the type of post you create.</h6>
                <select class="form-select mt-1" aria-label="Default select example" name="eventType">
                    <option selected>jam</option>
                    <option value="1">music event</option>
                    <option value="2">search band members</option>
                    <option value="3">sell instrument</option>
                </select>
            </div>
            <div class="col-6">
                <div class="form-floating mt-2">
                    <textarea class="form-control" placeholder="Description" id="location" name="location"></textarea>
                    <label for="location">Location</label>
                </div>
            </div>
        </div>
        <div class="my-2">
            <label for="formFileMultiple" class="form-label text-white">Add pictures.</label>
            <input class="form-control" type="file" id="formFileMultiple" multiple> <!-- name="pictures" -->
        </div>
        <button type="submit" class="btn btn-outline-light mt-3 px-3 py-2 rounded-4" onclick="hideCreatePostForm()">Create post</button>
    </form>
    `;
}

function hideCreatePostForm() {
    createPostForm.style.display = "none";
}

function sendRequestCreatePost() {
    const form = document.getElementById("createPostForm");
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
        fetchPosts();
        displayAllCards();
    });
    return false;
}

function showPosts() {
    posts.style.display = "block";
}