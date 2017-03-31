package com.demo.wyd.framedemo.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;

import com.demo.wyd.framedemo.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Description:
 * Created by wyd on 2017/3/23.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest1 {
    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, false);


    // 使用合法用户名和密码登陆 ,登陆成功,进入主页
    @Test
    public void test1() throws Exception {
        Intent intent = new Intent();
        rule.launchActivity(intent);

        onView(withId(R.id.email)).perform(typeText("10010@sina.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());

        onView(withId(R.id.btn_gain)).check(matches(withText("获取福利")));
    }

    //使用错误的用户名登陆 ,显示用户名或密码错误提示信息
    @Test
    public void test2() throws InterruptedException {
        Intent intent = new Intent();
        rule.launchActivity(intent);

        //根据id获取某个控件
        Button button = (Button) rule.getActivity().findViewById(R.id.email_sign_in_button);
        onView(withId(R.id.email)).perform(typeText(button.getText().toString()), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());

        Thread.sleep(2000);
        onView(withText(R.string.error_invalid_email))
                .inRoot(withDecorView(not(is(rule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }
}
