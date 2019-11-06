package au.edu.swin.sdmd.suncalculatorjava;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.TimeZone;

public class MapFragment extends Fragment{

    private MapView mapView;
    private String theLocations = "Melbourne";
    private double theLongitude = 145;
    private double theLatitude = -37;
    private Button button;
    private GoogleMap mMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        theLatitude = getArguments().getInt("latitude");
        theLongitude = getArguments().getInt("longitude");
        System.out.println(theLatitude+" and "+theLongitude);
        View view = inflater.inflate(R.layout.map_layout, container, false);

        mapView = view.findViewById(R.id.map);
        if(mapView != null)
        {
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    mMap = googleMap;

                    googleMap.getUiSettings().setCompassEnabled(true);
                    googleMap.getUiSettings().setRotateGesturesEnabled(true);
                    LatLng location = new LatLng(theLatitude, theLongitude);
                    mMap.addMarker(new MarkerOptions().position(location).title("Marker in "+theLocations));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location));


                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng point) {
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(point));
                            theLatitude = point.latitude;
                            theLongitude = point.longitude;
                        }
                    });
                }
            });
        }

        button=(Button) view.findViewById(R.id.getLocation);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveLocation();

            }
        });

        return view;
    }


    private void SaveLocation() {
        ((MainActivity)getActivity()).getMapPoints(theLatitude, theLongitude);
    }




    public MapFragment(){}

}
