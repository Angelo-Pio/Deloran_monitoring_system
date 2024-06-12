package org.delora.db.Model;

import java.time.LocalDateTime;

public class Gateway {

    private String id;
    private String cpu_usage;
    private String ram_usage;
    private String net_rx;
    private String net_tx;
    private LocalDateTime timestamp;


    public Gateway(String id, String cpu_usage, String ram_usage, String net_usage, LocalDateTime timestamp, String net_tx) {
        this.id = id;
        this.cpu_usage = cpu_usage;
        this.ram_usage = ram_usage;
        this.net_rx = net_usage;
        this.timestamp = timestamp;
        this.net_tx = net_tx;
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
