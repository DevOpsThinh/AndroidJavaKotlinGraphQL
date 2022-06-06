package com.forever.bee.akgraphql.trips

import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.forever.bee.akgraphql.GetAllTrips
import com.forever.bee.akgraphql.MainActivity
import com.forever.bee.akgraphql.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TripsAdapter(
    private val activity: MainActivity
) :
    RecyclerView.Adapter<TripsAdapter.TripsViewHolder>() {
    private var trips = mutableListOf<GetAllTrips.AllTrip>()

    inner class TripsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var akTitle = itemView.findViewById<View>(R.id.title) as TextView
        internal var akStartTime = itemView.findViewById<View>(R.id.start_time) as TextView
        internal var akIntro = itemView.findViewById<View>(R.id.intro) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsViewHolder {
        return TripsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.trip_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TripsViewHolder, position: Int) {
        val trip = trips[position]

        holder.akTitle.text = trip.title()
        holder.akStartTime.text = trip.startTime()
        holder.akIntro.text = R.string.kotlin_and_graphql.toString()
    }

    override fun getItemCount() = trips.size
}
