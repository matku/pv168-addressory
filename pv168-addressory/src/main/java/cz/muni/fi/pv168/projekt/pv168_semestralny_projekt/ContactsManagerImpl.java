package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

import java.util.List;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Otahal
 */
public class ContactsManagerImpl implements ContactsManager 
{
    private static final Logger LOGGER = Logger.getLogger(Addressory.class.getName());
    DataSource ds;
    
    public ContactsManagerImpl(DataSource ds)
    {
        if (ds == null)
        {
            throw new IllegalArgumentException("data source must not be null");
        }
        this.ds = ds;
    }
    
    @Override
    public void addContactToGroup(Contact contact, Group group) 
    {
        if (contact == null)
        {
            throw new IllegalArgumentException("contact is null");
        }
        if (group == null)
        {
            throw new IllegalArgumentException("group is null");
        }
        if (contact.getId() == null)
        {
            throw new IllegalArgumentException("contact not in db");
        }
        if (group.getId() == null)
        {
            throw new IllegalArgumentException("group not in db");
        }
        
        try (
                Connection conn = ds.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO entry (contact_id, group_id) VALUES (?, ?)");
                )
        {
            st.setLong(1, contact.getId());
            st.setLong(2, group.getId());
            int count = st.executeUpdate();
            assert count == 1;
        } catch (SQLException e)
        {
            LOGGER.log(Level.SEVERE, "Error when inserting entry into DB", e);
        }
        
        
    }

    @Override
    public void removeContactFromGroup(Contact contact, Group group) 
    {
        if (contact == null)
        {
            throw new IllegalArgumentException("contact is null");
        }
        if (group == null)
        {
            throw new IllegalArgumentException("group is null");
        }
        if (contact.getId() == null)
        {
            throw new IllegalArgumentException("contact not in db");
        }
        if (group.getId() == null)
        {
            throw new IllegalArgumentException("group not in db");
        }
        
        try (
                Connection conn = ds.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "DELETE FROM entry WHERE contact_id = ? AND group_id = ?");
                )
        {
            st.setLong(1, contact.getId());
            st.setLong(2, group.getId());
            int count = st.executeUpdate();
            assert count <= 1;
        } catch (SQLException e)
        {
            LOGGER.log(Level.SEVERE, "Error when deleting entry from DB", e);
        }
    }

    @Override
    public Group findGroupWithContact(Contact contact) {
        if (contact == null)
        {
            throw new IllegalArgumentException("contact is null");
        }
        if (contact.getId() == null)
        {
            return null;
        }
        try (
                Connection conn = ds.getConnection();
                PreparedStatement st1 = conn.prepareStatement(
                        "SELECT group_id FROM entry WHERE contact_id = ?");
                PreparedStatement st2 = conn.prepareStatement(
                        "SELECT * FROM group WHERE group_id = ?");
                )
        {
            st1.setLong(1, contact.getId());
            ResultSet rs = st1.executeQuery();
            if(!rs.next())
            {
                return null;
            }
            st2.setLong(1, rs.getLong(1));
            rs = st2.executeQuery();
            if (rs.next())
            {
                Group group = new Group();
                group.setId(rs.getLong(1));
                group.setType(Enum.valueOf(GroupType.class, rs.getString(2)));
                group.setNote(rs.getString(3));
                return group;
            }
            return null;
        } catch (SQLException e)
        {
            LOGGER.log(Level.SEVERE, "Error when working with DB", e);
        }
        return null;
    }

    @Override
    public List<Contact> findAllContactsInGroup(Group group) 
    {
        if (group == null)
        {
            throw new IllegalArgumentException("group is null");
        }
        if (group.getId() == null)
        {
            throw new IllegalArgumentException("group not in db");
        }
        
        ContactManager contactManager = new ContactManagerImpl(ds);
        
        try (
                Connection conn = ds.getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "SELECT contact_id FROM entry WHERE group_id = ?");
                )
        {
            st.setLong(1, group.getId());
            ResultSet rs = st.executeQuery();
            List<Contact> list = new ArrayList<>();
            while(rs.next())
            {
                Contact contact = contactManager.findContactByID(rs.getLong(1));
                list.add(contact);
            }
            
            return list;
            
        } catch (SQLException e)
        {
            LOGGER.log(Level.SEVERE, "Error when working with DB", e);
        }
        return null;
    }
    
}
