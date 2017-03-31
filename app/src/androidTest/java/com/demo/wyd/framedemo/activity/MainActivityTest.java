package com.demo.wyd.framedemo.activity;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.demo.wyd.framedemo.R;
import com.demo.wyd.framedemo.adapter.WelfareAdapter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.any;

/**
 * Description:
 * Created by wyd on 2017/3/17.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, false, true);

    @Test
    public void updateLayout() throws Exception {
        onView(withId(R.id.btn_gain)).perform(click());
        onView(withText("代码家")).check(matches(isDisplayed()));
        onView(withId(R.id.rv_welfare)).check(matches(isDisplayed()));
        Thread.sleep(4000);
    }

    @Test
    public void testRecyclerView() throws Exception {
        onView(withId(R.id.btn_gain)).perform(click());
//      onData(allOf(is(instanceOf(Welfare.class)), withText("代码家"))).onChildView(withId(R.id.tv_name)).perform(replaceText("niubile5555")).check(matches(isDisplayed()));
//
//        Thread.sleep(4000);
//        onView(withId(R.id.rv_welfare)).perform(RecyclerViewActions.actionOnHolderItem(new CustomViewHolderMatcher(withText("代码家")), ViewActions.scrollTo()));
        onView(withId(R.id.rv_welfare)).perform(RecyclerViewActions.actionOnItemAtPosition(1, ViewActions.swipeDown()));
        Thread.sleep(4000);
    }

    private static class CustomViewHolderMatcher extends TypeSafeDiagnosingMatcher<RecyclerView.ViewHolder> {
        private Matcher<View> itemMatcher = any(View.class);

        CustomViewHolderMatcher(Matcher<View> itemMatcher) {
            this.itemMatcher = itemMatcher;
        }

        @Override
        protected boolean matchesSafely(RecyclerView.ViewHolder item, Description mismatchDescription) {
            mismatchDescription.appendText(" was:").appendValue(item.itemView);
            return WelfareAdapter.ViewHolder.class.isAssignableFrom(item.getClass()) && itemMatcher.matches(item.itemView);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("is assignable from CustomViewHolder:").appendValue(itemMatcher);
        }
    }
}