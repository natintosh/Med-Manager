package com.example.oluwatobiloba.medmanager.utils;

import android.graphics.Color;

import java.util.Random;

public class TaskHelper {

    public static int generataColor() {
        Random mRandom = new Random();
        return Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
    }
}
