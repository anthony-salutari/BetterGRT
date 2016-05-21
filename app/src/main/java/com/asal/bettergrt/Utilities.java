package com.asal.bettergrt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.lang.reflect.Method;

/**
 * Created by Anthony on 5/14/2016.
 */
public class Utilities {
    private static SharedPreferences.Editor mEditor;
    public static int mTheme;
    public static float mMarkerHue;
    public static float mSelectedMarkerHue;

    private static final String TAG = "BetterGRT";

    private static final int RED = 0;
    public static final int PINK = 1;
    public static final int PURPLE = 2;
    public static final int DEEP_PURPLE = 3;
    public static final int INDIGO = 4;
    public static final int BLUE = 5;
    public static final int LIGHT_BLUE = 6;
    public static final int CYAN = 7;
    public static final int TEAL = 8;
    public static final int GREEN = 9;
    public static final int LIGHT_GREEN = 10;
    public static final int LIME = 11;
    public static final int YELLOW = 12;
    public static final int AMBER = 13;
    public static final int ORANGE = 14;
    public static final int DEEP_ORANGE = 15;
    public static final int BROWN = 16;
    public static final int GREY = 17;
    public static final int BLUE_GREY = 18;

    public static void changeTheme(Activity activity, int theme, SharedPreferences preferences) {
        mTheme = theme;

        // theme changed, update the shared preference
        mEditor = preferences.edit();
        mEditor.putInt("theme", theme).apply();

        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static void onCreateChangeTheme(Activity activity, SharedPreferences preferences) {
        switch (mTheme) {
            case RED:
                activity.setTheme(R.style.Red);
                mMarkerHue = 4;
                mSelectedMarkerHue = 184;
                break;
            case PINK:
                activity.setTheme(R.style.Pink);
                mMarkerHue = 340;
                mSelectedMarkerHue = 160;
                break;
            case PURPLE:
                activity.setTheme(R.style.Purple);
                mMarkerHue = 291;
                mSelectedMarkerHue = 111;
                break;
            case DEEP_PURPLE:
                activity.setTheme(R.style.DeepPurple);
                mMarkerHue = 262;
                mSelectedMarkerHue = 82;
                break;
            case INDIGO:
                activity.setTheme(R.style.Indigo);
                mMarkerHue = 231;
                mSelectedMarkerHue = 51;
                break;
            case BLUE:
                activity.setTheme(R.style.Blue);
                mMarkerHue = 207;
                mSelectedMarkerHue = 27;
                break;
            case LIGHT_BLUE:
                activity.setTheme(R.style.LightBlue);
                mMarkerHue = 199;
                mSelectedMarkerHue = 19;
                break;
            case CYAN:
                activity.setTheme(R.style.Cyan);
                mMarkerHue = 187;
                mSelectedMarkerHue = 7;
                break;
            case TEAL:
                activity.setTheme(R.style.Teal);
                mMarkerHue = 174;
                mSelectedMarkerHue = 354;
                break;
            case GREEN:
                activity.setTheme(R.style.Green);
                mMarkerHue = 122;
                mSelectedMarkerHue = 302;
                break;
            case LIGHT_GREEN:
                activity.setTheme(R.style.LightGreen);
                mSelectedMarkerHue = 268;
                mMarkerHue = 88;
                break;
            case LIME:
                activity.setTheme(R.style.Lime);
                mMarkerHue = 66;
                mSelectedMarkerHue = 246;
                break;
            case YELLOW:
                activity.setTheme(R.style.Yellow);
                mMarkerHue = 54;
                mSelectedMarkerHue = 234;
                break;
            case AMBER:
                activity.setTheme(R.style.Amber);
                mMarkerHue = 45;
                mSelectedMarkerHue = 225;
                break;
            case ORANGE:
                activity.setTheme(R.style.Orange);
                mMarkerHue = 36;
                mSelectedMarkerHue = 216;
                break;
            case DEEP_ORANGE:
                activity.setTheme(R.style.DeepOrange);
                mMarkerHue = 14;
                mSelectedMarkerHue = 194;
                break;
            case BROWN:
                activity.setTheme(R.style.Brown);
                mMarkerHue = 16;
                mSelectedMarkerHue = 196;
                break;
            case GREY:
                activity.setTheme(R.style.Grey);
                mMarkerHue = 0;
                mSelectedMarkerHue = 0;
                break;
            case BLUE_GREY:
                activity.setTheme(R.style.BlueGrey);
                mMarkerHue = 200;
                mSelectedMarkerHue = 20;
                break;
        }

        // store the marker hues in sharedpreferences
        mEditor = preferences.edit();
        mEditor.putFloat("markerHue", mMarkerHue);
        mEditor.putFloat("selectedMarkerHue", mSelectedMarkerHue);
        mEditor.apply();
    }
}