package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

import java.util.List;



/**
 *
 * @author Martin Otahal
 */
public interface ContactManager 
{
    void newContact(Contact contact);
    void editContact(Contact contact);
    void deleteContact(Contact contact);
    Contact findContactByID(Long id);
    Contact findContactByName(String name);
    List<Contact> findAllContacts();
}
