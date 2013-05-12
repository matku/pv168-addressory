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
    public void newGroup(Group group) throws AppException
    {
        if (group == null) {
            throw new IllegalArgumentException("group must not be null");
        }
        if (group.getId() != null) {
            throw new IllegalArgumentException("group is already in the database");
        }
        validateGroup(group);
        Connection conn = null;

        try {
            conn = ds.getConnection();
            try (
                    PreparedStatement st1 = conn.prepareStatement(
                    "INSERT INTO groups (type, note) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);) {

                conn.setAutoCommit(false);
                st1.setString(1, group.getType().name());
                st1.setString(2, group.getNote());
                int count = st1.executeUpdate();
                assert count == 1;

                ResultSet keys = st1.getGeneratedKeys();
                Long id;
                if (keys.next()) {
                    id = keys.getLong(1);
                    group.setId(id);
                } else {
                    throw new RuntimeException("did not add to the database properly"
                            + "or did not return ID");
                }

                conn.commit();
                conn.setAutoCommit(true);

            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                LOGGER.log(Level.SEVERE, "Error when inserting group into DB", e);
                throw new AppException("Error when inserting into DB", e);
            }
        } catch (SQLException e) {
            String msg = "Error when setting connection";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new AppException(msg, e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error when closing connection", ex);
                    throw new AppException("Error when closing connection", ex);
                }
            }
        }
    }

    @Override
    public void editGroup(Group group) throws AppException
    {
        if (group == null) {
            throw new IllegalArgumentException("group must not be null");
        }
        if (group.getId() == null) {
            throw new IllegalArgumentException("group is not in the database");
        }

        validateGroup(group);
        Connection conn = null;

        try {
            conn = ds.getConnection();

            try (
                    PreparedStatement st1 = conn.prepareStatement(
                    "UPDATE groups SET type = ?, note = ? WHERE id = ?");) {

                conn.setAutoCommit(false);
                st1.setString(1, group.getType().name());
                st1.setString(2, group.getNote());
                st1.setLong(3, group.getId());
                int count = st1.executeUpdate();
                assert count == 1;              //other than 1 line was updated
                conn.commit();
                conn.setAutoCommit(true);

            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);

                LOGGER.log(Level.SEVERE, "Error when inserting group into DB", e);
                throw new AppException("Error when inserting into DB", e);
            }
        } catch (SQLException e) {
            String msg = "Error when setting connection";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new AppException(msg, e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error when closing connection", ex);
                    throw new AppException("Error when closing connection", ex);
                }
            }
        }

    }

    @Override
    public void deleteGroup(Group group) throws AppException
    {
        if (group == null) {
            throw new IllegalArgumentException("group must not be null");
        }
        if (group.getId() == null) {
            throw new IllegalArgumentException("group not in database");
        }

        Connection conn = null;

        try {
            conn = ds.getConnection();
            try (
                    PreparedStatement st1 = conn.prepareStatement(
                    "DELETE FROM groups WHERE id = ?");) {
                conn.setAutoCommit(false);
                st1.setLong(1, group.getId());
                int count = st1.executeUpdate();
                assert count == 1;
                conn.commit();
                conn.setAutoCommit(true);

            } catch (SQLException e) {

                conn.rollback();
                conn.setAutoCommit(true);

                String msg = "Removing group failed.";
                LOGGER.log(Level.SEVERE, msg, e);
                throw new AppException(msg, e);
            }
        } catch (SQLException e) {
            String msg = "Error when setting connection";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new AppException(msg, e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error when closing connection", ex);
                    throw new AppException("Error when closing connection", ex);
                }
            }
        }

    }

    @Override
    public Group findGroupByID(Long id) throws AppException
    {
        if (id == null) {
            throw new IllegalArgumentException("id must be initialized and greater then 0");
        }

        Group group = new Group();

        try (
                Connection conn = ds.getConnection();
                PreparedStatement st1 = conn.prepareStatement(
                "SELECT * FROM groups WHERE id = ?");) {

            conn.setAutoCommit(false);
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

            conn.commit();
            return group;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error when selecting group from DB", e);
            throw new AppException("Error when selecting from DB", e);
        }
    }

    @Override
    public Group findGroupByType(GroupType type) throws AppException
    {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null or empty string");
        }

        Group group = new Group();

        try (
                Connection conn = ds.getConnection();
                PreparedStatement st1 = conn.prepareStatement(
                "SELECT * FROM groups WHERE type = ?");) {

            conn.setAutoCommit(false);
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

            conn.commit();
            return group;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error when selecting group from DB", e);
            throw new AppException("Error when selecting from DB", e);
        }
    }

    @Override
    public List<Group> findAllGroups() throws AppException
    {
        List<Group> list = new ArrayList();
        try (
                Connection conn = ds.getConnection();
                PreparedStatement st1 = conn.prepareStatement(
                "SELECT * FROM groups");) {
            conn.setAutoCommit(false);
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

            conn.commit();
            return list;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error when selecting group from DB", e);
            throw new AppException("Error when selecting from DB", e);
        }

    }

    private boolean validateGroup(Group group) throws AppException {
        if (group.getType() == null) {
            throw new AppException("group must not be null");
        }

        return true;
    }

    public void deleteAllGroups() 
    {

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
