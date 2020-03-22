package dev.bonch.herehackpurify.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.here.android.mpa.common.*
import com.here.android.mpa.common.LocationDataSourceHERE.IndoorPositioningMode
import com.here.android.mpa.common.LocationDataSourceHERE.IndoorPositioningModeSetResult
import com.here.android.mpa.common.PositioningManager.*
import com.here.android.mpa.mapping.*
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.Map.OnTransformListener
import com.here.android.mpa.search.ErrorCode
import com.here.android.mpa.search.ReverseGeocodeRequest
import com.here.android.positioning.StatusListener
import com.here.android.positioning.StatusListener.ServiceError
import dev.bonch.herehackpurify.R
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.File
import java.lang.ref.WeakReference
import java.util.*


class LocationActivity : AppCompatActivity(), OnPositionChangedListener,
    OnTransformListener {
    // map embedded in the map fragment
    private var map: Map? = null
    // map fragment embedded in this activity
    private var mapFragment: AndroidXMapFragment? = null
    // positioning manager instance
    private var mPositioningManager: PositioningManager? = null
    // HERE location data source instance
    private var mHereLocation: LocationDataSourceHERE? = null
    // flag that indicates whether maps is being transformed
    private var mTransforming = false
    // callback that is called when transforming ends
    private var mPendingUpdate: Runnable? = null
    // text view instance for showing location information
    private var mLocationInfo: TextView? = null

    private var m_tap_marker: MapScreenMarker? = null

    private var m_marker_image: Image? = null

    private var isFirst = true


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasPermissions(
                this,
                *RUNTIME_PERMISSIONS
            )
        ) {
            initializeMapsAndPositioning()
        } else {
            ActivityCompat
                .requestPermissions(
                    this,
                    RUNTIME_PERMISSIONS,
                    REQUEST_CODE_ASK_PERMISSIONS
                )
        }
        //setLocationMethod()

    }

    override fun onPause() {
        super.onPause()
        if (mPositioningManager != null) {
            mPositioningManager!!.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mPositioningManager != null) {
            mPositioningManager!!.start(LocationMethod.GPS_NETWORK_INDOOR)
        }
    }

    override fun onPositionUpdated(
        locationMethod: LocationMethod,
        geoPosition: GeoPosition,
        mapMatched: Boolean
    ) {
        val coordinate = geoPosition.coordinate
        if (mTransforming) {
            mPendingUpdate =
                Runnable { onPositionUpdated(locationMethod, geoPosition, mapMatched) }
        } else {
            if(isFirst){
                map!!.setCenter(coordinate, Map.Animation.BOW)
                updateLocationInfo(locationMethod, geoPosition)
                isFirst = false
            }

        }
    }

    override fun onPositionFixChanged(
        locationMethod: LocationMethod,
        locationStatus: LocationStatus
    ) { // ignored
    }

    override fun onMapTransformStart() {
        mTransforming = true
    }

    override fun onMapTransformEnd(mapState: MapState) {
        mTransforming = false
        if (mPendingUpdate != null) {
            mPendingUpdate!!.run()
            mPendingUpdate = null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                var index = 0
                while (index < permissions.size) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) { /*
                         * If the user turned down the permission request in the past and chose the
                         * Don't ask again option in the permission request system dialog.
                         */
                        if (!ActivityCompat
                                .shouldShowRequestPermissionRationale(this, permissions[index])
                        ) {
                            Toast.makeText(
                                this, "Required permission " + permissions[index]
                                        + " not granted. "
                                        + "Please go to settings and turn on for sample app",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this, "Required permission " + permissions[index]
                                        + " not granted", Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    index++
                }
                initializeMapsAndPositioning()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun getMapFragment(): AndroidXMapFragment? {
        return supportFragmentManager.findFragmentById(R.id.mapfragment) as AndroidXMapFragment?
    }

    /**
     * Initializes HERE Maps and HERE Positioning. Called after permission check.
     */
    private fun initializeMapsAndPositioning() {
        setContentView(R.layout.activity_maps)
        mLocationInfo = findViewById<View>(R.id.titleText) as TextView
        mapFragment = getMapFragment()
        mapFragment!!.retainInstance = false
        // Set path of disk cache
        val diskCacheRoot = (this.filesDir.path
                + File.separator + ".isolated-here-maps")
        // Retrieve intent name from manifest
        var intentName = ""
        try {
            val ai = packageManager.getApplicationInfo(
                this.packageName,
                PackageManager.GET_META_DATA
            )
            val bundle = ai.metaData
            intentName = bundle.getString("INTENT_NAME")!!
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(
                this.javaClass.toString(),
                "Failed to find intent name, NameNotFound: " + e.message
            )
        }
        val success =
            MapSettings.setIsolatedDiskCacheRootPath(
                diskCacheRoot,
                intentName
            )
        if (!success) { // Setting the isolated disk cache was not successful, please check if the path is valid and
// ensure that it does not match the default location
// (getExternalStorageDirectory()/.here-maps).
// Also, ensure the provided intent name does not match the default intent name.
        } else {
            mapFragment!!.init { error ->
                if (error == OnEngineInitListener.Error.NONE) {
                    map = mapFragment!!.map
                    (map as Map).setCenter(
                        GeoCoordinate(
                            59.938537,
                            30.295882,
                            0.0
                        ), Map.Animation.NONE
                    )
                    m_marker_image = Image()
                    (m_marker_image as Image).setImageResource(R.mipmap.ic_launcher_background)
                    markerToCenter()
                    (map as Map).setZoomLevel((map as Map).getMaxZoomLevel() - 1)
                    (map as Map).addTransformListener(this@LocationActivity)
                    mPositioningManager = getInstance()
                    mHereLocation = LocationDataSourceHERE.getInstance(
                        object : StatusListener {
                            override fun onOfflineModeChanged(offline: Boolean) { // called when offline mode changes
                            }

                            override fun onAirplaneModeEnabled() { // called when airplane mode is enabled
                            }

                            override fun onWifiScansDisabled() { // called when Wi-Fi scans are disabled
                            }

                            override fun onBluetoothDisabled() { // called when Bluetooth is disabled
                            }

                            override fun onCellDisabled() { // called when Cell radios are switch off
                            }

                            override fun onGnssLocationDisabled() { // called when GPS positioning is disabled
                            }

                            override fun onNetworkLocationDisabled() { // called when network positioning is disabled
                            }

                            override fun onServiceError(serviceError: ServiceError) { // called on HERE service error
                            }

                            override fun onPositioningError(positioningError: StatusListener.PositioningError) { // called when positioning fails
                            }

                            override fun onWifiIndoorPositioningNotAvailable() { // called when running on Android 9.0 (Pie) or newer
                            }

                            override fun onWifiIndoorPositioningDegraded() { // called when running on Android 9.0 (Pie) or newer
                            }
                        })
                    if (mHereLocation == null) {
                        Toast.makeText(
                            this@LocationActivity,
                            "LocationDataSourceHERE.getInstance(): failed, exiting",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                    (mPositioningManager as PositioningManager).setDataSource(mHereLocation)
                    (mPositioningManager as PositioningManager).addListener(
                        WeakReference(
                            this@LocationActivity
                        )
                    )
                    // start position updates, accepting GPS, network or indoor positions
                    if ((mPositioningManager as PositioningManager).start(LocationMethod.GPS_NETWORK_INDOOR)) {
                        mapFragment!!.positionIndicator.isVisible = true
                    } else {
                        Toast.makeText(
                            this@LocationActivity,
                            "PositioningManager.start: failed, exiting",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                    (mapFragment as AndroidXMapFragment).mapGesture
                        .addOnGestureListener(object : MapGesture.OnGestureListener {
                            override fun onPanStart() {
                            }

                            override fun onPanEnd() { /* show toast message for onPanEnd gesture callback */
                            }

                            override fun onMultiFingerManipulationStart() {}
                            override fun onMultiFingerManipulationEnd() {}
                            override fun onMapObjectsSelected(list: List<ViewObject>): Boolean {
                                return false
                            }

                            override fun onTapEvent(pointF: PointF): Boolean { /* show toast message for onPanEnd gesture callback */
                                /*
                                         * add map screen marker at coordinates of gesture. if map
                                         * screen marker already exists, change to new coordinate
                                         */

//                                if (m_tap_marker == null) {
//                                    m_tap_marker = MapMarker(map.center, m_marker_image)
//                                    map.addMapObject(m_tap_marker)
//                                } else {
//                                    (m_tap_marker as MapMarker).coordinate = map.center
//                                }

                                triggerRevGeocodeRequest((map as Map).center)
                                return false
                            }

                            override fun onDoubleTapEvent(pointF: PointF): Boolean {
                                return false
                            }

                            override fun onPinchLocked() {}
                            override fun onPinchZoomEvent(
                                v: Float,
                                pointF: PointF
                            ): Boolean {
                                return false
                            }

                            override fun onRotateLocked() {}
                            override fun onRotateEvent(v: Float): Boolean { /* show toast message for onRotateEvent gesture callback */
                                return false
                            }

                            override fun onTiltEvent(v: Float): Boolean {
                                return false
                            }

                            override fun onLongPressEvent(pointF: PointF): Boolean {
                                return false
                            }

                            override fun onLongPressRelease() {}
                            override fun onTwoFingerTapEvent(pointF: PointF): Boolean {
                                return false
                            }
                        }, 0, false)
                } else {}
            }
        }
    }

    /**
     * Update location information.
     * @param geoPosition Latest geo position update.
     */
    private fun updateLocationInfo(
        locationMethod: LocationMethod,
        geoPosition: GeoPosition
    ) {
        if (mLocationInfo == null) {
            return
        }
        val sb = StringBuffer()
        val coord = geoPosition.coordinate
        sb.append("Type: ")
            .append(String.format(Locale.US, "%s\n", locationMethod.name))
        sb.append("Coordinate:").append(
            String.format(
                Locale.US,
                "%.6f, %.6f\n",
                coord.latitude,
                coord.longitude
            )
        )
        if (coord.altitude != GeoCoordinate.UNKNOWN_ALTITUDE.toDouble()) {
            sb.append("Altitude:")
                .append(String.format(Locale.US, "%.2fm\n", coord.altitude))
        }
        if (geoPosition.heading != GeoPosition.UNKNOWN.toDouble()) {
            sb.append("Heading:").append(
                String.format(
                    Locale.US,
                    "%.2f\n",
                    geoPosition.heading
                )
            )
        }
        if (geoPosition.speed != GeoPosition.UNKNOWN.toDouble()) {
            sb.append("Speed:").append(
                String.format(
                    Locale.US,
                    "%.2fm/s\n",
                    geoPosition.speed
                )
            )
        }
        if (geoPosition.buildingName != null) {
            sb.append("Building: ").append(geoPosition.buildingName)
            if (geoPosition.buildingId != null) {
                sb.append(" (").append(geoPosition.buildingId).append(")\n")
            } else {
                sb.append("\n")
            }
        }
        if (geoPosition.floorId != null) {
            sb.append("Floor: ").append(geoPosition.floorId).append("\n")
        }
        sb.deleteCharAt(sb.length - 1)
        //mLocationInfo!!.text = sb.toString()
    }

    /**
     * Called when set location method -menu item is selected.
     */
//    private fun setLocationMethod() {
//        val builder = AlertDialog.Builder(this)
//        val names =
//            resources.getStringArray(R.array.locationMethodNames)
//        builder.setTitle(R.string.title_select_location_method)
//            .setItems(names, DialogInterface.OnClickListener { dialog, which ->
//                try {
//                    val values =
//                        resources.getStringArray(R.array.locationMethodValues)
//                    val method =
//                        LocationMethod.valueOf(values[which])
//                    setLocationMethod(method)
//                } catch (ex: IllegalArgumentException) {
//                    Toast.makeText(
//                        this@BasicPositioningActivity, "setLocationMethod failed: "
//                                + ex.message, Toast.LENGTH_LONG
//                    ).show()
//                } finally {
//                    dialog.dismiss()
//                }
//            })
//        builder.create().show()
//    }

    /**
     * Called when set indoor mode -menu item is selected.
     */

    /**
     * Sets location method for the PositioningManager.
     * @param method New location method.
     */
    private fun setLocationMethod(method: LocationMethod) {
        if (!mPositioningManager!!.start(method)) {
            Toast.makeText(
                this@LocationActivity,
                "PositioningManager.start($method): failed",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        // permissions request code
        private const val REQUEST_CODE_ASK_PERMISSIONS = 1
        private val RUNTIME_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
        )

        /**
         * Only when the app's target SDK is 23 or higher, it requests each dangerous permissions it
         * needs when the app is running.
         */
        private fun hasPermissions(
            context: Context,
            vararg permissions: String
        ): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }
    }

    private fun triggerRevGeocodeRequest(coordinate: GeoCoordinate) {
        //m_resultTextView.setText("")
        /* Create a ReverseGeocodeRequest object with a GeoCoordinate. */
        //val coordinate = GeoCoordinate(49.25914, -123.00777)
        val revGecodeRequest = ReverseGeocodeRequest(coordinate)
        revGecodeRequest.execute { p0, p1 ->
            if (p1 === ErrorCode.NONE) { /*
                             * From the location object, we retrieve the address and display to the screen.
                             * Please refer to HERE Android SDK doc for other supported APIs.
                             */
                // (location.getAddress().toString())
                titleText.text = p0.address.toString()
            } else {
                titleText.text = "ERROR:RevGeocode Request returned error code:$p1"
                //updateTextView("ERROR:RevGeocode Request returned error code:$errorCode")
            }
        }
    }

    private fun markerToCenter(){
        val mWidth: Float =
            resources.displayMetrics.widthPixels.toFloat()
        val mHeight: Float =
            resources.displayMetrics.heightPixels.toFloat()
        if (m_tap_marker == null) {
            m_tap_marker = MapScreenMarker(
                PointF(mWidth / 2, (mHeight / 2)-180),
                m_marker_image
            )
            map!!.addMapObject(m_tap_marker)
        } else {
            //(m_tap_marker as MapScreenMarker).screenCoordinate =
        }
    }
}