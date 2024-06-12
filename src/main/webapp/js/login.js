const mainContent = document.querySelector('#main-content');

function showRegisterForm() {
    mainContent.innerHTML = `
    <form class="container main-content w-50 rounded-5 p-5" onsubmit="">
            <h1 class="fw-bold text-white py-2 fs-1">Sign up</h1>
            <div class="row">
                <div class="col-6">
                    <div class="form-floating mb-3">
                        <textarea class="form-control" placeholder="Lastname" id="lastname" name="lastname"></textarea>
                        <label for="lastname">Lastname</label>
                    </div>
                </div>
                <div class="col-6">
                    <div class="form-floating mb-3">
                        <textarea class="form-control" placeholder="Firstname" id="firstname" name="firstname"></textarea>
                        <label for="firstname">First name</label>
                    </div>
                </div>
                <div class="col-6">
                    <div class="mb-3">
                        <label for="profilePicture" class="form-label text-white">Profile picture</label>
                        <input class="form-control form-control-sm" type="file" id="profilePicture">
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
                        <input type="email" class="form-control" id="floatingInput" placeholder="name@example.com">
                        <label for="floatingInput">Email address</label>
                    </div>     
                </div>
                <div class="col-6">
                    <div class="form-floating">
                        <input type="password" class="form-control" id="floatingPassword" placeholder="Password" aria-describedby="passwordHelpBlock">
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
                    <p class="text-white fs-6">Must be 8-20 characters long, contain letters and numbers, and must not contain spaces, special characters, or emoji.</p>
                </div>
            </div>
            <button type="submit" class="btn btn-outline-light px-3 py-2 rounded-4">Create account</button>
        </form>
    `;
}