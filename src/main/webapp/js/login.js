function showRegisterUserForm() {
    const mainContent = document.querySelector('#main-content');
    mainContent.innerHTML = `
    <form id="register-form" class="container main-content mt-5 w-50 rounded-5 p-5" onsubmit="return sendRequestCreateAccount()">
            <h1 class="fw-bold text-white py-2 fs-1">Sign up</h1>
            <div class="row">
                <div class="col-6" id="first-field">
                    <div class="form-floating mb-3">
                        <textarea class="form-control" placeholder="Firstname" id="firstname" name="name"></textarea>
                        <label for="firstname">First name</label>
                    </div>
                </div>
                <div class="col-6" id="second-field">
                    <div class="form-floating mb-3">
                        <textarea class="form-control" placeholder="Lastname" id="lastname" name="lastname"></textarea>
                        <label for="lastname">Lastname</label>
                    </div>
                </div>
                <div class="col-6">
                    <div class="mb-3">
                        <label for="profilePicture" class="form-label text-white">Profile picture</label>
                        <input class="form-control form-control-sm" type="file" id="profilePicture" name="picture">
                    </div>
                </div>
                <div class="col-6">
                    <div class="form-floating mb-3">
                        <textarea class="form-control" placeholder="phoneNumber" id="phoneNumber" name="phoneNumber"></textarea>
                        <label for="phoneNumber">Phone number</label>
                    </div>
                </div>
                <div class="col-12">
                    <div class="form-floating mb-3">
                        <textarea class="form-control" placeholder="Username" id="username" name="username"></textarea>
                        <label for="username">Username</label>
                    </div>
                </div>
                <div class="col-12">
                    <div class="form-floating mb-3">
                        <input type="email" class="form-control" id="floatingInput" placeholder="name@example.com" name="email">
                        <label for="floatingInput">Email address</label>
                    </div>     
                </div>
                <div class="col-6">
                    <div class="form-floating">
                        <input type="password" class="form-control" id="floatingPassword" placeholder="Password" name="password" aria-describedby="passwordHelpBlock">
                        <label for="floatingPassword">Password</label>
                    </div>
                </div>
                <div class="col-6">
                    <div class="form-floating">
                        <input type="password" class="form-control" id="floatingPassword2" placeholder="RepeatPassword">
                        <label for="floatingPassword">Repeat password</label>
                    </div>
                </div>
                <div id="passwordHelpBlock" class="form-text">
                    <p class="text-white fs-6">The password must be 8-20 characters long, contain letters and numbers, and must not contain spaces, special characters, or emoji.</p>
                </div>
            </div>
            <div class="d-flex justify-content-between">
                <button type="submit" class="btn btn-outline-light px-3 py-2 rounded-4">Create account</button>
                <div class="btn-group" role="group" aria-label="Basic radio toggle button group">
                    <input type="radio" class="btn-check" name="btnradio" id="normal-user-btn" autocomplete="off" checked>
                    <label class="btn btn-outline-light px-3 py-2 rounded-start-4" for="normal-user-btn" onclick="showRegisterUserForm()">Register as User</label>      
                    <input type="radio" class="btn-check" name="btnradio" id="sponsor-btn" autocomplete="off">
                    <label class="btn btn-outline-light px-2 py-2 rounded-end-4" for="sponsor-btn" onclick="showRegisterSponsorForm()">Register as Sponsor</label>
                </div>
            </div>
        </form>
    `;
}

function showRegisterSponsorForm() {
    const firstField = document.querySelector("#first-field");
    const secondField = document.querySelector("#second-field");

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

function sendRequestCreateAccount() {
    const form = document.getElementById("register-form");
    const normalUserBtn = document.querySelector("#normal-user-btn");
    const data = new FormData(form);
    let dataObject = {};
    let uriToSendRequest;

    for (const [key, value] of data.entries()) {
        dataObject[key] = value;
    }

    delete dataObject.btnradio;

    if(normalUserBtn.checked) {
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
    })
    location.reload();
    return false;
}
