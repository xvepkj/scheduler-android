package com.example.scheduler.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.scheduler.R
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment

class SchedulerIntro : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make sure you don't call setContentView!

        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        addSlide(
            AppIntroFragment.newInstance(
            title = "Welcome!",
            description = "Scheduler is an app to increase your productivity and " +
                    "help you maintain your schedule.",
             imageDrawable = R.drawable.clock_image,
             backgroundDrawable = R.drawable.intro_screen1
        ))
        addSlide(
            AppIntroFragment.newInstance(
                title = "Add Events",
                description = "Events are of three types untracked, tracked and logged with the two latter" +
                        " ones being trackable. You can add tags for events in the Stats Screen.",
                imageDrawable = R.drawable.add_event,
                backgroundDrawable = R.drawable.intro_screen2
            ))
        addSlide(
            AppIntroFragment.newInstance(
                title = "Add Templates",
                description = "Templates are a set of activities that you can apply according to" +
                        " different criteria in the templates screen.",
                imageDrawable = R.drawable.template,
                backgroundDrawable = R.drawable.intro_screen1
            ))
        addSlide(
            AppIntroFragment.newInstance(
                title = "Active Templates",
                description = "Active Templates are applied instances of a template. You can remove these" +
                        " in the templates screen.",
                imageDrawable = R.drawable.active_templates,
                backgroundDrawable = R.drawable.intro_screen2
            ))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        finish()
    }
}