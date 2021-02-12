import com.ericsson.otp.erlang.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class DispatcherResponse {
    public static JSONObject response_map(OtpErlangTuple msgReply) throws OtpErlangRangeException {
        //getting the message content
        JSONObject map_reply = new JSONObject();
        //it is supposed to be a tuple...
        String opcode_reply = ((OtpErlangAtom) msgReply.elementAt(0)).atomValue();

        if (opcode_reply.equals("map_reply")){
            OtpErlangList map_reply_body = (OtpErlangList) msgReply.elementAt(1);
            JSONArray map_reply_players_info = parseMapReply(map_reply_body);
            map_reply.put("response","OK")
                     .put("players", map_reply_players_info);

        }else {
            map_reply.put("response", "BAD");
        }

        return map_reply;
    }

    private static JSONArray parseMapReply(OtpErlangList map_reply_body) throws OtpErlangRangeException {

        JSONArray array_players = new JSONArray();

        //Extract info from erlang list //[{username1,version1,state1},{username2,version2,state2}..]
        int users_number = map_reply_body.arity();

        for (int i = 0; i < users_number; i++)
        {

            OtpErlangTuple user_info = (OtpErlangTuple) map_reply_body.elementAt(i); //{username,version,state}

            String username = ((OtpErlangAtom) user_info.elementAt(0)).atomValue();
            long version = ((OtpErlangLong) user_info.elementAt(1)).longValue();
            ApplicationState state = new ApplicationState((OtpErlangTuple) user_info.elementAt(2));

            System.out.println("Username -> " + username + " Version-> " + version + " State-> " + state);

            JSONObject player = new JSONObject()
                    .put("username",username)
                    .put("state",state.get_state_json())
                    .put("version",version);

            array_players.put(player);
        }

        return array_players; //[
        //                          {username : <user1> ,
        //                          state : {x : <value> , y: <value>},
        //                          version : <value>},
        //                          {....},
        //                          {....}
        //                      ]
    }

    public static JSONObject response_update_position(OtpErlangTuple msgReply) throws OtpErlangRangeException {
        JSONObject update_reply = new JSONObject();

        //it is supposed to be a tuple... {update_reply, New_version}
        String opcode_reply = ((OtpErlangAtom) msgReply.elementAt(0)).atomValue();

        if (opcode_reply.equals("update_reply")){
            OtpErlangLong new_version = (OtpErlangLong) msgReply.elementAt(1);

            update_reply.put("response","OK")
                    .put("version", new_version.longValue());

        }else {
            update_reply.put("response", "BAD");
        }

        return update_reply;
    }
}
