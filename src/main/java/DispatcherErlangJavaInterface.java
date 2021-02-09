import com.ericsson.otp.erlang.*;

import java.io.IOException;

public class DispatcherErlangJavaInterface {
    private static final String serverNodeName = "server@localhost"; //configuration parameter
    private static final String serverRegisteredName = "server"; //configuration parameter

    private static final String clientNodeName = "avg_client_node@localhost"; //configuration parameter
    private static OtpNode clientNode;  //initialized in constructor
    private final OtpMbox mbox; //one mailbox per task

    public static void main(String[] args) throws IOException, OtpAuthException, OtpErlangExit, OtpErlangDecodeException {

        String cookie = "erljava";
        OtpSelf self = new OtpSelf("client",cookie);
        OtpPeer server= new OtpPeer("d1@localhost");
        OtpConnection connection = self.connect(server);

        OtpErlangObject[] msg = new OtpErlangObject[2];
        msg[0] = new OtpErlangAtom("update");
        msg[1] = new OtpErlangAtom("hello, world");
        OtpErlangTuple tuple = new OtpErlangTuple(msg);
        connection.send("dispatcher",tuple);
        connection.sendRPC("dispatcher","updatePosition", new OtpErlangList());
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

                //sending out the request
                //driver.mbox.send("dispatcher","d1@localhost",reqMsg); //FOR ! messages
        //driver.mbox.send(serverRegisteredName, serverNodeName, reqMsg);

        //blocking receive operation
        //OtpErlangObject msg = driver.mbox.receive();

        //OtpNode javaNode = new OtpNode("jNode", "erljava");
        //OtpPeer server = new OtpPeer("server@localhost");


        //OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{this.mbox.self(), num});


        //OtpErlangAtom msg = new OtpErlangAtom("hello");

        /*OtpSelf self = new OtpSelf("client","erljava");
        OtpPeer other = new OtpPeer("server");
        OtpConnection conn = self.connect(other);

        OtpErlangAtom msg = new OtpErlangAtom("hello");
        conn.send("server", msg);*/
    }

    //constructor
    public DispatcherErlangJavaInterface(String cookie) throws IOException {

        if (cookie!="") {
            clientNode = new OtpNode(clientNodeName, cookie);
        }
        else {
            clientNode = new OtpNode(clientNodeName);
        }

        mbox = clientNode.createMbox("default_mbox_");
        System.out.println("Created mailbox "+ mbox.getName());

    }

    public void UpdatePosition(){

    }
}
