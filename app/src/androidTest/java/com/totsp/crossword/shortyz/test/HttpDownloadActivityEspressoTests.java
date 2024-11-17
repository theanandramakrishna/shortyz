package com.totsp.crossword.shortyz.test;

import android.app.Activity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.runner.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.totsp.crossword.HttpDownloadActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HttpDownloadActivityEspressoTests {
    // Pick the Activity to test
    @Rule
    public ActivityScenarioRule<HttpDownloadActivity> activityRule =
            new ActivityScenarioRule<>(HttpDownloadActivity.class);

    @Test
    public void listGoesOverTheFold() {
        //onView(withId("Hello world!")).check(matches(isDisplayed()));
    }
}

