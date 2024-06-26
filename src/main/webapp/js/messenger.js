let contacts = {};
let messages = {};
let posts = {};
let userID=null;
let selectedContact=null;
const contactElement=document.getElementById("contacts");
const messageElement=document.getElementById("messageBox");
const message=document.getElementById("but");
const loadingScreen = document.getElementById("loading-screen");
const modalBody=document.getElementById("contactModal").childNodes[1].childNodes[1].childNodes[3];

window.onload = function() {
    checkLoggedIn();
    fetchContacts();
}

function turnGrey(x) {
    x.className = "list-group-item list-group-item-action list-group-item-secondary d-flex justify-content-between align-items-center rounded-3"
}
function turnWhite(x) {
    x.className = "list-group-item d-flex justify-content-between align-items-center rounded-3"
}

function returnModalToNormal(){
    modalBody.innerHTML=`<form>
                    <label for="usernameContact">Username</label>
                    <textarea class="form-control" id="usernameContact" rows="1" placeholder="Message"></textarea>
                </form>
                <form>
                    <label for="newContactMessage">Want to send a first message?</label>
                    <textarea class="form-control" id="newContactMessage" rows="1" placeholder="Message"></textarea>
                </form>`
}


setInterval(fetchContacts, 2000);


function updateSeenMessages(){
    let unreadmessages=[];
    for (let i = 0; i < messages['data'].length; i++) {
        if (messages['data'][i].user_id!==userID && messages['data'][i].isread===false){
            unreadmessages.push(messages['data'][i].id);
        }
    }
    if (!(!Array.isArray(unreadmessages) || !unreadmessages.length)) {
        for (let i = 0; i < unreadmessages.length; i++) {
            fetch(`/notebridge/api/message/readmessages/${unreadmessages[i]}`, {method: "PUT"})
                .then(r=>{
                    getUnreadMessagesCount();
                })
                .catch(err => {
                    console.error(`Unable to read messages: ${err.status}`);
                    console.error(err);
                })
        }
    }
}

function loadPosts(){
    fetch(`/notebridge/api/posts/person/${userID}`, {method: "GET"})
        .then(res => res.json())
        .then(data => {
            posts=data;
            fillPosts();
        })
}

function fillPosts(){
    let inputElement=document.getElementById("inputGroupSelect");
    console.log(posts);
    inputElement.innerHTML = `
                ${posts.map(post => `${inputPosts(post)}`).join("\n")}`
}

function inputPosts(post){
    let p=post.title+"-"+post.eventType+"-"+post.id;
    return`
        <option value="${p}">${post.title} (Type: ${post.eventType}) </option>
        `
}

function newInvite(){
    let content=document.getElementById("sponsor_description").value;
    let post=document.getElementById("inputGroupSelect").value.split("-")[document.getElementById("inputGroupSelect").value.split("-").length-1];
    let tag=document.getElementById("inputGroupSelect").value.split("-")[document.getElementById("inputGroupSelect").value.split("-").length-2];
    let title=document.getElementById("inputGroupSelect").value.split("-")[document.getElementById("inputGroupSelect").value.split("-").length-3];
    let postLink= 'card-details.html?id=' + post;
    getStatus()
        .then(data => {
            let dataObject = {};
            let message = content+"-"+title+"-"+tag+"-"+postLink;
            dataObject.user_id = data.userId;
            dataObject.content = message;
            let encodedMessage = encodeURIComponent(message);
            fetch(`/notebridge/api/message/newmessage/${data.userId}/${selectedContact}/${encodedMessage}`, {
                method: "POST",
                body: JSON.stringify(dataObject),
                headers: {
                    "Content-type": "application/json"
                }
            }).then(r => {
                document.getElementById("messageBox").scrollTop=document.getElementById("messageBox").scrollHeight;
                loadMessagesWithID(selectedContact, dataObject.user_id, data.username);
            })
        })

}

