package np.com.naxa.staffattendance.attedancedashboard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_dashboard_attedance.*
import np.com.naxa.staffattendance.R
import np.com.naxa.staffattendance.utlils.DateConvertor

class AttendancesDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var list = arrayListOf<AttendanceDay>()
        setContentView(R.layout.activity_dashboard_attedance);
        for (x in -6 until 0 step 1) {
            var date = DateConvertor.getPastDate(x)
            var yearMonthDay = DateConvertor.getYearMonthDay(date);
            list.add(element = AttendanceDay(dayOfWeek = yearMonthDay[2], dayOfMonth = yearMonthDay[1],date = yearMonthDay[0], absentNoOfStaff = "", presentNoOfStaff = ""))
        }



        setupListAdapter(list);
    }

    private fun setupListAdapter(days: List<AttendanceDay>) {

        val manager = LinearLayoutManager(this)
        recycler_view.setLayoutManager(manager)
        recycler_view.setItemAnimator(DefaultItemAnimator())
        recycler_view.apply {
            layoutManager = GridLayoutManager(this@AttendancesDashboardActivity, 2)
            adapter = ListAdapter(days)
        }
        recycler_view.addItemDecoration(ItemOffsetDecoration(this,R.dimen.spacing_small))

    }
}