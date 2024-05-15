import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConnector implements ServletContextListener {

    private String host, userName, dbname, password;;
    private Integer port;
    public void contextInitialized(ServletContextEvent event) {
        Yaml yaml = new Yaml();
        try {
            HashMap mp = yaml.loadAs(new FileReader("D:\\Intellij Projects\\QnA\\src\\main\\resources\\config.yaml"), Map.class);
            setDBdata(mp);
        }
        catch (Exception e) {
            System.out.println("Config issue.");
            e.printStackTrace();
        }
        try {
            String url = "jdbc:postgresql://" + host+ ":" + port + "/" + dbname;
            String userName = this.userName;
            String password = this.password;

            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(url, userName, password);

            ServletContext servletContext = event.getServletContext();
            servletContext.setAttribute("dbCon", connection);
        } catch (Exception e) {
            System.out.println("Database connection issue.");
            e.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Project undeployed!");
    }

    private void setDBdata(HashMap mp) {
        this.host = (String)mp.get("host");
        this.port = (Integer)mp.get("port");
        this.dbname = (String)mp.get("dbname");
        this.userName = (String) mp.get("userName");
        this.password = (String) mp.get("password");
        //System.out.println(host + " " + port + " " + dbname + " " + userName + " " + password);
    }
}
