package np.com.naxa.staffattendance.attedancedashboard

import android.content.Context
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.ViewGroup
import android.view.LayoutInflater
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import kotlinx.android.synthetic.main.fragment_take_attedance_dialog.view.*
import np.com.naxa.staffattendance.R
import np.com.naxa.staffattendance.attendence.TeamMemberResposne
import np.com.naxa.staffattendance.common.IntentConstants


class AttedanceBottomFragment : BottomSheetDialogFragment() {

    var staff: TeamMemberResposne? = null;

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getSerializable(IntentConstants.EXTRA_OBJECT)?.let {
            staff = it as TeamMemberResposne
        }
    }

    @Nullable
    override fun onCreateView(inflater: LayoutInflater,
                              @Nullable container: ViewGroup?,
                              @Nullable savedInstanceState: Bundle?): View? {



        val view =  inflater.inflate(R.layout.fragment_take_attedance_dialog, container,
                false)

        view.tv_take_attedance_frag_staff_name.text = staff?.firstName;
        return view;
    }

    companion object {

        fun newInstance(): AttedanceBottomFragment {
            return AttedanceBottomFragment()
        }
    }
}