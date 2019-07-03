package np.com.naxa.staffattendance.attedancedashboard



import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import np.com.naxa.staffattendance.R


class HeaderVH(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.header, parent, false)) {
    private var tvHeaderTitle: TextView? = null

    init {
        tvHeaderTitle = itemView.findViewById(R.id.tv_header_title)
      }

    fun bind(title: String) {
        tvHeaderTitle?.text = title
    }

}