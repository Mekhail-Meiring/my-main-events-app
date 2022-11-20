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
import testmodels.Login;
import testmodels.SampleMessage;
import za.co.simpleChat.ClientService;
import models.Client;
import models.Message;


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

        HttpResponse<JsonNode> post3 = Unirest.post( serverUrl() + "/message" )
                .body(new SampleMessage(
                        "123mekhail@gmail.com", "123hogan@gmail.com"
                        , time, date, "Awe")
                ).asJson();

        assertEquals( HttpStatus.OK, post3.getStatus() );
        assertEquals( clientService.sizeOfMessageDataBase(), 1);
    }


    @Test
    @Order(3)
    @DisplayName("Check to see if you can get receive a message")
    public void canSeeAMessageFromAnotherPerson(){
        assertEquals(2, clientService.amountOfClients());
        assertEquals( clientService.sizeOfMessageDataBase(), 1);

        HttpResponse<JsonNode> response = Unirest.get( serverUrl() + "/messages/123mekhail@gmail.com/123hogan@gmail.com").asJson();

        Gson gson = new Gson();

        List<SampleMessage> sampleMessageHistory = new ArrayList<>();

        response.getBody().getArray().forEach(
                message -> {
                    Message m = gson.fromJson(message.toString(), Message.class);
                    assertEquals("123mekhail@gmail.com", m.getFromPersonEmail());
                    assertEquals("123hogan@gmail.com", m.getToPersonEmail());
                    assertEquals("Awe", m.getMessageBody());
                    assertNotNull(m.getDate());
                    assertNotNull(m.getTime());
                }
        );
    }

    @Test
    @Order(4)
    @DisplayName("Check for people in the database.")
    public void peopleInDatabase(){
        HttpResponse<JsonNode> response = Unirest.get( serverUrl() + "/people").asJson();
        System.out.println(response.getBody());

        List<String> listOfPeopleEmail = List.of("123mekhail@gmail.com", "123hogan@gmail.com");
        List<String> listOfPeopleNames = List.of("Mekhail", "Hogan");

        JSONArray clientsJsonArray =(JSONArray) response.getBody().getObject().get("listOfClients");
        Gson gson = new Gson();
        clientsJsonArray.forEach(
                clientInformation->{
                    Client client = gson.fromJson(clientInformation.toString(), Client.class);
                    assertTrue(listOfPeopleEmail.contains(client.getEmail()));
                    assertTrue(listOfPeopleNames.contains(client.getName()));
                }
        );

    }


    private String serverUrl(){
        return "http://localhost:" + TEST_PORT;
    }
}
