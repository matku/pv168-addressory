/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

import java.util.Map;
import java.util.TreeMap;
import javax.sql.DataSource;

/**
 *
 * @author Martin Otahal
 */
public class Addressory 
{
    public static void main(String[] args) 
    {
        ContactManagerImpl mngr = new ContactManagerImpl();
        Contact cont = mngr.findContactByID(110l);
        if (cont != null) System.out.println(cont.toString());
        
    }
    
}
