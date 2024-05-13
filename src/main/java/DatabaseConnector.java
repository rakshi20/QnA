import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnector implements ServletContextListener {

    private final String host = System.getProperty("PGHOST");
    private final String port = System.getProperty("PGPORT");
    private final String userName = System.getProperty("POSTGRES_USER");
    private final String name = System.getProperty("PGDATABASE");
    private final String password = System.getProperty("POSTGRES_PASSWORD");
    public void contextInitialized(ServletContextEvent event) {
        try {
            String url = "jdbc:postgresql://" + host+ ":" + port + "/" + name;
            String userName = this.userName;
            String password = this.password;

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
