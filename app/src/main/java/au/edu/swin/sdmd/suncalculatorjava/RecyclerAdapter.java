package au.edu.swin.sdmd.suncalculatorjava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Date> dateArr = new ArrayList<>();
    private ArrayList<Date> sunriseArr = new ArrayList<>();
    private ArrayList<Date> sunsetArr = new ArrayList<>();
    private Context context;

    public RecyclerAdapter(Context tContext, ArrayList<Date> theDate, ArrayList<Date> theSR, ArrayList<Date> theSS)
    {
        context = tContext;
        dateArr = theDate;
        sunriseArr = theSR;
        sunsetArr = theSS;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list,
                parent, false) ;
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        DateFormat df2 = new SimpleDateFormat("HH:mm");

        holder.dateText.setText(df.format(dateArr.get(position).getTime()));
        holder.sunriseText.setText(df2.format(sunriseArr.get(position).getTime()));
        holder.sunsetText.setText(df2.format(sunsetArr.get(position).getTime()));
    }


    @Override
    public int getItemCount() {
        return dateArr.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView dateText;
        TextView sunriseText;
        TextView sunsetText;

        RelativeLayout dateListLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.Date);
            sunriseText = itemView.findViewById(R.id.sunriseTime);
            sunsetText = itemView.findViewById(R.id.sunsetTime);
            dateListLayout = itemView.findViewById(R.id.date_list_layout);

        }
    }
}
