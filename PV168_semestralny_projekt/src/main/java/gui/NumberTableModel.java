package gui;

import cz.muni.fi.pv168.projekt.pv168_semestralny_projekt.Contact;
import cz.muni.fi.pv168.projekt.pv168_semestralny_projekt.ContactManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;



public class NumberTableModel extends AbstractTableModel
{
    private static final Logger log = Logger.getLogger(NumberTableModel.class.getName());
    private ContactManager contactManager;
    private Contact contact;
    private List<String> types = new ArrayList<String>();
    private List<String> numbers = new ArrayList<String>();
    //private Map<String, String> numbers = new TreeMap<String, String>();
    private static enum COLUMNS {
        TYPE, NUMBER
    }
    
    public void setContactManager(ContactManager contactManager) {
        this.contactManager = contactManager;
    }

    @Override
    public int getRowCount() {
        return numbers.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.values().length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
	switch (columnIndex) {
	    case 0:
	    case 1:
		return String.class;
	    default:
		throw new IllegalArgumentException("columnIndex");
	}
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String type = types.get(rowIndex);
        String number = numbers.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return type;
            case 1:
                return number;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    public void addNumber(String type, String number) {
	types.add(type);
        numbers.add(number);
	fireTableDataChanged();
    }
    
    public void removeNumber(int index) {
	types.remove(index);
        numbers.remove(index);
	fireTableDataChanged();
    }
    
    public void clear() {
        types.clear();
        numbers.clear();
        fireTableDataChanged();
    }

     //TODO i18n
    @Override
    public String getColumnName(int columnIndex) {
	switch (COLUMNS.values()[columnIndex]) {
	    case TYPE:
		return "TYPE";//java.util.ResourceBundle.getBundle("cz/muni/fi/pv168/autorental/gui/Bundle").getString("cars_table_id");
	    case NUMBER:
		return "NUMBER";//java.util.ResourceBundle.getBundle("cz/muni/fi/pv168/autorental/gui/Bundle").getString("cars_table_model");
	    default:
		throw new IllegalArgumentException("columnIndex");
	}
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	switch (columnIndex) {
	    case 0:
	    case 1:
		return true;
	    default:
		throw new IllegalArgumentException("columnIndex");
	}
    }
}