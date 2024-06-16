const nameSurnameHeader=document.getElementById("name-surname");
const usernameHeader=document.getElementById("username");
const createDateElement=document.getElementById("create-date");
const description=document.getElementById("description");
const email=document.getElementById("email");
const phoneNumber=document.getElementById("phone-number");
const profilePicture= document.getElementById("current-profile-picture");

document.getElementById('file').onchange = function () {
    var img = document.getElementById('img');
    var file = this.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        img.src = e.target.result;
    };
    reader.readAsDataURL(file);
};

document.getElementById('upload-img').onsubmit = function (e) {
    e.preventDefault();

    var file = document.getElementById('file').files[0];

    var formData = new FormData();

    //TODO IN CASE USER IS A SPONSOR CHANGE THE URL
    formData.append('file', file);
    getStatus()
        .then(data => {
            fetch(`http://localhost:8080/notebridge/api/persons/${data.userId}/image`, {
                method: 'PUT',
                body: formData
            }).then(response => {
                if (response.ok) {
                    alert('Image uploaded successfully!');
                    // Optionally, update the main profile picture on the page
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


loadUserData();
loadUserImage();


function loadUserData(){

    getStatus()
        .then(data=>{

            if(data.role==="sponsor"){
                fetch(`/notebridge/api/sponsors/${data.userId}`)
                    .then(res => res.json())
                    .then(data => {
                        const createDate = new Date(parseInt(data.createDate));
                        const formattedDate = createDate.toLocaleDateString();
                        const formattedTime = createDate.toLocaleTimeString();

                        nameSurnameHeader.innerHTML= data.companyName;
                        const companyWebsite = document.createElement('h2');
                        companyWebsite.innerHTML = `<a href="https://${data.websiteURL}" target="_blank">${data.websiteURL}</a>`;
                        nameSurnameHeader.parentNode.insertBefore(companyWebsite, nameSurnameHeader.nextSibling);

                        usernameHeader.innerHTML=`@${data.username}`;
                        description.innerHTML=data.description;
                        email.innerHTML=data.email;
                        phoneNumber.innerHTML=data.phoneNumber;
                        createDateElement.innerHTML = `Account Created on: ${formattedDate} ${formattedTime}`;
                    })
                    .catch(error=>{
                        console.error("Error", error);
                    })
            }else{
                fetch(`/notebridge/api/persons/${data.userId}`)
                    .then(res => res.json())
                    .then(data => {
                        const createDate = new Date(parseInt(data.createDate));
                        const formattedDate = createDate.toLocaleDateString();
                        const formattedTime = createDate.toLocaleTimeString();

                        nameSurnameHeader.innerHTML= data.name+" "+data.lastname;
                        usernameHeader.innerHTML=`@${data.username}`;
                        description.innerHTML=data.description;
                        email.innerHTML=data.email;
                        phoneNumber.innerHTML=data.phoneNumber;
                        createDateElement.innerHTML = `Account Created on: ${formattedDate} ${formattedTime}`;
                    })
                    .catch(error=>{
                        console.error("Error", error);
                    })
            }


    })
        .catch(error=>{
            console.error("Error", error.toString());
    })


}

function loadUserImage(){
    getStatus()
        .then(data=>{
            fetch(`/notebridge/api/persons/${data.userId}/image`)
                .then(res => res.blob())
                .then(blob => {
                    const imageUrl = URL.createObjectURL(blob);
                    // Set the src attribute of the img element
                    profilePicture.src = imageUrl;
                    document.getElementById("img").src=imageUrl;
                })
                .catch(error=>{
                    console.error("Error", error.toString());
                })

        })
        .catch(error=>{
            console.error("Error", error.toString());
        })
}