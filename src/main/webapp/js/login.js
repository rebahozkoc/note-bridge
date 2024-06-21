const form = document.querySelector("#login-form");

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
            //alert("Login successful!");
            window.location.replace("home.html")
        } else {
            res.text().then(data => {
                alert("Login did not work!");
                const warningMessage = document.querySelector("#warning-message");
                warningMessage.innerHTML = data;
            });
        }})
        .catch(err => {
        console.error("Log In Error", err)
    })
});
