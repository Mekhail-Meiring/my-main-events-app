import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import za.co.simpleChat.models.Client;
import za.co.simpleChat.models.Event;
import org.junit.jupiter.api.*;
import za.co.simpleChat.models.testmodels.Login;
import za.co.simpleChat.models.testmodels.SampleEvent;
import za.co.simpleChat.ClientService;
import za.co.simpleChat.WebServer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WebServiceApiTest {

    public static final int TEST_PORT = 9000;

    private static WebServer webServer;

    private static ClientService clientServer;


    @BeforeAll
    public static void startServer(){
        webServer = new WebServer().initialise();
        clientServer = new ClientService().initialise();
        webServer.startServer( TEST_PORT );
        clientServer.startServer();

    }

    @AfterAll
    public static void stopServer(){
        webServer.stopServer();
        clientServer.stopServer();
    }

    @Test
    @Order(1)
    public void canLoginToClientServer(){
        HttpResponse<JsonNode> post = Unirest.post( webServerUrl() + "/login" )
                .body(new Login("Mekhail", "123mekhail@gmail.com")).asJson();

        assertEquals( HttpStatus.OK, post.getStatus() );
        assertEquals(1, post.getBody().getArray().length());
    }

    @Test
    @Order(2)
    public void canSendMessagesToClientServer(){
        HttpResponse<JsonNode> post2 = Unirest.post( webServerUrl() + "/login" )
                .body(new Login("Hogan", "123hogan@gmail.com")).asJson();

        assertEquals( HttpStatus.OK, post2.getStatus() );

        LocalDateTime localDate = LocalDateTime.now();

        String time = localDate.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String date = localDate.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        HttpResponse<JsonNode> post3 = Unirest.post( webServerUrl() + "/event" )
                .body(new SampleEvent(
                        "123mekhail@gmail.com"
                        , time, date, "Awe")
                ).asJson();

        assertEquals( HttpStatus.OK, post3.getStatus() );
    }


    @Test
    @Order(3)
    public void getMessageFromClientServer(){
        HttpResponse<JsonNode> response = Unirest.get( webServerUrl()+"/events/123mekhail@gmail.com")
                .asJson();
        assertEquals(HttpStatus.OK, response.getStatus());


        response.getBody().getArray().forEach(
                message -> {
                    Event m = new Gson().fromJson(message.toString(), Event.class);
                    assertEquals("123mekhail@gmail.com", m.fromPersonEmail);
                    assertEquals("Awe", m.description);
                    assertNotNull(m.date);
                    assertNotNull(m.time);
                }
        );
    }

    @Test
    @Order(4)
    public void canPeopleFromClientServer(){
        HttpResponse<JsonNode> response = Unirest.get( webServerUrl() + "/people").asJson();
        assertEquals(HttpStatus.OK, response.getStatus());

        List<String> listOfPeopleEmail = List.of("123mekhail@gmail.com", "123hogan@gmail.com");
        List<String> listOfPeopleNames = List.of("Mekhail", "Hogan");

        response.getBody().getArray().forEach(
                clientInformation->{
                    Client client = new Gson().fromJson(clientInformation.toString(), Client.class);
                    assertTrue(listOfPeopleEmail.contains(client.getEmail()));
                    assertTrue(listOfPeopleNames.contains(client.getName()));
                }
        );
    }


    private String webServerUrl(){
        return "http://localhost:" + TEST_PORT;
    }
}
