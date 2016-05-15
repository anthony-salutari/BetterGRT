package com.asal.bettergrt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Method;

/**
 * Created by Anthony on 5/14/2016.
 */
public class Utilities {
    public static int mTheme;

    public static final int RED = 0;
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

    public static void changeTheme(Activity activity, int theme) {
        mTheme = theme;

        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static void onCreateChangeTheme(Activity activity) {
        switch (mTheme) {
            case RED:
                activity.setTheme(R.style.Red);
                break;
            case PINK:
                activity.setTheme(R.style.Pink);
                break;
            case PURPLE:
                activity.setTheme(R.style.Purple);
                break;
            case DEEP_PURPLE:
                activity.setTheme(R.style.DeepPurple);
                break;
            case INDIGO:
                activity.setTheme(R.style.Indigo);
                break;
            case BLUE:
                activity.setTheme(R.style.Blue);
                break;
            case LIGHT_BLUE:
                activity.setTheme(R.style.LightBlue);
                break;
            case CYAN:
                activity.setTheme(R.style.Cyan);
                break;
            case TEAL:
                activity.setTheme(R.style.Teal);
                break;
            case GREEN:
                activity.setTheme(R.style.Green);
                break;
            case LIGHT_GREEN:
                activity.setTheme(R.style.LightGreen);
                break;
            case LIME:
                activity.setTheme(R.style.Lime);
                break;
            case YELLOW:
                activity.setTheme(R.style.Yellow);
                break;
            case AMBER:
                activity.setTheme(R.style.Amber);
                break;
            case ORANGE:
                activity.setTheme(R.style.Orange);
                break;
            case DEEP_ORANGE:
                activity.setTheme(R.style.DeepOrange);
                break;
            case BROWN:
                activity.setTheme(R.style.Brown);
                break;
            case GREY:
                activity.setTheme(R.style.Grey);
                break;
            case BLUE_GREY:
                activity.setTheme(R.style.BlueGrey);
                break;
        }
    }
}