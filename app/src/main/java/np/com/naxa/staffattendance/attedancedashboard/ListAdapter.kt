package np.com.naxa.staffattendance.attedancedashboard

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import np.com.naxa.staffattendance.attedancedashboard.base.AddItemVH


class ListAdapter(private val list: List<Any>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_CONTENT = 0
    private val TYPE_ADD_CONTENT = 1
    private val TYPE_HEADER = 2
    private val TYPE_STATS = 3


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_ADD_CONTENT -> onBindAddItem(holder as AddItemVH, list[position] as AddItemButton)
            TYPE_CONTENT -> onBindContent(holder as CalendarVH, list[position] as AttendanceDay)
            TYPE_HEADER -> onBindHeader(holder as HeaderVH, list[position] as String)
            TYPE_STATS -> onBindTeamStats(holder as TeamStatsVH, list[position] as TeamStats)

            else -> throw IllegalArgumentException()
        }
    }

    private fun onBindTeamStats(teamStatsVH: TeamStatsVH, teamStats: TeamStats) {
        teamStatsVH.bind(teamStats)
    }


    private fun onBindHeader(headerVH: HeaderVH, s: String) {
        headerVH.bind(s);
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            TYPE_ADD_CONTENT -> return AddItemVH(inflater, parent)
            TYPE_CONTENT -> return CalendarVH(inflater, parent)
            TYPE_HEADER -> return HeaderVH(inflater, parent)
            TYPE_STATS -> return TeamStatsVH(inflater, parent)
            else -> throw IllegalArgumentException()
        }

    }


    private fun onBindAddItem(holder: AddItemVH, row: AddItemButton) {
        holder.bind(row);
    }

    private fun onBindContent(holder: CalendarVH, row: AttendanceDay) {
        holder.bind(row);
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is AttendanceDay -> TYPE_CONTENT
            is AddItemButton -> TYPE_ADD_CONTENT
            is String -> TYPE_HEADER
            is TeamStats -> TYPE_STATS

            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int = list.size
}





