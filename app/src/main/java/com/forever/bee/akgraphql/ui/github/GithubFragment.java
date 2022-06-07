package com.forever.bee.akgraphql.ui.github;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.forever.bee.akgraphql.BuildConfig;
import com.forever.bee.akgraphql.MainActivity;
import com.forever.bee.akgraphql.api.MyStars;

import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Build the [TripsFragment] class
 */
public class GithubFragment extends RecyclerViewFragment {
    private final OkHttpClient okClient = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request origin = chain.request();
                Request.Builder builder = origin.newBuilder()
                        .method(origin.method(), origin.body())
                        .header("Authorization", "bearer " + BuildConfig.GITHUB_TOKEN);

                return (chain.proceed(builder.build()));
            })
            .build();

    //An instance of ApolloClient, this is a wrapper around an OkHttpClient that knows about Apollo-Android
    private final ApolloClient apolloClient = ApolloClient.builder()
            .okHttpClient(okClient)
            .serverUrl(BuildConfig.SERVER_URL)
            .build();
    // An instance of RxJava Observable chains
    private Observable<MyStars.Data> observable;
    private Disposable sub;
    private ReposAdapter reposAdapter;
    private Response<MyStars.Data> _response;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting up RxJava Observable chains using the Rx2Apollo helper class
        observable = getPages()
                .subscribeOn(Schedulers.io())
                .map(this::getFields)
                .cache()
                .observeOn(AndroidSchedulers.mainThread());

    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLayoutManager(new LinearLayoutManager(getActivity()));

        getRecyclerView()
                .addItemDecoration(new DividerItemDecoration(requireActivity(),
                        LinearLayoutManager.VERTICAL));

        reposAdapter = new ReposAdapter(requireActivity().getLayoutInflater());
        setAdapter(reposAdapter);

        unsub();

        sub = observable.subscribe(
                s -> applyResults(s.viewer()),
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

    private MyStars.Data getFields(Response<MyStars.Data> response) {
        if (response.hasErrors())
            throw new RuntimeException(Objects.requireNonNull(response.getErrors()).get(0).getMessage());

        return response.getData();
    }

    private Observable<Response<MyStars.Data>> getPages() {
        return (Observable.generate(() -> (getPage(null)), (previousPage, responseEmitter) -> {
            MyStars.StarredRepositories repos = Objects.requireNonNull(previousPage.getData()).viewer().starredRepositories();
            List<MyStars.Edge> edges = repos.edges();
            assert edges != null;
            MyStars.Edge last = edges.get(edges.size() - 1);
            Response<MyStars.Data> result = getPage(last.cursor());

            responseEmitter.onNext(result);

            if (result.hasErrors() || !repos.pageInfo().hasNextPage()) {
                responseEmitter.onComplete();
            }

            return result;
        }));
    }

    private Response<MyStars.Data> getPage(String cursor) throws ApolloException {

        apolloClient.query(MyStars.builder().first(5).after(cursor).build()).enqueue(new ApolloCall.Callback<MyStars.Data>() {
            @Override
            public void onResponse(@NonNull Response<MyStars.Data> response) {
                _response = response;
                Log.e("Apollo", "Launch site: " + Objects.requireNonNull(response.getData()).viewer().login());
            }

            @Override
            public void onFailure(@NonNull ApolloException e) {
                _response = null;
                Log.e("Apollo", "Error", e);
            }
        });
        return  _response;
    }

    private void applyResults(MyStars.Viewer viewer) {
        ((MainActivity) requireActivity()).setLogin(viewer.login());
        reposAdapter.addEdges(viewer.starredRepositories().edges());
    }

//    private void setLogin(String login) {
//        ((MainActivity)requireActivity()).setLogin(login);
//    }
}
