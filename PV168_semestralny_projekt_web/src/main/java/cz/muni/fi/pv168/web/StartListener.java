package cz.muni.fi.pv168.web;

import cz.muni.fi.pv168.projekt.pv168_semestralny_projekt.GroupManager;
import cz.muni.fi.pv168.projekt.pv168_semestralny_projekt.ContactManager;
import cz.muni.fi.pv168.projekt.pv168_semestralny_projekt.ContactManagerImpl;
import cz.muni.fi.pv168.projekt.pv168_semestralny_projekt.GroupManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.commons.dbcp.BasicDataSource;


/**
 *
 * @author Martin Otahal
 */
@WebListener
public class StartListener implements ServletContextListener {

    final static Logger log = LoggerFactory.getLogger(StartListener.class);

    @Override
    public void contextInitialized(ServletContextEvent ev) {
        log.info("aplikace inicializována");
        ServletContext servletContext = ev.getServletContext();
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby://localhost:1527/skuska");
        bds.setUsername("martin");
        bds.setPassword("password");
        servletContext.setAttribute("GroupManager", new GroupManagerImpl(bds));
        servletContext.setAttribute("ContactManager", new ContactManagerImpl(bds));
        log.info("vytvořeny manažery");
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev) {
        log.info("aplikace končí");
    }
}
