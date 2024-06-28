let cardId = GetURLParameter('id');
let isPerson = false;
let postImages = document.getElementById("post-images");
let images = {};


const likeCountText = document.getElementById("like-countText");
const likeCount = document.getElementById("like-count");


const sponsorHeader = document.getElementById("sponsor-header");
const cardTitle = document.getElementById("title");
const description = document.getElementById("description");
const eventType = document.getElementById("event-type");
const eventLocation = document.getElementById("location");
const heartIcon = document.getElementById("heart-icon");
const deleteIcon = document.getElementById("delete-icon");
const editIcon = document.getElementById("edit-icon");
const interestButton = document.getElementById("interested-button");
const sponsorButton = document.getElementById("sponsor-button");
const postCreateDateSpan = document.getElementById("post-create-date");
const postLastUpdateDateSpan = document.getElementById("post-lastupdate-date");
const listOfUsernames = document.getElementById("list-of-usernames");
const confirmDeleteBtn = document.getElementById("confirmDelete");
const editPostModal = document.getElementById("editPostModal");
const editPostModalSaveBtn = document.getElementById("saveChangesPost");

const authorImage = document.getElementById("author-img");
const authorName = document.getElementById("author-name");
const authorUsername = document.getElementById("author-username");
const authorCreateDate = document.getElementById("author-createDate");

const loadingScreen = document.getElementById("loading-screen");

const commentsSection = document.getElementById("comments-section");
const userImage = document.getElementById("comment-img");

heartIcon.addEventListener("click", toggleLike);
confirmDeleteBtn.addEventListener("click", deletePost);

loadPostDetailsAndLikes(cardId);

editPostModalSaveBtn.addEventListener("click", updatePost);

window.onload = function () {
    checkLoggedIn();
    getUserId();
    getPostImages();
}

function updatePost() {
    if (document.getElementById("titleInput").value.trim() === "") {
        alert("Title cannot be empty!");
        return;
    }

    getStatus().then(data => {
        fetch(`/notebridge/api/posts/${cardId}`,
            {
                method: "PUT",
                body: JSON.stringify({
                    title: document.getElementById("titleInput").value,
                    description: document.getElementById("descriptionInput").value,
                    location: document.getElementById("locationInput").value,
                    personId: data.userId
                }),
                headers: {
                    "Content-type": "application/json"
                }
            })
            .then(res => {
                if (res.status === 200) {
                    alert("Post updated successfully!");
                    window.location.href = "card-details.html?id=" + cardId;
                } else {
                    return res.text().then(errorText => {
                        throw new Error(`${errorText}`);
                    });
                }
            })
            .catch(err => {
                console.error("Error updating post:", err);
            });
    }).catch(err => {
        console.error("Error getting status:", err);
    })


}

function deletePost() {
    fetch(`/notebridge/api/posts/${cardId}`, {
        method: "DELETE"
    })
        .then(res => {
            if (res.status === 200) {
                alert("Post deleted successfully!");
                window.location.href = "cards.html";
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }
        })
        .catch(err => {
            alert("Error deleting post!");
            console.error("Error deleting post:", err);
        });

}

function viewInterested(element) {

    if (!element.classList.contains("already-pressed-btn")) {

        element.classList.add("already-pressed-btn");
        element.innerHTML = "Hide Interested Users";
        if (listOfUsernames.hasChildNodes()) {
            fetch("/notebridge/api/posts/" + cardId + "/interestedusers")
                .then(res => {
                    if (res.status === 200) {
                        return res.json();
                    } else {
                        return res.text().then(errorText => {
                            throw new Error(`${errorText}`);
                        });
                    }

                }).then(data => {

                data.forEach(user => {
                    const listElement = document.createElement("li");
                    const anchorElement = document.createElement("a");
                    anchorElement.onclick = () => {
                        window.location.href = "profile.html?id=" + user.id;
                    };
                    anchorElement.innerHTML = user.username;
                    anchorElement.classList.add("interestedUser");
                    listElement.append(anchorElement);
                    listOfUsernames.appendChild(listElement);
                })

            }).catch(
                err => {
                    console.error(err);
                }
            )
        } else {
            for (let liElement of listOfUsernames.children) {
                liElement.style.display = "block";
            }
        }

    } else {
        element.classList.remove("already-pressed-btn");
        element.innerHTML = "View Interested Users";
        for (let liElement of listOfUsernames.children) {
            liElement.style.display = "none";
        }
    }


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
    if (images.length === 0) {
        postImages.innerHTML = `
        <img src="assets/images/image-placeholder.jpg" class="img-fluid border border-dark border-2 rounded-2" width="30%" height="30%" alt="post image placeholder">
        `;
    } else if (images.length === 1) {
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
                    getAuthor(data.userId, data.role);
                    if (data.role === "person") {
                        showCommentsSection();
                    }
                    if (data.role === "sponsor") {
                        hideCommentsSection();
                    }
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

function getAuthor(userId, role) {
    fetch("/notebridge/api/posts/" + cardId)
        .then(res => res.json())
        .then(data => {
            checkPostBelongsToUser(userId, data.personId);
            if (role === "person") {
                displayInterestedButton(userId, data.id, data.personId);

            }
        })
}


function saveSponsorshipDates() {
    const fromDate = document.getElementById('sponsored-from').value;
    const untilDate = document.getElementById('sponsored-until').value;

    fetch(`/notebridge/api/sponsors/${cardId}/post`,
        {
            method: "PUT",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                sponsoredFrom: fromDate,
                sponsoredUntil: untilDate,
            }),
        }).then(res => {
            if (res.status === 200) {
                alert("Post sponsored successfully!");
                window.location.href = "card-details.html?id=" + cardId;
            } else {
                alert("Failed to sponsor post!");
                return res.text().then(errorText => {
                    console.error(`${errorText}`);
                });
            }
        }
    )

}

