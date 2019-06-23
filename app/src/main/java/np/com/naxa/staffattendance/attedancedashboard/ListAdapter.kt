package np.com.naxa.staffattendance.attedancedashboard

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class ListAdapter(private val list: List<AttendanceDay>)
    : RecyclerView.Adapter<CalendarVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarVH {
        val inflater = LayoutInflater.from(parent.context)

        return CalendarVH(inflater, parent)
    }

    override fun onBindViewHolder(holder: CalendarVH, position: Int) {
        val movie: AttendanceDay = list[position]
        holder.bind(movie)
    }


    override fun getItemViewType(position: Int): Int {
        return position;
    }

    override fun getItemCount(): Int = list.size
}