function newContact() {
    const username = document.getElementById("usernameContact").value;
    const message = document.getElementById("newContactMessage").value;
    let modalElement=document.getElementById("contactModal");
    console.log(modalBody);
    let dataObject = {};
    getStatus()
        .then(data => {
            fetch(`/notebridge/api/persons/getid/${username}`, {method: "GET"})
                .then(res => {
                    if (res.status === 404) {
                        if (modalBody.querySelector(".alert") === null) {
                            modalBody.innerHTML += `<div class="alert alert-danger" role="alert">
                                                    User was not found.
                                                </div>`
                            message.value = '';
                        }else{
                            modalBody.innerHTML=modalBody.innerHTML;
                        }
                    } else {
                        res.json()
                            .then(res2 => {
                                fetch(`/notebridge/api/message/newhistory/${res2}/${data.userId}`, {method: "POST"})
                                    .then(res4 => {
                                        if (res4.status === 500) {
                                            if (modalBody.querySelector(".alert") === null) {
                                                modalBody.innerHTML += `<div class="alert alert-danger" role="alert">
                                                                                User is already a contact.
                                                                            </div>`
                                                message.value = '';
                                            } else {
                                                console.log(modalBody.querySelector(".alert").innerHTML);
                                                modalBody.querySelector(".alert").innerHTML = `User is already a contact.`;
                                                modalBody.innerHTML=modalBody.innerHTML;
                                            }
                                        } else {
                                            fetchContacts();
                                            selectedContact = res2;
                                            if (message !== undefined) {
                                                dataObject.user_id = data.userId;
                                                dataObject.content = message;
                                                let encodedMessage = encodeURIComponent(message);
                                                message.value = '';
                                                fetch(`/notebridge/api/message/newmessage/${data.userId}/${selectedContact}/${encodedMessage}`, {
                                                    method: "POST",
                                                    body: JSON.stringify(dataObject),
                                                    headers: {
                                                        "Content-type": "application/json"
                                                    }
                                                }).then(r => {
                                                    document.getElementById("messageBox").scrollTop = document.getElementById("messageBox").scrollHeight;
                                                    loadMessagesWithID(selectedContact, dataObject.user_id, data.username);
                                                })
                                            }
                                            modalBody.innerHTML = `<div class="alert alert-success" role="alert">
                                                    User added to contacts.
                                                </div>`;
                                        }
                                    })
                            })

                    }
                })
        })
    message.value='';
}

function loadMessages(id, username) {
    if (selectedContact === null) {
        selectedContact=contacts['data'][0].id;
        loadMessagesWithID(selectedContact,id, username);
    } else {
        loadMessagesWithID(selectedContact,id, username);
    }
}

function loadMessagesWithID(x, id, user){
    let username=null;
    for (const contact in contacts['data']){
        if (contacts['data'][contact].id===x){
            username=contacts['data'][contact].username;
        }
    }
    document.getElementById("contactName").innerText=username;
    fetchMessages(x, id, user);
}

function loadMessagesForThisUser(x){
    messageElement.innerHTML=``;
    messages={};
    selectedContact=getId(`${x.innerText.split("\n")[0]}`);
    document.getElementById("contactName").innerText=`${x.innerText.split("\n")[0]}`;
    getStatus()
        .then(data=> {
            fetchMessages(getId(`${x.innerText.split("\n")[0]}`),data.userId,data.username);
        })
}

function getId(x){
    for (let i = 0; i < contacts['data'].length; i++) {
        if (contacts['data'][i].username === x) {
            return contacts['data'][i].id;
        }
    }
}

function getUsername(x){
    for (let i = 0; i < contacts['data'].length; i++) {
        if (contacts['data'][i].id === x) {
            return contacts['data'][i].username;
        }
    }
}

function clearQuery(){
    let textbox=document.getElementById("message");
    textbox.value='';
}
function fetchContacts(){
    getStatus()
        .then(data=> {
            fetch(`/notebridge/api/message/contact/${data.userId}`, {method: "GET"})
                .then(res => res.json())
                .then(data2 => {
                    userID=data.userId;
                    noChangeInContacts(data2);
                    loadMessages(userID,data.username);
                    loadingScreen.style.display = "none";
                })
                .catch(err => {
                    console.error(`Unable to fetch contacts: ${err.status}`);
                    console.error(err);
                    if (contactElement.querySelector("p")===null) {
                        contactElement.innerHTML += `<p>No contacts found</p>`;
                    }else{

                    }
                    loadingScreen.style.display = "none";
                });
        })
}

function fetchMessages(x, id, username){
    fetch(`/notebridge/api/message/messages/${x}/${id}`, {method: "GET"})
        .then(res => res.json())
        .then(data => {
            noChangeInMessages(data,id,username);
        }).then(r=>{
        updateSeenMessages();
    }).catch(err => {
        console.error(`Unable to fetch messages: ${err.status}`);
        console.error(err);
    })
}

