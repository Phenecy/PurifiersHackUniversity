package dev.bonch.herehackpurify.activities;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;

import dev.bonch.herehackpurify.Main;

public class RouteUtil {


    public static RoutePlan createRoute() {
        /* Initialize a RoutePlan */
        final RoutePlan routePlan = new RoutePlan();

        /*
         * Initialize a RouteOption. HERE Mobile SDK allow users to define their own parameters for the
         * route calculation,including transport modes,route types and route restrictions etc.Please
         * refer to API doc for full list of APIs
         */
        final RouteOptions routeOptions = new RouteOptions();
        /* Other transport modes are also available e.g Pedestrian */
        routeOptions.setTransportMode(RouteOptions.TransportMode.PEDESTRIAN);
        /* Disable highway in this route. */
        routeOptions.setHighwaysAllowed(false);
        /* Calculate the shortest route available. */
        routeOptions.setRouteType(RouteOptions.Type.BALANCED);
        /* Calculate 1 route. */
        routeOptions.setRouteCount(1);
        /* Finally set the route option */
        routePlan.setRouteOptions(routeOptions);

        /* Define waypoints for the route */
        /* START: Holländerstraße, Wedding, 13407 Berlin */


        double x = Main.point.getX();
        double y = Main.point.getY();

        RouteWaypoint startPoint = new RouteWaypoint(
                new GeoCoordinate(x, y));

        /* MIDDLE: Lynarstraße 3 */
//        RouteWaypoint middlePoint = new RouteWaypoint(
//                new GeoCoordinate(52.54172, 13.36354));

        /* END: Agricolastraße 29, 10555 Berlin */
        RouteWaypoint destination = new RouteWaypoint(
                new GeoCoordinate(Main.bin.getX(), Main.bin.getY()));

        /* Add both waypoints to the route plan */
        routePlan.addWaypoint(startPoint);
//        routePlan.addWaypoint(middlePoint);
        routePlan.addWaypoint(destination);

        return routePlan;
    }

    static abstract class RouteListener<T, U extends Enum<?>> implements Router.Listener<T, U> {
        @Override
        public void onProgress(int i) {
            /* The calculation progress can be retrieved in this callback. */
        }
    }
}

