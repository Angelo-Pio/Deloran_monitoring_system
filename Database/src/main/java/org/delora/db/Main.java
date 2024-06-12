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
import org.delora.db.Model.Gateway;
import org.delora.db.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import static org.delora.db.Costants.QUEUE_NAME;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static Costants costants = new Costants();
    private static Mapper mapper = new Mapper();
    private static Channel channel;
    private static Connection connection;

    public static void main(String[] args) {
        Costants costants = new Costants();

        // MongoDB routine
        MongoClient mongoClient = MongoClients.create(costants.JDBC_URL);
        MongoDatabase db = mongoClient.getDatabase(costants.DB_NAME);
        MongoCollection<Document> collection = db.getCollection("gateway");
        System.out.println("connection enstablished with db");

        // Receive messages from rabbitmq
        try {
            receive(collection, mongoClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Close resources
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                channel.close();
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
        channel = connection.createChannel();
        System.out.println("connection enstablished with mqtt server");


        channel.queueDeclare(costants.QUEUE_NAME, false, false, false, null);


        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");

            //Create a list of Gateway objects from the JSON
            List<Gateway> gateways = mapper.JsonToGatewayModel(message);

            //Create a Document for each gateway to store in the db
            List<Document> docs = mapper.createDoc(gateways);

            //Store the Documents in the db

            InsertManyOptions options = new InsertManyOptions().ordered(false).bypassDocumentValidation(true);

            try {
                InsertManyResult results = collection.insertMany(docs, options);

                /*
                System.out.println(results.getInsertedIds().toString());
                System.out.println(results.getInsertedIds().size());
                System.out.println(String.valueOf(collection.countDocuments()));
                */
            } catch (MongoException e) {
                System.out.println(e.getMessage());


            }

        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }


}