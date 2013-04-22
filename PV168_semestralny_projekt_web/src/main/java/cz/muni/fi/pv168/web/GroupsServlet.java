package cz.muni.fi.pv168.web;

import cz.muni.fi.pv168.projekt.pv168_semestralny_projekt.AppException;
import cz.muni.fi.pv168.projekt.pv168_semestralny_projekt.Group;
import cz.muni.fi.pv168.projekt.pv168_semestralny_projekt.GroupManager;
import cz.muni.fi.pv168.projekt.pv168_semestralny_projekt.GroupManagerImpl;
import cz.muni.fi.pv168.projekt.pv168_semestralny_projekt.GroupType;
import static cz.muni.fi.pv168.web.GroupsServlet.URL_MAPPING;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author Martin Otahal
 */
@WebServlet(GroupsServlet.URL_MAPPING + "/*")
public class GroupsServlet extends HttpServlet 
{
    BasicDataSource bds = new BasicDataSource();
    GroupManager groupManager = null;

    private static final String LIST_JSP = "/list.jsp";
    public static final String URL_MAPPING = "/groups";

    private final static Logger log = LoggerFactory.getLogger(GroupsServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        setConnectionAndManager();
        showGroupsList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        setConnectionAndManager();
        request.setCharacterEncoding("utf-8");
        //action acording to URL
        String action = request.getPathInfo();
        switch (action) {
            case "/add":
                //get POST params from form
                String type = request.getParameter("type");
                String note = request.getParameter("note");
                //checking of required attributes
                if (type == null || type.length() == 0) {
                    request.setAttribute("chyba", "Je nutné vyplnit typ !");
                    showGroupsList(request, response);
                    return;
                }
                try {
                    Group group = new Group();
                    group.setNote(note);
                    //validating type attribute against list of valid types
                    try
                    {
                        group.setType(Enum.valueOf(GroupType.class, type));
                    } catch (IllegalArgumentException e)
                    {
                        log.error("No enum value!", e);
                        request.setAttribute("chyba", "Je nutné vyplnit validni typ !");
                        showGroupsList(request, response);
                        return;
                    }
                    groupManager.newGroup(group);
                    log.debug("created {}",group);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (AppException e) 
                {
                    log.error("Cannot add group", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    groupManager.deleteGroup(groupManager.findGroupByID(id));
                    log.debug("deleted group {}",id);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (AppException e) {
                    log.error("Cannot delete group", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            /*case "/update":
                //TODO
                return;*/
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    private void showGroupsList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException 
    {
        try {
            request.setAttribute("groups", groupManager.findAllGroups());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (AppException e) {
            log.error("Cannot show groups", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    private void setConnectionAndManager()
    {
        bds.setDriverClassName("org.apache.derby.jdbc.ClientDriver");
        bds.setUrl("jdbc:derby://localhost:1527/skuska");
        bds.setUsername("martin");
        bds.setPassword("password");
        if (groupManager == null)
        {
            groupManager = new GroupManagerImpl(bds);
        }
    }
}