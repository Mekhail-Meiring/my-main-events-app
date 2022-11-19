package za.co.simpleChat.api;

import za.co.simpleChat.models.Client;

import java.util.ArrayList;
import java.util.List;

public class Clients {

    private List<Client> listOfClients = new ArrayList<>();

    public void addClient(Client client){
        this.listOfClients.add(client);
    }
}
