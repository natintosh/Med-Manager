package com.example.oluwatobiloba.medmanager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.oluwatobiloba.medmanager.database.AppDatabase;
import com.example.oluwatobiloba.medmanager.models.Medication;

import java.util.Calendar;
import java.util.List;


public class BootReceiver extends BroadcastReceiver {

    private String mName;
    private long mTime;
    private long mStartDate;
    private long mEndDate;
    private String mRepeatNo;
    private String mRepeatType;
    private String mRepeat;
    private int mReceivedID;
    private long mRepeatTime;

    private Calendar mCalendar;
    private AlarmReceiver mAlarmReceiver;

    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            AppDatabase database = AppDatabase.getAppDatabase(context);
            mCalendar = Calendar.getInstance();
            mAlarmReceiver = new AlarmReceiver();

            List<Medication> medications = database.medicationDao().getAllMedication();


            for (Medication md : medications) {
                mReceivedID = (int) md.getId();
                mRepeat = md.getRepeat();
                mRepeatNo = md.getRepeatNumber();
                mRepeatType = md.getRepeatType();
                mStartDate = md.getStartDate();
                mEndDate = md.getEndDate();
                mTime = md.getInterval();

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(mTime);
                mCalendar.setTimeInMillis(mStartDate);
                mCalendar.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
                mCalendar.set(Calendar.MINUTE, c.get(Calendar.MINUTE));

                if (mRepeatType.equals("Minute")) {
                    mRepeatTime = Integer.parseInt(mRepeatNo) * milMinute;
                } else if (mRepeatType.equals("Hour")) {
                    mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
                } else if (mRepeatType.equals("Day")) {
                    mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
                } else if (mRepeatType.equals("Week")) {
                    mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
                } else if (mRepeatType.equals("Month")) {
                    mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
                }

                if (mRepeat.equals("true")) {
                    mAlarmReceiver.setRepeatAlarm(context, mCalendar, mReceivedID, mRepeatTime);
                } else if (mRepeat.equals("false")) {
                    mAlarmReceiver.setAlarm(context, mCalendar, mReceivedID);
                }
            }
        }
    }
}