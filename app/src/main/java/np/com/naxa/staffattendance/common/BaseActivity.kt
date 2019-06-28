package np.com.naxa.staffattendance.common

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_dashboard_attedance.*
import np.com.naxa.staffattendance.R

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    protected fun setupToolbar(title: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_toolbar_back)
        tv_screen_name.text = title

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed();
        }
        return super.onOptionsItemSelected(item)
    }



}
