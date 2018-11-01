package np.com.naxa.staffattendance.common

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.PRIORITY_MAX
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import np.com.naxa.staffattendance.R
import pub.devrel.easypermissions.EasyPermissions
import java.text.DecimalFormat

@SuppressLint("LogNotTimber", "MissingPermission")
class GeoPointForegroundService : Service(), LocationListener {
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        when (status) {
            LocationProvider.AVAILABLE -> if (location != null) {
                val message = applicationContext.getString(R.string.location_provider_accuracy, this.location!!.getProvider(), truncateDouble(this.location!!.getAccuracy()))
                updateNotification(message)
                Log.i(TAG, message);
            }
            LocationProvider.OUT_OF_SERVICE -> {
            }
            LocationProvider.TEMPORARILY_UNAVAILABLE -> {
            }
        }
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private val TAG_FOREGROUND_SERVICE = "GeoPointService"

    val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"

    val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"

    private val LOCATION_COUNT = "locationCount"

    val LOCATION = "gp"
    val ACCURACY_THRESHOLD = "accuracyThreshold"
    val READ_ONLY = "readOnly"

    val DEFAULT_LOCATION_ACCURACY = 25.0

    val TAG = this.javaClass.simpleName

    private var locationManager: LocationManager? = null
    private var location: Location? = null
    private var gpsOn = false
    private var networkOn = false
    private var locationAccuracy: Double = 0.toDouble()
    private var locationCount = 0

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not supported yet")
    }


    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (!hasPermission()) {
            stopForegroundService()
        }

        val action = intent?.action
        when (action) {
            ACTION_START_FOREGROUND_SERVICE -> {

                startForeground()
                setupLocationServices(intent);
                Toast.makeText(applicationContext, "Capturing location", Toast.LENGTH_LONG).show()
            }
            ACTION_STOP_FOREGROUND_SERVICE -> {
                stopForegroundService()
                Toast.makeText(applicationContext, "Foreground service is stopped.", Toast.LENGTH_LONG).show()
            }
        }


        return super.onStartCommand(intent, flags, startId)
    }

    private fun setupLocationServices(intent: Intent?) {

        locationAccuracy = DEFAULT_LOCATION_ACCURACY
        if (intent != null && intent.getExtras() != null) {
            if (intent.hasExtra(ACCURACY_THRESHOLD)) {
                locationAccuracy = intent.getDoubleExtra(ACCURACY_THRESHOLD,
                        DEFAULT_LOCATION_ACCURACY)
            }
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        gpsOn = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        networkOn = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!gpsOn && !networkOn) {
            exitWithMessage(R.string.provider_disabled_error)
        }

        if (gpsOn) {
            val loc = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (loc != null) {
                Log.i(TAG, "GeoPointActivity: " + System.currentTimeMillis()
                        + " lastKnownLocation(GPS) lat: "
                        + loc.latitude + " long: "
                        + loc.longitude + " acc: "
                        + loc.accuracy)

            } else {
                Log.i(TAG, "GeoPointActivity: " + System.currentTimeMillis()
                        + " lastKnownLocation(GPS) null location")
            }
        }

        if (networkOn) {
            val loc = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (loc != null) {
                Log.i(TAG, "GeoPointActivity: " + System.currentTimeMillis()
                        + " lastKnownLocation(Network) lat: "
                        + loc.latitude + " long: "
                        + loc.longitude + " acc: "
                        + loc.accuracy)
            } else {
                Log.i(TAG, "GeoPointActivity: " + System.currentTimeMillis()
                        + " lastKnownLocation(Network) null location")

            }
        }


        if (locationManager != null) {
            if (gpsOn) {
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
            }
            if (networkOn) {
                locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        // stops the GPS. Note that this will turn off the GPS if the screen goes to sleep.
        if (locationManager != null) {
            locationManager!!.removeUpdates(this)
        }


    }


    override fun onLocationChanged(location: Location?) {
        this.location = location
        if (this.location != null) {
            // Bug report: cached GeoPoint is being returned as the first value.
            // Wait for the 2nd value to be returned, which is hopefully not cached?
            ++locationCount
            Log.i(TAG, "GeoPointActivity: " + System.currentTimeMillis()
                    + " onLocationChanged(" + locationCount + ") lat: "
                    + this.location!!.getLatitude() + " long: "
                    + this.location!!.getLongitude() + " acc: "
                    + this.location!!.getAccuracy())

            if (locationCount > 1) {
                val message = applicationContext.getString(R.string.location_provider_accuracy, this.location!!.getProvider(), truncateDouble(this.location!!.getAccuracy()))
                Log.i(TAG, message);
                updateNotification(message)

                if (this.location!!.getAccuracy() <= locationAccuracy) {
                    returnLocation()
                }
            }
        } else {
            Log.i(TAG, "GeoPointActivity: " + System.currentTimeMillis()
                    + " onLocationChanged(" + locationCount + ") null location")

        }
    }

    private fun truncateDouble(number: Float): String {
        val df = DecimalFormat("#.##")
        return df.format(number.toDouble())
    }


    private fun returnLocation() {
        if (location != null) {

            val message = location!!.getLatitude().toString() + " " + location!!.getLongitude() + " " + location!!.getAltitude() + " " + location!!.getAccuracy()
            Log.i(TAG, message);

            val intent =  Intent ("location_result"); //put the same message as in the filter you used in the activity when registering the receiver
            intent.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            exitWithMessage("Location captured")
        }

    }


    fun exitWithMessage(@StringRes resId: Int) {
        exitWithMessage(getString(resId))
    }

    fun exitWithMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        stopForegroundService()
    }

    private fun startForeground() {

        val notification = generateNotification("");

        startForeground(101, notification)
    }

    private fun generateNotification(message: String): Notification {

        val title = "Capturing location"
        val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel("geopoint", "GeoPointService")
                } else {
                    // If earlier version channel ID is not used
                    // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                    ""
                }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentTitle(title)
                .setContentText(message)
                .build()

        return notification;
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }


    fun updateNotification(message: String) {

        val notification = generateNotification(message)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;

        mNotificationManager.notify(101, notification);
    }

    private fun stopForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.")

        stopForeground(true)
        stopSelf()
    }

    companion object {
        @JvmField
        var ACTION_START_FOREGROUND_SERVICE: String = "ACTION_START_FOREGROUND_SERVICE"
    }

    fun hasPermission(): Boolean {
        return EasyPermissions.hasPermissions(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION);
    }
}