package np.com.naxa.staffattendance.newstaff;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import np.com.naxa.staffattendance.FormCall;
import np.com.naxa.staffattendance.R;
import np.com.naxa.staffattendance.SharedPreferenceUtils;
import np.com.naxa.staffattendance.application.StaffAttendance;
import np.com.naxa.staffattendance.attendence.AttendanceViewPagerActivity;
import np.com.naxa.staffattendance.attendence.TeamMemberResposne;
import np.com.naxa.staffattendance.attendence.TeamMemberResposneBuilder;
import np.com.naxa.staffattendance.common.Constant;
import np.com.naxa.staffattendance.common.GeoPointActivity;
import np.com.naxa.staffattendance.common.PairSpinnerAdapter;
import np.com.naxa.staffattendance.database.NewStaffDao;
import np.com.naxa.staffattendance.database.StaffDao;
import np.com.naxa.staffattendance.database.TeamDao;
import np.com.naxa.staffattendance.pojo.BankPojo;
import np.com.naxa.staffattendance.pojo.NewStaffPojo;
import np.com.naxa.staffattendance.pojo.NewStaffPojoBuilder;
import np.com.naxa.staffattendance.pojo.Staff;
import np.com.naxa.staffattendance.pojo.StaffBuilder;
import np.com.naxa.staffattendance.pojo.StaffRepository;
import np.com.naxa.staffattendance.utlils.DialogFactory;
import np.com.naxa.staffattendance.utlils.NetworkUtils;
import np.com.naxa.staffattendance.utlils.ToastUtils;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;
import retrofit2.HttpException;


import static np.com.naxa.staffattendance.common.Constant.EXTRA_MESSAGE;

