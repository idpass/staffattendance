package np.com.naxa.staffattendance.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.transition.Fade
import android.view.View
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_splash_screen2.*
import np.com.naxa.staffattendance.attedancedashboard.AttendancesDashboardActivity
import np.com.naxa.staffattendance.attendence.AttendanceViewPagerActivity
import np.com.naxa.staffattendance.data.TokenMananger
import np.com.naxa.staffattendance.login.LoginActivity


class SplashScreenActivity : AppCompatActivity() {


    private var hanlder = Handler();
    private var runnable = Runnable {
        startActivity(AttendancesDashboardActivity.newIntent(this))

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeActivityFullScreen()
        with(window) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
                exitTransition = Fade()
            }
        }

        setContentView(np.com.naxa.staffattendance.R.layout.activity_splash_screen2)
        crossfade();

    }

    private fun crossfade() {


        splash_logo.alpha = 1f
        splash_logo.visibility = View.VISIBLE
        splash_logo.animate()
                .alpha(0f)
                .setStartDelay(2000L)
                .setDuration(1000L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {

                        if (TokenMananger.doesTokenExist()) {
                            startActivity(AttendancesDashboardActivity.newIntent(this@SplashScreenActivity))
                        }else{
                            LoginActivity.start(this@SplashScreenActivity);
                        }
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                    }
                })
    }


    override fun onPause() {
        super.onPause()
        hanlder.removeCallbacks(runnable);
    }

    private fun makeActivityFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

}
