package za.co.mainEvent;

import io.javalin.Javalin;
import io.javalin.http.Context;
import za.co.simpleChat.models.Client;
import za.co.simpleChat.models.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * <h3>Responsible for managing the clients of Simple-Chat.</h3>
 * <p>This class does all the thinking for managing clients. Then it can provide data about these clients to other services</p>
 */
public class ClientService {

    public static void main(String[] args) {
        ClientService clientService = new ClientService().initialise();
        clientService.startServer();
    }

    private Javalin server;
    private final int DEFAULT_PORT = 8080;

    private List<Client> clients;

    private final List<Event> eventsDatabase = new ArrayList<>();


    public ClientService initialise(){
        server = configureHttpServer();
        clients = new ArrayList<>();
        return this;
    }


    private Javalin configureHttpServer() {
        return Javalin.create()
                .post("/login", this::login)
                .get("/people", this::getPeople)
                .post("/event", this::createEvent)
                .get("/events", this::getEvents);
    }

    public void startServer(){
        this.startServer(DEFAULT_PORT);
    }

    public void startServer(int port){
        this.server.start(port);
    }

    public void stopServer(){
        this.server.stop();
    }

    public int amountOfClients(){
        return clients.size();
    }

    public int sizeOfEventsDatabase(){
        return eventsDatabase.size();
    }

    private boolean isClientInDatabase(Client client){

        AtomicBoolean clientExists = new AtomicBoolean(false);

        clients.forEach(c -> {
            if (c.getEmail().equalsIgnoreCase(client.getEmail())){
                clientExists.set(true);
            }
        });

        return clientExists.get();
    }


    /**
     * Functionality of the "/login" api end point.
     * @param context Server context.
     */
    private void login(Context context){

        Client client = context.bodyAsClass(Client.class);
        if (!isClientInDatabase(client)) {
            clients.add(client);
        }
        context.json(clients);
    }


    /**
     * Functionality of the "/people" API end-point.
     * @param context Server context.
     */
    private void getPeople(Context context) {
        context.json(clients);
    }


    /**
     * Functionality of the "/event" API end-point.
     * @param context Server context.
     */
    private void createEvent(Context context) {
        Event event = context.bodyAsClass(Event.class);
        eventsDatabase.add(event);
        context.result("Event created");
    }


    /**
     * Functionality of the "/messages/{fromPersonEmail}/{toPersonEmail}" API end-point.
     * @param context Server context.
     */
    private void getEvents(Context context) {
        context.json(eventsDatabase);
    }

}
