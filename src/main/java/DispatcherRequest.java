import com.ericsson.otp.erlang.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class DispatcherRequest {
    public static OtpErlangTuple request_map(OtpErlangPid self, JSONObject body) {

        OtpErlangAtom opcode = new OtpErlangAtom("map");

        JSONArray users_info_array = body.getJSONArray("close_players"); //since close_players: [..]
        int users_number = users_info_array.length();

        OtpErlangObject[] user_id_terms = new OtpErlangObject[users_number];

        for (int i = 0; i < users_number; i++)
        {
            String username = users_info_array.getJSONObject(i).getString("username");
            long version = users_info_array.getJSONObject(i).getLong("version");

            System.out.println("Username -> " + username + " Version " + version);

            OtpErlangAtom username_atom = new OtpErlangAtom(username);
            OtpErlangLong version_int = new OtpErlangLong(version);

            OtpErlangTuple user_id_version = new OtpErlangTuple(
                    new OtpErlangObject[]{username_atom, version_int}); //{username,version}

            user_id_terms[i] = user_id_version;
        }

        OtpErlangList list = new OtpErlangList(user_id_terms); //[{username,version}, {username,version}..]

        return new OtpErlangTuple(new OtpErlangObject[]{
                self,opcode,list}); //{self,map,[{username,version}, {username,version}..]}
    }

    public static OtpErlangTuple update_position(OtpErlangPid self, JSONObject body) {

        OtpErlangAtom opcode = new OtpErlangAtom("update");

        ApplicationState state = new ApplicationState(body.getJSONObject("state"));

        OtpErlangAtom username = new OtpErlangAtom(body.getString("username"));
        OtpErlangTuple state_erl = state.get_state_erlang();
        OtpErlangLong version = new OtpErlangLong(body.getLong("version"));
        OtpErlangInt priority = new OtpErlangInt(body.getInt("priority"));

        return new OtpErlangTuple(new OtpErlangObject[]{
                self,opcode,username,state_erl,version,priority}); //{self,update,User_ID, New_state, Version, Priority}
    }
}
