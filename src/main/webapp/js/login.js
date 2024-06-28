const form = document.querySelector("#login-form");

/**
 * When the form used for logging in is submitted by the user, this function is called.
 */
form.addEventListener("submit", function(event) {
    event.preventDefault();

    fetch("/notebridge/api/auth/login", {
        method: "POST",
        body: new URLSearchParams({
            'email': document.getElementById("email").value,
            'password': document.getElementById("password").value
        }).toString(),
        headers: {
            "Content-type": 'application/x-www-form-urlencoded'}
        }).then(res => {
        if(res.status === 200) {
            window.location.replace("home.html")
        } else {
            res.text().then(data => {
                const warningMessage = document.querySelector("#warning-message");
                warningMessage.innerHTML = data;
            });
        }})
        .catch(err => {
        console.error("Log In Error", err)
    })
});
