package za.co.simpleChat.models;

import java.util.HashMap;

public class Message {

    private String fromPersonEmail;
    private String toPersonEmail;
    private String date;
    private String time;
    private String messageBody;


    public String getFromPersonEmail() {
        return fromPersonEmail;
    }

    public String getToPersonEmail() {
        return toPersonEmail;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
