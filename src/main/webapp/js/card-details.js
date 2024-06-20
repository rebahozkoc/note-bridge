let cardId = GetURLParameter('id');
let isPerson=false;


const likeCountText= document.getElementById("like-countText");
const likeCount=document.getElementById("like-count");

const cardTitle = document.getElementById("title");
const description = document.getElementById("description");
const eventType = document.getElementById("event-type");
const eventLocation = document.getElementById("location");
const heartIcon = document.getElementById("heart-icon");
const deleteIcon = document.getElementById("delete-icon");
const editIcon = document.getElementById("edit-icon");

const loadingScreen=document.getElementById("loading-screen");

const commentsSection = document.getElementById("comments-section");

heartIcon.addEventListener("click", toggleLike);


loadPostDetailsAndLikes(cardId);


window.onload = function() {
    checkLoggedIn();
    getUserId();
}

function getUserId() {
    fetch("/notebridge/api/auth/status", {
        method: "GET"
    })
        .then(res => {
            if (res.status === 200) {
                return res.json().then(data => {
                    getAuthor(data.userId);
                    showCommentsSection();
                });
            } else {
                return res.text().then(errorText => {
                    hideCommentsSection();
                    throw new Error(`${errorText}`);
                });
            }
        })
}

function getAuthor(userId) {
    fetch("/notebridge/api/posts/" + cardId)
        .then(res => res.json())
        .then(data => {
            checkPostBelongsToUser(userId, data.personId);
        })
}

function checkPostBelongsToUser(userId, author) {
    console.log(userId);
    console.log(author);

    if(author === userId) {
        deleteIcon.innerHTML = `
        <button type="button" class="button"><img src="../assets/images/trash.png" alt="delete"> </button>
        `
        editIcon.innerHTML = `
        <span class="edit-icon" data-bs-toggle="modal" data-bs-target="#editPostModal">&#9998;</span>
        `
    }
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

    console.log(cardId)
    try{
        const post= await fetch('/notebridge/api/posts/' + cardId);

        const postData= await post.json();


        cardTitle.innerHTML = `<h3>${postData.title}</h3>`;
        description.innerHTML = `<h5>${postData.description}</h5>`;
        eventType.innerHTML = `${postData.eventType}`;
        eventLocation.innerHTML = `${postData.location}`;

        try{
            await updateTotalLikes(cardId);

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

function showCommentsSection() {
    commentsSection.style.display = "block";
}

function hideCommentsSection() {
    commentsSection.style.display = "none";
}


function submitComment() {
    const commentText = document.getElementById("comment-text").value;

    if (!commentText) {
        alert("Please enter a comment.");
        return;
    }

    const comment = {
        text: commentText,
        postId: cardId,

    };

    fetch("/notebridge/api/comments", {
        method: "POST",
        body: JSON.stringify(comment),
        headers: {
            "Content-type": "application/json"
        }
    }).then(res => {
        if (res.status === 200) {
            alert("Comment added!");
            return res.json();
        } else {
            return res.text().then(errorText => {
                throw new Error(`${errorText}`);
            });
        }
    }).then(data => {
        addCommentToPage(comment);
        document.getElementById("comment-text").value = "";
    }).catch(err => {
        console.error("Error submitting comment:", err);
    });
}


function addCommentToPage(comment) {
    const commentsContainer = document.getElementById("comments-container");
    const commentElement = document.createElement("div");
    commentElement.classList.add("comment");
    commentElement.innerHTML = `<p>${comment.text}</p>`;
    commentsContainer.appendChild(commentElement);
}

function getUser(){
    let user;
    getStatus().then(data => {
        user = data.user;
    });
    return user;
}



