package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.gson.Gson;

public class KeyspaceRepository {
    private Session session;
    private String keyspace;

    public KeyspaceRepository(String keyspace, Session session) {

        this.session = session;
        this.keyspace = keyspace;
    }

    /**
     * Method used to create any keyspace - schema.
     *
     * @param keyspaceName the name of the keyspaceName.
     * @param replicationStrategy the replication strategy.
     * @param numberOfReplicas the number of replicas.
     *
     */
    public void createKeyspace(String keyspaceName, String replicationStrategy, int numberOfReplicas) {
        StringBuilder sb = new StringBuilder("CREATE KEYSPACE IF NOT EXISTS ")
                .append(keyspaceName).append(" WITH replication = {")
                .append("'class':'").append(replicationStrategy)
                .append("','replication_factor':").append(numberOfReplicas).append("};");

        final String query = sb.toString();

        session.execute(query);
    }
    public void createTable(String tableName, String column1, String type1, String column2, String type2, String column3, String type3) {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(keyspace).append(".").append(tableName).append(" (").append(column1).append(" ").append(type1)
                .append(" primary key").append(",").append(column2).append(" ")
                .append(type2).append(",").append(column3).append(" ")
                .append(type3).append(");");

        final String query = sb.toString();

        session.execute(query);

    }
    public void insertRow(String tableName, AccountDetails accountDetails) {
        StringBuilder sb = new StringBuilder("insert into ").append(keyspace).append(".").append(tableName)
                .append(" (").append("accountid").append(",").append("name").append(",").append("balance").append(") values (")
                .append(accountDetails.accountId).append(",'").append(accountDetails.name).append("',").append(accountDetails.balance).append(") IF NOT EXISTS;");

        final String query = sb.toString();

        session.execute(query);
    }
    public AccountDetails selectRow(String tableName, int accountid) {
        StringBuilder sb = new StringBuilder("select * from ").append(keyspace).append(".")
                .append(tableName).append(" WHERE accountid=").append(accountid).append(";");

        final String query = sb.toString();

        ResultSet result = session.execute(query);
        Row row = result.one();
        AccountDetails ad = new AccountDetails(row.getInt("accountId"), row.getString("name"),row.getInt("balance"));
        return ad;
    }
    public String convertToJson(AccountDetails accountDetails){
        return new Gson().toJson(accountDetails);
    }
    public void createfile(String name){
        try {
            File myObj = new File(name);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public void writefile(String name, String input){
        try {
            FileWriter myWriter = new FileWriter(name);
            myWriter.write(input);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }
}