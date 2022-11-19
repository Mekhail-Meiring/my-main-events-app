package za.co.simpleChat;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class WebServer {

    public static void main(String[] args) {
        WebServer webServer = new WebServer().initialise();
        webServer.startServer();
    }

    private Javalin server;
    private final int DEFAULT_PORT = 8080;
    private static final String PAGES_DIR = "/website";

    private WebServer initialise(){
        server = configureHttpServer();
        return this;
    }

    private Javalin configureHttpServer() {
        return Javalin.create(config -> config.addStaticFiles(PAGES_DIR, Location.CLASSPATH))
                .get("/hello", context -> {
                    System.out.println("Hello World");
                });
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
}
