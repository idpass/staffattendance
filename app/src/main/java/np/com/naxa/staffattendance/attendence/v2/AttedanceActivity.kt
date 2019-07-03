package np.com.naxa.staffattendance.attendence.v2

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_dashboard_attedance.*
import np.com.naxa.staffattendance.R
import np.com.naxa.staffattendance.StaffListAdapter
import np.com.naxa.staffattendance.attedancedashboard.AttedanceBottomFragment
import np.com.naxa.staffattendance.attedancedashboard.AttedanceLocalSource
import np.com.naxa.staffattendance.attedancedashboard.ItemOffsetDecoration
import np.com.naxa.staffattendance.attendence.AttendanceResponse

import np.com.naxa.staffattendance.attendence.TeamMemberResposne
import np.com.naxa.staffattendance.common.BaseActivity
import np.com.naxa.staffattendance.common.IntentConstants
import np.com.naxa.staffattendance.database.AttendanceDao
import np.com.naxa.staffattendance.database.StaffDao
import np.com.naxa.staffattendance.database.TeamDao
import np.com.naxa.staffattendance.utlils.DateConvertor
import np.com.naxa.staffattendance.utlils.ToastUtils
import org.idpass.mobile.api.IDPassConstants
import org.idpass.mobile.api.IDPassIntent


class AttedanceActivity : BaseActivity(), StaffListAdapter.OnStaffItemClickListener {
    private val IDENTIFY_RESULT_INTENT = 1

    override fun onStaffClick(pos: Int, staff: TeamMemberResposne?) {
0
    }

    override fun onStaffLongClick(pos: Int) {
    }

    private var loadedDate: String? = null;
    private lateinit var stafflistAdapter: StaffListAdapter
    private var teamDao: TeamDao? = null
    private var enablePersonSelection = true;
    private var attedanceIds: List<String>? = emptyList()
    private lateinit var teamId: String
    private lateinit var teamName: String
    private lateinit var nfcAdapter: NfcAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_attedance)
        loadedDate = intent.getStringExtra(IntentConstants.ATTENDANCE_DATE);
        teamId = intent.getStringExtra(IntentConstants.TEAM_ID);
        teamName = intent.getStringExtra(IntentConstants.TEAM_NAME);

//        val dailyAttendance = AttendanceDao().getAttedanceByDate(teamId, loadedDate)
//        setAttendanceIds(dailyAttendance.presentStaffIds,dailyAttendance.getAttendanceDate(false))

        setupToolbar(title = teamName)
        setupRecyclerView()
        swiperefresh.isEnabled = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(applicationContext)
        }
    }

    public override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            nfcAdapter.enableReaderMode(this, { tag ->
                val intent = IDPassIntent.intentIdentify(
                        IDPassConstants.IDPASS_TYPE_MIFARE,
                        true,
                        true,
                        tag)
                startActivityForResult(intent, IDENTIFY_RESULT_INTENT)
            }, NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null)
        }
    }


    public override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            nfcAdapter.disableReaderMode(this)
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IDENTIFY_RESULT_INTENT && resultCode == Activity.RESULT_OK) {
            val signedActionBase64 = data!!.getStringExtra(IDPassConstants.IDPASS_SIGNED_ACTION_RESULT_EXTRA)

            val signedAction = IDPassIntent.signedActionBuilder(signedActionBase64)

            val idPassDID = signedAction.action.person.did

            val staffs = StaffDao().getStaffByIdPassDID(idPassDID)
            if (staffs.size > 0) {
                val staff = staffs[0]
                saveAttendance(staff, signedActionBase64)
            }
        }
    }

    fun saveAttendance(staff: TeamMemberResposne, signedAction: String) {
        val attendanceResponse = AttendanceResponse()
        attendanceResponse.setAttendanceDate(loadedDate)
        attendanceResponse.setStaffs(listOf(staff.id))
        attendanceResponse.setStaffProofs(listOf(signedAction))//todo: add attendanceProofToUpload
        attendanceResponse.dataSyncStatus = AttendanceDao.SyncStatus.FINALIZED
        AttedanceLocalSource.instance.updateAttendance(loadedDate, attendanceResponse, staff.teamID)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed();
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setupRecyclerView() {
        val teamDao = TeamDao()
        val mLayoutManager = LinearLayoutManager(applicationContext)
        val teamId = teamDao.oneTeamIdForDemo
        attedanceIds = AttendanceDao().getAttedanceByDate(teamId, loadedDate).presentStaffIds

        val staffs = StaffDao().getStaffByTeamId(teamId)
        stafflistAdapter = StaffListAdapter(this, staffs, enablePersonSelection, attedanceIds, this)
        recycler_view.layoutManager = mLayoutManager
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = stafflistAdapter
        var count = recycler_view.itemDecorationCount
        if (count == 0) {
            recycler_view.addItemDecoration(ItemOffsetDecoration(this, R.dimen.spacing_small))
        }
    }

    fun setAttendanceIds(attendanceIds: List<String>?, attendanceDate: String) {
        this.attedanceIds = attendanceIds
        val isAttedanceEmpty = attendanceIds?.isEmpty()
        val isAttedanceDateToday = DateConvertor.getCurrentDate().equals(attendanceDate, ignoreCase = true)
        if (isAttedanceEmpty!! && isAttedanceDateToday) {
            enablePersonSelection = true
        }

        val isAttendenceNotEmpty = !isAttedanceEmpty

        if (isAttedanceDateToday && isAttendenceNotEmpty) {
            enablePersonSelection = false
        }
    }


    companion object {
        fun newIntent(context: Context, date: String, teamId: String, teamName: String): Intent {
            val intent = Intent(context, AttedanceActivity::class.java)
            intent.putExtra(IntentConstants.ATTENDANCE_DATE, date);
            intent.putExtra(IntentConstants.TEAM_ID, teamId);
            intent.putExtra(IntentConstants.TEAM_NAME, teamName);
            return intent
        }
    }

}