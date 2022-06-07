package com.forever.bee.akgraphql.ui;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.forever.bee.akgraphql.GetAllTrips;
import com.forever.bee.akgraphql.R;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripsViewHolder> {
    private final List<GetAllTrips.AllTrip> trips;
    private final LayoutInflater inflater;
    private final DateFormat dateFormat;

    protected TripsAdapter(List<GetAllTrips.AllTrip> trips,
                           LayoutInflater inflater, DateFormat dateFormat) {
        this.trips = trips;
        this. inflater = inflater;
        this.dateFormat = dateFormat;
    }

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat ISO861 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    @NonNull
    @Override
    public TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return (new TripsViewHolder(inflater.inflate(R.layout.trip_item, parent, false), dateFormat));
    }

    @Override
    public void onBindViewHolder(@NonNull TripsViewHolder holder, int position) {
        holder.bind(trips.get(position));
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    protected static class TripsViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView startTime;
        private final TextView intro;
        private final DateFormat dateFormat;

        TripsViewHolder(View itemView, DateFormat dateFormat) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.title);
            startTime = (TextView)itemView.findViewById(R.id.start_time);
            intro = (TextView)itemView.findViewById(R.id.intro);
            this.dateFormat = dateFormat;
        }

        void bind(GetAllTrips.AllTrip trip) {
            try {
                Date parsedStartTime = ISO861.parse(trip.startTime());

                title.setText(trip.title());

                if (parsedStartTime != null) {
                    startTime.setText(dateFormat.format(parsedStartTime.toString()));
                }

                intro.setText(R.string.kotlin_and_graphql);

            } catch (ParseException e) {
                Log.e(getClass().getSimpleName(), "Exception parsing "+trip.startTime(), e);
            }
        }
    }
}



