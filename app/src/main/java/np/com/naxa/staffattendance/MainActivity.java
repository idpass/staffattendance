package np.com.naxa.staffattendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import np.com.naxa.staffattendance.POJO.Staff;
import np.com.naxa.staffattendance.database.StaffDao;

public class MainActivity extends AppCompatActivity {

    StaffDao staffDao;
    Staff staff;

    private static final String TAG = "MainActivity";
    Map<String, Integer> staffNameIDHashMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        staffDao = new StaffDao();

        initStaffHashMap();

//        initSpinner();



    }


    public void initStaffHashMap() {
        staffNameIDHashMap = new LinkedHashMap<>();
        staffNameIDHashMap.put("Select Staff Type", 0);
        staffNameIDHashMap.put("Staff Type 1", 1);
        staffNameIDHashMap.put("Staff Type 2", 2);
        staffNameIDHashMap.put("Staff Type 3", 3);
        staffNameIDHashMap.put("Staff Type 4", 4);
        staffNameIDHashMap.put("Staff Type 5", 5);


    }

    public String [] staffType (){
        String staffStringArrayType []= {""};
        List<String> staffTypeList ;

        Set<String> keySet = staffNameIDHashMap.keySet();
        Log.d(TAG, "staffType: "+keySet.toString());
        //Creating an ArrayList of keys by passing the keySet
        staffTypeList = new ArrayList<String>(keySet);

        staffStringArrayType = staffTypeList.toArray(new String[0]);
        return staffStringArrayType ;

    }

//    public void initSpinner(){
//        ArrayAdapter<String> staffTypeArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, staffType());
//        staffTypeArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        staffTypeSpinner.setAdapter(staffTypeArray);
//    }
//
//    @OnItemSelected(R.id.staff_type_spinner)
//    public void onSpinnerStaffTypeClicked() {
//
//        if (staffTypeSpinner.getSelectedItem().toString().equals("Select Staff Type")) {
//            Toast.makeText(this, "Please select staff type", Toast.LENGTH_SHORT).show();
//        }else {
//            Toast.makeText(this, "ID of selected staff type is "+getValuesFromStaffNameIDHashMap(staffTypeSpinner.getSelectedItem().toString()), Toast.LENGTH_SHORT).show();
//        }
//
//    }

    public String getValuesFromStaffNameIDHashMap (String staffType){
        int spinnerPosition = staffNameIDHashMap.get(staffType);

        return spinnerPosition+"";

    }
//
//    @OnClick(R.id.fab_activate_edit_mode)
//    public void onViewClicked() {
//
//        if(validateText(tvStaffNameEditable) && validateSpinner(staffTypeSpinner)) {
//
//            staff = new Staff(tvStaffNameEditable.getEditText().getText().toString(), getValuesFromStaffNameIDHashMap(staffTypeSpinner.getSelectedItem().toString()));
//
//            Log.d(TAG, "onViewClicked: " + staffDao.saveStaff(staff));
//
//            Intent intent = new Intent(MainActivity.this, AttendanceFormEditActivity.class);
//            startActivity(intent);
//        }
//
//    }

    private boolean validateText(TextInputLayout textInputLayout){
        if(TextUtils.isEmpty(textInputLayout.getEditText().getText())){
            textInputLayout.getEditText().requestFocus();
            textInputLayout.setError("Field is required");
           return false;
        }
        return true;
    }

    private boolean validateSpinner (Spinner spinner){
        if(spinner.getSelectedItemPosition() == 0){
            spinner.requestFocus();
            ((TextView)spinner.getSelectedView()).setError("Select an Option");
            return false;
        }
        return true;
    }
}
