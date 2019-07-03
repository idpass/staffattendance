package np.com.naxa.staffattendance.attedancedashboard

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import np.com.naxa.staffattendance.R


class TeamStatsVH(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.grid_item_stats, parent, false)) {

    private var tvTeamName: TextView? = null
    private var tvTeamMemberCount: TextView? = null


    init {
        tvTeamName = itemView.findViewById(R.id.tv_team_name)
        tvTeamMemberCount = itemView.findViewById(R.id.tv_members_count)

    }

    fun bind(day: TeamStats) {
        tvTeamName?.text = day.teamName
        tvTeamMemberCount?.text = day.teamMembersCount

    }

}