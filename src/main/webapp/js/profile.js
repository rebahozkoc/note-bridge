const nameSurnameSpan = document.getElementById("name-surname");
const usernameHeader = document.getElementById("username");
const createDateElement = document.getElementById("create-date");
const descriptionElement = document.getElementById("description");
const emailElement = document.getElementById("email");
const phoneNumberElement = document.getElementById("phone-number");
const profilePicture = document.getElementById("current-profile-picture");
const instrumentBox = document.getElementById("instrument-box");
const instrumentBoxContainer = document.getElementById("instrument-box-container");
const carouselInterestedPosts = document.getElementById("interestedPostsCarousel");
const innerCarousel = carouselInterestedPosts.querySelector(".carousel-inner");

const loadingScreen = document.getElementById("loading-screen");

//In case another user is trying to access the profile page of another user who is Person
let searchedUserId = GetURLParameter("id");

window.onload = function () {
    if (searchedUserId) {
        //User tries to access to another users' profile
        for (let editIcon of document.querySelectorAll(".edit-icon")) {
            editIcon.style.display = "none";
        }
        document.getElementById("editProfileBtn").style.display = "none";

        //Since only Person can show interest, we know for sure that the id provided is person's id

        //Get Person's data
        fetch("/notebridge/api/persons/" + searchedUserId)
            .then(res => {
                if (res.status === 200) {
                    return res.json();
                } else {
                    throw new Error("User not found");
                }

            })
            .then(data => {
                console.log(data)
                nameSurnameSpan.innerHTML = data.name + " " + data.lastname;
                usernameHeader.innerHTML = `@${data.username}`;
                descriptionElement.innerHTML = data.description;
                emailElement.innerHTML = data.email;
                phoneNumberElement.innerHTML = data.phoneNumber;


            }).catch(error => {
            console.error("Error while trying to fetch another user's data", error.toString());
        })

        //Get person's image
        fetch(`/notebridge/api/persons/${searchedUserId}/image`)
            .then(res => {
                if (res.status === 200) {
                    return res.blob();
                } else {
                    return res.text().then(errorText => {
                        throw new Error(`${errorText}`);
                    });
                }
            })
            .then(blob => {
                const imageUrl = URL.createObjectURL(blob);
                // Set the src attribute of the img element
                profilePicture.src = imageUrl;
                document.getElementById("img").src = imageUrl;
                loadingScreen.style.display = "none";
            })
            .catch(error => {
                console.error("Error", error.toString());
                loadingScreen.style.display = "none";
            });

        loadInstrumentData(searchedUserId);

    } else {
        //No id is provided, user is viewing his/her own profile
        modifyPageIfSponsor();
        loadUserData();
        loadUserImage();
    }
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


//Name Lastname Input Modal
nameLastnameModal = document.getElementById("editHeaderModal");
//Description Input Modal
descriptionModal = document.getElementById("editDescriptionModal");
//Contact Information Modal
contactInformationModal = document.getElementById("editContactModal");


nameLastnameModal.querySelector(".btn-primary").addEventListener("click", saveChangesNameLastname);
contactInformationModal.querySelector(".btn-primary").addEventListener("click", saveChangesContactInformation);
descriptionModal.querySelector(".btn-primary").addEventListener("click", saveChangesDescription);

function modifyPageIfSponsor() {
    getStatus().then(data => {
        if (data.role === "sponsor") {
            nameLastnameModal.querySelector("#editHeaderModalLabel").innerHTML = "Edit Company Name & Website URL";
            nameLastnameModal.querySelector("#nameInput").parentNode.querySelector("label").innerHTML = "Company Name";
            nameLastnameModal.querySelector("#lastnameInput").parentNode.querySelector("label").innerHTML = "Website URL";

            instrumentBoxContainer.style.display = "none";
        }
    }).catch(error => {
        console.error("Error", error.toString());
    })
}


//Change events for image upload in ProfilePicture Modal
document.getElementById('file').onchange = function () {
    var img = document.getElementById('img');
    var file = this.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        img.src = e.target.result;
    };
    reader.readAsDataURL(file);
};

