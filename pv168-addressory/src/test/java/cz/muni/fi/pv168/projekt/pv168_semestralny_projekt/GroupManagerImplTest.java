/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author lencii
 */
public class GroupManagerImplTest {
    private GroupManagerImpl manager;
    private DataSource ds;
    
    @Before
    public void setUp() {
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby://localhost:1527/skuska");
        bds.setUsername("martin");
        bds.setPassword("password");
        ds = bds;
        manager = new GroupManagerImpl(ds);
        manager.deleteAllGroups();
    }
    
    @After
    public void tearDown() {
        manager.deleteAllGroups();
    }
    
    @Test
    public void testFindAllGroups() 
    {
        

        Group g1 = newGroup(GroupType.WORK, "Pracovne");
        Group g2 = newGroup(GroupType.FAMILY, "Rodina");
        
        manager.newGroup(g1);
        manager.newGroup(g2);

        List<Group> expected = Arrays.asList(g1,g2);
        List<Group> actual = manager.findAllGroups();

        Collections.sort(actual,idComparator);
        Collections.sort(expected,idComparator);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }
    
    /**
     *
     */
    @Test
    public void testNewGroup() 
    {
        Group group = newGroup(GroupType.FRIENDS, "Priatelia");
        manager.newGroup(group);

        Long groupId = group.getId();
        assertNotNull(groupId);
        Group result = manager.findGroupByID(groupId);
        assertEquals(group, result);
        assertNotSame(group, result);
        assertDeepEquals(group, result);
    }
    
    @Test
    public void testEditGroup() 
    {   
        
        Group group = newGroup(GroupType.OTHERS, "Ostatni");
        manager.newGroup(group);
        
        group = newGroup(null,"xxx");
        try
        {          
            //group.setType(null);
            manager.editGroup(group);
            //fail();
        }
        catch (IllegalArgumentException e)
        {
            //OK
        }
    }
    
    /**
     *
     */
    @Test
    public void testDeleteGroup() 
    {
       
        Group group = newGroup(GroupType.OTHERS, "Ostatni");
        manager.newGroup(group);
        manager.deleteGroup(group);
      
        group = manager.findGroupByType(GroupType.OTHERS);
        
        
        assertNull(group);
    }
    
    @Test
    public void testFindGroupByID() 
    {        
        
        
        Group group = newGroup(GroupType.OTHERS,"Ostatni");
        manager.newGroup(group);
        Long groupId = group.getId();

        Group result = manager.findGroupByID(groupId);
        assertEquals(group, result);
        assertDeepEquals(group, result);
    }
    
    @Test
    public void testFindGroupByType() 
    {     
 
        Group group = newGroup(GroupType.OTHERS,"Ostatni");
        manager.newGroup(group);
        Group group2 = manager.findGroupByType(GroupType.OTHERS);
        
        assertEquals(group, group2);
    }
    
    @Test
    public void addGroupWithWrongAttributes()
    {
        try {
            manager.newGroup(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        Group group = newGroup(GroupType.FRIENDS, "Kamosi");
        
        group.setId(1l);
        try {
            manager.newGroup(group);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        group = newGroup(null, "zle");
        try {
            manager.newGroup(group);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        

    }
    
    private static Group newGroup(GroupType type, String note) 
    {
        Group group = new Group();
        group.setNote(note);
        group.setType(type);
       
        return group;
    }
    
    private static Comparator<Group> idComparator = new Comparator<Group>() {

        @Override
        public int compare(Group o1, Group o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
    
    private void assertDeepEquals(List<Group> expectedList, List<Group> actualList) 
    {
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

}
