package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

/**
 *
 * @author Martin Otahal
 */
public class Entry 
{
    private Long contactID;
    private Long groupID;

    public Entry() {
    }

    
    
    
    public Long getContactID() {
        return contactID;
    }

    public void setContactID(Long contactID) {
        this.contactID = contactID;
    }

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    
    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.contactID != null ? this.contactID.hashCode() : 0);
        hash = 97 * hash + (this.groupID != null ? this.groupID.hashCode() : 0);
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
        final Entry other = (Entry) obj;
        if (this.contactID != other.contactID && (this.contactID == null || !this.contactID.equals(other.contactID))) {
            return false;
        }
        if (this.groupID != other.groupID && (this.groupID == null || !this.groupID.equals(other.groupID))) {
            return false;
        }
        return true;
    }

    
    
    
    @Override
    public String toString() {
        return "Entry{" + "contactID=" + contactID + ", groupID=" + groupID + '}';
    }
    
    
}
