package com.tlc.deloran.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Document(collection = "gateway")
public class Gateway {


    @Id
    @Field("_id")
    private String _id;
    @Field("Id")
    private String id;
    @Field("CPU_usage")
    private String cpu_usage;
    @Field("RAM_usage")
    private String ram_usage;
    @Field("NET_RX")
    private String net_rx;
    @Field("NET_TX")
    private String net_tx;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Field("TIMESTAMP")
    private LocalDateTime timestamp;


    public Gateway(String _id, String id, String cpu_usage, String ram_usage, String net_usage, LocalDateTime timestamp, String net_tx) {
        this._id = _id;
        this.id = id;
        this.cpu_usage = cpu_usage;
        this.ram_usage = ram_usage;
        this.net_rx = net_usage;
        this.timestamp = timestamp;
        this.net_tx = net_tx;
    }

    @Override
    public String toString() {
        return "Gateway{" +
                "id='" + id + '\'' +
                ", cpu_usage='" + cpu_usage + '\'' +
                ", ram_usage='" + ram_usage + '\'' +
                ", net_rx='" + net_rx + '\'' +
                ", net_tx='" + net_tx + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public Gateway() {
    }

    public String getNet_tx() {
        return net_tx;
    }

    public void setNet_tx(String net_tx) {
        this.net_tx = net_tx;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCpu_usage() {
        return cpu_usage;
    }

    public void setCpu_usage(String cpu_usage) {
        this.cpu_usage = cpu_usage;
    }

    public String getRam_usage() {
        return ram_usage;
    }

    public void setRam_usage(String ram_usage) {
        this.ram_usage = ram_usage;
    }

    public String getNet_rx() {
        return net_rx;
    }

    public void setNet_rx(String net_rx) {
        this.net_rx = net_rx;
    }
}
