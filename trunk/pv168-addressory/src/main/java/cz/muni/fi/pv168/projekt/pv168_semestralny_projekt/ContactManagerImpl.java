package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author Martin Otahal
 */
public class ContactManagerImpl implements ContactManager 
{
    private static final Logger LOGGER = Logger.getLogger(Addressory.class.getName());
    private DataSource ds;

    public ContactManagerImpl(DataSource ds) 
    {
        if (ds == null)
        {
            throw new IllegalArgumentException("data source can not be null");
        }
        this.ds = ds;
    }
    
    public void setDataSource(DataSource ds)
    {
        if (ds == null)
        {
            throw new IllegalArgumentException("data source can not be null");
        }
        this.ds = ds;
    }
    
    @Override
    public void newContact(Contact contact) 
    {
        validateContact(contact);
        if (contact.getId() != null)
        {
            throw new IllegalArgumentException("contact is already in the database");
        }
        
        Connection conn = null;
        try
        {
            conn = ds.getConnection();
        
            try (
                    PreparedStatement st1 = conn.prepareStatement(
                        "INSERT INTO contact (first_name, last_name, address) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement st2 = conn.prepareStatement(
                        "INSERT INTO numbers (contact_id, type, number) VALUES (?, ?, ?)");

                    )
            {
                conn.setAutoCommit(false);
                st1.setString(1, contact.getName());
                st1.setString(2, contact.getSurname());
                st1.setString(3, contact.getAddress());
                int count = st1.executeUpdate();
                assert count == 1;              //other than 1 line was created

                ResultSet keys = st1.getGeneratedKeys();
                Long id = null;
                if (keys.next())
                {
                    id = keys.getLong(1);
                    contact.setId(id);
                }
                else
                {
                    throw new RuntimeException("did not add to the database properly"
                                            + "or did not return ID");
                }

                st2.setLong(1, id);
                for (Map.Entry<String, NumberType> entry : contact.getPhoneNumbers().entrySet())
                {
                    st2.setString(3, entry.getKey());
                    st2.setString(2, entry.getValue().name());
                    int countt = st2.executeUpdate();
                    assert countt == 1;          //other than 1 line was added
                }
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e)
            {
                conn.rollback();
                conn.setAutoCommit(true);
                LOGGER.log(Level.SEVERE, "Error when inserting contact into DB", e);
                throw new RuntimeException("Error when inserting into DB", e);
            }
        } catch (SQLException e)
        {
            String msg = "Error when setting connection";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new RuntimeException(msg, e);
        } finally
        {
            if (conn != null)
            {
                try
                {
                    conn.close();
                } catch (SQLException ex)
                {
                    LOGGER.log(Level.SEVERE, "Error when closing connection", ex);
                    throw new RuntimeException("Error when closing connection", ex);
                }
            }
        }
    }

    @Override
    public void editContact(Contact contact) 
    {
        validateContact(contact);
        if (contact.getId() == null)
        {
            throw new IllegalArgumentException("contact is not in the database");
        }
        
        Connection conn = null;
        
        try
        {
            conn = ds.getConnection();
            try (
                    PreparedStatement st1 = conn.prepareStatement(
                        "UPDATE contact SET first_name = ?, last_name = ?, address = ? WHERE id = ?");
                    PreparedStatement st2 = conn.prepareStatement(
                        "INSERT INTO numbers (contact_id, type, number) VALUES (?, ?, ?)");
                    PreparedStatement st3 = conn.prepareStatement(
                        "DELETE FROM numbers WHERE contact_id = ?");
                    )
            {
                conn.setAutoCommit(false);
                st1.setString(1, contact.getName());
                st1.setString(2, contact.getSurname());
                st1.setString(3, contact.getAddress());
                st1.setLong(4, contact.getId());
                int count = st1.executeUpdate();
                assert count == 1;              //other than 1 line was updated

                st3.setLong(1, contact.getId());
                st3.executeUpdate();
                st2.setLong(1, contact.getId());
                for (Map.Entry<String, NumberType> entry : contact.getPhoneNumbers().entrySet())
                {
                    st2.setString(3, entry.getKey());
                    st2.setString(2, entry.getValue().name());
                    int countt = st2.executeUpdate();
                    assert countt == 1;          //other than 1 line was updated
                }
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e)
            {
                conn.rollback();
                conn.setAutoCommit(true);
                LOGGER.log(Level.SEVERE, "Error when inserting contact into DB", e);
                throw new RuntimeException("Error when inserting into DB", e);
            }
        } catch (SQLException e)
        {
            String msg = "Error when setting connection";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new RuntimeException(msg, e);
        } finally
        {
            if (conn != null)
            {
                try
                {
                    conn.close();
                } catch (SQLException ex)
                {
                    LOGGER.log(Level.SEVERE, "Error when closing connection", ex);
                    throw new RuntimeException("Error when closing connection", ex);
                }
            }
        }
    }

    @Override
    public void deleteContact(Contact contact) 
    {
        validateContact(contact);
        if (contact.getId() == null)
        {
            throw new IllegalArgumentException("contact not in database");
        }
        
        Connection conn = null;
        
        try
        {
            conn = ds.getConnection();  
            
            try (
                    PreparedStatement st1 = conn.prepareStatement(
                        "DELETE FROM numbers WHERE contact_id = ?");            
                    PreparedStatement st2 = conn.prepareStatement(
                        "DELETE FROM contact WHERE id = ?");
                    )
            {
                conn.setAutoCommit(false);
                st1.setLong(1, contact.getId());
                st2.setLong(1, contact.getId());
                st1.executeUpdate();
                int count = st2.executeUpdate();
                assert count <= 1;                  //should delete only one contact
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e)
            {
                conn.rollback();
                conn.setAutoCommit(true);
                String msg = "Removing contact / numbers failed.";
                LOGGER.log(Level.SEVERE, msg, e);
            }
        } catch (SQLException e)
        {
            String msg = "Error when setting connection";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new RuntimeException(msg, e);
        } finally
        {
            if (conn != null)
            {
                try
                {
                    conn.close();
                } catch (SQLException ex)
                {
                    LOGGER.log(Level.SEVERE, "Error when closing connection", ex);
                    throw new RuntimeException("Error when closing connection", ex);
                }
            }
        }
    }

