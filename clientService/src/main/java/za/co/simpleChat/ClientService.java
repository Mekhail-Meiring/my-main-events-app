package za.co.simpleChat;

import io.javalin.Javalin;
import io.javalin.http.Context;
import kong.unirest.json.JSONObject;
import org.eclipse.jetty.util.ajax.JSONObjectConvertor;
import za.co.simpleChat.models.Clients;
import za.co.simpleChat.models.Client;
import za.co.simpleChat.models.Message;

import java.util.ArrayList;
import java.util.List;

public class ClientService {

    public static void main(String[] args) {
        ClientService clientService = new ClientService().initialise();
        clientService.startServer();
    }

    private Javalin server;
    private final int DEFAULT_PORT = 8080;

    private Clients clients;

    private final List<Message> messageDataBase = new ArrayList<>();

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
                .get("/messages/{fromPersonEmail}/{toPersonEmail}", this::getMessages)
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

    public int amountOfClients(){
        return clients.getListOfClients().size();
    }

    public int sizeOfMessageDataBase(){
        return messageDataBase.size();
    }


    private void login(Context context){
        clients.addClient(context.bodyAsClass(Client.class));
        context.result("Success");
    }

    private void getPeople(Context context) {
        context.json(clients);
    }

    private void sendMessage(Context context) {
        Message message = context.bodyAsClass(Message.class);
        messageDataBase.add(message);
        context.result("message Sent");
    }

    private void getMessages(Context context) {
        List<Message> messagesHistory = new ArrayList<>();

        String fromPersonEmail = context.pathParam("fromPersonEmail");
        String toPersonEmail = context.pathParam("toPersonEmail");

        messageDataBase.forEach(message -> {
            if (message.getFromPersonEmail().equalsIgnoreCase(fromPersonEmail)
            && message.getToPersonEmail().equalsIgnoreCase(toPersonEmail)){
                messagesHistory.add(message);
            }
        });

        context.json(messagesHistory);
    }

}
