const mainContent = document.querySelector("#main-content");
let posts = document.querySelector("#posts");
const createPostForm = document.querySelector("#createPostForm");
let cards = {};

showImageSlider();
fetchPosts();
displayAllCards();

function fetchPosts() {
    fetch('/notebridge/api/posts')
        .then(res => res.json())
        .then(data => {
            cards = data;
            displayAllCards();
        })
        .catch(err => {
            console.error(`Unable to fetch cards: ${err.status}`);
            console.error(err);
        });
}

function displayAllCards() {
    posts.innerHTML = `
        <h1 class="fw-bold text-primary-emphasis fs-2 my-5">Browse posts!</h1>
        ${cards.data.map(post => `${displayCard(post)}`).join("\n")}
    `;
}

function displayCard(card) {
    return `
    <div class="m-3">
            <div class="card">
                <div class="row g-0">
                    <div class="col-md-7">
                        <div class="card-body">
                            <h5 class="card-title mb-4">${card.title}</h5>
                            <p class="card-text">${card.description}</p>
                        </div>
                    </div>
                    <div class="col-md-5">
                        <img src="background2.png" class="img-fluid rounded-end" alt="card-image">
                    </div>
                </div>
            </div>
            <div class="mt-2 bg-white rounded-3 py-1 px-1">
                <button class="card-btn">&#128077;</button>
                ${card.title}
                <button class="card-btn" type="button" data-bs-toggle="collapse" data-bs-target="#collapse" aria-expanded="false" aria-controls="collapse">
                    &#128172;
                </button>
                ${card.title}
            </div>
            <div class="collapse mt-2" id="collapse">
                <div class="card card-body">
                    <div class="form-floating mb-3">
                        <textarea class="form-control" placeholder="Description" id="add-comment" style="height: 11vh"></textarea>
                        <label for="add-comment">Add a comment</label>
                        <button type="submit" class="btn btn-primary mt-3 rounded-4">Send</button>
                    </div>
                    <div class="comment rounded-3 mb-2 p-2">
                        <span>User 1</span> said:<br>Comment 1
                    </div>
                    <div class="comment rounded-3 mb-2 p-2">
                        <span>User 2</span> said:<br>Comment 2
                    </div>
                </div>
            </div>
        </div>
    `
}

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

function showCategories() {
    mainContent.innerHTML = `
    <div class="container w-75" id="categories">
        <h1 class="fw-bold text-white fs-2 my-5">Select a category</h1>
        <div class="btn-group w-100" role="group" aria-label="Basic example">
            <button type="button" class="btn btn-outline-light category-btn">Music events</button>
            <button type="button" class="btn btn-outline-light category-btn">Find band members</button>
            <button type="button" class="btn btn-outline-light category-btn">Sell or buy instruments</button>
        </div>
    </div>
    `;
}

function sendRequestCreatePost() {
    const form = document.getElementById("createPostForm");
    const data = new FormData(form);
    let dataObject = {};
    dataObject.personId = 123;

    for (const [key, value] of data.entries()) {
        dataObject[key] = value;
    }

    console.log("Sent: " + JSON.stringify(dataObject));

    fetch("/notebridge/api/posts", {
        method: "POST",
        body: JSON.stringify(dataObject),
        headers: {
            "Content-type": "application/json"
        }
    }).then(r => {
        console.log("Received: " + JSON.stringify(r));
        fetchPosts();
        displayAllCards();
    });
    return false;
}

function showImageSlider() {
    mainContent.innerHTML = `
    <h1 class="fw-bold text-white fs-2 m-5">Capture the feeling of a vibrant music community!</h1>
                <div id="carousel" class="carousel slide carousel-fade" data-bs-ride="carousel">
                    <div class="carousel-inner">

                        <!-- First card -->
                        <div class="carousel-item active">
                            <div class="card first-page-card rounded-5">
                                <img src="/src/main/webapp/images/main-image-2.jpg" class="card-img-top image-first-page-card rounded-top-5">
                                <div class="card-body">
                                    <h5 class="card-title">Are you looking for a new band member?</h5>
                                    <p class="card-text">Get yourself ready to be connected with musicians from all over the world!</p>
                                </div>
                            </div>
                        </div>

                        <!-- Second card -->
                        <div class="carousel-item">
                            <div class="card first-page-card rounded-5">
                                <img src="/src/main/webapp/images/main-image-1.jpg" class="card-img-top image-first-page-card rounded-top-5" alt="...">
                                <div class="card-body">
                                    <h5 class="card-title">Discover multiple musical events</h5>
                                    <p class="card-text">Connect with other musicians to have jam sessions together!</p>
                                </div>
                            </div>
                        </div>

                        <!-- Third card -->
                        <div class="carousel-item">
                            <div class="card first-page-card rounded-5">
                                <img src="/src/main/webapp/images/main-image-3.jpg" class="card-img-top image-first-page-card rounded-top-5" alt="...">
                                <div class="card-body">
                                    <h5 class="card-title">Sell or buy instruments</h5>
                                    <p class="card-text">Are you interested to buy an instrument or do you want to sell one? Check out the posts out!</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function showContactUsForm() {
    mainContent.innerHTML = `
    <div class="container main-content w-75 rounded-5 p-5">
        <h1 class="fw-bold text-white py-2 fs-2">Contact Us</h1>
        <form>
            <div class="mb-3">
              <label for="exampleFormControlInput1" class="form-label text-white">Email address</label>
              <input type="email" class="form-control" id="exampleFormControlInput1" placeholder="name@example.com">
            </div>
            <div class="mb-3">
              <label for="exampleFormControlTextarea1" class="form-label text-white">Message</label>
              <textarea class="form-control" id="exampleFormControlTextarea1" rows="3"></textarea>
            </div>
            <a href="" class="btn btn-outline-light mt-1 px-3 py-2 rounded-4">Send message</a>
        </form>
        <footer class="footer mt-4">
          <p class="text-white">&copy; 2024 NoteBridge © All rights reserved.</p>
          <p class="text-white">Designed by UT students.</p>
        </footer>
    </div>
    `;
}