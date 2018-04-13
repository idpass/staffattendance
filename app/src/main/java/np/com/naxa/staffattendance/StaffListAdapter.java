package np.com.naxa.staffattendance;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import np.com.naxa.staffattendance.attendence.TeamMemberResposne;
import np.com.naxa.staffattendance.utlils.FlipAnimator;

public class StaffListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<TeamMemberResposne> filetredsitelist;
    private List<TeamMemberResposne> staffList;
    private List<String> attedanceIds;
    private OnStaffItemClickListener listener;
    private SparseBooleanArray selectedItems;
    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;


    StaffListAdapter(Context mContext, List<TeamMemberResposne> staffList, List<String> attedanceIds, OnStaffItemClickListener listener) {
        this.mContext = mContext;
        this.staffList = staffList;
        this.filetredsitelist = staffList;
        this.attedanceIds = attedanceIds;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.staff_list_row, parent, false);

        return new StaffVH(itemView);
    }


    private boolean contains(List<String> list, String comparable) {
        //todo why did list.containts(string) not work?

        for (String string : list) {
            if (string.trim().contains(comparable)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        TeamMemberResposne staff = staffList.get(position);
        final StaffVH staffVH = (StaffVH) holder;
        boolean isPresent = contains(attedanceIds,staff.getId());
        Log.i("StaffListAdapter", String.format("Checking if %s contains %s hmmm %s", attedanceIds.toString(), staff.getId(), isPresent));

        Context context = staffVH.rootLayout.getContext();

        if (isPresent) {
            selectedItems.put(holder.getAdapterPosition(), true);
            animationItemsIndex.put(holder.getAdapterPosition(), true);
            applyAnimToPastAttedanceItems(staffVH,holder.getAdapterPosition());
            staffVH.rootLayout.setEnabled(false);
            staffVH.staffStatus.setText(context.getString(R.string.attedance_present));
        }else {
            applyAnimToTodayAttedanceItems(staffVH,holder.getAdapterPosition());
            staffVH.staffStatus.setText(context.getString(R.string.attedance_absent));
        }

        staffVH.staffName.setText(staff.getFirstName());
        staffVH.staffType.setText(staff.getTeamName());
        staffVH.iconText.setVisibility(View.VISIBLE);
        staffVH.imgProfile.setImageResource(R.drawable.circle_blue);
        staffVH.iconText.setText(staff.getFirstName().substring(0, 1));

        staffVH.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onStaffClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    public void toggleSelection(int pos) {

        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }


    private void applyAnimToTodayAttedanceItems(StaffVH holder, int position){
        applyIconAnimation(holder,position,true);
    }

    private void applyAnimToPastAttedanceItems(StaffVH holder, int position){
        applyIconAnimation(holder,position,false);
    }

    private void applyIconAnimation(StaffVH holder, int position,boolean shouldHightlight) {
        if (selectedItems.get(position, false)) {
            holder.rootLayout.setActivated(shouldHightlight);
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.rootLayout.setActivated(!shouldHightlight);
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }

    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }


    public ArrayList<TeamMemberResposne> getSelected() {
        ArrayList<TeamMemberResposne> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(staffList.get(i));
        }

        return items;
    }


    public class StaffVH extends RecyclerView.ViewHolder {
        private TextView staffName,staffStatus, siteAddress, sitePhone, staffType, sitePendingFormsNumber, site, iconText, timestamp, tvTagOfflineSite;
        private ImageView iconImp, imgProfile;
        private RelativeLayout iconContainer, iconBack, iconFront;
        private RelativeLayout rootLayout;
        private CardView card;

        public StaffVH(View view) {
            super(view);
            card = view.findViewById(R.id.card_site_lst_row);
            iconBack = view.findViewById(R.id.icon_back);
            iconFront = view.findViewById(R.id.icon_front);
            iconContainer = view.findViewById(R.id.icon_container);
            rootLayout = view.findViewById(R.id.root_layout);
            staffStatus = view.findViewById(R.id.staff_list_row_status);

            staffName = view.findViewById(R.id.staff_list_row_name);
            siteAddress = view.findViewById(R.id.staff_list_row_email);
            sitePhone = view.findViewById(R.id.staff_list_row_phone);
            staffType = view.findViewById(R.id.staff_list_row_type);
            iconText = view.findViewById(R.id.icon_text);
            tvTagOfflineSite = view.findViewById(R.id.staff_list_row_status);
            imgProfile = view.findViewById(R.id.icon_profile);
        }
    }

    public interface OnStaffItemClickListener {
        void onStaffClick(int pos);

        void onStaffLongClick(int pos);
    }
}

