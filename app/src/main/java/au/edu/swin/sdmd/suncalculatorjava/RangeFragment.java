package au.edu.swin.sdmd.suncalculatorjava;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import au.edu.swin.sdmd.suncalculatorjava.calc.AstronomicalCalendar;
import au.edu.swin.sdmd.suncalculatorjava.calc.GeoLocation;

public class RangeFragment extends Fragment {



    protected View myView;
    private String theLocations = "Melbourne";
    private double theLongitude  = 145;
    private double theLatitude  = 37;
    private TimeZone tz = TimeZone.getTimeZone("Australia/Melbourne");

    private Button button;

    private ArrayList<Date> datesArr = new ArrayList<>();
    private ArrayList<Date> sunrisesArr = new ArrayList<>();
    private ArrayList<Date> sunsetsArr = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        theLocations = getArguments().getString("tzname");
        theLatitude = getArguments().getDouble("latitude");
        theLongitude = getArguments().getDouble("longitude");
        View view = inflater.inflate(R.layout.date_range_layout, container, false);

        button=(Button) view.findViewById(R.id.gettimes);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateTable();

            }
        });

        tz = TimeZone.getTimeZone(theLocations);
        this.myView = view;
        return view;
    }


    private void generateTable() {
        GeoLocation geolocation = new GeoLocation(theLocations, theLatitude, theLongitude, tz);
        AstronomicalCalendar ac = new AstronomicalCalendar(geolocation);
        AstronomicalCalendar ac2 = ac;
        DatePicker dp2 = myView.findViewById(R.id.datePicker2);
        int year = dp2.getYear();
        int month = dp2.getMonth();
        int day = dp2.getDayOfMonth();


        DatePicker dp3 = myView.findViewById(R.id.datePicker3);
        int year2 = dp3.getYear();
        int month2 = dp3.getMonth();
        int day2 = dp3.getDayOfMonth();



        ac2.getCalendar().set(year2, month2, day2, 23, 59);
        Date end = ac2.getCalendar().getTime();

        ac.getCalendar().set(year, month, day, 0, 1);
        Date start = ac.getCalendar().getTime();

        datesArr.clear();
        sunrisesArr.clear();
        sunsetsArr.clear();

        while (true)
        {
            Date srise = ac.getSunrise();
            Date sset = ac.getSunset();

            datesArr.add(ac.getCalendar().getTime());
            sunrisesArr.add(srise);
            sunsetsArr.add(sset);

            ac.getCalendar().add(Calendar.DATE,1);

            if (end.compareTo(ac.getCalendar().getTime())==-1)
            {
                break;
            }
        }
        initRecyclerView();


    }

        private void initRecyclerView(){
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerview);
        RecyclerAdapter adapter = new RecyclerAdapter(myView.getContext(), datesArr, sunrisesArr, sunsetsArr);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(myView.getContext()));
    }
}
