package com.example.oluwatobiloba.medmanager.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "medication")
public class Medication {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "medicationId")
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "color")
    private String color;

    @ColumnInfo(name = "number_of_pills")
    private String numberOfPills;

    @ColumnInfo(name = "amount")
    private String amount;

    @ColumnInfo(name = "amount_used")
    private String amountUsed;

    @ColumnInfo(name = "interval")
    private long interval;

    @ColumnInfo(name = "start_date")
    private long startDate;

    @ColumnInfo(name = "end_date")
    private long endDate;

    @ColumnInfo(name = "should_repeat")
    private String repeat;

    @ColumnInfo(name = "repeat_number")
    private String repeatNumber;

    @ColumnInfo(name = "repeat_type")
    private String repeatType;

    @Ignore
    public Medication() {
    }

    public Medication(long id, String name, String description, String color, String numberOfPills, String amount, String amountUsed, String repeat, String repeatNumber, String repeatType, long interval, long startDate, long endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.numberOfPills = numberOfPills;
        this.amount = amount;
        this.amountUsed = amountUsed;
        this.repeat = repeat;
        this.repeatNumber = repeatNumber;
        this.repeatType = repeatType;
        this.interval = interval;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNumberOfPills() {
        return numberOfPills;
    }

    public void setNumberOfPills(String numberOfPills) {
        this.numberOfPills = numberOfPills;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmountUsed() {
        return amountUsed;
    }

    public void setAmountUsed(String amountUsed) {
        this.amountUsed = amountUsed;
    }


    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getRepeatNumber() {
        return repeatNumber;
    }

    public void setRepeatNumber(String repeatNumber) {
        this.repeatNumber = repeatNumber;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }
}
