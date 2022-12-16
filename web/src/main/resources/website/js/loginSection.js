import RouterSingleton from "./RouterSingleton.js";

/**
 * Updates the DOM to display the login form and adds an event listener to the form.
 * When the form is submitted, a POST request is sent to the server to log in the user.
 * If the login is successful, the user's information is stored in session storage and the app navigates to the events page.
 */
export function loginSection(){

    let singleton = new RouterSingleton();
    let router = singleton.router;

    document.getElementById("app").innerHTML = document.getElementById("login-section").innerText;

    document.getElementById("login-form").addEventListener("submit", (event) =>{
        event.preventDefault();

        const loginFormData = new FormData(event.target);

        let email = loginFormData.get("email");

        window.sessionStorage.setItem("personLoggedIn", email);

        const options = {
            method: "POST",
            body: JSON.stringify(
                {
                    email: email
                }
            ),
            headers: {
                "Content-type": "application/json; charset=UTF-8"
            }
        }

        fetch(`/login`, options).then((response) => response.json())
            .then((data) => {

                console.log(data);
                data = {
                    person: data
                }

                singleton.updateDom("app", "people-section", data);
                router.navigateTo("/events");

            })


    })
}