function checkPostBelongsToUser(userId, author) {
    if (author === userId) {
        deleteIcon.innerHTML = `
        <span class="button"  data-bs-toggle="modal" data-bs-target="#deleteModal" style="cursor: pointer; background-color: transparent; border: transparent; visibility: visible" id="delete-button" ><img src="assets/images/trash.png" style="width: 20px; height: 20px"> </span>
        `
        editIcon.innerHTML = `
        <span class="edit-icon" data-bs-toggle="modal" data-bs-target="#editPostModal" style="visibility: visible" id="edit-button">&#9998;</span>
        `
    }
}


function displayInterestedButton(userId, postId, author) {

    if (author !== userId) {

        //Check if user is already interested in the post before loading
        fetch("/notebridge/api/posts/" + postId + "/interested")
            .then(res => {
                if (res.status === 200) {
                    return res.json();
                } else {
                    return res.text().then(errorText => {
                        throw new Error(`${errorText}`);
                    });
                }
            }).then(data => {
                if (data.isInterested) {
                    interestButton.innerHTML = `
                    <a class="button-1 already-pressed-btn" data-post-id="${postId}" href="#" role="button" onclick="toggleInterest(this)">You are already interested!</a>
                    `;
                } else {
                    interestButton.innerHTML = `
                        <a class="button-1" data-post-id="${postId}" href="#" role="button" onclick="toggleInterest(this)">I'm Interested!</a>

                        `
                }
            }
        ).catch(err => {
            interestButton.innerHTML = `
                        <a class="btn btn-danger" data-post-id="${postId}" href="#" role="button">Failed to fetch interest information!</a>

                        `
        });

    }
}

function toggleInterest(element) {
    const postId = element.dataset.postId;

    fetch("/notebridge/api/posts/" + postId + "/interested", {
        method: "POST"
    }).then(res => {
        if (res.status === 200) {
            if (!element.classList.contains("already-pressed-btn")) {
                //User will show interest
                element.classList.add("already-pressed-btn");
                element.innerHTML = "You are already interested!";

            } else {
                //User will remove interest
                element.classList.remove("already-pressed-btn");
                element.innerHTML = "I'm Interested!";

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
     <a class="button-1" href="login.html" role="button">I'm Interested!</a>

    `
}

function GetURLParameter(sParam) {
    const sPageURL = window.location.search.substring(1);
    const sURLVariables = sPageURL.split('&');
    for (let i = 0; i < sURLVariables.length; i++) {
        let sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {
            return sParameterName[1];
        }
    }
}

async function loadPostDetailsAndLikes(cardId) {

    try {
        const post = await fetch('/notebridge/api/posts/' + cardId);

        const postData = await post.json();

        editPostModal.querySelector("#titleInput").value = postData.title;
        editPostModal.querySelector("#descriptionInput").value = postData.description;
        editPostModal.querySelector("#locationInput").value = postData.location;


        cardTitle.innerHTML = `<h3>${postData.title}</h3>`;
        description.innerHTML = `<h5>${postData.description}</h5>`;
        eventType.innerHTML = `${postData.eventType}`;
        eventLocation.innerHTML = `${postData.location}`;
        postCreateDateSpan.innerHTML = `${new Date(parseInt(postData.createDate)).toLocaleDateString()} ${new Date(parseInt(postData.createDate)).toLocaleTimeString()}`;
        postLastUpdateDateSpan.innerHTML = `${new Date(parseInt(postData.lastUpdate)).toLocaleDateString()} ${new Date(parseInt(postData.lastUpdate)).toLocaleTimeString()}`;

        const currentDate = new Date();
        if (postData.sponsoredBy != null && currentDate > new Date(parseInt(postData.sponsoredFrom)) && currentDate < new Date(parseInt(postData.sponsoredUntil))) {
            //console.log("Post is sponsored");

            loadSponsorData(postData);

        }else{
            //Post is not sponsored
            //if the user is a sponsor, show the sponsor button
            getStatus().then(data => {
                if(data.role==="sponsor"){
                    sponsorButton.style.display="block";
                }
            }).catch(err => {
                console.error("Error getting status:", err);

            })
        }


        try {
            await updateTotalLikes(cardId);
            await loadAuthorImage(postData.personId);
            await loadAuthorDetails(postData.personId);
        } catch (err) {
            console.error(err);
        }

        try {
            const statusData = await getStatus();
            let role = statusData.role;
            //Sponsors should not be able to like posts
            if (role === "sponsor") {
                heartIcon.style.display = "none";
            } else {
                isPerson = true;
            }

            await checkIfLiked(cardId);

            loadingScreen.style.display = "none";

        } catch (err) {
            console.error(err);
            heartIcon.style.display = "none";
            loadingScreen.style.display = "none";

        }

    } catch (err) {
        console.error(err);
        loadingScreen.style.display = "none";

    }
}

function loadSponsorData(postData) {
    sponsorHeader.style.display = "block";

    fetch(`/notebridge/api/sponsors/${postData.sponsoredBy}`).then(
        res => {
            if (res.status === 200) {
                res.json().then(data => {
                    sponsorHeader.innerHTML = `Sponsored by: ${data.companyName}`;
                    sponsorHeader.href = `https://${data.websiteURL}`;
                });
            }
        }
    )
}


