package dev.bonch.herehackpurify.activities

import android.graphics.PointF
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.here.android.mpa.common.*
import com.here.android.mpa.mapping.AndroidXMapFragment
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapGesture
import com.here.android.mpa.mapping.MapScreenMarker
import com.here.android.mpa.search.ErrorCode
import com.here.android.mpa.search.Location
import com.here.android.mpa.search.ResultListener
import com.here.android.mpa.search.ReverseGeocodeRequest
import dev.bonch.herehackpurify.R
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.File


class MapActivity : FragmentActivity() {
    // map embedded in the map fragment
    private lateinit var map: Map
    private var m_tap_marker: MapScreenMarker? = null
    private var m_marker_image: Image? = null


    // map fragment embedded in this activity
    private var mapFragment: AndroidXMapFragment? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    private fun initialize() {
        setContentView(R.layout.activity_maps)
        // Search for the map fragment to finish setup by calling init().
        mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapfragment) as AndroidXMapFragment?
        // Set up disk cache path for the map service for this application
// It is recommended to use a path under your application folder for storing the disk cache
        val success =
            MapSettings.setIsolatedDiskCacheRootPath(
                applicationContext.getExternalFilesDir(null).toString() + File.separator.toString() + ".here-maps",
                "PurifyIntent"
            ) /* ATTENTION! Do not forget to update {YOUR_INTENT_NAME} */
        if (!success) {
            Toast.makeText(
                applicationContext,
                "Unable to set isolated disk cache path.",
                Toast.LENGTH_LONG
            )
        } else {
            mapFragment!!.init { error ->
                if (error == OnEngineInitListener.Error.NONE) { // retrieve a reference of the map from the map fragment
                    map = mapFragment!!.map
                    // Set the map center to the Vancouver region (no animation)
                    map.setCenter(
                        GeoCoordinate(59.938537, 30.295882, 0.0),
                        Map.Animation.NONE
                    )
                    m_marker_image = Image()
                    (m_marker_image as Image).setImageResource(R.mipmap.ic_launcher_background)
                    markerToCenter()
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

                                triggerRevGeocodeRequest(map.center)
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
                    // Set the zoom level to the average between min and max
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2)
                } else {
                    println("ERROR: Cannot initialize Map Fragment")
                }
            }
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
                PointF(mWidth / 2, mHeight / 2),
                m_marker_image
            )
            map.addMapObject(m_tap_marker)
        } else {
            //(m_tap_marker as MapScreenMarker).screenCoordinate =
        }
    }
}