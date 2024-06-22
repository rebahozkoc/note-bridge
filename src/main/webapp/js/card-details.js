let cardId = GetURLParameter('id');
let isPerson=false;
let postImages = document.getElementById("post-images");
let images = {};


const likeCountText= document.getElementById("like-countText");
const likeCount=document.getElementById("like-count");



const cardTitle = document.getElementById("title");
const description = document.getElementById("description");
const eventType = document.getElementById("event-type");
const eventLocation = document.getElementById("location");
const heartIcon = document.getElementById("heart-icon");
const deleteIcon = document.getElementById("delete-icon");
const editIcon = document.getElementById("edit-icon");
const interestButton = document.getElementById("interested-button");
const postCreateDateSpan = document.getElementById("post-create-date");
const postLastUpdateDateSpan = document.getElementById("post-lastupdate-date");


const authorImage=document.getElementById("author-img");
const authorName=document.getElementById("author-name");
const authorUsername=document.getElementById("author-username");
const authorCreateDate=document.getElementById("author-createDate");

const loadingScreen=document.getElementById("loading-screen");

const commentsSection = document.getElementById("comments-section");

heartIcon.addEventListener("click", toggleLike);


loadPostDetailsAndLikes(cardId);


window.onload = function() {
    checkLoggedIn();
    getUserId();
    getPostImages();
}

function getPostImages() {
    fetch("/notebridge/api/posts/" + cardId + "/image", {
        method: "GET"
    })
        .then(res => res.json())
        .then(data => {
            images = data;
            displayPostImages();
        })
}

function displayPostImages() {
    if(images.length === 0) {
        postImages.innerHTML = `
        <img src="assets/images/placeholder.jpg" class="img-fluid border border-dark border-2 rounded-2" width="30%" height="30%" alt="post image placeholder">
        `;
    } else if(images.length === 1) {
        postImages.innerHTML = `
        <img src="data:image/png;base64,${images[0]}" width="40%" height="40%" alt="post image" class="img-fluid border border-dark border-2 rounded-2">
        `;
    } else {
        postImages.innerHTML = `
        <div id="carouselExampleFade" class="carousel slide carousel-fade">
            <div class="carousel-inner" id="multiple-images">
                <div class="carousel-item active" style="display: flex; justify-content: center">
                    <img src="data:image/png;base64,${images.shift()}" class="img-fluid border border-dark border-2 rounded-2 d-block" alt="post image" width="40%" height="40%">
                </div>       
            </div>
            <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleFade" data-bs-slide="prev">
                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                <span class="visually-hidden">Previous</span>
            </button>
            <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleFade" data-bs-slide="next">
                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                <span class="visually-hidden">Next</span>
            </button>
        </div>
        `;

        document.getElementById("multiple-images").innerHTML += `
        ${images.map(image => `${displayPostImage(image)}`).join("\n")}
        `;
    }
}

function displayPostImage(image) {
    return `
    <div class="carousel-item" style="display: flex; justify-content: center">
        <img src="data:image/png;base64,${image}" class="d-block img-fluid border border-dark border-2 rounded-2" width="40%" height="40%" alt="post image">
    </div>
    `;
}

function getUserId() {
    fetch("/notebridge/api/auth/status", {
        method: "GET"
    })
        .then(res => {
            if (res.status === 200) {
                return res.json().then(data => {
                    getAuthor(data.userId,data.role);
                    showCommentsSection();
                    loadComments();
                });
            } else {
                return res.text().then(errorText => {
                    hideCommentsSection();
                    rerouteInterestedButton();
                    throw new Error(`${errorText}`);
                });
            }
        })
}

function getAuthor(userId,role) {
    fetch("/notebridge/api/posts/" + cardId)
        .then(res => res.json())
        .then(data => {
            checkPostBelongsToUser(userId, data.personId);
            if(role==="person"){
                displayInterestedButton(userId, data.id ,data.personId);

            }
        })
}

function checkPostBelongsToUser(userId, author) {
    console.log(userId);
    console.log(author);

    if(author === userId) {
        deleteIcon.innerHTML = `
        <button type="button" class="button" style="background-color: transparent; border: transparent; visibility: visible" id="delete-button" ><img src="assets/images/trash.png" style="width: 20px; height: 20px"> </button>
        `
        editIcon.innerHTML = `
        <span class="edit-icon" data-bs-toggle="modal" data-bs-target="#editPostModal" style="visibility: visible" id="edit-button">&#9998;</span>
        `
    }
}


