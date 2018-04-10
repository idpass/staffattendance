package np.com.naxa.staffattendance;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;

import np.com.naxa.staffattendance.pojo.AttendancePojo;

/**
 * Created by samir on 4/1/2018.
 */

public class AttandanceStaffRecyclerAdapter extends RecyclerView.Adapter<AttandanceStaffRecyclerAdapter.MyViewHolder> {

    private List<AttendancePojo> staffList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox staffName ;

        public MyViewHolder(View view) {
            super(view);
            staffName = (CheckBox) view.findViewById(R.id.staff_recycler_list_item_check_box);
        }
    }


    public AttandanceStaffRecyclerAdapter(List<AttendancePojo> staffList) {
        this.staffList = staffList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_staff_list_item, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AttendancePojo attendancePOJO = staffList.get(position);
        holder.staffName.setText(attendancePOJO.getStaffName());
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }
}