package np.com.naxa.staffattendance.attendence;

public class AttendanceRemoteSource {

    private static AttendanceRemoteSource INSTANCE;

    public AttendanceRemoteSource() {
    }

    public static AttendanceRemoteSource getInstance() {
        if(INSTANCE ==  null){
            INSTANCE = new AttendanceRemoteSource();
        }
        return INSTANCE;
    }
}