function displayInterestedButton(userId, postId,author) {

    if(author !== userId) {

        //Check if user is already interested in the post before loading
        fetch("/notebridge/api/posts/" + postId + "/interested")
            .then(res => {
                if(res.status === 200) {
                    return res.json();
                }else{
                    return res.text().then(errorText => {
                        throw new Error(`${errorText}`);
                    });
                }
            }).then(data => {
                if(data.isInterested) {
                    interestButton.innerHTML = `
                    <a class="btn btn-secondary" data-post-id="${postId}" href="#" role="button" onclick="toggleInterest(this)">You are already interested in this post!</a>
                    `;
                }else{
                    interestButton.innerHTML = `
                        <a class="btn btn-primary" data-post-id="${postId}" href="#" role="button" onclick="toggleInterest(this)">I'm Interested!</a>

                        `
                }
            }
            ).catch(err => {
                interestButton.innerHTML = `
                        <a class="btn btn-danger" data-post-id="${postId}" href="#" role="button" onclick="toggleInterest(this)">Failed to fetch interest information!</a>

                        `
            });

    }
}
function toggleInterest(element){
    const postId=element.dataset.postId;

    fetch("/notebridge/api/posts/" + postId + "/interested", {
        method: "POST"
    }).then(res => {
        if (res.status === 200) {
            if(element.classList.contains("btn-primary")){
                //User will show interest
                element.classList.add("btn-secondary");
                element.classList.remove("btn-primary");
                element.innerHTML="You are already interested in this post!";

            }else{
                //User will remove interest
                element.classList.add("btn-primary");
                element.classList.remove("btn-secondary");
                element.innerHTML="I'm Interested!";

            }


        } else {
            return res.text().then(errorText => {
                throw new Error(`${errorText}`);
            });
        }
    }).catch(err => {
        console.error(err);
    });
}

