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


heartIcon.addEventListener("click", toggleLike);


loadPostDetailsAndLikes(cardId);



/** Check if the user is logged in to update the navbar. */
// window.onload = function() {
//     checkLoggedIn();
// }


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

function loadPostDetailsAndLikes(cardId) {


    fetch('/notebridge/api/posts/' + cardId)
        .then(res => res.json())
        .then(data => {
            cardTitle.innerHTML = `<h3>${data.title}</h3>`;
            description.innerHTML = `<h5>${data.description}</h5>`;
            eventType.innerHTML = `${data.eventType}`;
            eventLocation.innerHTML = `${data.location}`;



            //Get authentication status
            getStatus().then(data => {


                role=data.role;


                //Sponsors should not be able to like posts
                if(role==="sponsor"){
                    heartIcon.style.display="none";
                }else{
                    isPerson=true;
                }

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



            }).catch(err => {
                //Unauthenticated users should not be able to like posts
                loadingScreen.style.display="none";
                heartIcon.style.display="none";
                console.error(err)
            });




            //Get total likes
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




        })
        .catch(err => {
            alert(`Unable to fetch cards: ${err}`);

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

function getAuthor(cardId) {
    let author
    fetch("/notebridge/api/posts/" + cardId)
        .then(res => res.json())
        .then(data => {
            author = data.personId ;
        })
    return author;
}

function getUser(){
    let user;
    getStatus().then(data => {
        user = data.userId;
    });
    return user ;
}


function showEditCard() {
    let author = getAuthor(cardId);
    console.log(author)
    let viewer = getUser();
    console.log(viewer)

    if(author === viewer) {
        deleteIcon.innerHTML = `
        <button type="button" class="button"><img src="../assets/images/trash.png" alt="delete"> </button>
        `
        editIcon.innerHTML = `
        <span class="edit-icon" data-bs-toggle="modal" data-bs-target="#editPostModal">&#9998;</span>
        `
    }
}
