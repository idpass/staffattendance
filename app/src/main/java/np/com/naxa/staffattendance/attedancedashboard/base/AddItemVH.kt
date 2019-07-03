package np.com.naxa.staffattendance.attedancedashboard.base


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import np.com.naxa.staffattendance.R
import np.com.naxa.staffattendance.attedancedashboard.AddItemButton
import np.com.naxa.staffattendance.common.UIConstants
import np.com.naxa.staffattendance.newstaff.AddStaffFormActivity


class AddItemVH(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.grid_item_add, parent, false)) {



    fun bind(addItem: AddItemButton) {

        itemView.setOnClickListener {
            when(addItem.uuid){
                UIConstants.UUID_GRID_ITEM_TEAM_MEMBER ->{
                    AddStaffFormActivity.start(itemView.context,true);
                }
            }
        }

    }

}