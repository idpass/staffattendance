package np.com.naxa.staffattendance.attedancedashboard

import android.content.Context
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.ViewGroup
import android.view.LayoutInflater
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import kotlinx.android.synthetic.main.fragment_take_attedance_dialog.view.*
import np.com.naxa.staffattendance.R
import np.com.naxa.staffattendance.attendence.AttendanceResponse
import np.com.naxa.staffattendance.attendence.TeamMemberResposne
import np.com.naxa.staffattendance.common.IntentConstants
import np.com.naxa.staffattendance.database.AttendanceDao
import java.util.*
import kotlin.concurrent.fixedRateTimer


class AttedanceBottomFragment : BottomSheetDialogFragment() {

    private lateinit var demoTimer: Timer
    var staff: TeamMemberResposne? = null
    var statusDesc: Array<String>? = null
    var loadedDate: String? = null
    lateinit var listener: OnAttedanceTakenListener

    interface OnAttedanceTakenListener {
        fun onAttedanceTaken(position: Int)
    }


    fun onClickListener(listener: OnAttedanceTakenListener){
        this.listener = listener
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getSerializable(IntentConstants.EXTRA_OBJECT)?.let {
            staff = it as TeamMemberResposne
        }
        arguments?.getString(IntentConstants.ATTENDANCE_DATE)?.let {
            loadedDate = it;
        }

    }

    @Nullable
    override fun onCreateView(inflater: LayoutInflater,
                              @Nullable container: ViewGroup?,
                              @Nullable savedInstanceState: Bundle?): View? {


        val view = inflater.inflate(R.layout.fragment_take_attedance_dialog, container,
                false)

        view.tv_take_attedance_frag_staff_name.text = staff?.firstName
        demoTimer = fixedRateTimer("timer", false, 1000, 1000) {
            requireActivity().runOnUiThread {
                goToNextStep(view)
            }
        }
        statusDesc = resources.getStringArray(R.array.attedance_status_desc);
        loadFirstMessage(view)
        return view;
    }

    override fun onDestroy() {
        super.onDestroy()
        demoTimer.cancel()
    }

    private fun goToNextStep(view: View) {
        val currentCount: Int = view.statusViewScroller.statusView.currentCount
        if (currentCount < 4) {
            setMessage(currentCount - 1, view)
            view.statusViewScroller.statusView.currentCount = currentCount + 1

        } else {
            saveAttedance()
            dismiss()
            listener.onAttedanceTaken(1)
        }

    }

    fun saveAttedance() {
        val attendanceResponse = AttendanceResponse()
        attendanceResponse.setAttendanceDate(loadedDate)
        attendanceResponse.setStaffs(listOf(staff?.id))
        attendanceResponse.setStaffProofs(listOf("demo-attedance-proof"))//todo: add attendanceProofToUpload
        attendanceResponse.dataSyncStatus = AttendanceDao.SyncStatus.FINALIZED
        AttedanceLocalSource.instance.updateAttendance(loadedDate, attendanceResponse, staff?.teamID)

    }

    private fun loadFirstMessage(view: View) {
        setMessage(0, view)
    }

    private fun setMessage(currentCount: Int, view: View) {
        view.tv_take_attedance_frag_desc.text = statusDesc?.get(currentCount)
    }

    companion object {

        fun newInstance(): AttedanceBottomFragment {
            return AttedanceBottomFragment()
        }
    }
}