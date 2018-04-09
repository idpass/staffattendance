package np.com.naxa.staffattendance;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewStaffActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner staffType, bank, designation;
    private TextInputLayout firstName, lastName, dob, ethinicity, contactNumber, email, contractStartDate, contractEndDate;
    private RadioGroup gender;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
//        initCalender();
        initListeners();
    }

    private void initCalender() {
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateOnView(dob);
            }
        };
    }

    private void updateDateOnView(final TextInputLayout view) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        view.getEditText().setText(sdf.format(calendar.getTime()));
    }

    private void initUI() {
        firstName = findViewById(R.id.staff_first_name);
        lastName = findViewById(R.id.staff_last_name);
        dob = findViewById(R.id.staff_dob);
        gender = findViewById(R.id.staff_gender);
        staffType = findViewById(R.id.staff_type);
        ethinicity = findViewById(R.id.staff_ethinicity);
        bank = findViewById(R.id.staff_bank);
        contactNumber = findViewById(R.id.staff_contact_number);
        email = findViewById(R.id.staff_email);
        contractStartDate = findViewById(R.id.staff_contract_start_date);
        contractEndDate = findViewById(R.id.staff_contract_end_date);
        designation = findViewById(R.id.staff_designation);

    }

    private void initListeners() {
        dob.setOnClickListener(this);
        contractStartDate.setOnClickListener(this);
        contractEndDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.staff_dob:
                initCalender();
                new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
//                updateDateOnView((TextInputLayout) view);
                break;
            case R.id.staff_contract_start_date:
                break;
            case R.id.staff_contract_end_date:
                break;
        }
    }
}
