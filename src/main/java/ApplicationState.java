import com.ericsson.otp.erlang.*;
import org.json.JSONObject;

//The implementation of this class depends on the application usage
//In our case, the state is composed by:
// x : X coordinate position in the map
// y : Y coordinate position in the map
public class ApplicationState {

    private long x_coordinate;
    private long y_coordinate;

    public ApplicationState(OtpErlangTuple erl_state) throws OtpErlangRangeException {
        x_coordinate = ((OtpErlangLong) erl_state.elementAt(0)).longValue();
        y_coordinate = ((OtpErlangLong) erl_state.elementAt(1)).longValue();
    }

    public ApplicationState(JSONObject json_state){
        x_coordinate = json_state.getLong("x");
        y_coordinate = json_state.getLong("y");
    }

    public JSONObject get_state_json(){
        return new JSONObject()
                .put("x", x_coordinate)
                .put("y", y_coordinate);
    }

    public OtpErlangTuple get_state_erlang(){
        return new OtpErlangTuple((new OtpErlangObject[]{   //{X_coordinate,Y_coordinate}
                new OtpErlangLong(x_coordinate),
                new OtpErlangLong(y_coordinate)
        }));
    }
}
