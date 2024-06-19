const music = document.getElementById("music");
const logInBtn = document.getElementById("login-btn");
const messengerBtn = document.getElementById("messenger-btn");
const profileBtn = document.getElementById("profile-btn");

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

function addSoundTag(sound) {
    return `<source src=` + sound + ` type="audio/mpeg">`;
}

function logOut() {
    fetch("/notebridge/api/auth/logout");
}

function checkLoggedIn() {
    return fetch("/notebridge/api/auth/status", {
        method: "GET"
    })
        .then(res => {
            if (res.status === 200) {
                updateNavbar(true);
                return res.json();
            } else {
                updateNavbar(false);
                return res.text();
            }
        });
}

function updateNavbar(loggedIn) {
    if(loggedIn) {
        logInBtn.innerHTML = `
        <a href="home.html" class="button-cover ms-2" role="button" onclick="logOut()"><span class="text px-2">Log out</span><span>Log out</span></a>
        `
        messengerBtn.style.display = "block";
        profileBtn.style.display = "block";
    } else {
        logInBtn.innerHTML = `
        <a href="login.html" class="button-cover ms-2" role="button"><span class="text px-2">Log in</span><span>Log in</span></a>
        `
        messengerBtn.style.display = "none";
        profileBtn.style.display = "none";
    }
}
