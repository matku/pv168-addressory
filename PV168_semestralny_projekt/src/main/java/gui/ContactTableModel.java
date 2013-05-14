package gui;

import cz.muni.fi.pv168.projekt.pv168_semestralny_projekt.Contact;
import cz.muni.fi.pv168.projekt.pv168_semestralny_projekt.ContactManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;



public class ContactTableModel extends AbstractTableModel
{
    Locale locale_cs = new Locale("cs");
    Locale locale_en = new Locale("en");
    Locale locale_sk = new Locale("sk");
    
    private static final Logger log = Logger.getLogger(ContactTableModel.class.getName());
    private ContactManager contactManager;
    private List<Contact> contacts = new ArrayList<Contact>();
    private static enum COLUMNS {
        ID, NAME, SURNAME, ADDRESS
    }

    public void setContactManager(ContactManager contactManager) {
        this.contactManager = contactManager;
    }

    @Override
    public int getRowCount() {
        return contacts.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.values().length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
	switch (columnIndex) {
	    case 0:
		return Long.class;
	    case 1:
	    case 2:
	    case 3:
		return String.class;
	    default:
		throw new IllegalArgumentException("columnIndex");
	}
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Contact contact = contacts.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return contact.getId();
            case 1:
                return contact.getName();
            case 2:
                return contact.getSurname();
            case 3:
                return contact.getAddress();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    public void addContact(Contact contact) {
	contacts.add(contact);
	fireTableDataChanged();
    }
    
    public void removeContact(Contact contact) {
	contacts.remove(contact);
	fireTableDataChanged();
    }
    
    public void clear() {
	contacts.clear();
        fireTableDataChanged();
    }
    
     public List<Contact> getAllContacts() {
	return contacts;
    }

    @Override
    public String getColumnName(int columnIndex) {
	switch (COLUMNS.values()[columnIndex]) {
	    case ID:
		return java.util.ResourceBundle.getBundle("Bundle").getString("contacts_table_id");
	    case NAME:
		return java.util.ResourceBundle.getBundle("Bundle").getString("contacts_table_name");
	    case SURNAME:
		return java.util.ResourceBundle.getBundle("Bundle").getString("contacts_table_surname");
	    case ADDRESS:
		return java.util.ResourceBundle.getBundle("Bundle").getString("contacts_table_address");
	    default:
		throw new IllegalArgumentException("columnIndex");
	}
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	Contact contact = contacts.get(rowIndex);
	switch (COLUMNS.values()[columnIndex]) {
	    case NAME:
		contact.setName((String) aValue);
		break;
	    case SURNAME:
		contact.setSurname((String) aValue);
		break;
	    case ADDRESS:
		contact.setAddress((String) aValue);
		break;
	    default:
		throw new IllegalArgumentException("columnIndex");
	}
        try {
            contactManager.editContact(contact);
            fireTableDataChanged();
        } catch (Exception ex) {
            String msg = "User request failed";
            log.log(Level.INFO, msg);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	switch (columnIndex) {
	    case 1:
	    case 2:
            case 3:
	    case 0:
		return false;
	    default:
		throw new IllegalArgumentException("columnIndex");
	}
    }
}