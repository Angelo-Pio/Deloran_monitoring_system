package org.delora.db;
//TODO change to .env file or similar
public class Costants {
    public final static String SYSTEM_RESOURCES_INFO = "system_resources_info";
    public final static String PACKETS_RECEIVED_INFO = "packets_received_info";
    public final static String PACKETS_RK = "packet";
    public final static String RESOURCES_RK = "resource";
    public final static String DELORAN_MONITORING_SYSTEM_EXCHANGE = "deloran_monitoring_system_exchange";



    public final static String  HOST = "localhost";
    public final static String  JDBC_URL = "mongodb://localhost:27017";
    public final static String  DB_USERNAME = "username";
    public final static String  DB_PASSWORD = "secret";


    public static final String DB_NAME = "deloran";
    //TODO remove this type and change collection name
    public static final String resources_collection="gateway";
    public static final String packets_collection="packet";
}
