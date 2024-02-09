package org.wonkglorg.database;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Wonkglorg
 */
@SuppressWarnings("unused")
public class SqliteDatabase extends Database {
    protected final Path SOURCE_PATH;
    protected final Logger logger = Logger.getLogger(SqliteDatabase.class.getName());
    protected final Path DESTINATION_PATH;
    protected final String DATABASE_NAME;

    /**
     * Creates a Sqlite database at the specified copyToPath.
     * The sourcePath indicates where in the project the database file can be found(If it is not entered as an absolute path it will be outgoing from the resources directory), it will then be copied to the destinationPath destination.
     * If there is no database file it will be created at the destinationPath location.
     * <br>
     * !!IMPORTANT!!
     * <br>Use <br>
     * <pre>
     *     {@code
     * <plugin>
     * 	<groupId>org.apache.maven.plugins</groupId>
     * 	<artifactId>maven-resources-plugin</artifactId>
     * 	<version>3.3.1</version>
     * 	<configuration>
     * 		<nonFilteredFileExtensions>
     * 			<nonFilteredFileExtension>db</nonFilteredFileExtension>
     * 		</nonFilteredFileExtensions>
     * 	</configuration>
     * </plugin>
     * }
     * </pre>
     * otherwise sqlite database files will be filtered and become corrupted.
     *
     * @param sourcePath
     * @param destinationPath
     */
    public SqliteDatabase(Path sourcePath, Path destinationPath) {
        super(destinationPath.getFileName().toString(), DatabaseType.SQLITE);
        String name = destinationPath.getFileName().toString();
        DATABASE_NAME = name.endsWith(".db") ? name : name + ".db";
        SOURCE_PATH = sourcePath;
        DESTINATION_PATH = destinationPath;
        connect();
    }

    @Override
    public void connect() {
        if (connection != null) {
            return;
        }

        try {
            Class.forName(databaseType.getClassLoader());

            File databaseFile = DESTINATION_PATH.toAbsolutePath().toFile();
            if (!databaseFile.exists()) {
                copyDatabaseFile(databaseFile);
            }
            String connectionString = databaseType.getDriver() + DESTINATION_PATH;
            connection = DriverManager.getConnection(connectionString);

        } catch (ClassNotFoundException | SQLException | IOException e) {
            logger.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void copyDatabaseFile(File databaseFile) throws IOException {
        try (InputStream resourceStream = getResourceAsStream(SOURCE_PATH.toString())) {
            if (resourceStream != null) {
                Files.createDirectories(DESTINATION_PATH.getParent());
                Files.copy(resourceStream, databaseFile.toPath());
            } else {
                throw new FileNotFoundException("Database file not found: " + SOURCE_PATH);
            }
        }
    }

    private InputStream getResourceAsStream(String filename) throws IOException {
        if (isAbsolutePath(filename)) {
            return new FileInputStream(filename);
        } else {
            return getResourceFromJar(filename);
        }
    }

    private boolean isAbsolutePath(String filename) {
        return new File(filename).isAbsolute();
    }

    private InputStream getResourceFromJar(String filename) throws IOException {

        File file = new File(filename);


        URL url = getClass().getClassLoader().getResource(filename.replaceAll("\\\\", "/"));
        if (url == null) {
            return null;
        }
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        return connection.getInputStream();
    }


}
	
