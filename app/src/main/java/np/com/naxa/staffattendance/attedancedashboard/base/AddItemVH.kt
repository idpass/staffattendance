package np.com.naxa.staffattendance.attedancedashboard.base


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import np.com.naxa.staffattendance.R
import np.com.naxa.staffattendance.attedancedashboard.AddItemButton
import np.com.naxa.staffattendance.attedancedashboard.AttendanceDay
import np.com.naxa.staffattendance.common.UIConstants
import np.com.naxa.staffattendance.newstaff.NewStaffActivity
import np.com.naxa.staffattendance.utlils.ToastUtils


class AddItemVH(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.grid_item_add, parent, false)) {



    fun bind(addItem: AddItemButton) {

        itemView.setOnClickListener {
            when(addItem.uuid){
                UIConstants.UUID_GRID_ITEM_TEAM_MEMBER ->{
                    NewStaffActivity.start(itemView.context,true);
                }
            }
        }

    }

}