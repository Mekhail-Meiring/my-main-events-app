package za.co.simpleChat;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import za.co.simpleChat.models.Client;
import za.co.simpleChat.models.Event;

import javax.jms.Message;
import javax.jms.MessageListener;


/**
 * <h3>Responsible for providing data to the front-end of Simple-Chat.</h3>
 * <p>This class communicates with other services acting like the middle man for the client.</p>
 */
public class WebServer implements MessageListener {

    public static void main(String[] args) {
        WebServer webServer = new WebServer().initialise();
        webServer.startServer();
    }

    private Javalin server;
    private final int DEFAULT_PORT = 8000;


    private static final String PAGES_DIR = "/website";

    public WebServer initialise(){
        server = configureHttpServer();
        return this;
    }

    private Javalin configureHttpServer() {
        return Javalin.create(config -> config.addStaticFiles(PAGES_DIR, Location.CLASSPATH))
                .post("/login", this::login)
                .get("/people", this::getPeople)
                .post("/event", this::createEvent)
                .get("/events/{fromPersonEmail}", this::getEvents);
    }


    /**
     * Functionality of the "/events/{fromPersonEmail}" API end-point.
     * @param context Server context.
     */
    private void getEvents(Context context) {

        String fromPersonEmail = context.pathParam("fromPersonEmail");
        HttpResponse<JsonNode> response = Unirest.get( clientServiceUrl() + "/events/"+fromPersonEmail)
                .asJson();

        Event[] messageDTOS = new Gson().fromJson(response.getBody().toString(), Event[].class);
        context.json(messageDTOS);
    }


    /**
     * Functionality of the "/message" API end-point.
     * @param context Server context.
     */
    private void createEvent(Context context) {
        HttpResponse<JsonNode> post = Unirest.post( clientServiceUrl() + "/event")
                .body(context.bodyAsClass(Event.class)).asJson();

        System.out.println(post.toString());
    }


    /**
     * Functionality of the "/people" API end-point.
     * @param context Server context.
     */
    private void getPeople(Context context) {
        HttpResponse<JsonNode> response = Unirest.get( clientServiceUrl() + "/people").asJson();
        context.result(response.getBody().toString());
    }


    /**
     * Functionality of the "/login" api end point.
     * @param context Server context.
     */
    private void login(Context context) {

        HttpResponse<JsonNode> post = Unirest.post( clientServiceUrl() + "/login" )
                .body(context.bodyAsClass(Client.class)).asJson();

        Client[] clients = new Gson().fromJson(post.getBody().toString(), Client[].class);
        context.json(clients);
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


    /**
     * URL of the Client Service.
     * @return String
     */
    private String clientServiceUrl(){
        return "http://localhost:8080";
    }

    @Override
    public void onMessage(Message message) {

    }
}
