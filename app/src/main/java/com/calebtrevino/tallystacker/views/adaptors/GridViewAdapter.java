package com.calebtrevino.tallystacker.views.adaptors;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.calebtrevino.tallystacker.R;
import com.calebtrevino.tallystacker.models.Game;
import com.calebtrevino.tallystacker.models.Grid;
import com.calebtrevino.tallystacker.presenters.GridViewPresenter;
import com.calebtrevino.tallystacker.utils.Constants;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Ritesh Shakya
 */

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.GridViewHolder> {

    private final Context mContext;

    private List<Game> data;
    private final Grid mGrid;
    private GridViewPresenter viewPresenter;

    public GridViewAdapter(Context context, Grid grid) {
        this.mContext = context;
        data = grid.getGameList();
        mGrid = grid;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GridViewHolder viewHolder;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.grd_view_item, parent, false);
        viewHolder = new GridViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mGrid.getRowNo() * mGrid.getColumnNo();
    }

    @SuppressWarnings("unused")
    public void addGames(Game game) {
        if (!data.contains(game)) {
            data.add(game);
        }
        if (data.size() > 0) {
            viewPresenter.isEmpty(false);  // Broadcast that dataset is not empty.
        }
        this.notifyDataSetChanged();
    }


    public void removeCard(Game game) {
        data.remove(game);
        if (data.size() == 0) {
            viewPresenter.isEmpty(true); // Broadcast that dataset is empty.
        }
        this.notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        if (position < data.size()) {
            final Game currentGame = data.get(position);

            new DateTime(data.get(position).getGameDateTime(), Constants.DATE.VEGAS_TIME_ZONE).toDateTime(DateTimeZone.getDefault());
            long previousTs = 0;
            if (position > 0) {
                previousTs = data.get(position - 1).getGameDateTime();
            }
            setBannerVisibility(currentGame.getGameDateTime(), previousTs, holder.bannerView);

            holder.leagueName.setText(data.get(position).getLeagueType().getAcronym());
            holder.teamsName.setText(mContext.getString(R.string.team_vs_team,
                    data.get(position).getFirstTeam().getCity(),
                    data.get(position).getSecondTeam().getCity()));
        } else {
            holder.leagueName.setText("");
            holder.teamsName.setText("");
        }
    }

    private void setBannerVisibility(long ts1, long ts2, View bannerView) {
        if (ts2 == 0) {
            bannerView.setVisibility(View.VISIBLE);
        } else {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(new DateTime(ts1, Constants.DATE.VEGAS_TIME_ZONE).toDateTime(DateTimeZone.getDefault()).toDate());
            cal2.setTime(new DateTime(ts2, Constants.DATE.VEGAS_TIME_ZONE).toDateTime(DateTimeZone.getDefault()).toDate());

            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

            if (sameDay) {
                bannerView.setVisibility(View.GONE);
            } else {
                bannerView.setVisibility(View.VISIBLE);
            }

        }
    }

    public void setNullListener(GridViewPresenter viewPresenter) {
        this.viewPresenter = viewPresenter;
    }

    @SuppressWarnings("unused")
    public void addGames(List<Game> gameList) {
        data.addAll(gameList);
    }

    @SuppressWarnings("unused")
    public void setData(List<Game> gameList) {
        data = new LinkedList<>();
        data.addAll(gameList);
        this.notifyDataSetChanged();
    }

    class GridViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.leagueName)
        TextView leagueName;
        @BindView(R.id.teamsName)
        TextView teamsName;
        @BindView(R.id.new_banner)
        View bannerView;

        GridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