function loadAuthorImage(personId) {
    fetch(`/notebridge/api/persons/${personId}/image`)
        .then(res => {
            if (res.status === 200) {
                return res.blob();
            } else {
                authorImage.src = "assets/images/profile-picture-placeholder.png";
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
        .catch(error => {
            console.error("Error", error.toString());
        });
}

function loadAuthorDetails(personId) {
    fetch(`/notebridge/api/persons/${personId}`).then(res => {


            if (res.status === 200) {
                return res.json();
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }
        }
    ).then(data => {
        authorUsername.innerHTML =
            `<a href="profile.html?id=${data.id}" class="link-primary">@${data.username}</a>`;

        authorName.innerHTML = data.name + " " + data.lastname;
        const createDate = new Date(parseInt(data.createDate));
        const formattedDate = createDate.toLocaleDateString();
        const formattedTime = createDate.toLocaleTimeString();
        authorCreateDate.innerHTML = `Account Created on: ${formattedDate} ${formattedTime}`;

    }).catch(err => {
        console.error(err);
    })
}

function checkIfLiked(cardId) {


    fetch("/notebridge/api/posts/" + cardId + "/like")
        .then(res => {
            if (res.status === 200) {
                return res.json();
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }

        }).then(data => {


        if (data.isLiked) {

            heartIcon.classList.remove("bi-heart");
            heartIcon.classList.add("bi-heart-fill");
        }

        loadingScreen.style.display = "none";


    }).catch(err => {
        console.error(err);
        loadingScreen.style.display = "none";

    })
}


function updateTotalLikes(cardId) {
    fetch("/notebridge/api/posts/" + cardId + "/likes").then(
        res => {
            if (res.status === 200) {
                return res.json();
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }
        }
    ).then(data => {

        if (data.totalLikes === 0) {
            likeCount.innerHTML = "";
            likeCountText.innerHTML = "No one likes this posts yet!";
            if (isPerson) {
                likeCountText.innerHTML += ", Click on the heart icon to be the first one to like it!";
            }
        } else {
            likeCount.innerHTML = data.totalLikes;
            likeCountText.innerHTML = ` people liked this post!`;
        }

    }).catch(err => {
        alert(`Unable to fetch likes: ${err}`);

    });
}


function toggleLike() {


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

        if (parseInt(likeCount.innerHTML) == 1) {
            likeCount.innerHTML = "";
            likeCountText.innerHTML = "No one likes this posts yet, Click on to be the first one to like it!";
        } else {
            likeCount.innerHTML = parseInt(likeCount.innerHTML) - 1;
        }
    } else {


        heartIcon.classList.remove("bi-heart");
        heartIcon.classList.add("bi-heart-fill");
        if (likeCount.innerHTML == "") {
            likeCount.innerHTML = 1;
            likeCountText.innerHTML = " person liked this post!";
        } else {
            likeCount.innerHTML = parseInt(likeCount.innerHTML) + 1;
        }


    }
}

document.addEventListener("DOMContentLoaded", function () {
    loadComments();
});

let commentToDelete = null;

async function loadComments() {
    let currentUser;
    getStatus().then(data => {
        currentUser = data;
    });

    try {
        fetch(`/notebridge/api/posts/${cardId}/comments`).then(res => {
            if (res.status === 200) {
                return res.json().then(data => {
                    const comments = data.comments;
                    for (const comment of comments) {
                        //console.log(`Loading comment`);
                        console.log(comment);
                        addCommentToPage(comment, currentUser);
                    }
                });
            }
        });


    } catch (err) {
        console.error("Error loading comments:", err);
    }
}

function reloadComments() {
    const commentsContainer = document.getElementById("comments-container");
    commentsContainer.innerHTML = "";
    loadComments();
}

function deleteComment(commentId) {
    fetch(`/notebridge/api/comments/${commentId}`, {
        method: "DELETE"
    })
        .then(res => {
            if (res.status === 200) {
                alert("Comment deleted succesfully.");
                window.location.href = `card-details.html?id=${cardId}`; //we need to have comment element has an id attribute formatted as comment-{commentId},
                //document.getElementById(`comment-${commentId}`).remove();
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }
        })
        .catch(err => {
            console.error("Error deleting comment:", err);
        });
}

function showDeleteConfirmation(commentId) {
    commentToDelete = commentId;
    const deleteModal = new bootstrap.Modal(document.getElementById('deleteCommentModal'));
    deleteModal.show();
}


document.getElementById('confirmDeleteButton').addEventListener('click', function () {
    //console.log("Confirm delete clicked. Comment ID to delete:", commentToDelete);
    if (commentToDelete !== null) {
        deleteComment(commentToDelete);
        commentToDelete = null;
        const deleteModal = bootstrap.Modal.getInstance(document.getElementById('deleteCommentModal'));
        deleteModal.hide();
    }
});

function addCommentToPage(comment, user, addToTop = false) {

    const commentsContainer = document.getElementById("comments-container");
    const commentElement = document.createElement("div");
    commentElement.classList.add("comment");

    const formattedDate = new Date(comment.createDate).toLocaleDateString();
    const formattedTime = new Date(comment.createDate).toLocaleTimeString();

    let deleteIconHtml = '';
    if (user && user.userId === comment.personId) {
        deleteIconHtml = `<i class="bi bi-eraser delete-icon" onclick="showDeleteConfirmation(${comment.id})" style="cursor:pointer;"></i>`;
    } else {
        deleteIconHtml = `<small></small>`;
    }

    if (comment.picture === null || comment.picture === "") {
        comment.picture = "assets/images/profile-picture-placeholder.png"
    } else {
        comment.picture = `data:image/png;base64,${comment.picture}`;
    }


    commentElement.innerHTML = `
        <div class="row mb-2">
            <div class="col-md-12 card-body-2 text-left comment-header">
                <!-- User image -->
                <div class="comment-image">
                    <img src="${comment.picture}" class="img-fluid rounded-circle" alt="User Image">
                    <h6 class="mt-2 mb-0">
                        <a href="profile.html?id=${comment.personId}" class="link-primary">@${comment.username}</a>
                    </h6>
                    <small>${formattedDate} ${formattedTime}</small>
                </div>
                
                <!-- Comment body -->
                <div class="comment-body">
                    <p class="mb-0">${comment.content}</p>
                    ${deleteIconHtml}
                </div>
            </div>
        </div>
    `;
    //commentsContainer.insertBefore(commentElement, commentsContainer.firstChild);

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
                personId: user.userId,
                picture: user.picture

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
            document.getElementById("comment-text").value = "";
            reloadComments();
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

function shareOnFacebook() {
    const url = encodeURIComponent(window.location.href);
    const title = cardTitle.innerText;
    const summary = description.innerText;
    const facebookShareUrl = `https://www.facebook.com/sharer/sharer.php?s=100&p[url]=${url}&p[title]=${title}&p[summary]=${summary}&amp;src=sdkpreparse`;
    //href="https://www.facebook.com/sharer/sharer.php?s=100&p[url]=http://www.example.com&p[images][0]=&p[title]=Title%20Goes%20Here&p[summary]=Description%20goes%20here!"
    window.open(facebookShareUrl, '_blank');
}

function shareOnX() {
    const url = encodeURIComponent(window.location.href);
    const text = "Take a look at this post on Note-Bridge!";
    const twitterShareUrl = `https://twitter.com/intent/tweet?url=${url}&text=${text}`;
    window.open(twitterShareUrl, '_blank');
}

