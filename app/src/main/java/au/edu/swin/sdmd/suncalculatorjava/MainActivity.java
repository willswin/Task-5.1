package au.edu.swin.sdmd.suncalculatorjava;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private Toolbar toolbar;
    Fragment selectedFragment = null;

    private static final String TAG = "MainActivity";
    private static final int ERRROR_DIALOG_REQUEST = 9001;

    private String timeZoneName = "Australia/Melbourne";
    private String theLocations = "Melbourne";
    private double theLatitude  = -37.8136;
    private double theLongitude  = 144.9631;
    private boolean single = true;


    private ArrayList<String> suburbSortArr = new ArrayList<>();
    private ArrayList<String> suburbArr = new ArrayList<>();
    private ArrayList<String> latArr = new ArrayList<>();
    private ArrayList<String> lonArr = new ArrayList<>();
    private ArrayList<String> timeZoneArr = new ArrayList<>();

    public void ImportLocations(int file) {
        InputStream is = this.getResources().openRawResource(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        suburbArr.clear();
        latArr.clear();
        lonArr.clear();
        timeZoneArr.clear();

        try {
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(",");
                suburbArr.add(splitLine[0]);
                latArr.add(splitLine[1]);
                lonArr.add(splitLine[2]);
                timeZoneArr.add(splitLine[3]);
            }
            is.close();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> suburbSort2Arr = new ArrayList<>(suburbArr);
        suburbSortArr = suburbSort2Arr;
        Collections.sort(suburbSortArr, String.CASE_INSENSITIVE_ORDER);

    }

    public void ImportFileLocations() {
        FileInputStream fis = null;
        suburbArr.clear();
        latArr.clear();
        lonArr.clear();
        timeZoneArr.clear();
        Boolean custom = false;

        try
        {
            fis = openFileInput("Locations");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(",");
                if (splitLine[0].equals("Essendon") || splitLine[0].equals("Hawthorn"))
                {
                    custom = true;
                }
                suburbArr.add(splitLine[0]);
                latArr.add(splitLine[1]);
                lonArr.add(splitLine[2]);
                timeZoneArr.add(splitLine[3]);
            }
            if (custom == false)
            {
                suburbArr.add("Essendon");
                latArr.add("-37.7505");
                lonArr.add("144.9143");
                timeZoneArr.add("Australia/Melbourne");
                suburbArr.add("Hawthorn");
                latArr.add("-37.8226");
                lonArr.add("145.0354");
                timeZoneArr.add("Australia/Melbourne");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally
        {
            if (fis != null)
            {
                try
                {
                    fis.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        ArrayList<String> suburbSort2Arr = new ArrayList<>(suburbArr);
        suburbSortArr = suburbSort2Arr;
        Collections.sort(suburbSortArr, String.CASE_INSENSITIVE_ORDER);
    }

    public void SaveFileLocations()
    {
        FileOutputStream fos = null;
        try{
            File dir = getFilesDir();
            File file = new File(dir, "Locations");
            file.delete();

            fos = openFileOutput("Locations", MODE_PRIVATE);
            StringBuilder sb = new StringBuilder();
            Boolean duplicate = false;
            for (int i=0; i< suburbArr.size(); i++)
            {
                if (theLocations.equals(suburbArr.get(i)))
                {
                    duplicate = true;
                }

                sb.append(suburbArr.get(i)).append(",");
                sb.append(latArr.get(i)).append(",");
                sb.append(lonArr.get(i)).append(",");
                sb.append(timeZoneArr.get(i));
                sb.append("\n");
            }
            if (duplicate == false)
            {
                sb.append(theLocations).append(",");
                sb.append(theLatitude).append(",");
                sb.append(theLongitude).append(",");
                System.out.println(timeZoneName + "testlol");
                sb.append(timeZoneName);
                sb.append("\n");
            }

            fos.write(sb.toString().getBytes());
            Toast.makeText(this, "File Saved "+ getFilesDir(), Toast.LENGTH_LONG).show();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ImportLocations(R.raw.au_locations);
        ImportFileLocations();
        selectedFragment = new SingleFragment();
        initializeUI();
    }

    private void updateFrag(){

        Bundle bundle = new Bundle();
        bundle.putString("tzname", timeZoneName);
        bundle.putDouble("latitude", theLatitude);
        bundle.putDouble("longitude",theLongitude);

        selectedFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.single_date:
                            selectedFragment = new SingleFragment();
                            single = true;
                            break;
                        case R.id.date_range:
                            selectedFragment = new RangeFragment();
                            single = false;
                            break;
                    }
                    initializeUI();
                    return true;
                }
            };

    public void showPopup(){
        View menuItemView = findViewById(R.id.action_location);
        PopupMenu popup = new PopupMenu(this, menuItemView);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);


        Menu theMenu = popup.getMenu();
        for (int i=0; i<suburbSortArr.size(); i++)
        {
            theMenu.add(suburbSortArr.get(i));
        }


        popup.show();


    }

    public void getMapPoints(double lat, double lon) {
        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        theLatitude = lat;
        theLongitude = lon;
        theLocations = getAddress(getBaseContext(),lat, lon );

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        String url = "https://maps.googleapis.com/maps/api/timezone/json?location="+theLatitude+","+theLongitude+"&timestamp="+ts+"&key=AIzaSyAJRAeb8xHr9ZVBLFrM3t-BaUTvwuMGNsU";


        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {
                    timeZoneName = response.getString("timeZoneId");
                    SaveFileLocations();
                    ImportFileLocations();
                    refreshFragment();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("TZ", "JSON: " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e,
                                  JSONObject response) {

                Log.d("TZ", "Request fail! Status code: " + statusCode);
                Log.d("TZ", "Fail response: " + response);
                Log.e("ERROR", e.toString());

                Toast.makeText(MainActivity.this, "Request Failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showMap(){
        Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
        selectedFragment = new MapFragment();
        initializeUI();
        TextView locTV = findViewById(R.id.locationTV);
        locTV.setText("Choose Location");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);

        return true;
    }

    private void initializeUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView locTV = findViewById(R.id.locationTV);
        locTV.setText(theLocations);

        updateFrag();

        BottomNavigationView bottomNav = findViewById(R.id.bott_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    public String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);

            String add = obj.getCountryName();
            add = add + "/" + obj.getLocality();

            return add;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_location:
                showPopup();
                return true;

            case R.id.action_map:
                showMap();
                return true;

            case R.id.action_share:
                Toast.makeText(this, "Sharing", Toast.LENGTH_SHORT).show();
                SingleFragment fragment = new SingleFragment();
                String toSend = fragment.myAction();

                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String sharebody = toSend;
                String sharesub = "Sunrise and Sunset";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
                myIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
                startActivity(Intent.createChooser(myIntent, "Share Using"));

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }






    private void refreshFragment()
    {
        if (single) {
            selectedFragment = new SingleFragment();
        }
        else
        {
            selectedFragment = new RangeFragment();
        }
        initializeUI();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {


        int position = suburbArr.indexOf(menuItem.toString());
        theLocations = suburbArr.get(position);
        timeZoneName = timeZoneArr.get(position);
        theLongitude  = Double.parseDouble(lonArr.get(position));
        theLatitude  = Double.parseDouble(latArr.get(position));
        refreshFragment();
        return true;
    }
}
