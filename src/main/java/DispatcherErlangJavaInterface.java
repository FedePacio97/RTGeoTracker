import com.ericsson.otp.erlang.*;
import javafx.application.Application;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
This class is intended to be unique for each web server. 1 <-> 1 relationship
Since there will be more than one server (distributed), hence there will be
more than one DispatcherErlangJavaInterface object
 */

public class DispatcherErlangJavaInterface {
    //list of dispatchers (their name ids d1@localhost,..d5@localhost)
    private static List<String> serverNodeNameList; //configuration parameter
    private static final String serverNodeNameBase = "d @dispatcher "; //replace "_" to create list of dispatchers
    private static Random randomGenerator;
    private static final String serverRegisteredName = "dispatcher"; //configuration parameter

    private static final String clientNodeName = "client_node@localhost"; //configuration parameter
    private static OtpNode clientNode;  //initialized in constructor

    private static final int POOL_SIZE = 4; //configuration parameter
    private static final ExecutorService myExecutor = Executors.newFixedThreadPool(POOL_SIZE);

    private List<ClientTask> myTasks = new ArrayList<>(); //at each request, add a task

    private static volatile DispatcherErlangJavaInterface ref; //Used to implement singleton pattern

    public static void main(String[] args) throws IOException {
        //for testing
        DispatcherErlangJavaInterface driver = new DispatcherErlangJavaInterface("gtgp", 2);

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

        ClientTask T1 = new ClientTask(map_request);

        JSONObject update_player_1 = new JSONObject()
                .put("opcode","POS")
                .put("username","pippo")
                .put("state",new JSONObject().put("x",5).put("y",3))
                .put("version",2)
                .put("priority",1);

        JSONObject update_player_2 = new JSONObject()
                .put("opcode","POS")
                .put("username","pluto")
                .put("state",new JSONObject().put("x",4).put("y",2))
                .put("version",7)
                .put("priority",1);

        ClientTask T2 = new ClientTask(update_player_1);
        ClientTask T3 = new ClientTask(update_player_2);


        driver.myTasks.add(T2);
        driver.myTasks.add(T3);

        //driver.myTasks.add(T1);

        try {
            driver.doTest();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //constructor
    public DispatcherErlangJavaInterface(String cookie, int number_of_dispatchers) throws IOException {

        System.out.println("DispatcherErlangJavaInterface created");
        if (cookie!="") {
            clientNode = new OtpNode(clientNodeName, cookie);
        }
        else {
            clientNode = new OtpNode(clientNodeName);
        }

        if(number_of_dispatchers > 0){
            serverNodeNameList = new ArrayList<>();
            for (int i = 1; i <= number_of_dispatchers; i++)
                serverNodeNameList.add(serverNodeNameBase.replace(' ', Character.forDigit(i,10)));

            System.out.println(serverNodeNameList);
        }

        randomGenerator = new Random();

    }

    //method to run a test
    private void doTest() throws ExecutionException, InterruptedException {
        Future<JSONObject> result;
        for (Callable t : myTasks) {
            result = myExecutor.submit(t);
            System.out.println("Result " + result.get());
        }
        myExecutor.shutdown();
    }

    public Future executeClientTask(JSONObject task_body) throws IOException {
        ClientTask T = new ClientTask(task_body);

        System.out.println("Type " + T.type);
        Future result;
        synchronized (this){
            result = myExecutor.submit(T);
        }
        return result;
    }

    /**
     * A static nested class to implement a task acting as an independent Erlang client for avgserver
     * (a Runnable one).
     */

    //TODO Should be callable because called by servlet
    private static class ClientTask implements Callable<JSONObject> {

        private final String MAP_OPCODE = "MAP"; //Depend on application logic
        private final String POSITION_OPCODE = "POS"; //Depend on application logic

        private final OtpMbox mbox; //one mailbox per task
        private static final AtomicInteger counter= new AtomicInteger(0); //shared counter
        private final int taskID;  //one ID per task

        private final String type; //Can be either "map" or "update"
        private final JSONObject body; //This is the body of the request

        public ClientTask(JSONObject request) throws IOException {
            String opcode = request.getString("opcode");
            switch (opcode)
            {
                case MAP_OPCODE:
                    this.type = "map";
                    this.body = new JSONObject().
                            put("close_players", request.getJSONArray("close_players"));
                    break;
                case POSITION_OPCODE:
                    this.type = "update";
                    this.body = new JSONObject()
                            .put("username", request.getString("username"))
                            .put("version", request.getInt("version"))
                            .put("state", request.getJSONObject("state"))
                            .put("priority", request.getInt("priority"));
                    break;
                default:
                    this.type = "unknown";
                    this.body = null;
            }

            taskID = counter.getAndIncrement();
            mbox = clientNode.createMbox("default_mbox_"+ taskID);
            System.out.println("Created mailbox "+ mbox.getName());
        }

        @Override
        public JSONObject call() {

            JSONObject response = null;

            try {

                switch (this.type){
                    case "map":

                        System.out.println("Map requested");

                        OtpErlangTuple request_for_map_message = DispatcherRequest.request_map(this.mbox.self(),this.body);

                        //sending out the request
                        String serverNodeName = get_random_dispatcher();
                        mbox.send(serverRegisteredName, serverNodeName, request_for_map_message);
                        System.out.println("Request sent by " + Thread.currentThread().toString() + " : " +
                                request_for_map_message.toString());

                        //blocking receive operation
                        OtpErlangObject msg = mbox.receive();

                        System.out.println("Response received on mailbox "+mbox.getName()+" : " + msg.toString());

                        JSONObject map_reply = DispatcherResponse.response_map((OtpErlangTuple) msg);

                        System.out.println("Response to send to user " + map_reply.toString());

                        response = map_reply;

                        break;
                    case "update":

                        System.out.println("Update position requested");

                        OtpErlangTuple update_position_message = DispatcherRequest.update_position(this.mbox.self(),this.body);

                        //sending out the request
                        serverNodeName = get_random_dispatcher();
                        mbox.send(serverRegisteredName, serverNodeName, update_position_message);
                        System.out.println("Request sent by " + Thread.currentThread().toString() + " : " +
                                update_position_message.toString());

                        //blocking receive operation
                        msg = mbox.receive();

                        System.out.println("Response received on mailbox "+mbox.getName()+" : " + msg.toString());

                        JSONObject update_position_reply = DispatcherResponse.response_update_position((OtpErlangTuple) msg);

                        System.out.println("Response to send to user " + update_position_reply.toString());

                        response = update_position_reply;

                        break;

                    default:
                        System.out.println("Unknown opcode! ERROR!");

                        response = new JSONObject()
                                .put("error", "unknown opcode request");
                        break;
                }

            } catch (Exception e) {
                System.out.println("ERROR!\n" + e);
                e.printStackTrace();
            }

            return response;
        }

        private String get_random_dispatcher() {
            int index = randomGenerator.nextInt(serverNodeNameList.size());
            return serverNodeNameList.get(index);
        }
    }

}


/*
String cookie = "erljava";
        OtpSelf self = new OtpSelf("client",cookie);
        OtpPeer server= new OtpPeer("d1@localhost");
        OtpConnection connection = self.connect(server);

        OtpErlangObject[] msg = new OtpErlangObject[3];
        msg[0] = new OtpErlangAtom(self.node());
        msg[1] = new OtpErlangAtom("update");
        msg[2] = new OtpErlangAtom("hello, world");
        OtpErlangTuple tuple = new OtpErlangTuple(msg);
        connection.send("dispatcher",tuple);
        //connection.sendRPC("dispatcher","updatePosition", new OtpErlangList());
        OtpErlangObject reply = connection.receiveRPC();
        //connection.send("d1@localhost", tuple);

        //connection.sendRPC("dispatcher","hallau",new OtpErlangList());

                DispatcherErlangJavaInterface driver = new DispatcherErlangJavaInterface(cookie);
                OtpErlangInt num = new OtpErlangInt(1);
                OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{driver.mbox.self(), num});

                if (clientNode.ping("d1@localhost", 10000))
                    System.out.println("server is up");
                else
                    System.out.println("server is down");



 */