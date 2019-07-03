package np.com.naxa.staffattendance.newstaff;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.idpass.mobile.api.IDPassConstants;
import org.idpass.mobile.api.IDPassIntent;
import org.idpass.mobile.proto.SignedAction;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import np.com.naxa.staffattendance.FormCall;
import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.SharedPreferenceUtils;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.attendence.AttendanceViewPagerActivity;
import np.com.naxa.staffattendance.attendence.TeamMemberResposne;
import np.com.naxa.staffattendance.attendence.TeamMemberResposneBuilder;
import np.com.naxa.staffattendance.common.BaseActivity;
import np.com.naxa.staffattendance.common.SoftKeyboardUtils;
import np.com.naxa.staffattendance.database.NewStaffDao;
import np.com.naxa.staffattendance.database.StaffDao;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.pojo.NewStaffPojo;
import np.com.naxa.staffattendance.pojo.NewStaffPojoBuilder;
import np.com.naxa.staffattendance.utlils.DialogFactory;
import np.com.naxa.staffattendance.utlils.NetworkUtils;
import np.com.naxa.staffattendance.utlils.ToastUtils;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import rx.Observer;
import timber.log.Timber;

public class AddStaffFormActivity extends BaseActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private int IDENTIFY_RESULT_INTENT = 1;

    private Spinner bank, designation;
    private TextInputLayout firstName, lastName, ethinicity, contactNumber, email, address, accountNumber;
    private EditText dob, contractStartDate, contractEndDate, bankNameOther;
    private Button photo, idpassIdentify, idpassEnroll, save, create;
    private List<String> designationList = new ArrayList<>();
    private List<String> bankList = new ArrayList<>();
    private RadioGroup gender;
    private RadioButton male, female, other;
    private Calendar calendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date;
    private ArrayAdapter<String> spinnerAdapter;
    private File photoFileToUpload;

    private Gson gson;
    private Dialog msgDialog;
    private TextView idpassValue;
    DatePickerDialog datePickerDialog;

    private String idPassDID;
    private ScrollView scrollView;

    public static void start(Context context, boolean disableTrasition) {
        Intent intent = new Intent(context, AddStaffFormActivity.class);
        context.startActivity(intent);
        if (disableTrasition) ((Activity) context).overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_staff);
        gson = new Gson();

        initUI();

        initCalender();

        initListeners();

        spinnerValues();

        initSpinners();

        setupToolbar("Add Staff");
    }

    private void setFakeData() {
        firstName.getEditText().setText(randomName());
        try {
//            bank.setSelection(1);
//            designation.setSelection(1);
        } catch (Exception e) {

        }
    }

    private void spinnerValues() {
        FormCall formCall = new FormCall();

        if (!NetworkUtils.isInternetAvailable()) {
            String designations = SharedPreferenceUtils
                    .getFromPrefs(AddStaffFormActivity.this, SharedPreferenceUtils.KEY.Designation, "");

            String banks = SharedPreferenceUtils
                    .getFromPrefs(AddStaffFormActivity.this, SharedPreferenceUtils.KEY.Bank, "");

            if (TextUtils.isEmpty(designations) || TextUtils.isEmpty(banks)) {
                msgDialog = DialogFactory
                        .createMessageDialog(AddStaffFormActivity.this, "Message", "Connect to then internet and reopen form to get banks and designations");
                msgDialog.show();
            } else {
                Type typeToken = new TypeToken<ArrayList<String>>() {
                }.getType();

                designationList.clear();
                bankList.clear();
                bankList.add(getResources().getString(R.string.default_option));
                designationList.add(getResources().getString(R.string.default_option));
                designationList.addAll((ArrayList<String>) gson.fromJson(designations, typeToken));
                bankList.add(getResources().getString(R.string.default_option));
                bankList.addAll((ArrayList<String>) gson.fromJson(banks, typeToken));
            }

        }

        if (designationList.isEmpty()) {
            designationList.add(getResources().getString(R.string.default_option));
            formCall.getDesignation()
                    .subscribe(new Observer<List<String>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(List<String> strings) {
                            designationList.addAll(strings);

                            SharedPreferenceUtils
                                    .saveToPrefs(AddStaffFormActivity.this, SharedPreferenceUtils.KEY.Designation,
                                            gson.toJson(designationList));


                        }
                    });
        }

        if (bankList.isEmpty()) {
            bankList.add(getResources().getString(R.string.default_option));
            formCall.getBankList().subscribe(new Observer<List<String>>() {
                @Override
                public void onCompleted() {
                    bankList.add(getString(R.string.bank_other));

                    SharedPreferenceUtils
                            .saveToPrefs(AddStaffFormActivity.this, SharedPreferenceUtils.KEY.Bank,
                                    gson.toJson(bankList));
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(List<String> strings) {
                    bankList.addAll(strings);
                }
            });


        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (msgDialog != null && msgDialog.isShowing()) {
            msgDialog.dismiss();
        }
    }


    private void initSpinners() {
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, designationList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        designation.setAdapter(spinnerAdapter);

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bankList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String choice = adapterView.getItemAtPosition(i).toString();
                if (choice.equals(getResources().getString(R.string.default_option))) {
                    accountNumber.setVisibility(View.GONE);
                    bankNameOther.setVisibility(View.GONE);
                } else if (choice.equals(getResources().getString(R.string.bank_other))) {
                    accountNumber.setVisibility(View.VISIBLE);
                    bankNameOther.setVisibility(View.VISIBLE);
                } else {
                    accountNumber.setVisibility(View.VISIBLE);
                    bankNameOther.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        bank.setAdapter(this.spinnerAdapter);
    }

    private void initCalender() {
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        };
    }

    private void updateDateOnView(final EditText view, String formatedDate) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        view.setText(sdf.format(calendar.getTime()));
    }

    private void initUI() {
        designation = findViewById(R.id.staff_designation);
        firstName = findViewById(R.id.staff_first_name);
        lastName = findViewById(R.id.staff_last_name);
        dob = findViewById(R.id.staff_dob_date);
        gender = findViewById(R.id.staff_gender);
        male = findViewById(R.id.gender_male);
        female = findViewById(R.id.gender_female);
        other = findViewById(R.id.gender_other);
        ethinicity = findViewById(R.id.staff_ethinicity);
        bank = findViewById(R.id.staff_bank);
        accountNumber = findViewById(R.id.staff_bank_account);
        bankNameOther = findViewById(R.id.staff_bank_other);
        contactNumber = findViewById(R.id.staff_contact_number);
        email = findViewById(R.id.staff_email);
        address = findViewById(R.id.staff_address);
        contractStartDate = findViewById(R.id.staff_contract_start_date_date);
        contractEndDate = findViewById(R.id.staff_contract_end_date_date);
        photo = findViewById(R.id.staff_photo);
        idpassIdentify = findViewById(R.id.idpass_identify);
        idpassValue = findViewById(R.id.idpass_value);
        idpassEnroll = findViewById(R.id.idpass_enroll);
        save = findViewById(R.id.staff_save);
        create = findViewById(R.id.staff_send);
        scrollView = findViewById(R.id.scrollView);

    }

    private void initListeners() {
        dob.setOnClickListener(this);
        contractStartDate.setOnClickListener(this);
        contractEndDate.setOnClickListener(this);
        photo.setOnClickListener(this);
        idpassIdentify.setOnClickListener(this);
        idpassEnroll.setOnClickListener(this);
        save.setOnClickListener(this);
        create.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.staff_dob_date:
                getDatePicker((EditText) view);
                findViewById(R.id.staff_dob_heading).setVisibility(View.VISIBLE);
                break;

            case R.id.staff_contract_start_date_date:
                getDatePicker((EditText) view);
                findViewById(R.id.staff_contract_start_date_heading).setVisibility(View.VISIBLE);
                break;

            case R.id.staff_contract_end_date_date:
                getDatePicker((EditText) view);
                findViewById(R.id.staff_contract_end_date_heading).setVisibility(View.VISIBLE);
                break;

            case R.id.staff_photo:
                photo.setError(null);
                showImageOptionsDialog();
                break;

            case R.id.idpass_identify:
                idpassIdentify.setError(null);
                idpassEnroll.setError(null);
                idpassIdentify();
                break;

            case R.id.idpass_enroll:
                idpassIdentify.setError(null);
                idpassEnroll.setError(null);
                idpassEnroll();
                break;

            case R.id.staff_save:
                if (validate()) {
                    NewStaffPojo staff = getNewStaffDetail();
                    NewStaffDao.getInstance().saveNewStaff(staff);
                    putDataInStafftable(staff);
                    finish();
                    startActivity(getIntent());
                    ToastUtils.showShort("New staff detail saved.");
                }
                break;

            case R.id.staff_send:
                if (validate()) {
                    final ProgressDialog progressDialog = DialogFactory.createProgressDialogHorizontal(this, getString(R.string.msg_please_wait));

                    //progressDialog.show();

                    NewStaffPojo staff = getNewStaffDetail();
                    new NewStaffDao().saveNewStaff(staff);
                    putDataInStafftable(staff);
                    finish();

//
//                    new NewStaffCall().upload(getNewStaffDetail(), photoFileToUpload, new NewStaffCall.NewStaffCallListener() {
//                        @Override
//                        public void onError() {
//                            progressDialog.dismiss();
//                            AttendanceViewPagerActivity.start(AddStaffFormActivity.this, true);
//                            finish();
//                        }
//
//                        @Override
//                        public void onSuccess() {
//
//                            progressDialog.dismiss();
//                            AttendanceViewPagerActivity.start(AddStaffFormActivity.this, true);
//                            finish();
//                        }
//                    });
                }
                break;
        }
    }

    private void putDataInStafftable(NewStaffPojo newStaffDetail) {
        String id = new TeamDao().getOneTeamIdForDemo();
        TeamMemberResposne member = new TeamMemberResposneBuilder()
                .setFirstName(newStaffDetail.getFirstName())
                .setLastName(newStaffDetail.getLastName())
                .setDesignation(newStaffDetail.getDesignation())
                .setTeamID(id)
                .setIDPassDID(idPassDID)
                .setTeamName(new TeamDao().getTeamNameById(id))
                .setId(newStaffDetail.getId())
                .createTeamMemberResposne();

        new StaffDao().saveStaff(member);
    }

    private boolean validate() {
        boolean validation = false;

        if (designation.getSelectedItem().equals(getResources().getString(R.string.default_option))) {
            showErrorMessage(designation, "Select a designation");
        } else if (firstName.getEditText().getText().toString().isEmpty()) {
            showErrorMessage(firstName, "Enter first name");
        } else if (lastName.getEditText().getText().toString().isEmpty()) {
            showErrorMessage(lastName, "Enter last name");
        } else if (dob.getText().toString().isEmpty()) {
            showErrorMessage(dob, "Choose date of birth");
        } else if (gender.getCheckedRadioButtonId() == -1) {
            showErrorMessage(gender, "Select Gender");
        } else if (ethinicity.getEditText().getText().toString().isEmpty()) {
            showErrorMessage(ethinicity, "Enter ethnicity");
        } else if (bank.getSelectedItem().toString().equals(getResources().getString(R.string.default_option))) {
            showErrorMessage(bank, "Choose a bank");
        } else if (bank.getSelectedItem().toString().equals(getResources().getString(R.string.bank_other)) && bankNameOther.getText().toString().isEmpty()) {
            showErrorMessage(bankNameOther, "Enter Bank name");
        } else if (!bank.getSelectedItem().toString().equals(getResources().getString(R.string.default_option)) && accountNumber.getEditText().getText().toString().isEmpty()) {
            showErrorMessage(accountNumber, "Enter Account number");
        } else if (contactNumber.getEditText().getText().toString().isEmpty()) {
            showErrorMessage(contactNumber, "Enter contact number");
        } else if (address.getEditText().getText().toString().isEmpty()) {
            showErrorMessage(address, "Enter address");
        } else if (contractStartDate.getText().toString().isEmpty()) {
            showErrorMessage(contractEndDate, "Choose contract start date");
        } else if (contractEndDate.getText().toString().isEmpty()) {
            showErrorMessage(contractEndDate, "Choose contract end date");
        } else if (TextUtils.isEmpty(idPassDID)) {
            showErrorMessage(idpassEnroll, "Please register with IDPASS");
            showErrorMessage(idpassIdentify, "Please register with IDPASS");
        } else {
            validation = true;
        }
        return validation;
    }



    private void focusOnView(final ScrollView scroll, final View view) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int vLeft = view.getLeft();
                int vRight = view.getRight();
                int sWidth = scroll.getWidth();
                scroll.smoothScrollTo(((vLeft + vRight - sWidth) / 2), 0);
            }
        });
    }

    private void showErrorMessage(View view, String errorMessage) {

        if (view instanceof EditText) {
            ((EditText) view).setError(errorMessage);
            Objects.requireNonNull(((EditText) view)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ((EditText) view).setError(null);
                }
            });

        } else if (view instanceof TextInputLayout) {
            ((TextInputLayout) view).setError(errorMessage);
            Objects.requireNonNull(((TextInputLayout) view).getEditText()).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ((TextInputLayout) view).setError(null);
                }
            });
        } else if (view instanceof Button) {
            ((Button) view).setError(errorMessage);
        } else if (view instanceof Spinner) {
            ((TextView) ((Spinner) view).getSelectedView()).setError(errorMessage);
        } else {
            ToastUtils.showShort(errorMessage);
        }

        focusOnView(scrollView, view);
        SoftKeyboardUtils.hideSoftKeyboard(scrollView);

    }

    private void getDatePicker(final EditText view) {
        new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND));

            String formattedDate = String.format(Locale.US, "%s-%02d-%02d", year, month + 1, dayOfMonth);
            view.setText(formattedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    public NewStaffPojo getNewStaffDetail() {
        Timber.d("ID PASS: " + idPassDID);

        return new NewStaffPojoBuilder()
                .setID(String.valueOf(System.currentTimeMillis()))
                .setDesignation(designation.getSelectedItemPosition())
                .setFirstName(firstName.getEditText().getText().toString())
                .setLastName(lastName.getEditText().getText().toString())
                .setDateOfBirth(dob.getText().toString())
                .setGender(getGender())
                .setEthnicity(ethinicity.getEditText().getText().toString())
                .setBank(getBankId())
                .setBankName(bankNameOther.getText().toString())
                .setAccountNumber(accountNumber.getEditText().getText().toString())
                .setPhoneNumber(contactNumber.getEditText().getText().toString())
                .setEmail(email.getEditText().getText().toString())
                .setAddress(address.getEditText().getText().toString())
                .setContractStart(contractStartDate.getText().toString())
                .setContractEnd(contractEndDate.getText().toString())
                .setPhoto(getPhotoLocation())
                .setIDPass(idPassDID)
                .setStatus(NewStaffDao.SAVED)
                .setDesignationLabel(designation.getSelectedItem().toString())
                .createNewStaffPojo();
    }

    private String getPhotoLocation() {
        if (photoFileToUpload != null) {
            return photoFileToUpload.getAbsolutePath();
        }
        return null;
    }

    private Integer getGender() {
        if (male.isChecked()) {
            return 1;
        } else if (female.isChecked()) {
            return 2;
        }
        return 3;
    }

    private Integer getBankId() {
        int bankId = 2;
        if (bank.getSelectedItem().equals(getResources().getString(R.string.bank_other))) {
            bankId = 1;
        }
        return bankId;
    }


    private void idpassIdentify() {
        Intent intent = IDPassIntent.intentIdentify(
                IDPassConstants.IDPASS_TYPE_MIFARE,
                true,
                true,
                null);
        startActivityForResult(intent, IDENTIFY_RESULT_INTENT);
    }

    private void idpassEnroll() {
        String name = firstName.getEditText().getText().toString() + " " + lastName.getEditText().getText().toString();

        Intent intent = IDPassIntent.intentEnroll("L1", name, true, true, true);
        startActivityForResult(intent, IDENTIFY_RESULT_INTENT);
    }


    private void showImageOptionsDialog() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Dismiss"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int itemId) {
                dialog.dismiss();
                if (options[itemId].equals("Take Photo")) {
                    EasyImage.openCamera(AddStaffFormActivity.this, 0);
                } else if (options[itemId].equals("Choose from Gallery")) {
                    EasyImage.openChooserWithGallery(AddStaffFormActivity.this, "Select Staff Photo", 0);
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IDENTIFY_RESULT_INTENT && resultCode == Activity.RESULT_OK) {
            String signedActionBase64 = data.getStringExtra(IDPassConstants.IDPASS_SIGNED_ACTION_RESULT_EXTRA);

            SignedAction signedAction = IDPassIntent.signedActionBuilder(signedActionBase64);

            idPassDID = signedAction.getAction().getPerson().getDid();
            String name = signedAction.getAction().getPerson().getName();
            idpassValue.setText(name + " - " + idPassDID);
        } else {

            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                    photo.setError("");
                }

                @Override
                public void onImagePicked(File photoFileToUpload, EasyImage.ImageSource source, int type) {
                    AddStaffFormActivity.this.photoFileToUpload = photoFileToUpload;
                    photo.setText("Change Photo");
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_staff:

                break;

            case R.id.action_attedance:
                AttendanceViewPagerActivity.start(this, false);
                finish();
                break;


        }
        return true;
    }


    public static String randomName() {
        final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";


        final Random random = new Random();
        int sizeOfRandomString = 10;
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

}
