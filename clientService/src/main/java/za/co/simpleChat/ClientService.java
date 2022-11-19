package za.co.simpleChat;

import io.javalin.Javalin;
import io.javalin.http.Context;
import za.co.simpleChat.api.ApiHandler;
import za.co.simpleChat.api.Clients;
import za.co.simpleChat.models.Client;

public class ClientService {

    public static void main(String[] args) {
        ClientService clientService = new ClientService().initialise();
        clientService.startServer();
    }

    private Javalin server;
    private final int DEFAULT_PORT = 8080;

    private Clients clients;


    public ClientService initialise(){
        server = configureHttpServer();
        clients = new Clients();
        return this;
    }
    private Javalin configureHttpServer() {
        return Javalin.create()
                .post("/login", this::login)
                .get("/people", this::getPeople)
                .post("/message", this::sendMessage)
                .get("/messages/{email}", this::getMessages)
                ;
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


    private void login(Context context){
        clients.addClient(context.bodyAsClass(Client.class));
        context.result("done");
    }

    private void getPeople(Context context) {
        context.json(clients);
    }

    private void sendMessage(Context context) {

    }

    private void getMessages(Context context) {
    }

}
