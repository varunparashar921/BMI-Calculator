package pl.edu.pwr.swim.marcinkiewicz.szymon.bmi;

import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ButtonClickReactionTest{
    //GIVEN
    private float testMass = 100;//kg
    private float testHeight = 1.8f;//m
    private float result = 30.9f;       //Counted from equation.
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testCheckIfCorrectAnswer(){
        //WHEN
        //Set mass and height to ET
        onView(withId(R.id.heightET)).perform(typeText(String.valueOf(testHeight)));
        onView(withId(R.id.massET)).perform(typeText(String.valueOf(testMass)));
        //Click button
        onView(withId(R.id.countBMIBT)).perform(click());
        //THEN
        //Check results
        onView(withId(R.id.resultTV)).check(matches(withText(String.valueOf(result))));
    }
}