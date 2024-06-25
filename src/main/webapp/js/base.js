const music = document.getElementById("music");
const welcomeMessage = document.getElementById("welcome-message");
const logInBtn = document.getElementById("login-btn");
const messengerBtn = document.getElementById("messenger-btn");
const profileBtn = document.getElementById("profile-btn");
const myPostsBtn = document.getElementById("myposts-btn");

myPostsBtn.addEventListener("click", () => {
   getStatus().then(data => {

       window.location.href = 'cards.html?personId=' + data.userId;

   }).catch(err => {
       console.error("Error while navigating to my posts page: ", err);
   });
});


function pressPianoBtn1() {
    music.innerHTML = addSoundTag("assets/sounds/sound-1.mp3");
    music.play();
}

function pressPianoBtn2() {
    music.innerHTML = addSoundTag("assets/sounds/sound-2.mp3");
    music.play();
}

function pressPianoBtn3() {
    music.innerHTML = addSoundTag("assets/sounds/sound-3.mp3");
    music.play();
}

function pressPianoBtn4() {
    music.innerHTML = addSoundTag("assets/sounds/sound-4.mp3");
    music.play();
}

function pressPianoBtn5() {
    music.innerHTML = addSoundTag("assets/sounds/sound-5.mp3");
    music.play();
}

function pressPianoBtn6() {
    music.innerHTML = addSoundTag("assets/sounds/sound-6.mp3");
    music.play();
}

function pressPianoBtn7() {
    music.innerHTML = addSoundTag("assets/sounds/sound-7.mp3");
    music.play();
}

function pressPianoBtn8() {
    music.innerHTML = addSoundTag("assets/sounds/sound-8.mp3");
    music.play();
}

function pressPianoBtn9() {
    music.innerHTML = addSoundTag("assets/sounds/sound-9.mp3");
    music.play();
}

function pressPianoBtn10() {
    music.innerHTML = addSoundTag("assets/sounds/sound-10.mp3");
    music.play();
}

function pressPianoBtn11() {
    music.innerHTML = addSoundTag("assets/sounds/sound-11.mp3");
    music.play();
}

function pressPianoBtn12() {
    music.innerHTML = addSoundTag("assets/sounds/sound-12.mp3");
    music.play();
}

function pressPianoBtn13() {
    music.innerHTML = addSoundTag("assets/sounds/sound-13.mp3");
    music.play();
}

function pressPianoBtn14() {
    music.innerHTML = addSoundTag("assets/sounds/sound-14.mp3");
    music.play();
}


function addSoundTag(sound) {
    return `<source src=` + sound + ` type="audio/mpeg">`;
}

function logOut() {
    fetch("/notebridge/api/auth/logout");
}

function checkLoggedIn() {
    fetch("/notebridge/api/auth/status", {
        method: "GET"
    })
        .then(res => {
            if (res.status === 200) {
                updateNavbar(true);
                return res.json().then(data => {
                    welcomeMessage.innerHTML = `
                    <h1 class="title text-white fst-italic fs-2 mb-5"><strong>Welcome, ${data.username}</strong>!<br><span class="fs-4">We are delighted to see you as a part of our community.<br>Feel free to discover, interact, and collaborate with other musicians!</span></h1>
                    `;
                });
            } else {
                updateNavbar(false);
                welcomeMessage.innerHTML = `
                <h1 class="title text-white fst-italic fs-1 mb-5">Welcome to <strong>Note-Bridge</strong>!<br> Capture the feeling of a vibrant music community.</h1>
                `;
            }
        })
}

function updateNavbar(loggedIn) {
    if(loggedIn) {
        logInBtn.innerHTML = `
        <a href="home.html" id="log-out-btn" class="button-cover ms-2" role="button" onclick="logOut()"><span class="text px-2">Log out</span><span>Log out</span></a>
        `

        messengerBtn.innerHTML = `
        <a href="Messenger.html" class="button-cover me-3" role="button"><span class="text px-5">Messenger</span><span>Messenger</span></a>
        `;

        profileBtn.innerHTML = `
        <a class="nav-link" href="profile.html"><img src="assets/images/user-icon.png" width="35px" height="35px"></a>
        `;

        myPostsBtn.innerHTML = `
        <a  class="button-cover me-3" role="button"><span class="text px-5">My Posts</span><span>My Posts</span></a>
        `;
    } else {
        logInBtn.innerHTML = `
        <a href="login.html" id="log-in-btn" class="button-cover ms-2" role="button"><span class="text px-2">Log in</span><span>Log in</span></a>
        `
        messengerBtn.innerHTML = ``;
        profileBtn.innerHTML = ``;
        myPostsBtn.innerHTML = ``;
    }
}