function rerouteInterestedButton() {
    interestButton.innerHTML = `
     <a class="btn btn-primary" href="login.html" role="button">I'm Interested!</a>

    `
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

async function loadPostDetailsAndLikes(cardId) {

    try{
        const post= await fetch('/notebridge/api/posts/' + cardId);

        const postData= await post.json();


        cardTitle.innerHTML = `<h3>${postData.title}</h3>`;
        description.innerHTML = `<h5>${postData.description}</h5>`;
        eventType.innerHTML = `${postData.eventType}`;
        eventLocation.innerHTML = `${postData.location}`;
        postCreateDateSpan.innerHTML=`${new Date(parseInt(postData.createDate)).toLocaleDateString()} ${new Date(parseInt(postData.createDate)).toLocaleTimeString()}`;
        postLastUpdateDateSpan.innerHTML=`${new Date(parseInt(postData.lastUpdate)).toLocaleDateString()} ${new Date(parseInt(postData.lastUpdate)).toLocaleTimeString()}`;
        try{
            await updateTotalLikes(cardId);
            await loadAuthorImage(postData.personId);
            await loadAuthorDetails(postData.personId);
        } catch(err){
            console.error(err);
        }

        try{
            const statusData= await getStatus();
            let role=statusData.role;
            //Sponsors should not be able to like posts
            if(role==="sponsor"){
                heartIcon.style.display="none";
            }else{
                isPerson=true;
            }

            await checkIfLiked(cardId);

            loadingScreen.style.display="none";

        }catch(err){
            console.error(err);
            heartIcon.style.display="none";
            loadingScreen.style.display="none";

        }

    }catch(err){
        console.error(err);
        loadingScreen.style.display="none";

    }


}


function loadAuthorImage(personId) {

    console.log(personId)

    fetch(`/notebridge/api/persons/${personId}/image`)
        .then(res => {
            if(res.status===200) {
                return res.blob();
            }else{
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }

        })
        .then(blob => {

            const imageUrl = URL.createObjectURL(blob);
            // Set the src attribute of the img element
            authorImage.src = imageUrl;

        })
        .catch(error=>{
            console.error("Error", error.toString());
        });


}

function loadAuthorDetails(personId){
    fetch(`/notebridge/api/persons/${personId}`).then(res=> {


            if (res.status === 200) {
                return res.json();
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }
        }
    ).then(data => {
        authorUsername.innerHTML = data.username;
        authorName.innerHTML = data.name + " " + data.lastname;
        const createDate = new Date(parseInt(data.createDate));
        const formattedDate = createDate.toLocaleDateString();
        const formattedTime = createDate.toLocaleTimeString();
        authorCreateDate.innerHTML = `Account Created on: ${formattedDate} ${formattedTime}`;

    }).catch(err=>{
        console.error(err);
    })
}

function checkIfLiked(cardId) {



    fetch("/notebridge/api/posts/" + cardId + "/like")
        .then(res => {
            if(res.status === 200) {
                return res.json();
            }else{
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }

        }).then(data=>{


            if(data.isLiked){

                heartIcon.classList.remove("bi-heart");
                heartIcon.classList.add("bi-heart-fill");
            }

            loadingScreen.style.display="none";


    }).catch(err => {
        console.error(err);
        loadingScreen.style.display="none";

    })
}


function updateTotalLikes(cardId){
    fetch("/notebridge/api/posts/" + cardId + "/likes").then(
        res=>{
            if(res.status === 200){
                return res.json();
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }
        }
    ).then(data => {

        if(data.totalLikes===0){
            likeCount.innerHTML="";
            likeCountText.innerHTML="No one likes this posts yet!";
            if(isPerson){
                likeCountText.innerHTML+=", Click on the heart icon to be the first one to like it!";
            }
        }else{
            likeCount.innerHTML=data.totalLikes;
            likeCountText.innerHTML=` people liked this post!`;
        }

    }).catch(err => {
        alert(`Unable to fetch likes: ${err}`);

    });
}


function toggleLike(){


    fetch("/notebridge/api/posts/" + cardId + "/likes", {
        method: "POST"
    }).then(res => {
        if (res.status === 200) {
            return res.json();
        } else {
            return res.text().then(errorText => {
                throw new Error(`${errorText}`);
            });
        }
    }).then(data => {
        toggleHeart();
    }).catch(err => {
        alert(`${err}`);

    });
}



function toggleHeart() {
    if (heartIcon.classList.contains("bi-heart-fill")) {



        heartIcon.classList.remove("bi-heart-fill");
        heartIcon.classList.add("bi-heart");

        if(parseInt(likeCount.innerHTML)==1){
            likeCount.innerHTML = "";
            likeCountText.innerHTML="No one likes this posts yet, Click on to be the first one to like it!";
        }else{
            likeCount.innerHTML = parseInt(likeCount.innerHTML) - 1;
        }
    } else {


        heartIcon.classList.remove("bi-heart");
        heartIcon.classList.add("bi-heart-fill");
        if(likeCount.innerHTML==""){
            likeCount.innerHTML = 1;
            likeCountText.innerHTML=" person liked this post!";
        }else{
            likeCount.innerHTML = parseInt(likeCount.innerHTML) + 1;
        }



    }
}

document.addEventListener("DOMContentLoaded", function() {
    loadComments();
});

function loadComments() {
    fetch(`/notebridge/api/posts/${cardId}/comments`)
        .then(res => {
            if (res.status === 200) {
                return res.json();
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }
        })
        .then(data => {
            const comments = data.comments;
            comments.forEach(comment => {
                addCommentToPage(comment);
            });
        })
        .catch(err => {
            console.error("Error loading comments:", err);
        });
}

function addCommentToPage(comment) {
    const commentsContainer = document.getElementById("comments-container");
    const commentElement = document.createElement("div");
    commentElement.classList.add("comment");

    const formattedDate = new Date(comment.createDate).toLocaleDateString();
    const formattedTime = new Date(comment.createDate).toLocaleTimeString();

    commentElement.innerHTML = `
        <div class="row mb-2">
            <div class="col-md-2 text-left">
                <img src="${comment.picture || 'https://via.placeholder.com/50'}" class="img-fluid mb-2" alt="Author" style="width: 50px; height: 50px;">
                <h6 class="mb-2">@${comment.username}</h6> 
                <small>${formattedDate} ${formattedTime}</small>
            </div>
            <div class="col-md-10">
                <p>${comment.content}</p>
            </div>
        </div>
    `;
    commentsContainer.appendChild(commentElement);
}

function submitComment() {
    const commentText = document.getElementById("comment-text").value;

    if (!commentText) {
        alert("Please enter a comment.");
        return;
    }

    fetch("/notebridge/api/auth/status", {
        method: "GET"
    })
        .then(res => res.json())
        .then(user => {
            const comment = {
                content: commentText,
                postId: cardId,
                personId: user.userId
            };

            return fetch("/notebridge/api/comments", {
                method: "POST",
                body: JSON.stringify(comment),
                headers: {
                    "Content-type": "application/json"
                }
            });
        })
        .then(res => {
            if (res.status === 200) {
                return res.json();
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }
        })
        .then(data => {
            addCommentToPage(data);
            document.getElementById("comment-text").value = "";
        })
        .catch(err => {
            console.error("Error submitting comment:", err);
        });
}

function showCommentsSection() {
    commentsSection.style.display = "block";
}

function hideCommentsSection() {
    commentsSection.style.display = "none";
}

function getUser(){
    let user;
    getStatus().then(data => {
        user = data.user;
    });
    return user;
}



