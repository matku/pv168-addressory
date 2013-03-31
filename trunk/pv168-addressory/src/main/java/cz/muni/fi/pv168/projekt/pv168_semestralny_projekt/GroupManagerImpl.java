package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author Martin Otahal
 */
public class GroupManagerImpl implements GroupManager {

    private static final Logger LOGGER = Logger.getLogger(Addressory.class.getName());
    private DataSource ds;

    public GroupManagerImpl(DataSource ds) {
        if (ds == null) {
            throw new IllegalArgumentException("data source can not be null");
        }
        this.ds = ds;
    }

    public void setDataSource(DataSource ds) {
        if (ds == null) {
            throw new IllegalArgumentException("data source can not be null");
        }
        this.ds = ds;
    }

    @Override
    public void newGroup(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("group must not be null");
        }
        if (group.getId() != null) {
            throw new IllegalArgumentException("group is already in the database");
        }
        validateGroup(group);

        try (
                Connection conn = ds.getConnection();
                PreparedStatement st1 = conn.prepareStatement(
                "INSERT INTO groups (type, note) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS);) {
            //st1.setLong(1, group.getId());
            st1.setString(1, group.getType().name());
            st1.setString(2, group.getNote());
            int count = st1.executeUpdate();
            assert count == 1;              //other than 1 line was created

            ResultSet keys = st1.getGeneratedKeys();
            Long id;
            if (keys.next()) {
                id = keys.getLong(1);
                group.setId(id);
            } else {
                throw new RuntimeException("did not add to the database properly"
                        + "or did not return ID");
            }


        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error when inserting group into DB", e);
            throw new RuntimeException("Error when inserting into DB", e);
        }
    }

    @Override
    public void editGroup(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("group must not be null");
        }
        if (group.getId() == null) {
            throw new IllegalArgumentException("group is not in the database");
        }

        validateGroup(group);

        try (
                Connection conn = ds.getConnection();
                PreparedStatement st1 = conn.prepareStatement(
                "UPDATE groups SET type = ?, note = ? WHERE id = ?");) {
            st1.setString(1, group.getType().name());
            st1.setString(2, group.getNote());
            st1.setLong(3, group.getId());
            int count = st1.executeUpdate();
            assert count == 1;              //other than 1 line was updated


        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error when inserting group into DB", e);
            throw new RuntimeException("Error when inserting into DB", e);
        }
    }

    @Override
    public void deleteGroup(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("group must not be null");
        }
        if (group.getId() == null) {
            throw new IllegalArgumentException("group not in database");
        }

        try (
                Connection conn = ds.getConnection();
                PreparedStatement st1 = conn.prepareStatement(
                "DELETE FROM groups WHERE id = ?");) {
            st1.setLong(1, group.getId());
            int count = st1.executeUpdate();
            assert count == 1;                  //should delete only one contact
        } catch (SQLException e) {
            String msg = "Removing group failed.";
            LOGGER.log(Level.SEVERE, msg, e);
        }
    }

    @Override
    public Group findGroupByID(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be initialized and greater then 0");
        }

        Group group = new Group();

        try (
                Connection conn = ds.getConnection();
                PreparedStatement st1 = conn.prepareStatement(
                "SELECT * FROM groups WHERE id = ?");) {
            st1.setLong(1, id);
            ResultSet rs = st1.executeQuery();
            if (rs.next()) {
                group.setId(rs.getLong(1));
                group.setNote(rs.getString(3));
                if (rs.getString(2).equals("FAMILY")) {
                    group.setType(GroupType.FAMILY);
                }
                if (rs.getString(2).equals("WORK")) {
                    group.setType(GroupType.WORK);
                }
                if (rs.getString(2).equals("FRIENDS")) {
                    group.setType(GroupType.FRIENDS);
                }
                if (rs.getString(2).equals("OTHERS")) {
                    group.setType(GroupType.OTHERS);
                }

            } else {
                return null;
            }


            return group;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error when selecting group from DB", e);
            throw new RuntimeException("Error when selecting from DB", e);
        }
    }

    @Override
    public Group findGroupByType(GroupType type) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null or empty string");
        }

        Group group = new Group();


        try (
                Connection conn = ds.getConnection();
                PreparedStatement st1 = conn.prepareStatement(
                "SELECT * FROM groups WHERE type = ?");) {

            st1.setString(1, type.name());



            ResultSet rs = st1.executeQuery();
            if (rs.next()) {
                group.setId(rs.getLong(1));
                group.setNote(rs.getString(3));
                if (rs.getString(2).equals("FAMILY")) {
                    group.setType(GroupType.FAMILY);
                }
                if (rs.getString(2).equals("WORK")) {
                    group.setType(GroupType.WORK);
                }
                if (rs.getString(2).equals("FRIENDS")) {
                    group.setType(GroupType.FRIENDS);
                }
                if (rs.getString(2).equals("OTHERS")) {
                    group.setType(GroupType.OTHERS);
                }

            } else {
                return null;
            }


            return group;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error when selecting group from DB", e);
            throw new RuntimeException("Error when selecting from DB", e);
        }
    }

    @Override
    public List<Group> findAllGroups() {
        List<Group> list = new ArrayList();
        try (
                Connection conn = ds.getConnection();
                PreparedStatement st1 = conn.prepareStatement(
                "SELECT * FROM groups");) {
            ResultSet rs = st1.executeQuery();
            while (rs.next()) {
                Group group = new Group();
                group.setNote(rs.getString(3));
                group.setId(rs.getLong(1));

                if (rs.getString(2).equals("FAMILY")) {
                    group.setType(GroupType.FAMILY);
                }
                if (rs.getString(2).equals("WORK")) {
                    group.setType(GroupType.WORK);
                }
                if (rs.getString(2).equals("FRIENDS")) {
                    group.setType(GroupType.FRIENDS);
                }
                if (rs.getString(2).equals("OTHERS")) {
                    group.setType(GroupType.OTHERS);
                }
                list.add(group);
            }



        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error when selecting group from DB", e);
            throw new RuntimeException("Error when selecting from DB", e);
        }

        return list;
    }

    private boolean validateGroup(Group group) {
        if (group.getType() == null) {
            throw new IllegalArgumentException("group must not be null");
        }

        return true;
    }

    public void deleteAllGroups() {

        try {
            Connection conn = ds.getConnection();
            conn = ds.getConnection();
            PreparedStatement st1 = conn.prepareStatement(
                    "DELETE FROM groups");
            {
                st1.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(GroupManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
