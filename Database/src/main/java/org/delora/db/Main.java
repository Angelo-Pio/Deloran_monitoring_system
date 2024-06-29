package org.delora.db;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.result.InsertManyResult;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.bson.Document;
import org.delora.db.Model.Resource;
import org.delora.db.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.delora.db.Costants.SYSTEM_RESOURCES_INFO;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static Costants costants = new Costants();
    private static Mapper mapper = new Mapper();
    private static Channel resource_channel;
    private static Channel packets_channel;
    private static Connection connection;
    private static MongoCollection<Document> resources_collection;
    private static MongoCollection<Document> packets_collection;

    public static void main(String[] args) {
        Costants costants = new Costants();

        // MongoDB routine
        MongoClient mongoClient = MongoClients.create(costants.JDBC_URL);
        MongoDatabase db = mongoClient.getDatabase(costants.DB_NAME);
        resources_collection = db.getCollection(costants.resources_collection);
        packets_collection = db.getCollection(costants.packets_collection);

        System.out.println("connection enstablished with db");

        // Receive messages from rabbitmq
        try {
            receive(resources_collection, mongoClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Close resources
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                resource_channel.close();
                packets_channel.close();
                connection.close();
                mongoClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

    }

    public static void receive(MongoCollection<Document> collection, MongoClient mongoClient) throws Exception {


        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(costants.HOST);
        connection = factory.newConnection();
        resource_channel = connection.createChannel();
        packets_channel = connection.createChannel();
        System.out.println("connection enstablished with mqtt server");

        // Declare exchange
        resource_channel.exchangeDeclare("deloran_monitoring_system_exchange", "topic");
        packets_channel.exchangeDeclare("deloran_monitoring_system_exchange", "topic");

//        Declare and bind queue
        resource_channel.queueDeclare(costants.SYSTEM_RESOURCES_INFO, true, false, false, null);
        resource_channel.queueBind(costants.SYSTEM_RESOURCES_INFO, costants.DELORAN_MONITORING_SYSTEM_EXCHANGE, costants.RESOURCES_RK);

        packets_channel.queueDeclare(costants.PACKETS_RECEIVED_INFO, true, false, false, null);
        packets_channel.queueBind(costants.PACKETS_RECEIVED_INFO, costants.DELORAN_MONITORING_SYSTEM_EXCHANGE, costants.PACKETS_RK);

        deliverCallback(resource_channel, costants.SYSTEM_RESOURCES_INFO);
        deliverCallback(packets_channel, costants.PACKETS_RECEIVED_INFO);


    }

    private static void deliverCallback(Channel channel, String queue_name) {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");

            if (queue_name.equals(costants.SYSTEM_RESOURCES_INFO)) {

                //Create a list of Gateway objects from the JSON
                List<Resource> gateways = mapper.JsonToResourceModel(message);

                //Create a Document for each gateway to store in the db
                List<Document> docs = mapper.createDoc(gateways);

                //Store the Documents in the db

                InsertManyOptions options = new InsertManyOptions().ordered(false).bypassDocumentValidation(true);

                try {
                    InsertManyResult results = resources_collection.insertMany(docs, options);

                } catch (MongoException e) {
                    System.out.println(e.getMessage());

                }
            }
            if(queue_name.equals(costants.PACKETS_RECEIVED_INFO)){
                List<Document> packets = mapper.JsonToListOfPackets(message);

                packets_collection.insertMany(packets);
            }


        };
        try {
            channel.basicConsume(SYSTEM_RESOURCES_INFO, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}