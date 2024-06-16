const nameSurnameHeader=document.getElementById("name-surname");
const usernameHeader=document.getElementById("username");
const createDateElement=document.getElementById("create-date");
const description=document.getElementById("description");
const email=document.getElementById("email");
const phoneNumber=document.getElementById("phone-number");




loadUserData();


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