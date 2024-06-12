package org.delora.db.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.delora.db.Model.Gateway;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Mapper {

    /*
    Trasforma il messaggio dal broker in un'oggetto manipolabile
     */
    public List<Gateway> JsonToGatewayModel(String JSON) {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        List<Gateway> gateways = new LinkedList<>();
        try {
            node = mapper.readTree(JSON);

            if (!node.isArray()) {
                gateways.add(createGateway(node));
            } else {
                int size = node.size();
                for (int i = 0; i < size; i++) {
                    gateways.add(createGateway(node.get(i)));
                    System.out.println("gateway" + String.valueOf(i) + " created");
                }

            }
            System.out.println("gateways list created");

            return gateways;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }


    }

    private static Gateway createGateway(JsonNode node) {
        Gateway gateway = new Gateway();

        gateway.setCpu_usage(node.get("cpu_usage").asText());
        gateway.setRam_usage(node.get("ram_usage").asText());
        LocalDateTime timestamp = LocalDateTime.parse(node.get("timestamp").asText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        gateway.setTimestamp(timestamp);
        gateway.setId(node.get("Id").asText());

        JsonNode net_usage = node.path("net_usage");
        gateway.setNet_rx(net_usage.path("RX").asText());
        gateway.setNet_tx(net_usage.path("TX").asText());

        return gateway;
    }

    //Create the document, refer to Model>Gateway for object schema
    public List<Document> createDoc(List<Gateway> gateways) {

        List<Document> docs = new LinkedList<>();
        for (Gateway g : gateways) {

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
}
