package com.calebtrevino.tallystacker.presenters;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;

import com.calebtrevino.tallystacker.models.Game;
import com.calebtrevino.tallystacker.models.database.DatabaseContract;
import com.calebtrevino.tallystacker.models.database.DatabaseTask;
import com.calebtrevino.tallystacker.models.listeners.ChildGameEventListener;
import com.calebtrevino.tallystacker.presenters.mapper.DashMapper;
import com.calebtrevino.tallystacker.views.DashView;
import com.calebtrevino.tallystacker.views.adaptors.DashAdapter;

import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Ritesh Shakya
 */
public class DashPresenterImpl implements DashPresenter, ChildGameEventListener {

    private static final String TAG = DashPresenterImpl.class.getSimpleName();

    private static final String POSITION_PARCELABLE_KEY = TAG + ":" + "PositionParcelableKey";
    private final DashView mDashView;
    private final DashMapper mDashMapper;
    private Parcelable mPositionSavedState;
    private DashAdapter mDashAdapter;
    private DatabaseContract.DbHelper dbHelper;

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
    }

    @Override
    public void initializeDataFromPreferenceSource() {
        mDashAdapter = new DashAdapter(mDashView.getActivity());
        mDashMapper.registerAdapter(mDashAdapter);
        mDashAdapter.setNullListener(this);
        new DatabaseTask(dbHelper) {
            @Override
            protected void callInUI(Object o) {
            }

            @Override
            protected List<Game> executeStatement(DatabaseContract.DbHelper dbHelper) {
                return dbHelper.selectUpcomingGames(new DateTime().withTimeAtStartOfDay().getMillis());
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
            dbHelper.removeChildGameEventListener(this);
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
            mPositionSavedState = savedState.getParcelable(POSITION_PARCELABLE_KEY);

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
        dbHelper.addChildGameEventListener(this);
    }

    @Override
    public void onChildAdded(final Game game) {
        if (game.getGameAddDate() == new DateTime().withTimeAtStartOfDay().getMillis()) {
            mDashView.handleInMainUI(new Runnable() {
                @Override
                public void run() {
                    if (mDashAdapter!=null)
                    mDashAdapter.addGame(game);
                }
            });
        }
    }

    @Override
    public void onChildChanged(Game game) {
    }

    @Override
    public void onChildRemoved(final Game game) {
        mDashView.handleInMainUI(new Runnable() {
            @Override
            public void run() {
                mDashAdapter.removeCard(game);
            }
        });
    }
}
