package com.demo.wyd.framedemo.activity;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.Button;

import com.demo.wyd.framedemo.R;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Description:
 * Created by wyd on 2017/3/23.
 */
@RunWith(Parameterized.class)
@LargeTest
public class LoginActivityTest {

    @Rule //定义规则,用来指明被测试的Activity;
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(LoginActivity.class, true, false);

    @Parameterized.Parameter
    public String email;


    @Parameterized.Parameters
    public static Collection<String> initParams() {
        List<String> email = new ArrayList<>();
        email.add("Android");
        email.add("中文测试");
        email.add("1234@qq.com");
        return email;
    }


   /* @Parameterized.Parameters
    public Map<String,String> initParams(){
        Map<String,String> params = new HashMap<>();
        params.put("email","123@qq.com");
        params.put("password","123456");
        return params;
    }*/


    /**
     * onView(ViewMatchers)
     * .perform(ViewAction)
     * .check(ViewAssertions)
     */
    @Test //测试用例
    public void testLogin() throws Exception {

        // 清除 之前输入点用户名和密码
        onView(withId(R.id.email)).perform(clearText());
        onView(withId(R.id.password)).perform(clearText());

        // 输入用户名和密码并关闭软键盘
        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(getButtonText(withId(R.id.email_sign_in_button))), closeSoftKeyboard());

        // 触发登录按钮的点击事件
        onView(withId(R.id.email_sign_in_button)).perform(click());

        //通过断言判断是否登录成功，这里通过会话列表是否显示来判断
//        onView(withId(R.id.login_progress)).check(not(matches(isDisplayed())));
//        activityRule.launchActivity(new Intent(activityRule.getActivity(),MainActivity.class));
    }

    /**
     * 获取某个控件上的文字
     */
    public String getButtonText(Matcher<View> matcher) {
        final String[] text = new String[1];

        onView(matcher).perform(new ViewAction() {

            //识别所操作的对象类型
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(Button.class);
            }

            //视图操作的一个描述
            @Override
            public String getDescription() {
                return "getting text from a Button";
            }

            //实际的一个操作，获取到操作的对象
            @Override
            public void perform(UiController uiController, View view) {
                Button button = ((Button) view);
                text[0] = button.getText().toString();
            }
        });
        return text[0];
    }

}