package np.com.naxa.staffattendance.attedancedashboard

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import np.com.naxa.staffattendance.R



class CalendarVH(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.grid_item_calendra, parent, false)) {
    private var tvDay: TextView? = null
    private var tvDate: TextView? = null
    private var tvMonthYear: TextView? = null


    init {
        tvDay = itemView.findViewById(R.id.tv_day)
        tvDate = itemView.findViewById(R.id.tv_date)
        tvMonthYear = itemView.findViewById(R.id.tv_month_year)
    }

    fun bind(day: AttendanceDay) {
        tvDay?.text = day.dayOfWeek
        tvDate?.text = day.dayOfMonth
        tvMonthYear?.text = day.date


    }

}