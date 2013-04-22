/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

import cz.muni.fi.pv168.projekt.commons.DBUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import static org.junit.Assert.*;

/**
 *
 * @author Martin Otahal
 */
public class ContactsManagerImplTest 
{
    
    private ContactManagerImpl contactManager;
    private ContactsManagerImpl manager;
    private GroupManagerImpl groupManager;
    private static DataSource ds;
    
    private static DataSource prepareDataSource() throws SQLException 
    {
        BasicDataSource bds = new BasicDataSource();
        //we will use in memory database
//        bds.setUrl("jdbc:derby:memory:contactmgr-test;create=true");
        bds.setUrl("jdbc:derby://localhost:1527/skuska");
        bds.setUsername("martin");
        bds.setPassword("password");
        return bds;
    }
    
    /*@Before
    public void setUp() 
    {
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby://localhost:1527/skuska");
        bds.setUsername("martin");
        bds.setPassword("password");
        ds = bds;
        manager = new ContactsManagerImpl(ds);
        contactManager = new ContactManagerImpl(ds);
        groupManager = new GroupManagerImpl(ds);
    }*/
    
//    @BeforeClass
//    public static void setClassUp() throws SQLException
//    {
//        ds = prepareDataSource();
////        DBUtils.createTables(ds);
////        DBUtils.executeSqlScript(ds,Addressory.class.getResource("createTables.sql"));
//    }
    
    @Before
    public void setUp() throws SQLException
    {
        ds = prepareDataSource();
        manager = new ContactsManagerImpl(ds);
        contactManager = new ContactManagerImpl(ds);
        groupManager = new GroupManagerImpl(ds);
        //DBUtils.createTables(ds);
        DBUtils.executeSqlScript(ds,Addressory.class.getResource("/createTables.sql"));
    }
    
    @After
    public void tearDown() throws SQLException 
    {
        //DBUtils.deleteTables(ds);
//        DBUtils.deleteFromTables(ds);
        DBUtils.executeSqlScript(ds,Addressory.class.getResource("/dropTables.sql"));
    }
    
//    @AfterClass
//    public static void tearClassDown() throws SQLException
//    {
////        DBUtils.deleteTables(ds);
////        DBUtils.executeSqlScript(ds,Addressory.class.getResource("deleteTables.sql"));
//    }
    
    /**
     * Test of removeContactFromGroup method, of class ContactsManagerImpl.
     */
    @Test
    public void testRemoveContactFromGroup() throws AppException 
    {
        Group group = newGroup(GroupType.FRIENDS, "Priatelia");
        groupManager.newGroup(group);
        Contact contact = newContact("Matus", "Kralik", "Trybova 3", newNumbers(0));
        contactManager.newContact(contact);
        manager.addContactToGroup(contact, group);
        manager.removeContactFromGroup(contact, group);
        assertDeepEqualsContacts(new ArrayList<Contact>(), manager.findAllContactsInGroup(group));
        assertNull(manager.findGroupWithContact(contact));
    }
    
    /**
     * Test of findGroupWithContact method, of class ContactsManagerImpl.
     */
    @Test
    public void testFindGroupWithContact() throws AppException {
        Group group = newGroup(GroupType.FRIENDS, "Priatelia");
        groupManager.newGroup(group);
        Contact contact = newContact("Matus", "Kralik", "Trybova 3", newNumbers(0));
        contactManager.newContact(contact);
        manager.addContactToGroup(contact, group);
        assertDeepEquals(group, manager.findGroupWithContact(contact));
        manager.removeContactFromGroup(contact, group);
    }
    
    /**
     * Test of findAllContactsInGroup method, of class ContactsManagerImpl.
     */
    @Test
    public void testFindAllContactsInGroup() throws AppException 
    {
        Group group = newGroup(GroupType.FRIENDS, "Priatelia");
        groupManager.newGroup(group);
        Contact c1 = newContact("Matus", "Kralik", "Trybova 3", newNumbers(0));
        Contact c2 = newContact("Daniel", "Fuhrman", "Trybova 3", newNumbers(1));
        contactManager.newContact(c1);
        contactManager.newContact(c2);
        
        manager.addContactToGroup(c1, group);
        manager.addContactToGroup(c2, group);
        
        List<Contact> expected = Arrays.asList(c1,c2);
        List<Contact> actual = manager.findAllContactsInGroup(group);
        
        Collections.sort(actual,idContactComparator);
        Collections.sort(expected,idContactComparator);
        
        assertDeepEqualsContacts(expected, actual);
    }

    /**
     * Test of addContactToGroup method, of class ContactsManagerImpl.
     */
    @Test
    public void testAddContactToGroup() throws AppException 
    {
        Group group = newGroup(GroupType.FRIENDS, "Priatelia");
        groupManager.newGroup(group);
        Contact contact = newContact("Matus", "Kralik", "Trybova 3", newNumbers(0));
        contactManager.newContact(contact);
        manager.addContactToGroup(contact, group);
        assertDeepEquals(group, manager.findGroupWithContact(contact));
        List<Contact> list = new ArrayList<Contact>();
        list.add(contact);
        assertDeepEqualsContacts(manager.findAllContactsInGroup(group), list);
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

    private static Group newGroup(GroupType type, String note) 
    {
        Group group = new Group();
        group.setNote(note);
        group.setType(type);
       
        return group;
    }
    
    private static Comparator<Group> idComparatorGroup = new Comparator<Group>() {


        @Override
        public int compare(Group o1, Group o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
    
    private void assertDeepEqualsGroups(List<Group> expectedList, List<Group> actualList) 
    {
        assertEquals(expectedList.size(), actualList.size());
        for (int i = 0; i < expectedList.size(); i++) 
        {
            Group expected = expectedList.get(i);
            Group actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }
    
    private void assertDeepEquals(Group expected, Group actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getNote(), actual.getNote());
        
    }
    
    
    private void assertDeepEqualsContacts(List<Contact> expectedList, List<Contact> actualList) 
    {
        assertEquals(expectedList.size(), actualList.size());
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

    private static Comparator<Contact> idContactComparator = new Comparator<Contact>() {

        @Override
        public int compare(Contact o1, Contact o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
}   