//Upload image form
document.getElementById('upload-img').onsubmit = function (e) {
    e.preventDefault();

    var file = document.getElementById('file').files[0];

    var formData = new FormData();

    formData.append('file', file);
    getStatus()
        .then(data => {
            fetch(`/notebridge/api/${data.role}s/${data.userId}/image`, {
                method: 'PUT',
                body: formData
            }).then(response => {
                if (response.ok) {
                    alert('Image uploaded successfully!');
                    profilePicture.src = document.getElementById('img').src;
                    document.getElementById("editProfileModal").querySelector(".btn-close").click();
                } else {
                    console.error('Image upload failed!');
                    alert('Image upload failed!');
                }
            });


        })
        .catch(error => console.error('Error:', error.toString()));


};


//When the page is requested, loading user data along with picture from the server
//loadUserData();
//loadUserImage();

//MAKING PUT REQUEST TO UPDATE USER INFORMATION IN THE SERVER
async function updateUserInformation(updatedInfo) {
    try {
        const data = await getStatus();
        let response;
        if (data.role === "sponsor") {
            response = await fetch(`/notebridge/api/sponsors/${data.userId}`, {
                method: 'PUT',
                body: JSON.stringify(updatedInfo),
                headers: {
                    "Content-type": "application/json"
                }
            });
        } else {
            response = await fetch(`/notebridge/api/persons/${data.userId}`, {
                method: 'PUT',
                body: JSON.stringify(updatedInfo),
                headers: {
                    "Content-type": "application/json"
                }
            });
        }
        if (response.ok) {
            return true;
        } else {
            console.error('Failed to update user information:', response.status);
            return false;
        }

    } catch (error) {
        console.error('Error:', error.toString());
        return false;
    }
}


//Function to save changes in name and lastname for NAME SURNAME MODAL
function saveChangesNameLastname(event) {
    event.preventDefault();
    const name = nameLastnameModal.querySelector("#nameInput").value;
    const lastname = nameLastnameModal.querySelector("#lastnameInput").value;

    let updatedInfo;
    getStatus().then(data => {
        if (data.role === "sponsor") {
            updatedInfo = {
                companyName: name,
                websiteURL: lastname
            }
        } else {
            updatedInfo = {
                name: name,
                lastname: lastname
            }
        }
        if (updateUserInformation(updatedInfo)) {
            alert("Update successful");
            if (data.role === "sponsor") {
                nameSurnameSpan.innerHTML = name;
                nameSurnameSpan.parentNode.nextSibling.innerHTML = `<a href="https://${lastname}" target="_blank">${lastname}</a>`;
            } else {
                nameSurnameSpan.innerHTML = name + " " + lastname;

            }
        } else {
            alert("Update failed")
            loadUserData();
        }
        nameLastnameModal.querySelector(".btn-close").click();

    }).catch(error => {
        console.error("Error", error.toString());

    })

}

//Function to save changes in description for CONTACT INFORMATION MODAL
function saveChangesContactInformation(event) {
    event.preventDefault();
    const email = contactInformationModal.querySelector("#emailInput").value;
    const phoneNumber = contactInformationModal.querySelector("#phoneInput").value;
    const updatedInfo = {
        email: email,
        phoneNumber: phoneNumber
    }
    if (updateUserInformation(updatedInfo)) {
        alert("Contact Information updated successfully");
        emailElement.innerHTML = email;
        phoneNumberElement.innerHTML = phoneNumber;
    } else {
        alert("Contact Information update failed")
        loadUserData();

    }
    contactInformationModal.querySelector(".btn-close").click();
}

function saveChangesDescription(event) {
    event.preventDefault();
    const description = descriptionModal.querySelector("#descriptionInput").value;
    const updatedInfo = {
        description: description

    }
    if (updateUserInformation(updatedInfo)) {
        alert("Description updated successfully");
        descriptionElement.innerHTML = description;

    } else {
        alert("Description update failed")
        loadUserData();

    }
    descriptionModal.querySelector(".btn-close").click();
}


