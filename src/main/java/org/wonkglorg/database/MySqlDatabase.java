package org.wonkglorg.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IMPORTANT! Please add the mysql Jconnector to the project if you want to use MySql, I did not include this myself to not inflate the libraries
 * size. groupId : mysql artifactId : mysql-connector-java
 */
@SuppressWarnings("unused")
public class MySqlDatabase extends Database {

    protected final String USERNAME;
    protected final String URL;
    protected final String PASSWORD;
    protected final Logger logger = Logger.getLogger(MySqlDatabase.class.getName());
    private BlockingQueue<Connection> connectionPool;

    public MySqlDatabase(String url, String username, String password, String databaseName, int poolSize) {
        super(databaseName, DatabaseType.MYSQL);

        if (username == null || url == null || password == null || DATABASE_NAME == null) {
            throw new RuntimeException();
        }
        USERNAME = username;
        URL = url;
        PASSWORD = password;

        initializeConnectionPool(poolSize);
    }

    @Override
    public Connection getConnection() {
        try {
            return connectionPool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void releaseConnection(Connection connection) {
        connectionPool.offer(connection);
    }

    /**
     * Unused handled by connection pool
     */
    @Override
    public void connect() {
        //unused handled by connection pool
    }

    @Override
    public void disconnect() {
        for (Connection connection : connectionPool) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error closing connection: " + e.getMessage());
            }
        }
    }

    private void initializeConnectionPool(int poolSize) {
        connectionPool = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            connectionPool.add(createConnection());
        }
    }

    private Connection createConnection() {
        try {
            Class.forName(databaseType.getClassLoader());
            return DriverManager.getConnection(getDatabaseType().getDriver() + "//" + URL + "/" + DATABASE_NAME, USERNAME, PASSWORD);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Mysql-Connector-J Could not be found");
            logger.log(Level.SEVERE, "Make sure to Include the MySql-Connector-J library in your project");
            logger.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }


}