package org.delora.broker.sender;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.delora.broker.Costants;

import java.text.MessageFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Sender {
    public static final int MESSAGE_SIZE = 500;
    private Costants costants = new Costants();

    public void send() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(costants.HOST);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(costants.QUEUE_NAME, false, false, false, null);

            String message = produceMessage();
            channel.basicPublish("",costants.QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String produceMessage() {

        String pattern = "'{'\n" +
                "    \"Id\" : {0},\n" +
                "    \"cpu_usage\" : \"{1}Ghz\",\n" +
                "    \"ram_usage\" : \"{2}Mbit\",\n" +
                "    \"net_usage\" : \"{3}\",\n" +
                "    \"timestamp\" : \"{4}\"\n" +
                "'}'";
        StringBuilder message = new StringBuilder("[ ");
        Random random = new Random();
        for (int i = 0; i <= MESSAGE_SIZE; i++) {

            String format = MessageFormat.format(pattern, i, random.nextInt(2000), random.nextInt(2000), random.nextInt(2000), LocalTime.now().toString());
            message.append(format);
            if(i < MESSAGE_SIZE ){
                message.append(" , ");
            }
        }
        message = message.append(" ] ");
        return message.toString();
    }
}