//TO UPDATE THE CHANGES INSIDE THE INPUT VALUES OF THE MODALS
function loadModalDataPerson(data) {
    nameLastnameModal.querySelector("#nameInput").value = data.name;
    nameLastnameModal.querySelector("#lastnameInput").value = data.lastname;
    descriptionModal.querySelector("#descriptionInput").value = data.description;
    contactInformationModal.querySelector("#emailInput").value = data.email;
    contactInformationModal.querySelector("#phoneInput").value = data.phoneNumber;
}

function loadModalDataSponsor(data) {
    nameLastnameModal.querySelector("#nameInput").value = data.companyName;
    nameLastnameModal.querySelector("#lastnameInput").value = data.websiteURL;
    descriptionModal.querySelector("#descriptionInput").value = data.description;
    contactInformationModal.querySelector("#emailInput").value = data.email;
    contactInformationModal.querySelector("#phoneInput").value = data.phoneNumber;
}


function loadUserData() {

    getStatus()
        .then(data => {

            if (data.role === "sponsor") {
                fetch(`/notebridge/api/sponsors/${data.userId}`)
                    .then(res => res.json())
                    .then(data => {

                        loadModalDataSponsor(data);

                        const createDate = new Date(parseInt(data.createDate));
                        const formattedDate = createDate.toLocaleDateString();
                        const formattedTime = createDate.toLocaleTimeString();

                        nameSurnameSpan.innerHTML = data.companyName;
                        const companyWebsite = document.createElement('h2');
                        companyWebsite.innerHTML = `<a href="https://${data.websiteURL}" target="_blank">${data.websiteURL}</a>`;
                        nameSurnameSpan.parentNode.parentNode.insertBefore(companyWebsite, nameSurnameSpan.parentNode.nextSibling);

                        usernameHeader.innerHTML = `@${data.username}`;
                        descriptionElement.innerHTML = data.description;
                        emailElement.innerHTML = data.email;
                        phoneNumberElement.innerHTML = data.phoneNumber;
                        createDateElement.innerHTML = `Account Created on: ${formattedDate} ${formattedTime}`;
                    })
                    .catch(error => {
                        console.error("Error", error);
                    });
            } else {
                fetch(`/notebridge/api/persons/${data.userId}`)
                    .then(res => res.json())
                    .then(data => {

                        loadModalDataPerson(data);
                        getInterestedPosts(data.id);

                        const createDate = new Date(parseInt(data.createDate));
                        const formattedDate = createDate.toLocaleDateString();
                        const formattedTime = createDate.toLocaleTimeString();

                        nameSurnameSpan.innerHTML = data.name + " " + data.lastname;
                        usernameHeader.innerHTML = `@${data.username}`;
                        descriptionElement.innerHTML = data.description;
                        emailElement.innerHTML = data.email;
                        phoneNumberElement.innerHTML = data.phoneNumber;
                        createDateElement.innerHTML = `Account Created on: ${formattedDate} ${formattedTime}`;
                    })
                    .catch(error => {
                        console.error("Error", error);
                    });

                loadInstrumentData(data.userId);
            }


        })
        .catch(error => {
            console.error("Error", error.toString());
        })

}


function loadInstrumentData(personId) {
    //Get Person's instrument interests
    fetch("/notebridge/api/instruments/persons/" + personId)
        .then(res => {
            if (res.status === 200) {
                return res.json();
            } else {
                throw new Error("User not found");
            }

        })
        .then(data => {
            console.log(data)

            instrumentBox.innerHTML = '';

            data.forEach(instrument => {
                const liElement = document.createElement('li');

                // Add a class to the li element
                liElement.classList.add('instrument-item');

                liElement.textContent = "Playing " + instrument.instrumentName + " for " + instrument.yearsOfExperience + " years.";

                instrumentBox.appendChild(liElement);
            });

        }).catch(error => {
        console.error("Error while trying to fetch another user's data", error.toString());
    })
}


