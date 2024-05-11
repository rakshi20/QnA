import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class PrintDataServlet extends HttpServlet  {

    private ServletContext ctx;

    private PrintWriter out;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        out = response.getWriter();
        ctx = getServletContext();
        out.println("Welcome to " + ctx.getInitParameter("projectName") + "<br/>");
        print("public", "user");
        print("public", "question");
        print("public", "answer");
    }

    public void print(String schemaName, String tableName) {
        String queryString = "select * from " + schemaName + "." + tableName;
        out.println(tableName + "<br/>");
        printResult(out, query((Connection) ctx.getAttribute("dbCon"), queryString));
    }

    public static void printResult(PrintWriter out, ResultSet rs) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            for (int i = 1; i <= columnsNumber; i++) out.print(rsmd.getColumnName(i) + "|");
            out.println("<br/>");
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    out.print(rs.getString(i) + "| ");
                }
                out.println("<br/>");
            }
            out.println("<br/>");
        }
        catch (Exception e) {
            System.out.println("Error while printing data");
            e.printStackTrace();
        }
    }

    public static ResultSet query(Connection connection, String query) {
        try {
            System.out.println("Querying " + query);
            Statement st = connection.createStatement();
            return st.executeQuery(query);
        }
        catch (Exception e) {
            System.out.println("Error while querying " + query);
            e.printStackTrace();
        }
        return null;
    }
}
