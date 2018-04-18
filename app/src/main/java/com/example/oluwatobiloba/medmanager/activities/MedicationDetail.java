package com.example.oluwatobiloba.medmanager.activities;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oluwatobiloba.medmanager.R;
import com.example.oluwatobiloba.medmanager.database.AppDatabase;
import com.example.oluwatobiloba.medmanager.models.Medication;
import com.example.oluwatobiloba.medmanager.receivers.AlarmReceiver;

import java.util.Calendar;
import java.util.Objects;

public class MedicationDetail extends AppCompatActivity {

    public static final String EXTRA_MEDICATION_ID = "medication_ID";

    long medicationId;

    TextView mColorText;
    TextView mNameText;
    TextView mDescriptionText;
    TextView mStartText;
    TextView mEndText;
    TextView mIntervalText;
    TextView mPillsText;
    TextView mAmountText;

    String mColor;
    String mName;
    String mDescription;
    long mStart;
    long mEnd;
    String mInterval;
    String mIntevalType;
    String mPills;
    String mAmount;
    private String mRepeat;

    AppDatabase database;
    Medication medication;

    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;
    private long mRepeatTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_detail);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();

        medicationId = Long.parseLong(intent.getStringExtra(EXTRA_MEDICATION_ID));

        database = AppDatabase.getAppDatabase(this);
        medication = database.medicationDao().loadById(medicationId);

        initializingVariables();


        mName = medication.getName();
        mDescription = medication.getDescription();
        mColor = medication.getColor();
        mStart = medication.getStartDate();
        mEnd = medication.getEndDate();
        mInterval = medication.getRepeatNumber();
        mIntevalType = medication.getRepeatType();
        mPills = medication.getNumberOfPills();
        mAmount = medication.getAmount();
        mRepeat = medication.getRepeat();


        mNameText.setText(mName);
        ((GradientDrawable) mColorText.getBackground()).setColor(Integer.parseInt(mColor));
        mColorText.setText(String.valueOf(mName.charAt(0)).toUpperCase());
        mDescriptionText.setText(mDescription);
        mPillsText.setText(mPills);
        mAmountText.setText(mAmount);

        setDate(mStartText, mStart);
        setDate(mEndText, mEnd);

        String interval = mInterval + " " + mIntevalType;
        mIntervalText.setText(interval);

        if (hasDueDatePass()) {
            new AlarmReceiver().cancelAlarm(this, (int) medicationId);
        } else {
            saveReminder();
        }
    }

    private void setDate(TextView tv, long milliseconds) {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.setTimeInMillis(milliseconds);
        String date = android.text.format.DateFormat.getLongDateFormat(this).format(mCalendar.getTime());
        tv.setText(date);
    }


    private boolean hasDueDatePass() {
        long time = System.currentTimeMillis();
        return time > mEnd;
    }

    private void initializingVariables() {
        mColorText = findViewById(R.id.medication_details_icon);
        mNameText = findViewById(R.id.medication_details_name);
        mDescriptionText = findViewById(R.id.medication_details_description);
        mStartText = findViewById(R.id.medication_details_start);
        mEndText = findViewById(R.id.medication_details_end);
        mIntervalText = findViewById(R.id.medication_details_interval);
        mPillsText = findViewById(R.id.medication_details_pills);
        mAmountText = findViewById(R.id.medication_details_dose);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        } else if (id == R.id.menu_delete_medication) {
            deleteMedication();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteMedication() {
        new AlarmReceiver().cancelAlarm(this, (int) medicationId);
        database.medicationDao().delete(medication);

        Toast.makeText(getApplicationContext(), "Deleted ",
                Toast.LENGTH_SHORT).show();

        onBackPressed();
    }

    public void saveReminder() {

        // Set up calender for creating the notification
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mStart);

        // Check repeat type
        switch (mIntevalType) {
            case "Minute":
                mRepeatTime = Integer.parseInt(mInterval) * milMinute;
                break;
            case "Hour":
                mRepeatTime = Integer.parseInt(mInterval) * milHour;
                break;
            case "Day":
                mRepeatTime = Integer.parseInt(mInterval) * milDay;
                break;
            case "Week":
                mRepeatTime = Integer.parseInt(mInterval) * milWeek;
                break;
            case "Month":
                mRepeatTime = Integer.parseInt(mInterval) * milMonth;
                break;
        }

        int ID = (int) medicationId;

        // Create a new notification
        if (mRepeat.equals("true")) {
            new AlarmReceiver().setRepeatAlarm(getApplicationContext(), calendar, ID, mRepeatTime);
        } else if (mRepeat.equals("false")) {
            new AlarmReceiver().setAlarm(getApplicationContext(), calendar, ID);
        }

        // Create toast to confirm new reminder
        Toast.makeText(getApplicationContext(), "Saved ",
                Toast.LENGTH_SHORT).show();

        onBackPressed();
    }

}
