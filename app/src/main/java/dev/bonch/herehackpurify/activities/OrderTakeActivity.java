package dev.bonch.herehackpurify.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.MapEngine;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.guidance.TrafficUpdater;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.MapScreenMarker;
import com.here.android.mpa.mapping.MapView;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.DynamicPenalty;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteTta;
import com.here.android.mpa.routing.RoutingError;

import java.io.File;
import java.io.IOException;
import java.util.List;

import dev.bonch.herehackpurify.Main;
import dev.bonch.herehackpurify.R;

public class OrderTakeActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 0x754;

    private static final String[] RUNTIME_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    private MapView m_mapView;
    private Map m_map;
    private Route m_route;
    private TrafficUpdater.RequestInfo m_requestInfo;
    private CoreRouter m_coreRouter;
    private MapRoute m_mapRoute;
    private MapGesture mapGesture;
    private MapScreenMarker m_tap_marker;
    private static Image m_marker_image = new Image();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_cleaners);
        m_mapView = findViewById(R.id.mapView);


        if (hasPermissions(this, RUNTIME_PERMISSIONS)) {
            initMap();
        } else {
            ActivityCompat
                    .requestPermissions(this, RUNTIME_PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();

    }


    private static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                for (int index = 0; index < permissions.length; index++) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {

                        /*
                         * If the user turned down the permission request in the past and chose the
                         * Don't ask again option in the permission request system dialog.
                         */
                        if (!ActivityCompat
                                .shouldShowRequestPermissionRationale(this, permissions[index])) {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                            + " not granted. "
                                            + "Please go to settings and turn on for sample app",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                    + " not granted", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                initMap();
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (MapEngine.isInitialized()) {
            TrafficUpdater.getInstance().enableUpdate(false);
        }

        if (m_requestInfo != null) {
            /*  Cancel request by request Id */
            TrafficUpdater.getInstance().cancelRequest(m_requestInfo.getRequestId());
        }
    }

    /**
     * Initialize m_map engine.
     * After initialization set m_map object for {@link MapView}.
     * Also in callback check if initialization completed successfully
     */
    private void initMap() {
        // Set path of disk cache
        String diskCacheRoot = this.getFilesDir().getPath()
                + File.separator + ".isolated-here-maps";
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }

        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);
        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
            return;
        }

        MapEngine.getInstance().init(new ApplicationContext(getApplicationContext()),
                new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(Error error) {
                        if (error == Error.NONE) {
                            /* get the map object */
                            m_map = new Map();
                            m_map.setCenter(new GeoCoordinate(59.938537,
                                    30.295882), Map.Animation.NONE);
                            m_mapView.setMap(m_map);
                            ss();
                            //m_calculateRouteBtn.setEnabled(true);
                        } else {
                            new AlertDialog.Builder(OrderTakeActivity.this).setMessage(
                                    "Error : " + error.name() + "\n\n" + error.getDetails())
                                    .setTitle("Error")
                                    .setNegativeButton(android.R.string.cancel,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    OrderTakeActivity.this.finish();
                                                }
                                            }).create().show();
                        }
                    }
                });
    }

    private void ss() {
        Main.orderList.forEach(it ->
                {
                    try {
                        m_marker_image.setImageResource(R.drawable.ic_point);
                        m_map.addMapObject(new MapMarker(new GeoCoordinate(
                                it.getPoint().getX(), it.getPoint().getY()
                        )).setIcon(m_marker_image));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        Button button = findViewById(R.id.takeOrder);
        button.setOnClickListener(v -> {
            calculateRoute();
        });
    }

    private void calculateTta() {
        /*
         * Receive arrival time for the whole m_route, if you want to get time only for part of
         * m_route pass parameter in bounds 0 <= m_route.getSublegCount()
         */
        final RouteTta ttaExcluding = m_route.getTtaExcludingTraffic(Route.WHOLE_ROUTE);
        TextView textView = findViewById(R.id.tta_tv);
        textView.setText(String.valueOf(ttaExcluding.getDuration() / 60) + " Минут");
        final RouteTta ttaIncluding = m_route.getTtaIncludingTraffic(Route.WHOLE_ROUTE);

        //      final TextView tvInclude = findViewById(R.id.tvTtaInclude);
//        tvInclude.setText("Tta included: " + String.valueOf(ttaIncluding.getDuration()));

        //    final TextView tvExclude = findViewById(R.id.tvTtaExclude);
        //  tvExclude.setText("Tta excluded: " + String.valueOf(ttaExcluding.getDuration()));
    }

    private void calculateTtaUsingDownloadedTraffic() {
        /* Turn on traffic updates */
        TrafficUpdater.getInstance().enableUpdate(true);

        m_requestInfo = TrafficUpdater.getInstance().request(
                m_route, new TrafficUpdater.Listener() {
                    @Override
                    public void onStatusChanged(TrafficUpdater.RequestState requestState) {
                        final RouteTta ttaDownloaded = m_route.getTtaUsingDownloadedTraffic(
                                Route.WHOLE_ROUTE);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final TextView tvDownload = findViewById(R.id.tvTtaDowload);

                                if (tvDownload != null) {
                                    tvDownload.setText("Tta downloaded: " +
                                            String.valueOf(ttaDownloaded.getDuration()));
                                }
                            }
                        });
                    }
                });
    }

    private void calculateRoute() {
        /* Initialize a CoreRouter */
        m_coreRouter = new CoreRouter();

        /* For calculating traffic on the m_route */
        DynamicPenalty dynamicPenalty = new DynamicPenalty();
        dynamicPenalty.setTrafficPenaltyMode(Route.TrafficPenaltyMode.OPTIMAL);
        m_coreRouter.setDynamicPenalty(dynamicPenalty);

        final RoutePlan routePlan = RouteUtil.createRoute();
        m_coreRouter.calculateRoute(routePlan,
                new RouteUtil.RouteListener<List<RouteResult>, RoutingError>() {
                    @Override
                    public void onCalculateRouteFinished(List<RouteResult> routeResults,
                                                         RoutingError routingError) {
                        /* Calculation is done. Let's handle the result */
                        if (routingError == RoutingError.NONE) {
                            /* Get route fro results */
                            m_route = routeResults.get(0).getRoute();
                            TextView textView = findViewById(R.id.dist_tv);
                            textView.setText(String.format("%.1f", m_route.getLength() / 1000f) + " Км");
                            /* check if map route is already on map and if it is,
                                delete it.
                             */
                            if (m_mapRoute != null) {
                                m_map.removeMapObject(m_mapRoute);
                            }

                            /* Create a MapRoute so that it can be placed on the map */
                            m_mapRoute = new MapRoute(routeResults.get(0).getRoute());

                            /* Add the MapRoute to the map */
                            m_map.addMapObject(m_mapRoute);

                            /*
                             * We may also want to make sure the map view is orientated properly so
                             * the entire route can be easily seen.
                             */
                            m_map.zoomTo(m_route.getBoundingBox(), Map.Animation.NONE, 15);

                            /* Get TTA */
                            calculateTta();
                            calculateTtaUsingDownloadedTraffic();
                        } else {
                            Toast.makeText(getApplicationContext(), routingError.name(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
