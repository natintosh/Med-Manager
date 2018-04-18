package com.example.oluwatobiloba.medmanager.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.oluwatobiloba.medmanager.R;
import com.example.oluwatobiloba.medmanager.database.AppDatabase;
import com.example.oluwatobiloba.medmanager.models.Medication;
import com.example.oluwatobiloba.medmanager.receivers.AlarmReceiver;
import com.example.oluwatobiloba.medmanager.ui.DatePickerFragment;
import com.example.oluwatobiloba.medmanager.ui.TimePickerFragment;
import com.example.oluwatobiloba.medmanager.utils.DateTimeHelper;
import com.example.oluwatobiloba.medmanager.utils.TaskHelper;

import java.util.Calendar;
import java.util.Objects;

public class AddMedication extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Toolbar mToolbar;

    TextInputLayout mNameLayout, mDescriptionLayout;
    EditText mNameEditText, mDescriptionEditText;

    TextView mStartDateTv, mEndDateTv, mTimeTv, mPillsTv, mAmountTv,
            mRepeatTv, mRepeatNumberTv, mRepeatTypeTv;


    String mName, mDescription, mRepeatType, mRepeat, mPills, mAmount;
    long mStartDate, mEndDate, mTime;
    String mRepeatNumber;

    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay;

    DateTimeHelper mDateTimeHelper;

    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;
    private long mRepeatTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_activity_add_medication);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initializingVariable();
    }

    private void initializingVariable() {
        mNameEditText = findViewById(R.id.add_medication_name);
        mDescriptionEditText = findViewById(R.id.add_medication_description);

        mNameLayout = findViewById(R.id.medication_name_layout);
        mDescriptionLayout = findViewById(R.id.medication_description_layout);

        mStartDateTv = findViewById(R.id.set_start_date);
        mEndDateTv = findViewById(R.id.set_end_date);
        mTimeTv = findViewById(R.id.set_time);
        mPillsTv = findViewById(R.id.set_pills);
        mAmountTv = findViewById(R.id.set_amount);
        mRepeatTv = findViewById(R.id.set_repeat);
        mRepeatNumberTv = findViewById(R.id.set_repeat_no);
        mRepeatTypeTv = findViewById(R.id.set_repeat_type);

        mPills = "0";
        mAmount = "0";
        mRepeatNumber = "1";
        mRepeatType = "Hour";
        mRepeat = "true";

        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);

        mStartDate = mCalendar.getTimeInMillis();
        mEndDate = mCalendar.getTimeInMillis();

        String mStartDateText = mDay + "/" + mMonth + "/" + mYear;
        String mEndDateText = mDay + "/" + mMonth + "/" + mYear;

        String mTimeText = (mMinute < 10 ? (mHour + ":0" + mMinute) : (mHour + ":" + mMinute));

        mStartDateTv.setText(mStartDateText);
        mEndDateTv.setText(mEndDateText);
        mTimeTv.setText(mTimeText);
        mPillsTv.setText(mPills);
        mAmountTv.setText(mAmount);
        mRepeatNumberTv.setText(mRepeatNumber);
        mRepeatTypeTv.setText(mRepeatType);


        mNameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mName = s.toString().trim();
                mNameEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mDescriptionEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDescription = s.toString().trim();
                mDescriptionEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    public void setStartDate(View view) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datepicker");
        mDateTimeHelper = new DateTimeHelper();
        mDateTimeHelper.setTextText(mStartDateTv);
    }

    public void setEndDate(View view) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datepicker");
        mDateTimeHelper = new DateTimeHelper();
        mDateTimeHelper.setTextText(mEndDateTv);
    }

    public void setTime(View view) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "datepicker");
        mDateTimeHelper = new DateTimeHelper();
        mDateTimeHelper.setTextText(mTimeTv);
    }

    public void setPills(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");

        // Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().length() == 0) {
                            mPills = Integer.toString(0);
                            mPillsTv.setText(mPills);
                        } else {
                            mPills = input.getText().toString().trim();
                            mPillsTv.setText(mPills);
                        }
                    }
                });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
    }

    public void setAmount(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");

        // Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().length() == 0) {
                            mAmount = Integer.toString(1);
                            mAmountTv.setText(mAmount);
                        } else {
                            mAmount = input.getText().toString().trim();
                            mAmountTv.setText(mAmount);
                        }
                    }
                });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
    }

    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = "true";
            mRepeatTv.setText(String.format("Every %s %s(s)", mRepeatNumber, mRepeatType));
        } else {
            mRepeat = "false";
            mRepeatTv.setText(R.string.repeat_text_off);
        }
    }

    public void setRepeatNo(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");

        // Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().length() == 0) {
                            mRepeatNumber = Integer.toString(1);
                            mRepeatNumberTv.setText(mRepeatNumber);
                            mRepeatTv.setText(String.format("Every %s %s(s)", mRepeatNumber, mRepeatType));
                        } else {
                            mRepeatNumber = input.getText().toString().trim();
                            mRepeatNumberTv.setText(mRepeatNumber);
                            mRepeatTv.setText(String.format("Every %s %s(s)", mRepeatNumber, mRepeatType));
                        }
                    }
                });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
    }

    public void selectRepeatType(View view) {
        final String[] items = new String[5];

        items[0] = "Minute";
        items[1] = "Hour";
        items[2] = "Day";
        items[3] = "Week";
        items[4] = "Month";

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Type");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                mRepeatType = items[item];
                mRepeatTypeTv.setText(mRepeatType);
                mRepeatTv.setText(String.format("Every %s %s(s)", mRepeatNumber, mRepeatType));
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, dayOfMonth);
        TextView textView = mDateTimeHelper.getTextView();
        if (textView.equals(mStartDateTv)) {
            String date = DateFormat.getDateFormat(this).format(mCalendar.getTime());
            mDateTimeHelper.getTextView().setText(date);
            mStartDate = mCalendar.getTimeInMillis();

            mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
            mMinute = mCalendar.get(Calendar.MINUTE);
            mYear = mCalendar.get(Calendar.YEAR);
            mMonth = mCalendar.get(Calendar.MONTH) + 1;
            mDay = mCalendar.get(Calendar.DATE);

        } else if (textView.equals(mEndDateTv)) {
            String date = DateFormat.getDateFormat(this).format(mCalendar.getTime());
            mDateTimeHelper.getTextView().setText(date);
            mEndDate = mCalendar.getTimeInMillis();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        String time = (minute < 10 ? (hourOfDay + ":0" + minute) : (hourOfDay + ":" + minute));
        mDateTimeHelper.getTextView().setText(time);
        mTime = mCalendar.getTimeInMillis();
    }

    private boolean isErrorInInput() {
        mName = mNameEditText.getText().toString();
        mDescription = mDescriptionEditText.getText().toString();
        if (mName.isEmpty()) {
            mNameLayout.setError("Enter a name");
        } else if (mDescription.isEmpty()) {
            mDescriptionLayout.setError("Enter a short description");
        } else {
            mNameLayout.setErrorEnabled(false);
            mDescriptionLayout.setErrorEnabled(false);
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        } else if (id == R.id.menu_add_medication) {
            if (!isErrorInInput()) {
                saveReminder();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveReminder() {
        AppDatabase database = AppDatabase.getAppDatabase(this);

        Medication medication = new Medication();
        medication.setName(mName);
        medication.setDescription(mDescription);
        medication.setStartDate(mStartDate);
        medication.setEndDate(mEndDate);
        medication.setInterval(mTime);
        medication.setNumberOfPills(mPills);
        medication.setAmount(mAmount);
        medication.setAmountUsed("0");
        medication.setColor(String.valueOf(TaskHelper.generataColor()));
        medication.setRepeat(mRepeat);
        medication.setRepeatNumber(mRepeatNumber);
        medication.setRepeatType(mRepeatType);

        // Creating medication
        int ID = (int) database.medicationDao().insertMedication(medication);

        // Set up calender for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        // Check repeat type
        switch (mRepeatType) {
            case "Minute":
                mRepeatTime = Integer.parseInt(mRepeatNumber) * milMinute;
                break;
            case "Hour":
                mRepeatTime = Integer.parseInt(mRepeatNumber) * milHour;
                break;
            case "Day":
                mRepeatTime = Integer.parseInt(mRepeatNumber) * milDay;
                break;
            case "Week":
                mRepeatTime = Integer.parseInt(mRepeatNumber) * milWeek;
                break;
            case "Month":
                mRepeatTime = Integer.parseInt(mRepeatNumber) * milMonth;
                break;
        }

        // Create a new notification
        if (mRepeat.equals("true")) {
            new AlarmReceiver().setRepeatAlarm(getApplicationContext(), mCalendar, ID, mRepeatTime);
        } else if (mRepeat.equals("false")) {
            new AlarmReceiver().setAlarm(getApplicationContext(), mCalendar, ID);
        }

        // Create toast to confirm new reminder
        Toast.makeText(getApplicationContext(), "Saved ",
                Toast.LENGTH_SHORT).show();

        onBackPressed();
    }

}
