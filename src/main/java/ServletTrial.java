import org.json.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@WebServlet(name = "ServletTrial")
public class ServletTrial extends HttpServlet {

    private DispatcherErlangJavaInterface driver;
    private final int NumberOfDispatchers = 2;
    private final String cookie = "erljava";


    public ServletTrial() throws IOException {

        driver = new DispatcherErlangJavaInterface(cookie, NumberOfDispatchers);

    }

    //POST for update request
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //Get JSON content request body
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        JSONObject jsonObject;
        try {
            jsonObject =  new JSONObject(jb.toString());
        } catch (JSONException e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }

        System.out.println(jsonObject);

        /*JSONObject update_player = new JSONObject()
                .put("opcode","POS")
                .put("username","pippo")
                .put("state",new JSONObject().put("x",5).put("y",3))
                .put("version",2)
                .put("priority",1);
        */

        JSONObject update_player = jsonObject;

        //Siccome ritorna una future, assegnarla alla variabile result e darla indietro al client (MODO DA DEFINIRE (Decidi te marco))
        Future result = driver.executeClientTask(update_player);

        //TODO to be deleted after Marco(rella) will have implemented the response content
        try {
            System.out.println("Result " + result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try (PrintWriter writer = response.getWriter()) {
            writer.println("<!DOCTYPE html><html>");
            writer.println("<head>");
            writer.println("<meta charset=\"UTF-8\" />");
            writer.println("<title>MyServlet.java:doGet(): Servlet code!</title>");
            writer.println("</head>");
            writer.println("<body>");

            writer.println("<h1>This is a simple java servlet doPOST.</h1>");

            writer.println("</body>");
            writer.println("</html>");
        }

    }

    //PUT for map request
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //Get JSON content request body
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        JSONObject jsonObject;
        try {
            jsonObject =  new JSONObject(jb.toString());
        } catch (JSONException e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }

        System.out.println(jsonObject);


        /*JSONObject player1 = new JSONObject()
                .put("username","pippo")
                .put("version",4);
        JSONObject player2 = new JSONObject()
                .put("username","pluto")
                .put("version",2);
        JSONArray array_players = new JSONArray()
                .put(player1)
                .put(player2);

        JSONObject map_request = new JSONObject()
                .put("opcode","MAP")
                .put("close_players",array_players);*/

        JSONObject map_request = jsonObject;

        //Siccome ritorna una future, assegnarla alla variabile result e darla indietro al client (MODO DA DEFINIRE (Decidi te marco))
        Future result = driver.executeClientTask(map_request);

        //TODO to be deleted after Marco(rella) will have implemented the response content
        try {
            System.out.println("Result " + result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try (PrintWriter writer = response.getWriter()) {
            writer.println("<!DOCTYPE html><html>");
            writer.println("<head>");
            writer.println("<meta charset=\"UTF-8\" />");
            writer.println("<title>MyServlet.java:doGet(): Servlet code!</title>");
            writer.println("</head>");
            writer.println("<body>");

            writer.println("<h1>This is a simple java servlet doPOST.</h1>");

            writer.println("</body>");
            writer.println("</html>");
        }

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        JSONObject player1 = new JSONObject()
                .put("username","pippo")
                .put("version",4);
        JSONObject player2 = new JSONObject()
                .put("username","pluto")
                .put("version",2);
        JSONArray array_players = new JSONArray()
                .put(player1)
                .put(player2);

        JSONObject map_request = new JSONObject()
                .put("opcode","MAP")
                .put("close_players",array_players);

        //Siccome ritorna una future, assegnarla alla variabile result e darla indietro al client (MODO DA DEFINIRE (Decidi te marco))
        Future result = driver.executeClientTask(map_request);

        //TODO to be deleted after Marco(rella) will have implemented the response content
        try {
            System.out.println("Result " + result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        try (PrintWriter writer = response.getWriter()) {
            writer.println("<!DOCTYPE html><html>");
            writer.println("<head>");
            writer.println("<meta charset=\"UTF-8\" />");
            writer.println("<title>MyServlet.java:doGet(): Servlet code!</title>");
            writer.println("</head>");
            writer.println("<body>");

            writer.println("<h1>This is a simple java servlet.</h1>");

            writer.println("</body>");
            writer.println("</html>");
        }


    }

}
