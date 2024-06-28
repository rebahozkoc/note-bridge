const form = document.querySelector("#register-form");
const registerAsUserBtn = document.querySelector("#user-btn");
const firstField = document.querySelector("#first-field");
const secondField = document.querySelector("#second-field");
const password = document.querySelector("#password");
const repeatPassword = document.querySelector("#repeat-password");
const warningMessage = document.querySelector("#warning-message");
const myInput = document.getElementById("password");
const letter = document.getElementById("letter");
const capital = document.getElementById("capital");
const number = document.getElementById("number");
const length = document.getElementById("length");

/**
 * This function is called when the form for signing up is submitted. There are checks: the 'password' and 'repeat-password' fields
 * must contain the same content. Also, the user must enter minimum one special character in the password field.
 */
form.addEventListener("submit", function (event) {
    event.preventDefault();

    if (password.value !== repeatPassword.value) {
        warningMessage.innerHTML = "Passwords do not match!";
        return;
    }

    if (!password.value.includes("!") &&
        !password.value.includes("@") &&
        !password.value.includes("#") &&
        !password.value.includes("$") &&
        !password.value.includes("%") &&
        !password.value.includes("^") &&
        !password.value.includes("&") &&
        !password.value.includes("*") &&
        !password.value.includes("(") &&
        !password.value.includes(")") &&
        !password.value.includes("-") &&
        !password.value.includes("_") &&
        !password.value.includes("+") &&
        !password.value.includes("=") &&
        !password.value.includes(".")) {
        warningMessage.innerHTML = "The password must include at least one character from: !@#$%^&*()-_+=.";
        return;
    }

    const data = new FormData(form);
    let dataObject = {};
    let uriToSendRequest;

    for (const [key, value] of data.entries()) {
        dataObject[key] = value;
    }

    delete dataObject.btnradio;

    if (registerAsUserBtn.checked) {
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
        if (res.status === 200) {
            window.location.replace("login.html");
            alert('Welcome! You have created an account!');
        } else {
            res.text().then(data => {
                warningMessage.innerHTML = data;
            });
        }
    })
        .catch(err => {
            console.error("Register Error", err)
        })
})

/**
 * Display the form for signing in a user. The fields 'firstname' and 'lastname' are added.
 */
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

/**
 * Display the form for signing in a sponsor. The fields 'company name' and 'website URL' are added.
 */
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

/**
 * Update the animations which indicate if the password entered by the user meets all requirements.
 */
myInput.onkeyup = function() {
    let specialCharacters = "!@#$%^&*()-_+=.";
    let hasSpecialCharacter = false;

    for(let i= 0; i<specialCharacters.length; i++) {
        if(myInput.value.includes(specialCharacters.charAt(i))) {
            letter.classList.remove("invalid");
            letter.classList.add("valid");
            hasSpecialCharacter = true;
            break;
        }
    }
    if(!hasSpecialCharacter) {
        letter.classList.remove("valid");
        letter.classList.add("invalid");
    }

    let upperCaseLetters = /[A-Z]/g;
    if(myInput.value.match(upperCaseLetters)) {
        capital.classList.remove("invalid");
        capital.classList.add("valid");
    } else {
        capital.classList.remove("valid");
        capital.classList.add("invalid");
    }

    let numbers = /(\D*\d){2,}/g;
    if(myInput.value.match(numbers)) {
        number.classList.remove("invalid");
        number.classList.add("valid");
    } else {
        number.classList.remove("valid");
        number.classList.add("invalid");
    }

    if(myInput.value.length >= 8) {
        length.classList.remove("invalid");
        length.classList.add("valid");
    } else {
        length.classList.remove("valid");
        length.classList.add("invalid");
    }
}
