package com.tlc.deloran.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Document(collection = "#{@environment.getProperty('deloran.env.mongodb.collection.packets')}")
@Component
public class Packet {

    @Field("timestamp")
    private LocalDateTime timestamp;

    @Field("packet")
    private String packet;

    @Override
    public String toString() {
        return "Packet{" +
                "timestamp=" + timestamp +
                ", packet='" + packet + '\'' +
                '}';
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPacket() {
        return packet;
    }

    public void setPacket(String packet) {
        this.packet = packet;
    }
}
