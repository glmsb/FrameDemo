package com.demo.wyd.framedemo.activity;

import android.support.test.runner.AndroidJUnit4;

import com.demo.wyd.framedemo.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Description:
 * Created by wyd on 2017/3/17.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest{
    @Test
    public void onCreate() throws Exception {
        onView(withId(R.id.btn_gain)).perform(click());
    }

    @Test
    public void updateLayout() throws Exception {
        matches(withId(R.id.rv_welfare));
//        LauncherActivity()
    }

}