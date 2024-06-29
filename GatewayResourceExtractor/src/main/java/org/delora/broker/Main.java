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


        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(costants.HOST);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            //Declare the exchange
            channel.exchangeDeclare(costants.DELORAN_MONITORING_SYSTEM_EXCHANGE,"topic");



//            channel.queueDeclare(costants.SYS_RESOURCES_QUEUE, false, false, false, null);
                String packetsInfo = Mocker.genericPacketList();

            //Read system info and produce a message to send to the broker
            while (true) {
                String systemInfo = getSystemInfo();


//                channel.basicPublish("", costants.SYS_RESOURCES_QUEUE, null, systemInfo.getBytes());

                //Public sys resources to basic exchange with routing key for resources queu
                channel.basicPublish(costants.DELORAN_MONITORING_SYSTEM_EXCHANGE, costants.RESOURCES_RK, null, systemInfo.getBytes());

                channel.basicPublish(costants.DELORAN_MONITORING_SYSTEM_EXCHANGE, costants.PACKETS_RK, null, packetsInfo.getBytes());


                System.out.println(" [x] Sent '" + systemInfo + "'");
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