function noChangeInContacts(newContacts){
    if (JSON.stringify(newContacts)!==JSON.stringify(contacts)){
        contacts = newContacts;
        showContacts();
    }
}

function noChangeInMessages(newMessages, id ,username){
    if (JSON.stringify(newMessages)!==JSON.stringify(messages)){
        messages=newMessages;
        showMessageHistory(id,username);
        document.getElementById("messageBox").scrollTop=document.getElementById("messageBox").scrollHeight;
    }
}

function showMessageHistory(id, username){
    messageElement.innerHTML = `
                ${messages.data.map(message => `${divideMessages(message, id, username)}`).join("\n")}`
}

function divideMessages(message, id, username){
    if (`${message.user_id}` === id.toString()) {
        return `${showMessageRight(message, username)}`;
    } else {
        return `${showMessageLeft(message)}`;
    }
}


function takeToCard(postLink){
    window.location.href=postLink;
}

function showMessageLeft(message){
    let time=message.createddate;
    const date = new Date(time);
    const formatter = new Intl.DateTimeFormat('en-US', { hour: '2-digit', minute: '2-digit'});
    const formattedTime = formatter.format(date);
    let username=getUsername(message.user_id);
    if (message.content.includes("-card-details.html?id=")){
        let post_id=message.content.split("-card-details.html?id=")[message.content.split("-card-details.html?id=").length-1];
        let tag=message.content.split("-")[message.content.split("-").length-3];
        let title=message.content.split("-")[message.content.split("-").length-4];
        let postLink="card-details.html?id="+post_id;
        let content=message.content.replace("-"+title+"-"+tag+"-"+postLink,"");

        return `
            <div class="left">
            <p style="color: white;text-align: right; margin: 0;padding: 0;">${username}</p>
            <div class="dropup-center dropup message" style="justify-self: right">
                <button class="btn data-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false" style="text-align: left;width: 100%" value="${message.content}">
                    ${username} has invited you to their event:
                    <h1>${title}</h1>
                    <div style="border-radius: 25px; background-color: pink; padding-left: 1vh;padding-right: 1vh; width: fit-content">${tag}</div>
                    ${content}
                </button>
                <input type="hidden" value=${message.createddate}>
                <ul class="dropdown-menu">
                    <li><a class="dropdown-item" onclick="takeToCard('${postLink}')">Open Link</a></li>
                </ul>
            </div>
                <p style="color: white;text-align: right;">${formattedTime}</p>
            </div>
            `
    }else {
        return `
         <div class="left">
         <p style="color: white;text-align: left; margin: 0;padding: 0;">${username}</p>
         <div class="dropup-center dropup message" style="justify-self: left">
            <button class="btn data-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false" style="text-align: left;width: 100%">
                ${message.content}
            </button>
         </div>
         <p style="color: white">${formattedTime}</p>
         </div>
        `
    }
}
function showMessageRight(message, username) {
    let time = message.createddate;
    const date = new Date(time);
    const formatter = new Intl.DateTimeFormat('en-US', {hour: '2-digit', minute: '2-digit'});
    const formattedTime = formatter.format(date);
    if (message.content.includes("-card-details.html?id=")) {
        let post_id = message.content.split("-card-details.html?id=")[message.content.split("-card-details.html?id=").length - 1];
        let tag = message.content.split("-")[message.content.split("-").length - 3];
        let title = message.content.split("-")[message.content.split("-").length - 4];
        let postLink = "card-details.html?id=" + post_id;
        let content=message.content.replace("-"+title+"-"+tag+"-"+postLink,"");

        return `
            <div class="right">
            <p style="color: white;text-align: right; margin: 0;padding: 0;">${username}</p>
            <div class="dropup-center dropup message" style="justify-self: right">
                <button class="btn data-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false" style="text-align: left;width: 100%" value="${message.content}">
                    ${username} has invited you to their event:
                    <h1>${title}</h1>
                    <div style="border-radius: 25px; background-color: pink; padding-left: 1vh;padding-right: 1vh; width: fit-content">${tag}</div>
                    ${content}
                </button>
                <input type="hidden" value=${message.createddate}>
                <ul class="dropdown-menu">
                    <li><a class="dropdown-item" onclick="deleteMessage(this.parentNode.parentNode.parentNode.parentNode)">Delete</a></li>
                    <li><a class="dropdown-item" onclick="takeToCard('${postLink}')">Open Link</a></li>
                </ul>
            </div>
                <p style="color: white;text-align: right;">${formattedTime}</p>
            </div>
            `
    } else {
        return `
         <div class="right">
         <p style="color: white;text-align: right; margin: 0;padding: 0;">${username}</p>
         <div class="dropup-center dropup message" style="justify-self: right">
            <button class="btn data-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false" style="text-align: left;width: 100%" value="${message.content}">
                ${message.content}
            </button>
            <input type="hidden" value=${message.createddate}>
            <ul class="dropdown-menu">
                <li><a class="dropdown-item" onclick="deleteMessage(this.parentNode.parentNode.parentNode.parentNode)">Delete</a></li>
            </ul>
         </div>
         <p style="color: white;text-align: right;">${formattedTime}</p>
         </div>
        `
    }
}

