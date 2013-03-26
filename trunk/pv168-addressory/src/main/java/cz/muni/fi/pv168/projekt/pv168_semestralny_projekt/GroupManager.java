package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

import java.util.List;

/**
 *
 * @author Martin Otahal
 */
public interface GroupManager 
{
    void newGroup(Group group);
    void editGroup(Group group);
    void deleteGroup(Group group);
    Group findGroupByID(Long id);
    Group findGroupByType(GroupType type);
    List<Group> findAllGroups();
}