function loadUserImage() {
    getStatus()
        .then(data => {
            fetch(`/notebridge/api/${data.role}s/${data.userId}/image`)
                .then(res => {
                    if (res.status === 200) {
                        return res.blob();
                    } else {
                        return res.text().then(errorText => {
                            throw new Error(`${errorText}`);
                        });
                    }
                })
                .then(blob => {
                    const imageUrl = URL.createObjectURL(blob);
                    // Set the src attribute of the img element
                    profilePicture.src = imageUrl;
                    document.getElementById("img").src = imageUrl;
                    loadingScreen.style.display = "none";
                })
                .catch(error => {
                    console.error("Error", error.toString());
                    loadingScreen.style.display = "none";
                });

        })
        .catch(error => {
            console.error("Error", error.toString());
            loadingScreen.style.display = "none";
        });
}


function getInterestedPosts(personId) {
    fetch("/notebridge/api/persons/" + personId + "/interestedposts")
        .then(res => {
            if (res.status === 200) {
                return res.json();
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }
        }).then(data => {
        if (data.length !== 0) {
            displayInterestedPosts(data);
        }
    }).catch(error => {
        console.error("Error", error.toString());

    })
}

function displayInterestedPosts(data) {
    carouselInterestedPosts.parentNode.style.display = "block";
    if (data.length <= 3) {
        for (card of data) {
            appendActiveCarousel(card);

        }
        const nextPrevIcons = carouselInterestedPosts.querySelectorAll("a");
        for (icon of nextPrevIcons) {
            icon.style.display = "none";
        }
    } else {
        let i = 0;
        for (i; i < 3; i++) {
            appendActiveCarousel(data[i]);
        }
        while (i + 3 <= data.length) {
            appendRegularCarousel(data.slice(i, i + 3));
            i += 3;

        }
        appendRegularCarousel(data.slice(i));
    }
}

function selectCard(card) {
    const cardId = card.getAttribute("data-card-id");
    window.location.href = 'card-details.html?id=' + cardId;
}

function appendActiveCarousel(card) {
    const activeCarousel = innerCarousel.querySelector(".active");

    if (!card.hasImage) {
        imageSource = "assets/images/placeholder.jpg";
    } else {
        imageSource = "data:image/png;base64,";
        imageSource += card.image;
    }

    activeCarousel.innerHTML += `
                <div class="card" data-card-id="${card.id}" onclick="selectCard(this)" style="width: 20rem; height: 27rem; margin: 35px 15px 15px;" id="displayed-card">
                    <img src="${imageSource}" height="250" class="card-img-top"  alt="card image">
                    <div class="card-body">
                        <h5 class="card-title">${card.title}</h5>
                        <p class="card-text">${card.description}</p>
                        <p class="card-text">Event type: ${card.eventType}</p>
                        <p class="card-text">Location: ${card.location}</p>
                    </div>
                </div>
                            `;


}

function appendRegularCarousel(cards) {
    if (cards.length !== 0) {
        const regularCarousel = document.createElement("div");
        regularCarousel.classList.add("carousel-item");
        regularCarousel.style.display = "flex";
        regularCarousel.style.justifyContent = "center";

        for (card of cards) {
            if (!card.hasImage) {
                imageSource = "assets/images/placeholder.jpg";
            } else {
                imageSource = "data:image/png;base64,";
                imageSource += card.image;
            }
            regularCarousel.innerHTML += `
                <div class="card" data-card-id="${card.id}" onclick="selectCard(this)" style="width: 20rem; height: 27rem; margin: 35px 15px 15px;" id="displayed-card">
                    <img src="${imageSource}" height="250" class="card-img-top"  alt="card image">
                    <div class="card-body">
                        <h5 class="card-title">${card.title}</h5>
                        <p class="card-text">${card.description}</p>
                        <p class="card-text">Event type: ${card.eventType}</p>
                        <p class="card-text">Location: ${card.location}</p>
                    </div>
                </div>
                            `;
        }
        innerCarousel.appendChild(regularCarousel);
    }


}