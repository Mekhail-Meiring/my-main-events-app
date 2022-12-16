import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import org.junit.jupiter.api.*;
import za.co.simpleChat.models.Event;
import za.co.simpleChat.models.testmodels.Login;
import za.co.simpleChat.models.testmodels.SampleEvent;
import za.co.mainEvent.ClientService;
import za.co.simpleChat.models.Client;


import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientServiceApiTest {

    public static final int TEST_PORT = 9999;

    private static ClientService clientService;

    @BeforeAll
    public static void startServer(){
        clientService = new ClientService().initialise();
        clientService.startServer( TEST_PORT );
    }

    @AfterAll
    public static void stopServer(){
        clientService.stopServer();
    }

    @Test
    @Order(1)
    @DisplayName("Check to see if you can log into the database.")
    public void successfulLogin(){

        HttpResponse<JsonNode> post = Unirest.post( serverUrl() + "/login" )
                .body(new Login("Mekhail", "123mekhail@gmail.com")).asJson();

        assertEquals( HttpStatus.OK, post.getStatus() );
        assertEquals(1, clientService.amountOfClients());
    }

    @Test
    @Order(2)
    @DisplayName("Check to see if you can send a message.")
    public void canSendAMessageToAnotherClient(){
        HttpResponse<JsonNode> post2 = Unirest.post( serverUrl() + "/login" )
                .body(new Login("Hogan", "123hogan@gmail.com")).asJson();

        assertEquals( HttpStatus.OK, post2.getStatus() );
        assertEquals(2, clientService.amountOfClients());

        LocalDateTime localDate = LocalDateTime.now();

        String time = localDate.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String date = localDate.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        HttpResponse<JsonNode> post3 = Unirest.post( serverUrl() + "/event" )
                .body(new SampleEvent(
                        "123mekhail@gmail.com", time, date, "Awe")
                ).asJson();

        assertEquals( HttpStatus.OK, post3.getStatus() );
        assertEquals( clientService.sizeOfEventsDatabase(), 1);
    }


    @Test
    @Order(3)
    @DisplayName("Check to see if you can get receive a message")
    public void canSeeAMessageFromAnotherPerson(){
        assertEquals(2, clientService.amountOfClients());
        assertEquals( clientService.sizeOfEventsDatabase(), 1);

        HttpResponse<JsonNode> response = Unirest.get( serverUrl() + "/events/123mekhail@gmail.com").asJson();

        assertEquals(HttpStatus.OK, response.getStatus());

        List<SampleEvent> sampleEventHistory = new ArrayList<>();

        JSONArray jsonArray = response.getBody().getArray();

        jsonArray.forEach(
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
    @DisplayName("Check for people in the database.")
    public void peopleInDatabase(){
        HttpResponse<JsonNode> response = Unirest.get( serverUrl() + "/people").asJson();

        assertEquals(HttpStatus.OK, response.getStatus());

        List<String> listOfPeopleEmail = List.of("123mekhail@gmail.com", "123hogan@gmail.com");
        List<String> listOfPeopleNames = List.of("Mekhail", "Hogan");

        response.getBody().getArray().forEach(
                clientInformation->{
                    Client client = new Gson().fromJson(clientInformation.toString(), Client.class);
                    assertTrue(listOfPeopleEmail.contains(client.getEmail()));
                }
        );

    }


    private String serverUrl(){
        return "http://localhost:" + TEST_PORT;
    }
}
