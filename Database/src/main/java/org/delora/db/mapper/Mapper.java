package org.delora.db.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bson.Document;
import org.delora.db.Model.Resource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class Mapper {


    /*
            Trasforma il messaggio dal broker in un'oggetto manipolabile
             */
    public List<Resource> JsonToResourceModel(String JSON) {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        List<Resource> resource = new LinkedList<>();
        try {
            node = mapper.readTree(JSON);

            if (!node.isArray()) {
                resource.add(createGateway(node));
            } else {
                int size = node.size();
                for (int i = 0; i < size; i++) {
                    resource.add(createGateway(node.get(i)));
                    System.out.println("resource" + String.valueOf(i) + " created");
                }

            }
            System.out.println("resource list created");

            return resource;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }


    }

    private static Resource createGateway(JsonNode node) {
        Resource resource = new Resource();

        resource.setCpu_usage(node.get("cpu_usage").asText());
        resource.setRam_usage(node.get("ram_usage").asText());
        LocalDateTime timestamp = LocalDateTime.parse(node.get("timestamp").asText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        timestamp = timestamp.atZone(ZoneId.of("Europe/Rome")).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        resource.setTimestamp(timestamp);
        resource.setId(node.get("Id").asText());

        JsonNode net_usage = node.path("net_usage");
        resource.setNet_rx(net_usage.path("RX").asText());
        resource.setNet_tx(net_usage.path("TX").asText());

        return resource;
    }

    //Create the document, refer to Model>Gateway for object schema
    public List<Document> createDoc(List<Resource> resource) {

        List<Document> docs = new LinkedList<>();
        for (Resource g : resource) {

            Document doc = new Document(
                    "Id", g.getId())
                    .append("CPU_usage", g.getCpu_usage())
                    .append("RAM_usage", g.getRam_usage())
                    .append("TIMESTAMP", g.getTimestamp())
                    .append("NET_RX", g.getNet_rx())
                    .append("NET_TX", g.getNet_tx());

            docs.add(doc);
        }
        System.out.println("docs created, ready to save into db");
        return docs;
    }

    public List<Document> JsonToListOfPackets(String message) {

        ObjectMapper mapper = new ObjectMapper();
        if (message == null) {
            return List.of();
        }
        List<Document> packet_list = new LinkedList<>();

        try {
            JsonNode array = mapper.readTree(message);

            if (array.isArray()) {

                for (JsonNode node : array) {
                    String timestamp = node.get("timestamp").asText();

                    ObjectNode n = (ObjectNode) node;
                    n.remove("timestamp");

                    JsonNode packet = (JsonNode) n;
                    Document doc = Document.parse(packet.toString());
                    doc.append("timestamp", LocalDateTime.parse(timestamp,DateTimeFormatter.ISO_LOCAL_DATE_TIME ));

                    packet_list.add(doc);
                }
            }

            System.out.println(packet_list.toString());
            return packet_list;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
