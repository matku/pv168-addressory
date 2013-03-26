package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

import java.util.Map;

/**
 *
 * @author Martin Otahal
 */
public class Contact 
{
    private Long id;
    private String name;
    private String surname;
    private String address;
    private Map<String, NumberType> phoneNumbers;

    public Contact() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, NumberType> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Map<String, NumberType> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Contact other = (Contact) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Contact{" + "id=" + id + ", name=" + name + ", surname=" + surname + ", address=" + address + ", phoneNumbers=" + phoneNumbers + '}';
    }
    
    
}
