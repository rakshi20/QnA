import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class QuestionServlet extends HttpServlet {

    private ServletContext ctx;

    private PrintWriter out;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        out = response.getWriter();
        ctx = getServletContext();
        PrintDataServlet.printResult(out, PrintDataServlet.query((Connection) ctx.getAttribute("dbCon"), "select qn from public.question"));
    }
}
