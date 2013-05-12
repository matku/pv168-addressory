package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

import java.util.List;

/**
 *
 * @author Martin Otahal
 */
public interface GroupManager 
{
    void newGroup(Group group) throws AppException;
    void editGroup(Group group) throws AppException;
    void deleteGroup(Group group) throws AppException;
    Group findGroupByID(Long id) throws AppException;
    Group findGroupByType(GroupType type) throws AppException;
    List<Group> findAllGroups() throws AppException;
}
