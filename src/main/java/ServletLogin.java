import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "ServletLogin")
public class ServletLogin extends HttpServlet {
    HashMap<String,String> login = new HashMap<>();
    public ServletLogin(){
        login.put("pippo", "pippo");
        login.put("graziano", "graziano");
        login.put("mariano", "mariano");
        login.put("silvano", "silvano");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if(login.get(username) == password){
            response.sendRedirect(request.getContextPath() + "/map.html");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.getRequestDispatcher("/html/login.html").forward(request, response);
    }
}
