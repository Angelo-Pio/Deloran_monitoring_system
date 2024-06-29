package org.delora.broker.mockup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.delora.broker.Costants;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Mocker {

    private static ObjectMapper mapper = new ObjectMapper();

    public static List<String> genericPacketList() throws IOException, InterruptedException {

        InputStream inputStream = Costants.class.getClassLoader().getSystemResourceAsStream("packet_object_template.json");
        ObjectNode node = (ObjectNode) mapper.readTree(inputStream);
        LinkedList ret = new LinkedList<>();
        Random rand = new Random();
        for (int i = 0; i < 50; i++) {

            node.put("timestamp", LocalDateTime.now().toString());
            ret.add(node.toString());
            Thread.sleep(rand.nextInt(10)*100);
            System.out.println(node.toString());
        }
        return ret;

    }

}
