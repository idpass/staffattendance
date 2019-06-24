package np.com.naxa.staffattendance.settings


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import np.com.naxa.staffattendance.BuildConfig
import np.com.naxa.staffattendance.R
import np.com.naxa.staffattendance.SharedPreferenceUtils
import np.com.naxa.staffattendance.common.KEY_APP_UPDATE
import np.com.naxa.staffattendance.common.KEY_LOGOUT
import np.com.naxa.staffattendance.data.TokenMananger
import np.com.naxa.staffattendance.database.DatabaseHelper
import np.com.naxa.staffattendance.login.LoginActivity
import np.com.naxa.staffattendance.utlils.DialogFactory
import java.util.*


class SettingsFragment : PreferenceFragment(), Preference.OnPreferenceClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        initNavigationPrefs()
    }

    private fun initNavigationPrefs() {


    }

    private fun init() {
        addPreferencesFromResource(R.xml.staff_attendance_preferences)
        val formattedAppName = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME
        findPreference(KEY_APP_UPDATE).title = formattedAppName

        findPreference(KEY_APP_UPDATE).onPreferenceClickListener = this
        findPreference(KEY_LOGOUT).onPreferenceClickListener = this
    }


    override fun onPreferenceClick(preference: Preference): Boolean {
        when (preference.key) {
            KEY_APP_UPDATE -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("market://details?id" + BuildConfig.APPLICATION_ID)
                    startActivity(intent)
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")))
                }
            }
            KEY_LOGOUT -> logoutConfirmDialog()
        }
        return false
    }

    private fun logoutConfirmDialog() {
        val a = DatabaseHelper.getDatabaseHelper().getNewStaffCount(DatabaseHelper.getDatabaseHelper().writableDatabase)
        val b = DatabaseHelper.getDatabaseHelper().getFinalizedCount(DatabaseHelper.getDatabaseHelper().writableDatabase)

        if (a + b > 0) run {
            var msg = "If you logout all your account data including"
            if (a > 0) msg += String.format(Locale.US, "%d un-synced staff(s)", a)
            if (b > 0) msg += String.format(Locale.US, "\n %d finalized attendance(s)", b)
            msg += "\nwill be deleted."
            val btnText = "Delete and logout";

            DialogFactory.createActionDialog(activity, "Caution", msg)
                    .setPositiveButton(btnText) { dialogInterface, i ->
                        dialogInterface.dismiss()
                        clearCache()
                    }
                    .setNegativeButton("Dismiss") { dialogInterface, i -> dialogInterface.dismiss() }
                    .create()
                    .show()
        } else {
            clearCache()
        }


    }

    fun clearCache() {

        TokenMananger.clearToken()
        SharedPreferenceUtils.purge(activity)
        DatabaseHelper.getDatabaseHelper().delteAllRows(DatabaseHelper.getDatabaseHelper().writableDatabase)
        LoginActivity.start(activity)
        activity.finish()
    }

}