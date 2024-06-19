const form = document.querySelector("#register-form");
const registerAsUserBtn = document.querySelector("#user-btn");
const firstField = document.querySelector("#first-field");
const secondField = document.querySelector("#second-field");
const password = document.querySelector("#password");
const repeatPassword = document.querySelector("#repeat-password");
const warningMessage = document.querySelector("#warning-message");

form.addEventListener("submit", function(event) {
    event.preventDefault();

    if (password.value !== repeatPassword.value) {
        warningMessage.innerHTML = "Passwords do not match!";
        return;
    }

    const data = new FormData(form);
    let dataObject = {};
    let uriToSendRequest;

    for (const [key, value] of data.entries()) {
        dataObject[key] = value;
    }

    delete dataObject.btnradio;

    if(registerAsUserBtn.checked) {
        uriToSendRequest = "/notebridge/api/persons";
    } else {
        uriToSendRequest = "/notebridge/api/sponsors";
    }

    fetch(uriToSendRequest, {
        method: "POST",
        body: JSON.stringify(dataObject),
        headers: {
            "Content-type": "application/json"
        }
    }).then(res => {
        if(res.status === 200) {
            window.location.replace("login.html");
            alert('Welcome! You have created an account!');
        } else {
            res.text().then(data => {
                warningMessage.innerHTML = data;
            });
        }})
        .catch(err => {
            console.error("Register Error", err)
        })
})

function showRegisterUserForm() {
    firstField.innerHTML = `
    <div class="form-floating mb-3">
        <textarea class="form-control" placeholder="Firstname" id="firstname" name="name"></textarea>
        <label for="firstname">First name</label>
    </div>
    `;

    secondField.innerHTML = `
    <div class="form-floating mb-3">
        <textarea class="form-control" placeholder="Lastname" id="lastname" name="lastname"></textarea>
        <label for="lastname">Lastname</label>
    </div>
    `;
}

function showRegisterSponsorForm() {
    firstField.innerHTML = `
    <div class="form-floating mb-3">
        <textarea class="form-control" placeholder="Company name" id="companyName" name="companyName"></textarea>
        <label for="companyName">Company name</label>
    </div>
    `;

    secondField.innerHTML = `
    <div class="form-floating mb-3">
        <textarea class="form-control" placeholder="Website URL" id="websiteURL" name="websiteURL"></textarea>
        <label for="websiteURL">Website URL</label>
    </div>
    `;
}
