package org.delora.broker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.delora.broker.mockup.Mocker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class Main {


    private static final Costants costants = new Costants();

    public static void main(String[] args) {
        //Connection to rabbitmq
        try {
            List<String> strings = Mocker.genericPacketList();
            for (String string : strings) {
                System.out.println(string);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(costants.HOST);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(costants.SYS_RESOURCES_QUEUE, false, false, false, null);

            //Read system info and produce a message to send to the broker
            while (true) {
                String message = getSystemInfo();
                channel.basicPublish("", costants.SYS_RESOURCES_QUEUE, null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'" + "to queue: " + costants.SYS_RESOURCES_QUEUE);
                Thread.sleep(costants.READING_INTERVAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }




    }


    //Read system info launching .c program and retrieving info from stdout
    private static String getSystemInfo() {

        try {

            URL resource = Main.class.getClassLoader().getResource("scripts/extract_resource");

//            System.out.println(resource.getPath());
            Process process = new ProcessBuilder(resource.getPath()).start();
            System.out.println("Process executing? :" + String.valueOf(process.isAlive()) + "\n");

            // Read output from the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder sysInfo = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sysInfo.append(line);
            }

            // Wait for the process to finish
            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);
            return sysInfo.toString();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO throw exception
            return "Error";
        }


    }


}