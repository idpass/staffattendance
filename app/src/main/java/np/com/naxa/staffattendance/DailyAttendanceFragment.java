package np.com.naxa.staffattendance;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import np.com.naxa.staffattendance.pojo.Staff;

public class DailyAttendanceFragment extends Fragment implements StaffListAdapter.OnStaffItemClickListener {

    private RecyclerView recyclerView;
    private StaffListAdapter stafflistAdapter;

    public DailyAttendanceFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_attendence, container, false);
        bindUI(rootView);
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        List<Staff> staffs = new ArrayList<>();


        staffs.add(new Staff("Nishon", "General"));
        staffs.add(new Staff("Ramesh", "General"));

        stafflistAdapter = new StaffListAdapter(getActivity(), staffs, this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(stafflistAdapter);
    }

    private void bindUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_staff_list);
    }

    @Override
    public void onStaffClick(int pos) {
        stafflistAdapter.toggleSelection(pos);
    }

    @Override
    public void onStaffLongClick(int pos) {

    }
}
