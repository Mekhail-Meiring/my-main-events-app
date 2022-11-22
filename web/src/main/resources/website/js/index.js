function loginSection(){

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
                    name: loginFormData.get("name"),
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
                updateDom("app", "people-section", data)

                createEvent();
                sendCreatedEvent();
            })


    })
}


function getEvents(element) {

    let fromPersonEmail = element.value;
    let personLoggedIn = window.sessionStorage.getItem("personLoggedIn")


    fetch(`/events/${fromPersonEmail}`)
        .then((response) => response.json())
        .then((data) =>{
            if (data.length === 0){
                document.getElementById("message-body").innerHTML=
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

                updateDom("message-body", "chat-message-section", data);
            }
        })




}

function createEvent(){

    let date = new Date();

    let email = sessionStorage.getItem("personLoggedIn");
    console.log(email)

    let data = {
        fromPersonEmail: email
    }

    updateDom("send-message", "send-message-section", data);

}


function sendCreatedEvent(){
    document.getElementById("message-form").addEventListener("submit", (event) =>{
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

        fetch("/event", options).then(createEvent());

    })
}

function updateDom(elementToUpdate, templated, data){
    let template = document.getElementById(templated).innerText;
    let compiledFunction = Handlebars.compile(template);
    document.getElementById(elementToUpdate).innerHTML = compiledFunction(data);
}

window.addEventListener("load", ()=>{
    loginSection();
})