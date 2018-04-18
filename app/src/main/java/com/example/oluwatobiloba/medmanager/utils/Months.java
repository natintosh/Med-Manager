package com.example.oluwatobiloba.medmanager.utils;

public enum Months {
    SELECT(0, "Select a month"),
    JANUARY(1, "January"), FEBRUARY(2, "February"), MARCH(3, "March"), APRIL(4, "April"),
    MAY(5, "May"), JUNE(6, "June"), JULY(7, "July"), AUGUST(8, "August"), SEPTEMBER(9, "September"),
    OCTOBER(10, "October"), NOVEMBER(11, "November"), DECEMBER(12, "December");

    int monthId;
    String monthName;

    Months(int id, String name) {
        monthId = id;
        monthName = name;
    }

    public String getMonthName() {
        return monthName;
    }

}
