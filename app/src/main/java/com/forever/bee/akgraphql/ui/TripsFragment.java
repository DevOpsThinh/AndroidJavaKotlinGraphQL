package com.forever.bee.akgraphql.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.forever.bee.akgraphql.BuildConfig;
import com.forever.bee.akgraphql.GetAllTrips;
import java.util.Objects;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

/**
 * Build the [TripsFragment] class
 */
public class TripsFragment extends RecyclerViewFragment {
    //An instance of ApolloClient, this is a wrapper around an OkHttpClient that knows about Apollo-Android
    private final ApolloClient apolloClient = ApolloClient.builder()
            .okHttpClient(new OkHttpClient())
            .serverUrl(BuildConfig.GRAPHQL_API_KEY)
            .build();
    // An instance of RxJava Observable chains
    private Observable<GetAllTrips.Data> observable;
    private Disposable sub;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting up RxJava Observable chains using the Rx2Apollo helper class
        observable = Rx2Apollo.from(apolloClient.query(new GetAllTrips()).watcher())
                .subscribeOn(Schedulers.io())
                .map(this::getAllTripsFields)
                .cache()
                .observeOn(AndroidSchedulers.mainThread());

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLayoutManager(new LinearLayoutManager(getActivity()));

        getRecyclerView()
                .addItemDecoration(new DividerItemDecoration(getActivity(),
                        LinearLayoutManager.VERTICAL));

        unsub();

        sub = observable.subscribe(
                s -> setAdapter(buildRVAdapter(s)),
                error -> {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(getClass().getSimpleName(), "Exception processing request",
                            error);
                }
        );
    }

    @Override
    public void onDestroy() {
        unsub();
        super.onDestroy();
    }

    private void unsub() {
        if (sub != null && !sub.isDisposed()) {
            sub.dispose();
        }
    }

    private GetAllTrips.Data getAllTripsFields(Response<GetAllTrips.Data> response) {
        if (response.hasErrors()) {
            throw new RuntimeException(Objects.requireNonNull(response.getErrors()).get(0).getMessage());
        }

        return response.getData();
    }

    private RecyclerView.Adapter buildRVAdapter(GetAllTrips.Data response) {
        return (new TripsAdapter(response.allTrips(), getActivity().getLayoutInflater(),
                android.text.format.DateFormat.getDateFormat(getActivity())));
    }
}
