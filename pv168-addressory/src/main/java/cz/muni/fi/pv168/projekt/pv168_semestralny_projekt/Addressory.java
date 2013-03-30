/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

import java.util.Map;
import java.util.TreeMap;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author Martin Otahal
 */
public class Addressory 
{
    private static DataSource ds;
    
    public static void main(String[] args) 
    {
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby://localhost:1527/skuska");
        bds.setUsername("martin");
        bds.setPassword("password");
        ds = bds;
        ContactManagerImpl mngr = new ContactManagerImpl(ds);
        /*Contact cont = mngr.findContactByID(110l);
        if (cont != null) 
        {
            System.out.println(cont.toString());
        }*/
        
    }
    
}
