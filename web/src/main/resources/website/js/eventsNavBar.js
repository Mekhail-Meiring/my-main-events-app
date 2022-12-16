
/**
 * Returns a string containing the HTML for the home section of the app.
 * The home section includes a greeting to the logged-in user and a navigation menu.
 */
export function homeSection() {

    const person = window.sessionStorage.getItem("personLoggedIn");

    return`
        <h1>Main-Events</h1>
        <h3>Hello ${person}.</h3>
        
        <nav id="nav">
            <ul>
                <li><a href="#/events">All events</a></li>
                <li><a href="#/newEvent">Create new event</a></li>
                <li><a href="#/logout">Log out</a></li>
            </ul>
        </nav>
        
        <section id="main"></section>
        `;

}