function showContact(contact) {
    let contactElement = `
        <div class="contact">
            <li id="${contact.username}" class="list-group-item d-flex justify-content-between align-items-center rounded-3" onmouseenter="turnGrey(this)" onmouseleave="turnWhite(this)" onclick="loadMessagesForThisUser(this)">
                ${contact.username}
            </li>
        `
    contactElement += `</div>`;
    return contactElement;
}
setInterval(function () {
    getUnreadMessagesCount();
}, 1000);

function showContacts() {
    contactElement.innerHTML = `
        ${contacts.data.map(contact => `${showContact(contact)}`).join("\n")}
    `;
}

function getUnreadMessagesCount(){
    for (let i = 0; i < contacts["data"].length; i++) {
        let contactElement=document.getElementById(contacts["data"][i].username);
        fetch(`/notebridge/api/message/count/${userID}/${contacts["data"][i].id}`,{method:"GET"})
            .then(res=>res.json()
                .then(data=>{
                    inputCount(data,contactElement);
                })
            )
    }
}

function inputCount(data, contactElement){
    let currentCount = contactElement.querySelector("span");
    if (data!==0) {
        if (currentCount === undefined || currentCount===null) {
            contactElement.innerHTML += `
                <span class="badge rounded-pill bg-primary">${data}</span>`;
        }
    }else{
        if (currentCount!==null) {
            contactElement.removeChild(currentCount);
        }
    }
}

const form = document.getElementById("newMessage");
form.addEventListener("submit", function(event) {
    getStatus()
        .then(data => {
            let dataObject = {};
            let message = document.getElementById("message").value;
            dataObject.user_id = data.userId;
            dataObject.content = message;
            let encodedMessage = encodeURIComponent(message);
            fetch(`/notebridge/api/message/newmessage/${data.userId}/${selectedContact}/${encodedMessage}`, {
                method: "POST",
                body: JSON.stringify(dataObject),
                headers: {
                    "Content-type": "application/json"
                }
            }).then(r => {
                document.getElementById("messageBox").scrollTop=document.getElementById("messageBox").scrollHeight;
                loadMessagesWithID(selectedContact, dataObject.user_id, data.username);
                clearQuery();
            })
        })
    event.preventDefault();

})

function formatDate(date) {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    let milliseconds = date.getMilliseconds().toString();
    while (milliseconds.endsWith('0')) {
        milliseconds = milliseconds.substring(0, milliseconds.length - 1);
    }
    const offset = -date.getTimezoneOffset();
    const timezone = (offset >= 0 ? '+' : '-') +
        Math.abs(Math.floor(offset / 60)).toString();

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}.${milliseconds}${timezone}`;
}

function deleteMessage(x){
    let idElement=x.querySelector("input[type='hidden']");
    let timestamp=idElement.value;
    const date = new Date(parseInt(timestamp));
    console.log(parseInt(timestamp));
    let time=formatDate(date);
    let user_id=userID;
    let contentElement=x.querySelector("button");
    let content=contentElement.value;
    let encodedMessage = encodeURIComponent(content);
    console.log(content);
    fetch(`/notebridge/api/message/deletemessage/${user_id}/${time}/${encodedMessage}`, { method: "DELETE" })
        .then(() => {
            getStatus()
                .then(data => {
                    loadMessages(data.userId,data.username);
                })
        })
        .catch(err => {
            console.error(`Unable to delete message: ${err.status}`);
            console.error(err);
        });
}
