package com.forever.bee.akgraphql.ui.github;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.forever.bee.akgraphql.api.MyStars;
import com.forever.bee.akgraphql.R;
import java.util.ArrayList;
import java.util.List;

public class ReposAdapter extends RecyclerView.Adapter<ReposAdapter.GithubViewHolder> {
    private final LayoutInflater inflater;
    private final List<MyStars.Edge> edges = new ArrayList<>();

    ReposAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    protected static class GithubViewHolder extends RecyclerView.ViewHolder {
        private final TextView username;

        GithubViewHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.title);
        }

        void bind(MyStars.Node node) {
            username.setText(node.name());
        }
    }

    @Override
    public GithubViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        return (new GithubViewHolder(inflater.inflate(R.layout.trip_item, parent, false)));
    }

    @Override
    public void onBindViewHolder( GithubViewHolder holder, int position) {
        holder.bind(edges.get(position).node());
    }

    @Override
    public int getItemCount() {
        return edges.size();
    }

    protected void addEdges(List<MyStars.Edge> infoEdges) {
        int size = edges.size();

        edges.addAll(infoEdges);

        notifyItemRangeInserted(size, infoEdges.size());
    }
}
