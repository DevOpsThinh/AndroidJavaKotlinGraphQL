package com.forever.bee.akgraphql.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Wraps a RecyclerView
 *
 * */
public class RecyclerViewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(getActivity());

        recyclerView.setHasFixedSize(true);

        return recyclerView;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        getRecyclerView().setAdapter(adapter);
    }

    public RecyclerView.Adapter getAdapter() {
        return (getRecyclerView().getAdapter());
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        getRecyclerView().setLayoutManager(manager);
    }

    public  RecyclerView getRecyclerView() {
        return ((RecyclerView) getView());
    }
}
