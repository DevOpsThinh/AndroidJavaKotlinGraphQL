package com.forever.bee.akgraphql.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Transformations.map
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.forever.bee.akgraphql.BuildConfig
import com.forever.bee.akgraphql.GetAllTrips
import com.forever.bee.akgraphql.MainActivity
import com.forever.bee.akgraphql.databinding.FragmentTripsBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient

/**
 *
 * @property apolloClient  An instance of [ApolloClient]
 * */
class TripsFragment : Fragment() {
    private var _binding: FragmentTripsBinding? = null
    private val binding get() = _binding!!
    private lateinit var tripsAdapter: TripsAdapter
    private lateinit var callingActivity: MainActivity
    private lateinit var observable: Observable<GetAllTrips.Data>

    /*
    * This is a wrapper around an OkHttpClient that knows about Apollo-Android.
    * */
    private val apolloClient = ApolloClient.builder()
        .okHttpClient(OkHttpClient())
        .serverUrl(BuildConfig.GRAPHQL_API_KEY)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observable = Rx2Apollo.from(apolloClient.query(GetAllTrips()).watcher())
            .subscribeOn(Schedulers.io())
            .cache()
            .observeOn(AndroidSchedulers.mainThread())
            .map{response -> (getAllTripsFields(response))}
    }

    /**
     * Initialise the fragment_trips layout's binding class so the fragment can interact
     * with the layout's components.
     * */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
        callingActivity = activity as MainActivity
        _binding = FragmentTripsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()

        // Initialise the TripsAdapter adapter
        tripsAdapter = TripsAdapter(callingActivity)
        binding.recyclerView.adapter = tripsAdapter
    }

    /**
     * Prevent the fragment from accessing components of the layout in the event the layout has been
     * closed but the fragment is not yet shut down.
     * */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAllTripsFields(response: Response<GetAllTrips.Data>): GetAllTrips.Data? {
        if (response.hasErrors()) {
            throw RuntimeException(response.errors?.get(0)?.message ?: "Unknown Exception")
        }

        return response.data
    }
}


