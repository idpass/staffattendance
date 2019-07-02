package np.com.naxa.staffattendance.attedancedashboard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import np.com.naxa.staffattendance.application.StaffAttendance;
import timber.log.Timber;


//import com.rillmark.royalworldcup.MainApplication;

public abstract class BaseRecyclerViewAdapter<L, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>  {
    private List<L> l;
    private int layout;

    protected BaseRecyclerViewAdapter(List<L> l, int layout) {
        this.l = l;
        this.layout = layout;
        Timber.d("listSize = %s", l.size());
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(StaffAttendance.getStaffAttendance()).inflate(layout, parent, false);
        return attachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        viewBinded(holder, l.get(position));
    }

    public List<L> getData() {
        return this.l;
    }

    @Override
    public int getItemCount() {
        return l.size();
    }

    public abstract void viewBinded(VH vh, L l);

    public abstract VH attachViewHolder(View view);



}