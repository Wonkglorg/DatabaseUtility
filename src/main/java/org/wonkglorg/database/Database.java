package org.wonkglorg.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Wonkglorg
 */
@SuppressWarnings("unused")
public abstract class Database {
    protected Connection connection;
    protected final String DATABASE_NAME;
    protected final DatabaseType databaseType;

    public Database(String name, DatabaseType databaseType) {
        this.DATABASE_NAME = name;
        this.databaseType = databaseType;
    }

    public abstract void connect();

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }

    }

    public Connection getConnection() {
        connect();

        return connection;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public enum DatabaseType {
        MYSQL("Mysql", "jdbc:mysql:", "com.mysql.cj.jdbc.Driver"),
        SQLITE("Sqlite", "jdbc:sqlite:", "org.sqlite.JDBC"),
        H2("H2", "jdbc:h2:", "org.h2.Driver"),
        POSTGRESQL("Postgresql", "jdbc:postgresql:", "org.postgresql.Driver"),
        MARIADB("MariaDB", "jdbc:mariadb:", "org.mariadb.jdbc.Driver"),
        HSQLDB("HSQLDB", "jdbc:hsqldb:", "org.hsqldb.jdbc.JDBCDriver"),
        DERBY("Derby", "jdbc:derby:", "org.apache.derby.jdbc.EmbeddedDriver"),
        FIREBIRD("Firebird", "jdbc:firebirdsql:", "org.firebirdsql.jdbc.FBDriver"),
        DB2("DB2", "jdbc:db2:", "com.ibm.db2.jcc.DB2Driver"),
        ;
        private final String driver;
        private final String classLoader;
        private final String name;

        DatabaseType(String name, String driver, String classLoader) {
            this.driver = driver;
            this.classLoader = classLoader;
            this.name = name;
        }

        public String getDriver() {
            return driver;
        }

        public String getClassLoader() {
            return classLoader;
        }

        public String getName() {
            return name;
        }
    }

}