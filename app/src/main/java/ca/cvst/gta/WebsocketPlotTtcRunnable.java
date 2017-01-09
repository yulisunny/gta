package ca.cvst.gta;

import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by harryyu on 2017-01-09.
 */

public class WebsocketPlotTtcRunnable implements Runnable {
    private final JSONObject ttcVehicle;
    private Bitmap ttcIcon;
    private int index;
    private ArrayList<Marker> ttcMarkers;
    private Map<Integer, Integer> ttcInvertedIndex;
    private boolean ttcIsChecked;
    private GoogleMap mMap;

    public WebsocketPlotTtcRunnable(JSONObject ttcVehicle,
                                    Map<Integer, Integer> ttcInvertedIndex,
                                    int index,
                                    ArrayList<Marker> ttcMarkers,
                                    GoogleMap mMap,
                                    boolean ttcIsChecked,
                                    Bitmap ttcIcon) {
        this.ttcVehicle = ttcVehicle;
        this.ttcInvertedIndex = ttcInvertedIndex;
        this.index = index;
        this.ttcMarkers = ttcMarkers;
        this.mMap = mMap;
        this.ttcIsChecked = ttcIsChecked;
        this.ttcIcon = ttcIcon;
    }

    @Override
    public void run() {
        try {
            // data format:
            // {"id":8526,
            // "timestamp":1483736583,
            // "routeNumber":"35",
            // "category":"ttc",
            // "predictable":true,
            // "dateTime":"2017-01-06 21:03:03+00:00",
            // "name":"35-Jane",
            // "lastTime":"2017-01-06 21:03:01+00:00",
            // "GPStime":1483736571,"dirTag":"35_0_35D",
            // "heading":"162",
            // "coordinates":[-79.531799,43.7945179]}

            JSONObject data = ttcVehicle.getJSONObject("data");
            int vehicle_id = data.getInt("id");
            if (ttcInvertedIndex.containsKey(vehicle_id)) {
                int arrayIndex = ttcInvertedIndex.get(vehicle_id);
                Marker m = ttcMarkers.get(arrayIndex);

                JSONArray coordinates = data.getJSONArray("coordinates");
                LatLng location = new LatLng(coordinates.getDouble(1), coordinates.getDouble(0));
                m.setPosition(location);

                String direction = Helper.calculateDirection(Integer.parseInt(data.getString("heading")));

                String dateTime = Helper.convertTimestampToString(data.getLong("GPStime"));

                m.setSnippet("Bus ID: " + vehicle_id + '\n' + "Direction: " + direction + '\n' + "Time: " + dateTime);
            } else {
                ttcInvertedIndex.put(vehicle_id, index);

                JSONArray coordinates = data.getJSONArray("coordinates");
                LatLng location = new LatLng(coordinates.getDouble(1), coordinates.getDouble(0));

                String route_name = data.getString("name");

                String direction = Helper.calculateDirection(Integer.parseInt(data.getString("heading")));

                String dateTime = Helper.convertTimestampToString(data.getLong("GPStime"));

                ttcMarkers.add(mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(ttcIcon))
                        .title(route_name)
                        .snippet("Bus ID: " + vehicle_id + '\n' + "Direction: " + direction + "Time: " + dateTime)
                        .visible(ttcIsChecked)));
                index = index + 1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
