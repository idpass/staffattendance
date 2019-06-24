package np.com.naxa.staffattendance.settings

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_dashboard_attedance.*
import np.com.naxa.staffattendance.R
import np.com.naxa.staffattendance.attedancedashboard.AttendancesDashboardActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupToolbar(title = getString(R.string.title_activity_settings))
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed();
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar(title: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_toolbar_back)
        tv_screen_name.text = title

    }


    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, SettingsActivity::class.java)
            return intent
        }
    }
}
