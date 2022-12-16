import RouterSingleton from "./RouterSingleton.js";


/**
 * Retrieves a list of events from the server and updates the DOM to display them.
 * If there are no events, a message indicating this is displayed.
 */
export function getEvents() {

    const singleton = new RouterSingleton();
    const router = singleton.router;

    let personLoggedIn = window.sessionStorage.getItem("personLoggedIn")

    fetch(`/events`)
        .then((response) => response.json())
        .then((data) =>{
            if (data.length === 0){
                document.getElementById("main").innerHTML=
                    `<h3>No events yet..</h3>`
            }

            else{
                data = {
                    messageHistory: data
                }

                for (let i = 0; i < data.messageHistory.length; i++) {
                    if (data.messageHistory[i].fromPersonEmail === personLoggedIn){
                        data.messageHistory[i].fromPersonEmail = "(ME)";
                    }
                }

                singleton.updateDom("main", "main-events-section", data);
            }
        })
}


/**
 * Updates the DOM to display a form for creating a new event.
 * When the form is submitted, a POST request is sent to the server to create the event.
 */
export function createEvent(){

    const singleton = new RouterSingleton();
    const router = singleton.router;

    let email = sessionStorage.getItem("personLoggedIn");
    console.log(email)

    let data = {
        fromPersonEmail: email
    }

    singleton.updateDom("main", "create-event-section", data);

    document.getElementById("create-event-form").addEventListener("submit", (event) =>{
        event.preventDefault();

        let formData = new FormData(event.target);

        let options = {
            method: "POST",
            body: JSON.stringify(
                {
                    fromPersonEmail: formData.get("fromPersonEmail"),
                    date: formData.get("date"),
                    time: formData.get("time"),
                    description: formData.get("description")
                }
            )
        }

        fetch("/event", options).then(router.navigateTo("/events"));

    })
}
