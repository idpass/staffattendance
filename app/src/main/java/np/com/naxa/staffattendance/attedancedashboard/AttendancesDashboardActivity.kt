package np.com.naxa.staffattendance.attedancedashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_dashboard_attedance.*
import np.com.naxa.staffattendance.R
import np.com.naxa.staffattendance.common.UIConstants
import np.com.naxa.staffattendance.database.StaffDao
import np.com.naxa.staffattendance.database.TeamDao
import np.com.naxa.staffattendance.utlils.DateConvertor
import np.com.naxa.staffattendance.utlils.ToastUtils
import java.util.concurrent.TimeUnit

class AttendancesDashboardActivity : AppCompatActivity() {

    private var exitOnBackPress: Boolean = false;
    private val backPressHandler = Handler()
    private val runnable = { exitOnBackPress = false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_attedance);

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        setupListAdapter(generateGridItems());
    }

    private fun generateGridItems(): ArrayList<Any> {
        val teamId = TeamDao().oneTeamIdForDemo
        val list = arrayListOf<Any>()
        var teamName: String = ""
        val staffs = StaffDao().getStaffByTeamId(teamId)
        list.add(getString(R.string.title_team))
        list.add("")
        if (staffs.size > 0) {
            teamName = staffs[0].teamName
            val teamMembersCount = staffs.count().toString()
            list.add(TeamStats(teamName, teamMembersCount))
        }
        list.add(AddItemButton(UIConstants.UUID_GRID_ITEM_TEAM_MEMBER))
        list.add(getString(R.string.title_attedance))
        list.add("")
        for (x in -6 until 1 step 1) {
            val date = DateConvertor.getPastDate(x)
            val yearMonthDay = DateConvertor.getYearMonthDay(date);
            list.add(element = AttendanceDay(dayOfWeek = yearMonthDay[2],
                    dayOfMonth = yearMonthDay[1],
                    date = yearMonthDay[0],
                    absentNoOfStaff = "",
                    presentNoOfStaff = "",
                    teamId = teamId,
                    teamName = teamName,
                    fullDate = DateConvertor.formatDate(DateConvertor.getDateForPosition(x))));
        }

        return list;
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.main_dashboard_setting -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        if (exitOnBackPress) {
            finish()
            return
        }

        exitOnBackPress = true
        ToastUtils.showShort(getString(R.string.msg_backpress_to_exit))
        backPressHandler.postDelayed(runnable, TimeUnit.SECONDS.toMillis(2))
    }

    private fun setupListAdapter(days: List<Any>) {

        val manager = LinearLayoutManager(this)
        recycler_view.setLayoutManager(manager)
        recycler_view.setItemAnimator(DefaultItemAnimator())
        recycler_view.apply {
            layoutManager = GridLayoutManager(this@AttendancesDashboardActivity, 2)
            adapter = ListAdapter(days)
        }
        recycler_view.addItemDecoration(ItemOffsetDecoration(this, R.dimen.spacing_small))

    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, AttendancesDashboardActivity::class.java)
            return intent
        }
    }
}