package com.example.storyapp.view.login

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.storyapp.utils.EspressoIdlingResource
import com.example.storyapp.R
import com.example.storyapp.view.main.MainActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {
    private val dummyEmail = "syarif@gmail.com"
    private val dummyPassword = "12345678"

    private val dummyInvalidEmail = "syarif123"
    private val dummyInvalidPassword = "123"
    private val emptyInput = ""
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val emptyEmail = context.getString(R.string.fill_email)
    private val invalidEmail = context.getString(R.string.invalid_email)
    private val invalidPassword = context.getString(R.string.password_min_length)
    private val logoutTitle = context.getString(R.string.logout)
    private val logouttxtbtn = context.getString(R.string.yes)

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        ActivityScenario.launch(LoginActivity::class.java)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun assertEmptyEmail() {
        // pengecekan input untuk email
        onView(withId(R.id.loginemailEditText)).perform(typeText(emptyInput), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.loginemailEditText)).check(matches(hasErrorText(emptyEmail)))
    }

    @Test
    fun assertInvalidLoginData() {
        onView(withId(R.id.loginemailEditText)).perform(
            typeText(dummyInvalidEmail),
            closeSoftKeyboard()
        )
        onView(withId(R.id.loginemailEditText)).check(matches(hasErrorText(invalidEmail)))

        onView(withId(R.id.passwordEditText)).perform(
            typeText(dummyInvalidPassword),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordEditText)).check(matches(hasErrorText(invalidPassword)))
        onView(withId(R.id.loginButton)).perform(click())
        onView(withText("StoryApp")).check(matches(isDisplayed()))
    }

    //test proses login sampai logout
    @Test
    fun login_logout_success_mechanism() {
        Intents.init()
        onView(withId(R.id.loginemailEditText)).perform(
            typeText(dummyEmail),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordEditText)).perform(
            typeText(dummyPassword),
            closeSoftKeyboard()
        )
        onView(withId(R.id.loginButton)).perform(click())
        onView(withText("StoryApp")).check(matches(isDisplayed()))
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())
        intended(hasComponent(MainActivity::class.java.name))
        onView(withId(R.id.rvStories)).check(matches(isDisplayed()))

        onView(withId(R.id.logout))
            .perform(click())
        onView(withText(logoutTitle)).check(matches(isDisplayed()))
        onView(withText(logouttxtbtn)).inRoot(isDialog()).check(matches(isDisplayed()))
            .perform(click())
        intended(hasComponent(LoginActivity::class.java.name))

    }


}