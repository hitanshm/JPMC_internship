package org.example;

import com.datastax.driver.core.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Cassandra {
    private static String keyspaceName;
    private static String tableName;
    private Cluster cluster;
    private Session session;
    private CassandraTable table;
    private static String user;
    private static String password;
    public Cassandra(Session session, CassandraTable table, String user, String password){
        this.session=session;
        this.table=table;
        this.user=user;
        this.password=password;
        keyspaceName=table.getKeyspaceName();
        tableName=table.getTableName();
    }
    //Connecting Cassandra
    public void connect(String node, Integer port, String user, String password) {
        Cluster.Builder b = Cluster.builder().addContactPoint(node).withCredentials(user, password);
        if (port != null) {
            b.withPort(port);
        }
        cluster = b.build();

        session = cluster.connect();
    }

    public Session getSession() {

        return this.session;
    }

    public void close() {
        session.close();
        cluster.close();
    }
    public static List<Row> getAllFromTable(CassandraTable table){

        ResultSet result = getResult(table);
        List<Row> allData=result.all();
        return allData;
    }
    public static List<String> getAllColumnsFromTable(CassandraTable table){
        ResultSet result = getResult(table);
        List<String> columnNames =
                result.getColumnDefinitions().asList().stream()
                        .map(cl -> cl.getName())
                        .collect(Collectors.toList());
        return columnNames;
    }
    public static List getTables(String keyspaceName){

        List tableNames = new ArrayList();

        Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").withCredentials(user, password).build();
        Metadata metadata = cluster.getMetadata();

        for (TableMetadata t : metadata.getKeyspace(keyspaceName).getTables()) {
            tableNames.add(t.getName());

        }
        int numOfTables = tableNames.size();

        System.out.println("Table names: " + tableNames);
        System.out.println("Total table count: " + numOfTables);

        return tableNames;

    }
    public static ResultSet getResult(CassandraTable table){
        String query = "SELECT * FROM " + table.getTableName();
        //Creating Cluster object
        Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").withCredentials(user, password).build();
        //Creating Session object
        Session session = cluster.connect(table.getKeyspaceName());
        //Getting the ResultSet
        ResultSet result = session.execute(query);
        return result;
    }

}
