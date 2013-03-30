package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

import static org.junit.Assert.*;


/**
 *
 * @author Martin Otahal
 */
public class ContactManagerImplTest
{
    
    private ContactManagerImpl manager;
    private DataSource ds;
    
    @Before
    public void setUp() {
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby://localhost:1527/skuska");
        bds.setUsername("martin");
        bds.setPassword("password");
        ds = bds;
        manager = new ContactManagerImpl(ds);
    }
    
    /**
     * Test of findAllContacts method, of class ContactManagerImpl.
     */
    @Test
    public void testFindAllContacts() 
    {
        assertTrue(manager.findAllContacts().isEmpty());

        Contact c1 = newContact("Matus", "Kralik", "Trybova 3", newNumbers(0));
        Contact c2 = newContact("Daniel", "Fuhrman", "Trybova 3", newNumbers(1));
        
        manager.newContact(c1);
        manager.newContact(c2);

        List<Contact> expected = Arrays.asList(c1,c2);
        List<Contact> actual = manager.findAllContacts();

        Collections.sort(actual,idComparator);
        Collections.sort(expected,idComparator);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }

    /**
     * Test of newContact method, of class ContactManagerImpl.
     */
    @Test
    public void testNewContact() 
    {
        Contact contact = newContact("Matus", "Kralik", "Trybova 3", newNumbers(0));
        manager.newContact(contact);

        Long contactId = contact.getId();
        assertNotNull(contactId);
        Contact result = manager.findContactByID(contactId);
        assertEquals(contact, result);
        assertNotSame(contact, result);
        assertDeepEquals(contact, result);
    }

    /**
     * Test of editContact method, of class ContactManagerImpl.
     */
    @Test
    public void testEditContact() 
    {
        Contact contact = newContact("Matus", "Kralik", "Trybova 3", newNumbers(0));
        manager.newContact(contact);
        
        contact = manager.findContactByName("Matus Kralik");
        try
        {
            //contact.setId(10l);
            contact.setName(null);
            manager.editContact(contact);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            //OK
        }
    }

    /**
     * Test of deleteContact method, of class ContactManagerImpl.
     */
    @Test
    public void testDeleteContact() 
    {
        Contact contact = newContact("Ivan", "Tkac", "Trybova 3", newNumbers(0));
        manager.newContact(contact);
        manager.deleteContact(contact);
        contact = manager.findContactByName("Ivan Tkac");
        assertNull(contact);
    }

    /**
     * Test of findContactByID method, of class ContactManagerImpl.
     */
    @Test
    public void testFindContactByID() 
    {        
        assertNull(manager.findContactByID(1l));
        
        Contact contact = newContact("Matus", "Kralik", "Trybova 3", newNumbers(0));
        manager.newContact(contact);
        Long contactId = contact.getId();

        Contact result = manager.findContactByID(contactId);
        assertEquals(contact, result);
        assertDeepEquals(contact, result);
    }

    /**
     * Test of findContactByName method, of class ContactManagerImpl.
     */
    /*@Test
    public void testFindContactByName() 
    {
        String name = "";
        ContactManagerImpl instance = new ContactManagerImpl();
        Contact expResult = null;
        Contact result = instance.findContactByName(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    
    
    @Test
    public void addContactWithWrongAttributes()
    {
        try {
            manager.newContact(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        Contact contact = newContact("Matus", "Kralik", "Trybova 3", newNumbers(0));
        
        contact.setId(1l);
        try {
            manager.newContact(contact);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        contact = newContact(null, "Kralik", "Trybova 3", newNumbers(0));
        try {
            manager.newContact(contact);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        contact = newContact("", "Kralik", "Trybova 3", newNumbers(0));
        try {
            manager.newContact(contact);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        contact = newContact("Matus", null, "Trybova 3", newNumbers(0));
        try {
            manager.newContact(contact);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        contact = newContact("Matus", "", "Trybova 3", newNumbers(0));
        try {
            manager.newContact(contact);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        contact = newContact("Matus", "Kralik", "Trybova 3", null);
        try {
            manager.newContact(contact);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        //these variants should be ok
        contact = newContact("Matus", "Kralik", null, newNumbers(0));
        manager.newContact(contact);
        Contact result = manager.findContactByID(contact.getId()); 
        assertNotNull(result);
        assertNull(result.getAddress());

        contact = newContact("Matus", "Kralik", "Trybova 3", new TreeMap<String, NumberType>());
        manager.newContact(contact);
        result = manager.findContactByID(contact.getId()); 
        assertNotNull(result);
    }
    
    private static Contact newContact(String name, String surname, String address, Map<String, NumberType> phoneNumbers) 
    {
        Contact contact = new Contact();
        contact.setAddress(address);
        contact.setName(name);
        contact.setPhoneNumbers(phoneNumbers);
        contact.setSurname(surname);
        return contact;
    }
    
    private static Map<String, NumberType> newNumbers(int x)
    {
        Map map = new TreeMap<String, NumberType>();
        for (int i = x; i < 3; i++)
        {
            map.put("" + i + i + i + i + i + i + i + i + i, NumberType.WORK);
        }
        for (int i = x+3; i < 6; i++)
        {
            map.put("" + i + i + i + i + i + i + i + i + i, NumberType.MOBILE);
        }
        for (int i = x+6; i < 9; i++)
        {
            map.put("" + i + i + i + i + i + i + i + i + i, NumberType.HOME);
        }
        for (int i = x; i < 3; i++)
        {
            map.put("" + i + (i+1) + (i+1) + (i+2) + (i+3) + (i+3) + (i+4) + i, NumberType.FAX);
        }
        return map;
    }

    private void assertDeepEquals(List<Contact> expectedList, List<Contact> actualList) 
    {
        for (int i = 0; i < expectedList.size(); i++) 
        {
            Contact expected = expectedList.get(i);
            Contact actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEquals(Contact expected, Contact actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getSurname(), actual.getSurname());
        assertEquals(expected.getPhoneNumbers(), actual.getPhoneNumbers());
    }

    private static Comparator<Contact> idComparator = new Comparator<Contact>() {

        @Override
        public int compare(Contact o1, Contact o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
}
