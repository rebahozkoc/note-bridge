const nameSurnameSpan=document.getElementById("name-surname");
const usernameHeader=document.getElementById("username");
const createDateElement=document.getElementById("create-date");
const descriptionElement=document.getElementById("description");
const emailElement=document.getElementById("email");
const phoneNumberElement=document.getElementById("phone-number");
const profilePicture= document.getElementById("current-profile-picture");

const loadingScreen=document.getElementById("loading-screen");






//Name Lastname Input Modal
nameLastnameModal=document.getElementById("editHeaderModal");
//Description Input Modal
descriptionModal=document.getElementById("editDescriptionModal");
//Contact Information Modal
contactInformationModal=document.getElementById("editContactModal");



modifyPageIfSponsor();

nameLastnameModal.querySelector(".btn-primary").addEventListener("click",saveChangesNameLastname);
contactInformationModal.querySelector(".btn-primary").addEventListener("click",saveChangesContactInformation);
descriptionModal.querySelector(".btn-primary").addEventListener("click",saveChangesDescription);

function modifyPageIfSponsor(){
    getStatus().then(data=>{
        if(data.role==="sponsor"){
            nameLastnameModal.querySelector("#editHeaderModalLabel").innerHTML="Edit Company Name & Website URL";
            nameLastnameModal.querySelector("#nameInput").parentNode.querySelector("label").innerHTML="Company Name";
            nameLastnameModal.querySelector("#lastnameInput").parentNode.querySelector("label").innerHTML="Website URL";
        }
    }).catch(error=>{
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
            fetch(`http://localhost:8080/notebridge/api/${data.role}s/${data.userId}/image`, {
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
loadUserData();
loadUserImage();

//MAKING PUT REQUEST TO UPDATE USER INFORMATION IN THE SERVER
async function updateUserInformation(updatedInfo) {
    try {
        const data = await getStatus();
        let response;
        if (data.role === "sponsor") {
            response= await fetch(`/notebridge/api/sponsors/${data.userId}`, {
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
function saveChangesNameLastname(event){
    event.preventDefault();
    const name=nameLastnameModal.querySelector("#nameInput").value;
    const lastname=nameLastnameModal.querySelector("#lastnameInput").value;

    let updatedInfo;
    getStatus().then(data=>{
        if(data.role==="sponsor"){
            updatedInfo={
                companyName:name,
                websiteURL:lastname
            }
        }else{
            updatedInfo={
                name:name,
                lastname:lastname
            }
        }
        if(updateUserInformation(updatedInfo)){
            alert("Update successful");
            if(data.role==="sponsor"){
                nameSurnameSpan.innerHTML=name;
                nameSurnameSpan.parentNode.nextSibling.innerHTML=`<a href="https://${lastname}" target="_blank">${lastname}</a>`;
            }else{
                nameSurnameSpan.innerHTML=name+" "+lastname;

            }
        }else{
            alert("Update failed")
            loadUserData();
        }
        nameLastnameModal.querySelector(".btn-close").click();

    }).catch(error=>{
        console.error("Error", error.toString());

    })

}

//Function to save changes in description for CONTACT INFORMATION MODAL
function saveChangesContactInformation(event){
    event.preventDefault();
    const email=contactInformationModal.querySelector("#emailInput").value;
    const phoneNumber=contactInformationModal.querySelector("#phoneInput").value;
    const updatedInfo={
        email:email,
        phoneNumber:phoneNumber
    }
    if(updateUserInformation(updatedInfo)) {
        alert("Contact Information updated successfully");
        emailElement.innerHTML=email;
        phoneNumberElement.innerHTML=phoneNumber;
    }else{
        alert("Contact Information update failed")
        loadUserData();

    }
    contactInformationModal.querySelector(".btn-close").click();
}

function saveChangesDescription(event){
    event.preventDefault();
    const description=descriptionModal.querySelector("#descriptionInput").value;
    const updatedInfo={
        description:description

    }
    if(updateUserInformation(updatedInfo)) {
        alert("Description updated successfully");
        descriptionElement.innerHTML=description;

    }else{
        alert("Description update failed")
        loadUserData();

    }
    descriptionModal.querySelector(".btn-close").click();
}





//TO UPDATE THE CHANGES INSIDE THE INPUT VALUES OF THE MODALS
function loadModalDataPerson(data){
    console.log(nameLastnameModal);
    nameLastnameModal.querySelector("#nameInput").value=data.name;
    nameLastnameModal.querySelector("#lastnameInput").value=data.lastname;
    descriptionModal.querySelector("#descriptionInput").value=data.description;
    contactInformationModal.querySelector("#emailInput").value=data.email;
    contactInformationModal.querySelector("#phoneInput").value=data.phoneNumber;
}
function loadModalDataSponsor(data){
    console.log(nameLastnameModal);
    nameLastnameModal.querySelector("#nameInput").value=data.companyName;
    nameLastnameModal.querySelector("#lastnameInput").value=data.websiteURL;
    descriptionModal.querySelector("#descriptionInput").value=data.description;
    contactInformationModal.querySelector("#emailInput").value=data.email;
    contactInformationModal.querySelector("#phoneInput").value=data.phoneNumber;
}


function loadUserData(){

    getStatus()
        .then(data=>{

            if(data.role==="sponsor"){
                fetch(`/notebridge/api/sponsors/${data.userId}`)
                    .then(res => res.json())
                    .then(data => {

                        loadModalDataSponsor(data);

                        const createDate = new Date(parseInt(data.createDate));
                        const formattedDate = createDate.toLocaleDateString();
                        const formattedTime = createDate.toLocaleTimeString();

                        nameSurnameSpan.innerHTML= data.companyName;
                        const companyWebsite = document.createElement('h2');
                        companyWebsite.innerHTML = `<a href="https://${data.websiteURL}" target="_blank">${data.websiteURL}</a>`;
                        nameSurnameSpan.parentNode.parentNode.insertBefore(companyWebsite, nameSurnameSpan.parentNode.nextSibling);

                        usernameHeader.innerHTML=`@${data.username}`;
                        descriptionElement.innerHTML=data.description;
                        emailElement.innerHTML=data.email;
                        phoneNumberElement.innerHTML=data.phoneNumber;
                        createDateElement.innerHTML = `Account Created on: ${formattedDate} ${formattedTime}`;
                    })
                    .catch(error=>{
                        console.error("Error", error);
                    });
            }else{
                fetch(`/notebridge/api/persons/${data.userId}`)
                    .then(res => res.json())
                    .then(data => {

                        loadModalDataPerson(data);

                        const createDate = new Date(parseInt(data.createDate));
                        const formattedDate = createDate.toLocaleDateString();
                        const formattedTime = createDate.toLocaleTimeString();

                        nameSurnameSpan.innerHTML= data.name+" "+data.lastname;
                        usernameHeader.innerHTML=`@${data.username}`;
                        descriptionElement.innerHTML=data.description;
                        emailElement.innerHTML=data.email;
                        phoneNumberElement.innerHTML=data.phoneNumber;
                        createDateElement.innerHTML = `Account Created on: ${formattedDate} ${formattedTime}`;
                    })
                    .catch(error=>{
                        console.error("Error", error);
                    });
            }


    })
        .catch(error=>{
            console.error("Error", error.toString());
    })


}

function loadUserImage(){
    getStatus()
        .then(data=>{
            fetch(`/notebridge/api/${data.role}s/${data.userId}/image`)
                .then(res => res.blob())
                .then(blob => {
                    const imageUrl = URL.createObjectURL(blob);
                    // Set the src attribute of the img element
                    profilePicture.src = imageUrl;
                    document.getElementById("img").src=imageUrl;
                    loadingScreen.style.display="none";
                })
                .catch(error=>{
                    console.error("Error", error.toString());
                    loadingScreen.style.display="none";
                });

        })
        .catch(error=>{
            console.error("Error", error.toString());
            loadingScreen.style.display="none";
        });
}