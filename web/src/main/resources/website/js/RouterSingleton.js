import {loginSection} from "./loginSection.js";
import {homeSection} from "./eventsNavBar.js";
import {getEvents, createEvent} from "./events.js";

let instance = null

/**
 * A singleton class for managing the application's routing.
 * The class sets up the router and defines the routes for the app.
 */
export default class RouterSingleton {

    constructor() {
        if (instance) {return instance;}

        instance = this;

        window.addEventListener('load', () => {
            this.app = $("#app");
            const loginTemplate = Handlebars.compile($('#login-section').html());
            const defaultTemplate = Handlebars.compile($('#default-template').html());

            this.router = new Router({
                mode:'hash',
                root:'',
                page404: (path) => {
                    console.log(path + " not found")
                    const html = defaultTemplate();
                    this.app.html(html);
                }
            });

            this.router.add('/login', async () => {
                console.log("login section");
                this.app.html(loginTemplate());
                loginSection();
            });

            this.router.add('/events', async () =>{
                this.app.html(homeSection());
                getEvents();
            });

            this.router.add('/newEvent', async () => {
                this.app.html(homeSection());
                createEvent();
            });

            this.router.add('/logout', async () => {
                sessionStorage.removeItem("personLoggedInName");
                sessionStorage.removeItem("personLoggedIn");
                this.router.navigateTo("/login")
            });

            this.router.addUriListener();

            if (!sessionStorage.getItem("userID") || !sessionStorage.getItem("userEmail")) {
                this.router.navigateTo("/login")
            }

            else {
                const currentPath = window.location.hash
                const href = currentPath.substring(currentPath.lastIndexOf('/')+1);
                window.location.hash === "" ? this.router.navigateTo("/events") : this.router.navigateTo(href);
            }
        })
    }


    /**
     * Updates the DOM by compiling the specified template with the given data and inserting the resulting HTML into the
     * specified element.
     * @param {string} elementToUpdate - The ID of the element to update.
     * @param {string} templated - The ID of the template to use.
     * @param {object} data - The data to use in the template.
     */
    updateDom(elementToUpdate, templated, data){
        let template = document.getElementById(templated).innerText;
        let compiledFunction = Handlebars.compile(template);
        document.getElementById(elementToUpdate).innerHTML = compiledFunction(data);
    }
}