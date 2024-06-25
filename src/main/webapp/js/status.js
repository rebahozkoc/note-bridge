function getStatus() {
    return fetch("/notebridge/api/auth/status", {
        method: "GET"
    })
        .then(res => {
            if (res.status === 200) {
                return res.json();
            } else {
                return res.text().then(errorText => {
                    throw new Error(`${errorText}`);
                });
            }
        });
}