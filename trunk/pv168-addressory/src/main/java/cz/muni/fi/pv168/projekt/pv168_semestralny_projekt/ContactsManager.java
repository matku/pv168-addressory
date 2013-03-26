package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

import java.util.List;

/**
 *
 * @author Martin Otahal
 */
public interface ContactsManager 
{
    void addContactToGroup(Contact contact, Group group);
    void removeContactFromGroup(Contact contact, Group group);
    Group findGroupWithContact(Contact contact);
    List<Contact> findAllContactsInGroup(Group group);
}
