import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

public class DatabaseConnector implements ServletContextListener {

    private String host, userName, dbname, password;;
    private Integer port;
    public void contextInitialized(ServletContextEvent event) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
//            HashMap mp = objectMapper.readValue(new URL("file:D:\\Intellij Projects\\QnA\\src\\main\\resources\\config.yaml"), HashMap.class);
//            setDBdata(mp);
//        }
//        catch (Exception e) {
//            System.out.println("Config issue.");
//            e.printStackTrace();
//        }
        try {
            //String url = "jdbc:postgresql://" + host+ ":" + port + "/" + dbname;
            String url = System.getenv("pgUrl");
            String userName = System.getenv("userName");
            String password = System.getenv("password");

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
