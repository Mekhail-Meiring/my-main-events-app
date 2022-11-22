package za.co.simpleChat.spike;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.qpid.jms.JmsConnectionFactory;

import javax.jms.*;
import java.net.URI;
import java.net.URISyntaxException;

public class Listener {

    private final String TOPIC_PREFIX = "topic://";

    private String host = env("ACTIVEMQ_HOST", "localhost");
    private int port = Integer.parseInt(env("ACTIVEMQ_PORT", "5672"));
    private final String destinationName;
    private final Connection connection;

    private Destination destination;

    private Session session;


    public Listener(String[] args) throws Exception {
        this.destinationName = arg(args, 0, "stage");
        System.out.println(destinationName);

        BrokerService broker = BrokerFactory.createBroker(new URI(
                "broker:(tcp://localhost:61616)"));
        broker.start();
        // Producer
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                "tcp://localhost:61616");
        connection = connectionFactory.createConnection();
    }


    public void setUpConsumer(MessageListener messageListener) throws JMSException {

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        if (destinationName.startsWith(TOPIC_PREFIX)) {
            destination = session.createTopic(destinationName.substring(TOPIC_PREFIX.length()));
        } else {
            destination = session.createQueue(destinationName);
        }

        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(messageListener);
        connection.start();
    }


    public void sendMessagePayload(String messagePayload) throws JMSException {
        Message msg = session.createTextMessage(messagePayload);
        MessageProducer producer = session.createProducer(destination);
        System.out.println("Sending text '" + messagePayload + "'");
        producer.send(msg);
    }


    private static String env(String key, String defaultValue) {
        String rc = System.getenv(key);
        if (rc == null) {
            return defaultValue;
        }
        return rc;
    }

    private static String arg(String[] args, int index, String defaultValue) {
        if (index < args.length) {
            return args[index];
        }
        else {
            return defaultValue;
        }
    }


}
