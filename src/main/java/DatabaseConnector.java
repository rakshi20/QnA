import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnector implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        try {
            String url = "jdbc:postgresql://localhost:5432/QnA";
            String userName = "admin";
            String password = "password";

            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(url, userName, password);

            ServletContext servletContext = event.getServletContext();
            servletContext.setAttribute("dbCon", connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Project undeployed!");
    }
}