    @Override
    public Contact findContactByID(Long id) 
    {
        if (id == null || id.longValue() == 0)
        {
            throw new IllegalArgumentException("id must be initialized and greater then 0");
        }
        
        Contact contact = new Contact();
        
        try (
                Connection conn = ds.getConnection();//DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement st1 = conn.prepareStatement(
                        "SELECT * FROM contact WHERE id = ?");
                PreparedStatement st2 = conn.prepareStatement(
                        "SELECT * FROM numbers WHERE contact_id = ?");
                )
        {
            conn.setAutoCommit(false);
            st1.setLong(1, id);
            ResultSet rs = st1.executeQuery();
            if (rs.next())
            {
                contact.setId(rs.getLong(1));
                contact.setAddress(rs.getString(4));
                contact.setName(rs.getString(2));
                contact.setSurname(rs.getString(3));
            }
            else
            {
                return null;
            }
            
            st2.setLong(1, id);
            rs = st2.executeQuery();
            Map<String, NumberType> map = new TreeMap();
            while(rs.next())
            {
                map.put(rs.getString("number"), Enum.valueOf(NumberType.class, rs.getString("type")));
            }
            contact.setPhoneNumbers(map);
            conn.commit();
            return contact;
        } catch (SQLException e)
        {
            LOGGER.log(Level.SEVERE, "Error when selecting contact from DB", e);
            throw new RuntimeException("Error when selecting from DB", e);
        }
    }

    @Override
    public Contact findContactByName(String name) 
    {
        if (name == null || name.equals(""))
        {
            throw new IllegalArgumentException("name must not be null or empty string");
        }
        
        Contact contact = new Contact();
        
        String[] names = name.split(" ", 2);
        try (
                Connection conn = ds.getConnection();//DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement st1 = conn.prepareStatement(
                        "SELECT * FROM contact WHERE first_name = ? AND last_name = ?");
                PreparedStatement st2 = conn.prepareStatement(
                        "SELECT * FROM numbers WHERE contact_id = ?");
                )
        {
            conn.setAutoCommit(false);
            st1.setString(1, names[0]);
            st1.setString(2, names[1]);
            ResultSet rs = st1.executeQuery();
            if (rs.next())
            {
                contact.setId(rs.getLong(1));
                contact.setAddress(rs.getString(4));
                contact.setName(rs.getString(2));
                contact.setSurname(rs.getString(3));
            }
            else
            {
                return null;
            }
            
            st2.setLong(1, contact.getId());
            rs = st2.executeQuery();
            Map<String, NumberType> map = new TreeMap();
            while(rs.next())
            {
                map.put(rs.getString("number"), Enum.valueOf(NumberType.class, rs.getString("type")));
            }
            contact.setPhoneNumbers(map);
            conn.commit();
            return contact;
        } catch (SQLException e)
        {
            LOGGER.log(Level.SEVERE, "Error when selecting contact from DB", e);
            throw new RuntimeException("Error when selecting from DB", e);
        }
    }

    @Override
    public List<Contact> findAllContacts() 
    {
        List<Contact> list = new ArrayList();
        try (
                Connection conn = ds.getConnection();//DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement st1 = conn.prepareStatement(
                        "SELECT * FROM contact");
                PreparedStatement st2 = conn.prepareStatement(
                        "SELECT * FROM numbers WHERE contact_id = ?");
                )
        {
            conn.setAutoCommit(false);
            ResultSet rs = st1.executeQuery();
            while(rs.next())
            {
                Contact contact = new Contact();
                contact.setAddress(rs.getString(4));
                contact.setId(rs.getLong(1));
                contact.setName(rs.getString(2));
                contact.setSurname(rs.getString(3));
                list.add(contact);
            }
            
            for (Contact contact : list) 
            {
                Map<String, NumberType> map = new TreeMap();
                st2.setLong(1, contact.getId());
                ResultSet resultSet = st2.executeQuery();
                while(resultSet.next())
                {
                    map.put(resultSet.getString("number"), Enum.valueOf(
                            NumberType.class, resultSet.getString("type")));
                }
                contact.setPhoneNumbers(map);
            }
            conn.commit();
            return list;
        } catch (SQLException e)
        {
            LOGGER.log(Level.SEVERE, "Error when selecting contact from DB", e);
            throw new RuntimeException("Error when selecting from DB", e);
        }
    }
    
    private boolean validateContact(Contact contact)
    {
        if (contact == null)
        {
            throw new IllegalArgumentException("contact is null");
        }
        if (contact.getName() == null || contact.getName().equals(""))
        {
            throw new IllegalArgumentException("first name must not be null or empty");
        }
        if (contact.getSurname() == null || contact.getSurname().equals(""))
        {
            throw new IllegalArgumentException("last name must not be null or empty");
        }
        if (contact.getPhoneNumbers() == null)
        {
            throw new IllegalArgumentException("must have created numbers map");
        }
        return true;
    }
}
