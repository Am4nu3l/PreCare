package com.example.preg_women.Screens;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DailyCounter {
    private static final String PREFS_NAME = "PREF_PERSONAL_DATA";
    private static final String KEY_DAY_COUNT = "days";
    private static final String KEY_OTHER_FIELD = "month";

    public static void incrementField(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int dayCount = prefs.getInt(KEY_DAY_COUNT, 0);
        int otherField = prefs.getInt(KEY_OTHER_FIELD, 0);
        dayCount++;
        if (dayCount >= 30) {
            dayCount = 0;
            otherField++;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_DAY_COUNT, dayCount);
        editor.putInt(KEY_OTHER_FIELD, otherField);
        editor.apply();
    }
    public static void checkAndIncrementField(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int dayCount = prefs.getInt(KEY_DAY_COUNT, 0);
        Date lastDate = new Date(prefs.getLong("LastDate", 0));

        Calendar currentDate = Calendar.getInstance();
        Calendar lastSavedDate = Calendar.getInstance();
        lastSavedDate.setTime(lastDate);

        if (currentDate.get(Calendar.DAY_OF_YEAR) != lastSavedDate.get(Calendar.DAY_OF_YEAR)) {
            incrementField(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("LastDate", currentDate.getTimeInMillis());
            editor.apply();
        }
    }
}
