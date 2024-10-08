package org.delora.broker.mockup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.delora.broker.Costants;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Random;

public class Mocker {

    private static ObjectMapper mapper = new ObjectMapper();

    public static String genericPacketList() throws IOException, InterruptedException {

        InputStream inputStream = Costants.class.getClassLoader().getSystemResourceAsStream("packet_object_template.json");
        ObjectNode node = (ObjectNode) mapper.readTree(inputStream);
        Random rand = new Random();
        StringBuilder ret = new StringBuilder("[");
        int num  = rand.nextInt(100);
        for (int i = 0; i < num; i++) {

            node.put("timestamp", LocalDateTime.now().toString());
            ret.append(node.toString());
            if(i < num-1){
                ret.append(",");
            }
//            Thread.sleep(rand.nextInt(10)*100);

        }
        ret.append("]");
        System.out.println(ret.toString());
        return ret.toString();

    }

}
