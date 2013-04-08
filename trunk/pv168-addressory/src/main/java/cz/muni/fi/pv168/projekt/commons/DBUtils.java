/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.projekt.commons;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author Martin Otahal
 */

public class DBUtils 
{

    private static final Logger logger = Logger.getLogger(DBUtils.class.getName());

    /**
     * Closes connection and logs possible error.
     * 
     * @param conn connection to close
     * @param statements  statements to close
     */
    public static void closeQuietly(Connection conn, Statement ... statements) {
        for (Statement st : statements) {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Error when closing statement", ex);
                }                
            }
        }        
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error when switching autocommit mode back to true", ex);
            }
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error when closing connection", ex);
            }
        }
    }

    /**
     * Rolls back transaction and logs possible error.
     * 
     * @param conn connection
     */
    public static void doRollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                if (conn.getAutoCommit()) {
                    throw new IllegalStateException("Connection is in the autocommit mode!");
                }
                conn.rollback();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error when doing rollback", ex);
            }
        }
    }

    /**
     * Extract key from given ResultSet.
     * 
     * @param key resultSet with key
     * @return key from given result set
     * @throws SQLException when operation fails
     */
    public static Long getId(ResultSet key) throws SQLException {
        if (key.getMetaData().getColumnCount() != 1) {
            throw new IllegalArgumentException("Given ResultSet contains more columns");
        }
        if (key.next()) {
            Long result = key.getLong(1);
            if (key.next()) {
                throw new IllegalArgumentException("Given ResultSet contains more rows");
            }
            return result;
        } else {
            throw new IllegalArgumentException("Given ResultSet contain no rows");
        }
    }

    /**
     * Reads SQL statements from file. SQL commands in file must be separated by 
     * a semicolon.
     * 
     * @param url url of the file
     * @return array of command  strings
     */
    private static String[] readSqlStatements(URL url) {
        try {
            char buffer[] = new char[256];
            StringBuilder result = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
            while (true) {
                int count = reader.read(buffer);
                if (count < 0) {
                    break;
                }
                result.append(buffer, 0, count);
            }
            return result.toString().split(";");
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read " + url, ex);
        }
    }
    
    /**
     * Try to execute script for creating tables. If tables already exist, 
     * appropriate exception is catched and ignored.
     * 
     * @param ds dataSource 
     * @param scriptUrl url of script for creating tables
     * @throws SQLException when operation fails
     */
    public static void tryCreateTables(DataSource ds, URL scriptUrl) throws SQLException {
        try {
            executeSqlScript(ds, scriptUrl);
            logger.warning("Tables created");
        } catch (SQLException ex) {
            if ("X0Y32".equals(ex.getSQLState())) {
                // This code represents "Table/View/... already exists"
                // This code is Derby specific!
                return;
            } else {
                throw ex;
            }
        }
    }
    
    /**
     * Executes SQL script.
     * 
     * @param ds datasource
     * @param scriptUrl url of sql script to be executed
     * @throws SQLException when operation fails
     */
    public static void executeSqlScript(DataSource ds, URL scriptUrl) throws SQLException {
        Connection conn = null;
        try {
            conn = ds.getConnection();
            for (String sqlStatement : readSqlStatements(scriptUrl)) {
                if (!sqlStatement.trim().isEmpty()) {
                    conn.prepareStatement(sqlStatement).executeUpdate();
                }
            }
        } finally {
            closeQuietly(conn);
        }
    }

    /*public static void createTables(DataSource ds)
    {
        Connection conn = null;
        try
        {
            conn = ds.getConnection();
            PreparedStatement st = conn.prepareStatement(
                    "CREATE TABLE contact ("
                    + "id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
                    + "first_name VARCHAR(20), "
                    + "last_name VARCHAR(20), "
                    + "address VARCHAR(50))");
            st.executeUpdate();
            
            conn.prepareStatement(
                    "CREATE TABLE numbers ("
                    + "contact_id INTEGER, "
                    + "type VARCHAR(10), "
                    + "number VARCHAR(15), "
                    + "FOREIGN KEY (contact_id) REFERENCES contact(id))").executeUpdate();
            
            conn.prepareStatement(
                    "CREATE TABLE groups ("
                    + "id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
                    + "type VARCHAR(10), "
                    + "note VARCHAR(255))").executeUpdate();
            
            conn.prepareStatement(
                    "CREATE TABLE entry ("
                    + "contact_id INTEGER, "
                    + "group_id INTEGER, "
                    + "PRIMARY KEY (contact_id, group_id), "
                    + "FOREIGN KEY (contact_id) REFERENCES contact(id), "
                    + "FOREIGN KEY (group_id) REFERENCES groups(id))").executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (conn != null)
            {
                try
                {
                    conn.close();
                }
                catch (SQLException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public static void deleteTables(DataSource ds)
    {
        Connection conn = null;
        try
        {
            conn = ds.getConnection();
            conn.prepareStatement(
                    "DROP TABLE numbers").executeUpdate();
            
            conn.prepareStatement(
                    "DROP TABLE entry").executeUpdate();
            
            conn.prepareStatement(
                    "DROP TABLE contact").executeUpdate();
            
            conn.prepareStatement(
                    "DROP TABLE groups").executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (conn != null)
            {
                try
                {
                    conn.close();
                }
                catch (SQLException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }*/
    
    public static void deleteFromTables(DataSource ds)
    {
        Connection conn = null;
        try
        {
            conn = ds.getConnection();
            conn.prepareStatement(
                    "DELETE FROM numbers").executeUpdate();
            
            conn.prepareStatement(
                    "DELETE FROM entry").executeUpdate();
            
            conn.prepareStatement(
                    "DELETE FROM contact").executeUpdate();
            
            conn.prepareStatement(
                    "DELETE FROM groups").executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (conn != null)
            {
                try
                {
                    conn.close();
                }
                catch (SQLException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
}
