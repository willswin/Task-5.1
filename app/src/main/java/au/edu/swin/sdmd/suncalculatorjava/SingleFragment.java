package au.edu.swin.sdmd.suncalculatorjava;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import au.edu.swin.sdmd.suncalculatorjava.calc.AstronomicalCalendar;
import au.edu.swin.sdmd.suncalculatorjava.calc.GeoLocation;

public class SingleFragment extends Fragment implements MyInterface {

    protected View myView;
    private String theLocations = "Australia/Melbourne";
    private double theLongitude = 144.9631;
    private double theLatitude = -37.8136;
    private TimeZone tz = TimeZone.getTimeZone("Australia/Melbourne");
    static String expSR;
    static String expSS;
    static String tD;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        theLocations = getArguments().getString("tzname");
        theLatitude = getArguments().getDouble("latitude");
        theLongitude = getArguments().getDouble("longitude");
        View view = inflater.inflate(R.layout.single_date_layout, container, false);
        tz = TimeZone.getTimeZone(theLocations);
        this.myView = view;
        initializeUI();
        return view;
    }


    private void initializeUI() {
        DatePicker dp = myView.findViewById(R.id.datePicker);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        dp.init(year, month, day, dateChangeHandler); // setup initial values and reg. handler
        updateTime(year, month, day);
    }

    DatePicker.OnDateChangedListener dateChangeHandler = new DatePicker.OnDateChangedListener() {
        public void onDateChanged(DatePicker dp, int year, int monthOfYear, int dayOfMonth) {
            updateTime(year, monthOfYear, dayOfMonth);

        }
    };

    private void updateTime(int year, int monthOfYear, int dayOfMonth) {
        GeoLocation geolocation = new GeoLocation(theLocations, theLatitude, theLongitude, tz);
        AstronomicalCalendar ac = new AstronomicalCalendar(geolocation);
        ac.getCalendar().set(year, monthOfYear, dayOfMonth);
        Date srise = ac.getSunrise();
        Date sset = ac.getSunset();

        SimpleDateFormat dateForm = new SimpleDateFormat("dd/MM/yy");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        expSR = sdf.format(srise);
        System.out.println("expSR is " + expSR);
        expSS = sdf.format(sset);
        tD = dateForm.format(ac.getCalendar().getTime());

        TextView sunriseTV = myView.findViewById(R.id.sunriseTimeTV);
        TextView sunsetTV = myView.findViewById(R.id.sunsetTimeTV);
        Log.d("SUNRISE Unformatted", srise + "");

        sunriseTV.setText(expSR);
        sunsetTV.setText(expSS);

    }

    @Override
    public String myAction() {
        String toSend = "On the date " + tD + " the sunrise will be at " + expSR + " and the sunset will be at " + expSS + ".";
        System.out.println(toSend);
        return toSend;
    }


}
