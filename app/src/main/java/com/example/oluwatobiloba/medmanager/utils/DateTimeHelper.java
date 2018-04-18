package com.example.oluwatobiloba.medmanager.utils;

import android.widget.TextView;

public class DateTimeHelper {

    private TextView textView;
    private int year;
    private int month;
    private int dayOfMonth;
    private int hourOfDay;
    private int minute;

    public DateTimeHelper() {

    }

    public DateTimeHelper(TextView textView) {
        this.textView = textView;
    }

    public DateTimeHelper(TextView textView, int year, int month, int dayOfMonth) {
        this.textView = textView;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    public DateTimeHelper(TextView textView, int hourOfDay, int minute) {
        this.textView = textView;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    public DateTimeHelper(TextView textView, int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        this.textView = textView;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextText(TextView textView) {
        this.textView = textView;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return this.dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
