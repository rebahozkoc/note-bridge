const feedbackForm = document.getElementById("feedback-form");

feedbackForm.addEventListener("submit", function(e) {
    e.preventDefault();

    const formData = new FormData(feedbackForm);
    let dataObject = {};

    // iterate through the form data and add it to the dataObject
    formData.forEach((value,key) => {
        dataObject[key] = value;
    })

    fetch("/notebridge/api/feedback/send", {
        method: "POST",
        body: JSON.stringify(dataObject),
        headers: {
            "Content-type": "application/json"
        }
        // if successful, redirect to the home page if there is an error show an alert
    }).then(r => {
        if (r.ok){
            alert("Feedback sent successfully");
        }else {
            alert("Error sending feedback");
        }
    }).catch(e => {
        alert("Error sending feedback");
        console.error(e);
    });
})

