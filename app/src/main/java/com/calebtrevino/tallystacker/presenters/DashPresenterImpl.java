package com.calebtrevino.tallystacker.presenters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.calebtrevino.tallystacker.controllers.receivers.GameUpdateReceiver;
import com.calebtrevino.tallystacker.models.Game;
import com.calebtrevino.tallystacker.models.database.DatabaseContract;
import com.calebtrevino.tallystacker.models.database.DatabaseTask;
import com.calebtrevino.tallystacker.models.enums.GameStatus;
import com.calebtrevino.tallystacker.models.listeners.ChildGameEventListener;
import com.calebtrevino.tallystacker.presenters.mapper.DashMapper;
import com.calebtrevino.tallystacker.views.DashView;
import com.calebtrevino.tallystacker.views.adaptors.DashAdapter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

/**
 * @author Ritesh Shakya
 */
public class DashPresenterImpl implements DashPresenter, ChildGameEventListener {

    private static final String TAG = DashPresenterImpl.class.getSimpleName();

    private static final String POSITION_PARCELABLE_KEY = TAG + ":" + "PositionParcelableKey";
    private final DashView mDashView;
    private final DashMapper mDashMapper;
    private DashAdapter mDashAdapter;
    private DatabaseContract.DbHelper dbHelper;
    @SuppressWarnings("FieldCanBeLocal")
    private ArrayAdapter<String> mSpinnerAdapter;

    public DashPresenterImpl(DashView dashView, DashMapper dashMapper) {
        this.mDashView = dashView;
        this.mDashMapper = dashMapper;
    }

    @Override
    public void initializeViews() {
        mDashView.initializeToolbar();
        mDashView.initializeEmptyRelativeLayout();
        mDashView.initializeRecyclerLayoutManager(new LinearLayoutManager(mDashView.getActivity()));
        mDashView.initializeBasePageView();
        mDashMapper.initializeSpinnerListener();
    }

    @Override
    public void initializeDataFromPreferenceSource() {
        mDashAdapter = new DashAdapter(mDashView.getActivity());
        mDashMapper.registerAdapter(mDashAdapter);
        mDashAdapter.setNullListener(this);
        new DatabaseTask<List<Game>>(dbHelper) {
            @Override
            protected void callInUI(List<Game> o) {
            }

            @Override
            protected List<Game> executeStatement(DatabaseContract.DbHelper dbHelper) {
                return dbHelper.selectUpcomingGames();
            }
        }.execute();
    }

    @Override
    public void saveState(Bundle outState) {
        if (mDashMapper.getPositionState() != null) {
            outState.putParcelable(POSITION_PARCELABLE_KEY, mDashMapper.getPositionState());
        }
    }

    @Override
    public void releaseAllResources() {
        if (mDashAdapter != null) {
            mDashAdapter = null;
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Override
    public void restorePosition() {
        if (mDashAdapter != null) {
            mDashAdapter = null;
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(POSITION_PARCELABLE_KEY)) {
//            Parcelable mPositionSavedState = savedState.getParcelable(POSITION_PARCELABLE_KEY);

            savedState.remove(POSITION_PARCELABLE_KEY);
        }
    }

    @Override
    public void isEmpty(boolean isEmpty) {
        if (isEmpty) {
            mDashView.showEmptyRelativeLayout();
        } else {
            mDashView.hideEmptyRelativeLayout();
        }
    }

    @Override
    public void initializeDatabase() {
        dbHelper = new DatabaseContract.DbHelper(mDashView.getActivity());
    }

    @Override
    public void onChildAdded(final Game game) {
        if (DatabaseContract.checkGameValidity(game)) {
            if (mDashAdapter != null)
                mDashAdapter.addGame(game);
            if (new DateTime(game.getGameDateTime(), DateTimeZone.getDefault()).plusMinutes(game.getLeagueType().getAvgTime()).isBeforeNow() && game.getGameStatus() == GameStatus.NEUTRAL) {
                Intent gameIntent = new Intent(mDashView.getActivity(), GameUpdateReceiver.class);
                Log.i(TAG, "onChildAdded: Should Have completed game " + game.get_id());
                gameIntent.putExtra("game", game.get_id());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mDashView.getActivity(), (int) game.get_id(), gameIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                long interval = game.getLeagueType().getRefreshInterval() * 60 * 1000L;
                AlarmManager manager = (AlarmManager) mDashView.getActivity().getSystemService(Context.ALARM_SERVICE);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, new DateTime().getMillis(), interval, pendingIntent);
            }
        }
    }

    @Override
    public void initializeSpinner() {
        mSpinnerAdapter = new ArrayAdapter<>(mDashView.getActivity(), android.R.layout.simple_spinner_item, new String[]{"Time", "League"});
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDashMapper.registerSpinner(mSpinnerAdapter);
    }

    @Override
    public void spinnerClicked(int position) {
        if (position == 0) {
            mDashAdapter.changeSort(new Game.GameTimeComparator());
        } else {
            mDashAdapter.changeSort(new Game.GameComparator());
        }
    }

    @Override
    public void onChildChanged(final Game game) {
        mDashAdapter.modify(game);
    }

    @Override
    public void onChildRemoved(final Game game) {
        mDashAdapter.removeCard(game);
    }
}