public class NewStaffActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.act_new_staff_btn_location)
    public Button btnLocation;

    private Spinner spinnerBank, spinnerDesgination;
    private TextInputLayout firstName, lastName, ethinicity, contactNumber, email, address, accountNumber;
    private EditText dob, contractStartDate, contractEndDate, bankNameOther;
    private Button photo, save, create;
    private List<String> designationList = new ArrayList<>();
    private List<String> bankList = new ArrayList<>();
    private RadioGroup gender;
    private RadioButton male, female, other;
    private Calendar calendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date;
    private ArrayAdapter<String> spinnerAdapter;
    private File photoFileToUpload;
    private BottomNavigationView bottomNavigationView;
    private Gson gson;
    private Dialog msgDialog;
    DatePickerDialog datePickerDialog;
    private String latitude, longitude, accurary;

    private StaffRepository staffRepository;

    public static void start(Context context, boolean disableTrasition) {
        Intent intent = new Intent(context, NewStaffActivity.class);
        context.startActivity(intent);
        if (disableTrasition) ((Activity) context).overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_staff);
        ButterKnife.bind(this);
        gson = new Gson();
        staffRepository = StaffRepository.getInstance();

        initUI();

        initCalender();

        initListeners();


        if (!NetworkUtils.isInternetAvailable()) {
            loadBanks();
            loadStaffDesignation();
        }


        FormCall formCall = new FormCall();
        formCall.getBankList()

                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            try {
                                ResponseBody responseBody = ((HttpException) e).response().errorBody();
                                showErrorDialog("Failed to download banks", responseBody.string());
                            } catch (NullPointerException | IOException e1) {
                                showErrorDialog("Failed to download banks", "");
                                e1.printStackTrace();
                            }
                        } else if (e instanceof SocketTimeoutException) {
                            showErrorDialog("Failed to download banks", "Server took too long to respond");
                        } else if (e instanceof IOException) {
                            showErrorDialog("Failed to download banks", e.getMessage());
                        } else {
                            showErrorDialog("Failed to download banks", e.getMessage());
                        }
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<String> strings) {
                        loadBanks();
                    }
                });

        formCall.getDesignation()
                .subscribe(new Observer<ArrayList<ArrayList<String>>>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            try {
                                ResponseBody responseBody = ((HttpException) e).response().errorBody();
                                showErrorDialog("Failed to download designation", responseBody.string());
                            } catch (NullPointerException | IOException e1) {
                                showErrorDialog("Failed to download designation", "");
                                e1.printStackTrace();
                            }
                        } else if (e instanceof SocketTimeoutException) {
                            showErrorDialog("Failed to download designation", "Server took too long to respond");
                        } else if (e instanceof IOException) {
                            showErrorDialog("Failed to download designation", e.getMessage());
                        } else {
                            showErrorDialog("Failed to download designation", e.getMessage());
                        }
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<ArrayList<String>> arrayLists) {
                        loadStaffDesignation();
                    }
                });


        bottomNavigationView.setSelectedItemId(R.id.action_add_staff);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


    }

    private void showErrorDialog(String title, String message) {
        DialogFactory.createSimpleOkErrorDialog(NewStaffActivity.this,
                title,
                message)
                .show();
    }

    private void loadStaffDesignation() {

        StaffDesignationLocalSource.getInstance().getAsPairs()
                .subscribe(new DisposableObserver<List<Pair>>() {
                    @Override
                    public void onNext(List<Pair> designation) {
                        setupSpinnerDesgination(designation);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showErrorDialog("Failed to load designation", "Connect to then internet and reopen form to get designation");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadBanks() {
        BankLocalSource.getInstance()
                .getAsPairs()
                .subscribe(new DisposableObserver<List<Pair>>() {
                    @Override
                    public void onNext(List<Pair> banks) {
                        setupSpinner(banks);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogFactory.createSimpleOkErrorDialog(NewStaffActivity.this,
                                "Failed to load banks",
                                "Connect to then internet and reopen form to get banks")
                                .show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void spinnerValues() {
        FormCall formCall = new FormCall();

        if (!NetworkUtils.isInternetAvailable()) {
            String designations = SharedPreferenceUtils
                    .getFromPrefs(NewStaffActivity.this, SharedPreferenceUtils.KEY.Designation, "");

            String banks = SharedPreferenceUtils
                    .getFromPrefs(NewStaffActivity.this, SharedPreferenceUtils.KEY.Bank, "");

            if (TextUtils.isEmpty(designations) || TextUtils.isEmpty(banks)) {
                msgDialog = DialogFactory
                        .createMessageDialog(NewStaffActivity.this, "Message",
                                "Connect to then internet and reopen form to get banks and designations");
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

            formCall.getDesignation().subscribe(new Observer<ArrayList<ArrayList<String>>>() {
                @Override
                public void onComplete() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ArrayList<ArrayList<String>> arrayLists) {

                }
            });
        }

        if (bankList.isEmpty()) {
            bankList.add(getResources().getString(R.string.default_option));
            formCall.getBankList().subscribe(new Observer<List<String>>() {
                @Override
                public void onComplete() {
                    bankList.add(getString(R.string.bank_other));

                    SharedPreferenceUtils
                            .saveToPrefs(NewStaffActivity.this, SharedPreferenceUtils.KEY.Bank,
                                    gson.toJson(bankList));
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onSubscribe(Disposable d) {

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


    private void setupSpinnerDesgination(@NonNull List<Pair> desgination) {

        PairSpinnerAdapter pairSpinnerAdapter = null;

        //desgination spinner
        pairSpinnerAdapter = new PairSpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, desgination);
        pairSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDesgination.setAdapter(pairSpinnerAdapter);
    }

    private void setupSpinner(@NonNull List<Pair> banks) {
        PairSpinnerAdapter pairSpinnerAdapter = null;

        pairSpinnerAdapter = new PairSpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, banks);
        pairSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBank.setAdapter(pairSpinnerAdapter);

        spinnerBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String choice = (String) ((Pair) adapterView.getItemAtPosition(i)).second;
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
    }

    private void initSpinners() {
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, designationList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDesgination.setAdapter(spinnerAdapter);

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bankList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String choice = (String) ((Pair) adapterView.getItemAtPosition(i)).second;
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


        spinnerBank.setAdapter(this.spinnerAdapter);
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
        spinnerDesgination = findViewById(R.id.staff_designation);
        firstName = findViewById(R.id.staff_first_name);
        lastName = findViewById(R.id.staff_last_name);
        dob = findViewById(R.id.staff_dob_date);
        gender = findViewById(R.id.staff_gender);
        male = findViewById(R.id.gender_male);
        female = findViewById(R.id.gender_female);
        other = findViewById(R.id.gender_other);
        ethinicity = findViewById(R.id.staff_ethinicity);
        spinnerBank = findViewById(R.id.staff_bank);
        accountNumber = findViewById(R.id.staff_bank_account);
        bankNameOther = findViewById(R.id.staff_bank_other);
        contactNumber = findViewById(R.id.staff_contact_number);
        email = findViewById(R.id.staff_email);
        address = findViewById(R.id.staff_address);
        contractStartDate = findViewById(R.id.staff_contract_start_date_date);
        contractEndDate = findViewById(R.id.staff_contract_end_date_date);
        photo = findViewById(R.id.staff_photo);
        save = findViewById(R.id.staff_save);
        create = findViewById(R.id.staff_send);
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
    }

    private void initListeners() {
        dob.setOnClickListener(this);
        contractStartDate.setOnClickListener(this);
        contractEndDate.setOnClickListener(this);
        photo.setOnClickListener(this);
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

            case R.id.staff_save:
                if (validate()) {
                    new NewStaffDao().saveNewStaff(getNewStaffDetail());
                    finish();
                    startActivity(getIntent());
                    ToastUtils.showShort("New staff detail saved.");
                }
                break;

            case R.id.staff_send:

                if (validate()) {
                    final ProgressDialog progressDialog = DialogFactory.createProgressDialogHorizontal(this, getString(R.string.msg_please_wait));

                    //progressDialog.show();

                    Staff newStaff = getNewStaff();
                    staffRepository.save(newStaff);

                    NewStaffPojo staff = getNewStaffDetail();
                    new NewStaffDao().saveNewStaff(staff);
                    putDataInStafftable(staff);
                    AttendanceViewPagerActivity.start(NewStaffActivity.this, true);
                    finish();

//
//                    new NewStaffCall().upload(getNewStaffDetail(), photoFileToUpload, new NewStaffCall.NewStaffCallListener() {
//                        @Override
//                        public void onError() {
//                            progressDialog.dismiss();
//                            AttendanceViewPagerActivity.start(NewStaffActivity.this, true);
//                            finish();
//                        }
//
//                        @Override
//                        public void onSuccess() {
//
//                            progressDialog.dismiss();
//                            AttendanceViewPagerActivity.start(NewStaffActivity.this, true);
//                            finish();
//                        }
//                    });
                }
                break;
        }
    }

    private Staff getNewStaff() {
        String id = new TeamDao().getOneTeamIdForDemo();

        Pair selectedDesignation = ((Pair) spinnerDesgination.getSelectedItem());
        Integer selectedDesignationId = (Integer) selectedDesignation.first;
        String selectedDesignationLabel = (String) selectedDesignation.second;

        Pair selectedBank = ((Pair) spinnerBank.getSelectedItem());
        Integer selectedBankId = (Integer) selectedBank.first;
        String selectedBankLabel = (String) selectedBank.second;

        StaffBuilder builder = new StaffBuilder()
                .setId(String.valueOf(System.currentTimeMillis()))
                .setDesignation(selectedDesignationId)
                .setFirstName(firstName.getEditText().getText().toString())
                .setLastName(lastName.getEditText().getText().toString())
                .setDateOfBirth(dob.getText().toString())
                .setGender(getGender())
                .setEthnicity(ethinicity.getEditText().getText().toString())
                .setBankName(bankNameOther.getText().toString())
                .setAccountNumber(accountNumber.getEditText().getText().toString())
                .setPhoneNumber(contactNumber.getEditText().getText().toString())
                .setEmail(email.getEditText().getText().toString())
                .setAddress(address.getEditText().getText().toString())
                .setContractStart(contractStartDate.getText().toString())
                .setContractEnd(contractEndDate.getText().toString())
                .setPhoto(getPhotoLocation())
                .setTeamID(id)
                .setTeamName(new TeamDao().getTeamNameById(id))
                .setStatus(NewStaffDao.SAVED);


        if (selectedBankId != 1) {
            builder.setBank(selectedBankId);
        }

        return builder.createStaff();

    }

    private void putDataInStafftable(NewStaffPojo newStaffDetail) {
        String id = new TeamDao().getOneTeamIdForDemo();
        TeamMemberResposne member = new TeamMemberResposneBuilder()
                .setFirstName(newStaffDetail.getFirstName())
                .setLastName(newStaffDetail.getLastName())
                .setDesignation(newStaffDetail.getDesignation())
                .setTeamID(id)
                .setTeamName(new TeamDao().getTeamNameById(id))
                .setId(newStaffDetail.getId())
                .createTeamMemberResposne();

        new StaffDao().saveStaff(member);
    }


    private boolean validate() {
        boolean validation = false;

        String selectedDesgination = (String) ((Pair) spinnerDesgination.getSelectedItem()).second;
        String selectedBank = (String) ((Pair) spinnerBank.getSelectedItem()).second;

        String defaultOption = getResources().getString(R.string.default_option);

        if (defaultOption.equalsIgnoreCase(selectedDesgination)) {
            showValidationError("Select a designation", spinnerDesgination);
        } else if (firstName.getEditText().getText().toString().isEmpty()) {
            showValidationError("Enter first name", firstName.getEditText());
        } else if (lastName.getEditText().getText().toString().isEmpty()) {
            showValidationError("Enter last name", lastName.getEditText());
        } else if (dob.getText().toString().isEmpty()) {
            showValidationError("Choose date of birth", dob);
        } else if (gender.getCheckedRadioButtonId() == -1) {
            ToastUtils.showShort("Select gender");
        } else if (ethinicity.getEditText().getText().toString().isEmpty()) {
            showValidationError("Enter ethnicity", ethinicity.getEditText());
        } else if (defaultOption.equalsIgnoreCase(selectedBank)) {
            showValidationError("Choose a option", spinnerBank);

        } else if (spinnerBank.getSelectedItem().toString().equals(getResources().getString(R.string.bank_other)) && bankNameOther.getText().toString().isEmpty()) {
            showValidationError("Enter spinnerBank name", bankNameOther);

        } else if (!spinnerBank.getSelectedItem().toString().equals(getResources().getString(R.string.default_option)) && accountNumber.getEditText().getText().toString().isEmpty()) {

            showValidationError("Enter account number", accountNumber.getEditText());

        } else if (contactNumber.getEditText().getText().toString().isEmpty()) {
            showValidationError("Enter contact number", contactNumber.getEditText());
        } else if (address.getEditText().getText().toString().isEmpty()) {
            showValidationError("Enter address", address.getEditText());
        } else if (contractStartDate.getText().toString().isEmpty()) {
            showValidationError("Choose contract start date", contractStartDate);
        } else if (contractEndDate.getText().toString().isEmpty()) {
            showValidationError("Choose contract end date", contractEndDate);
        } else {
            validation = true;
        }
        return validation;
    }

    private void getDatePicker(final EditText view) {
        new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND));

            String formattedDate = String.format(Locale.US, "%s-%02d-%02d", year, month + 1, dayOfMonth);
            view.setText(formattedDate);
            view.setError(null);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void scrollToView(final View view) {
        view.getParent().requestChildFocus(view, view);
//        view.requestFocus();
//        ScrollUtils.scrollToView(findViewById(R.id.scrollView),view);
    }

    private void showValidationError(String message, View view) {
        scrollToView(view);
        if (view instanceof EditText) {
            EditText et = (EditText) view;
            et.setError(message);
        } else if (view instanceof Spinner) {
            Spinner spinner = (Spinner) view;
            TextView errorText = (TextView) spinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText(message);
        }
    }


    public NewStaffPojo getNewStaffDetail() {

        Pair selectedDesignation = ((Pair) spinnerDesgination.getSelectedItem());
        Integer selectedDesignationId = (Integer) selectedDesignation.first;
        String selectedDesignationLabel = (String) selectedDesignation.second;

        Pair selectedBank = ((Pair) spinnerBank.getSelectedItem());
        Integer selectedBankId = (Integer) selectedBank.first;
        String selectedBankLabel = (String) selectedBank.second;

        NewStaffPojoBuilder builder = new NewStaffPojoBuilder()
                .setID(String.valueOf(System.currentTimeMillis()))
                .setDesignation(selectedDesignationId)
                .setFirstName(firstName.getEditText().getText().toString())
                .setLastName(lastName.getEditText().getText().toString())
                .setDateOfBirth(dob.getText().toString())
                .setGender(getGender())
                .setEthnicity(ethinicity.getEditText().getText().toString())
                .setBankName(bankNameOther.getText().toString())
                .setAccountNumber(accountNumber.getEditText().getText().toString())
                .setPhoneNumber(contactNumber.getEditText().getText().toString())
                .setEmail(email.getEditText().getText().toString())
                .setAddress(address.getEditText().getText().toString())
                .setContractStart(contractStartDate.getText().toString())
                .setContractEnd(contractEndDate.getText().toString())
                .setPhoto(getPhotoLocation())
                .setStatus(NewStaffDao.SAVED);


        if (selectedBankId != 1) {
            builder.setBank(selectedBankId);
        }

        return builder.createNewStaffPojo();

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
        int bankId = 1;

        if (spinnerBank.getSelectedItem().equals(getResources().getString(R.string.bank_other))) {
            bankId = 1;
        }
        return bankId;
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
                    EasyImage.openCamera(NewStaffActivity.this, 0);
                } else if (options[itemId].equals("Choose from Gallery")) {
                    EasyImage.openChooserWithGallery(NewStaffActivity.this, "Select Staff Photo", 0);
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;


        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                photo.setError("");
            }

            @Override
            public void onImagePicked(File photoFileToUpload, EasyImage.ImageSource source, int type) {
                NewStaffActivity.this.photoFileToUpload = photoFileToUpload;
                photo.setText("Change Photo");
            }
        });

        switch (requestCode) {
            case Constant.Key.GEOPOINT_RESULT_CODE:
                String location = data.getStringExtra(EXTRA_MESSAGE);
                String[] locationSplit = location.split(" ");
                latitude = locationSplit[0];
                longitude = locationSplit[1];
                accurary = locationSplit[3];
                btnLocation.setText(getString(R.string.message_location_recorded, accurary));
                break;
        }
    }


    @Override
    public void onBackPressed() {
        AttendanceViewPagerActivity.start(this, false);
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


    @AfterPermissionGranted(Constant.Key.RC_LOCATION)
    @OnClick(R.id.act_new_staff_btn_location)
    public void getLocation() {
        String perms = Manifest.permission.ACCESS_FINE_LOCATION;

        boolean hasPermission = EasyPermissions.hasPermissions(this, perms);

        if (hasPermission) {
            Intent toGeoPointWidget = new Intent(this, GeoPointActivity.class);
            startActivityForResult(toGeoPointWidget, Constant.Key.GEOPOINT_RESULT_CODE);
        } else {
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, Constant.Key.RC_LOCATION, perms)
                            .setRationale(R.string.rationale_location_permission)
                            .setPositiveButtonText(R.string.dialog_action_ok)
                            .setNegativeButtonText(R.string.dialog_action_dismiss)
                            .build());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
