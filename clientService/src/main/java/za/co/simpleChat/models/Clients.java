package za.co.simpleChat.models;

import za.co.simpleChat.models.Client;

import java.util.ArrayList;
import java.util.List;

public class Clients {

    private final List<Client> listOfClients = new ArrayList<>();

    public void addClient(Client client){
        this.listOfClients.add(client);
    }

    public List<Client> getListOfClients() {
        return listOfClients;
    }
}
