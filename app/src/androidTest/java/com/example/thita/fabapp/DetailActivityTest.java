package com.example.thita.fabapp;

import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {

    private static final String TITLE_ORDER = "MY ORDER";
    private static final String QUANTITY = "2";
    private static final String UINT = "Kg";
    private static String PACKAGE_ANDROID_DIALER = "com.example.thita.fabapp";

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Starting with Android Lollipop the dialer package has changed.
            PACKAGE_ANDROID_DIALER = "com.example.thita.fabapp";
        }
    }

    @Rule
    public IntentsTestRule<DetailActivity> mActivityTestRule = new IntentsTestRule<>(DetailActivity.class);

    @Test
    public void typeNumber_ValidInput_InitiatesCall() {

        onView(withId(R.id.title_editText))
                .perform(typeText(TITLE_ORDER), closeSoftKeyboard());
        onView(withId(R.id.quantity_text))
                .perform(typeText(QUANTITY), closeSoftKeyboard());
        onView(withId(R.id.unit_text))
                .perform(typeText(UINT), closeSoftKeyboard());
        onView(withId(R.id.order_btn)).perform(click());
    }

}
