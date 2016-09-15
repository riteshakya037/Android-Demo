package com.calebtrevino.tallystacker.presenters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ArrayAdapter;

import com.calebtrevino.tallystacker.models.Grid;
import com.calebtrevino.tallystacker.models.database.DatabaseContract;
import com.calebtrevino.tallystacker.models.database.DatabaseTask;
import com.calebtrevino.tallystacker.models.listeners.FinishedListener;
import com.calebtrevino.tallystacker.models.listeners.GridChangeListener;
import com.calebtrevino.tallystacker.presenters.mapper.GridPagerMapper;
import com.calebtrevino.tallystacker.views.GridPagerView;
import com.calebtrevino.tallystacker.views.adaptors.GridFragmentPagerAdapter;
import com.calebtrevino.tallystacker.views.custom.CreateNewGridDialog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fatal on 9/5/2016.
 */
public class GridPagePresenterImpl implements GridPagePresenter {


    public static final String TAG = GridPagePresenterImpl.class.getSimpleName();

    private static final String POSITION_PARCELABLE_KEY = TAG + ":" + "PositionParcelableKey";

    private final GridPagerView mGridPagerView;
    private final GridPagerMapper mGridPagerMapper;
    private GridFragmentPagerAdapter mGridPageAdapter;
    private Parcelable mPositionSavedState;
    private DatabaseContract.DbHelper dbHelper;
    private GridChangeListener gridChangeListener;
    private SharedPreferences mPrefs;
    final String PREFS_NAME = "GridPagePrefs";
    private static final String VAL_CURRENT_GRID = "current_grid";
    private HashMap<String, String> grids;
    ArrayAdapter<String> mSpinnerAdapter;

    public GridPagePresenterImpl(GridPagerView gridPagerView, GridPagerMapper gridPagerMapper) {
        this.mGridPagerView = gridPagerView;
        this.mGridPagerMapper = gridPagerMapper;
    }

    @Override
    public void initializePrefs() {
        mPrefs = mGridPagerView.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void initializeViews() {
        mGridPagerView.initializeToolbar();
        mGridPagerView.initializeBasePageView();
        mGridPagerView.initializeEmptyRelativeLayout();
    }

    @Override
    public void initializeDatabase() {
        dbHelper = new DatabaseContract.DbHelper(mGridPagerView.getActivity());
    }

    @Override
    public void saveState(Bundle outState) {
        if (mGridPagerMapper.getPositionState() != null) {
            outState.putParcelable(POSITION_PARCELABLE_KEY, mGridPagerMapper.getPositionState());
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
    public void releaseAllResources() {
        if (mGridPageAdapter != null) {
//            mGridPageAdapter.setCursor(null);
            mGridPageAdapter = null;
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Override
    public void restorePosition() {
        if (mPositionSavedState != null) {
            mGridPagerMapper.setPositionState(mPositionSavedState);
            mPositionSavedState = null;
        }
    }

    @Override
    public void initializeDataFromPreferenceSource() {
        final String currentGridId = mPrefs.getString(VAL_CURRENT_GRID, "0");
        if (currentGridId.equals("0"))
            mGridPagerView.showEmptyRelativeLayout();
        else {
            mGridPagerView.showLoadingRelativeLayout();
            mGridPageAdapter = new GridFragmentPagerAdapter(mGridPagerView.getFragmentManager(), mGridPagerView.getActivity());
            mGridPagerView.hideEmptyRelativeLayout();
            mGridPagerMapper.registerAdapter(mGridPageAdapter);
            initializeTabLayoutFromAdaptor();
        }
        new DatabaseTask(dbHelper) {
            @Override
            protected void callInUI(Object o) {
                if (o != null) {
                    updateSpinner();
                    if (mGridPageAdapter != null)
                        mGridPageAdapter.changeTo((Grid) o);
                }

            }

            @Override
            protected Grid executeStatement(DatabaseContract.DbHelper dbHelper) {
                grids = dbHelper.getGrids();
                if (!grids.isEmpty()) {
                    if (!"0".equals(currentGridId)) {
                        return dbHelper.onSelectGrid(currentGridId);
                    }
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void initializeTabLayoutFromAdaptor() {
        mGridPagerMapper.registerTabs(mGridPageAdapter);
    }

    @Override
    public void createNewGrid() {
        final CreateNewGridDialog createNew = new CreateNewGridDialog(mGridPagerView.getActivity());
        createNew.show();
        createNew.setFinishedListener(new FinishedListener() {
            @Override
            public void onFinished(Grid grid) {
                createNew.dismiss();
                ProgressDialog dialog = new ProgressDialog(mGridPagerView.getActivity());
                dialog.setMessage("Creating Grid");
                dialog.show();
                mPrefs.edit().putString(VAL_CURRENT_GRID, String.valueOf(grid.get_id())).apply(); // Set current set in preference.
                dbHelper.onInsertGrid(grid);
                initializeDataFromPreferenceSource();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void initializeSpinner() {
        mSpinnerAdapter = new ArrayAdapter<>(mGridPagerView.getActivity(), android.R.layout.simple_spinner_item, new ArrayList<String>());
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGridPagerMapper.registerSpinner(mSpinnerAdapter);
    }

    private void updateSpinner() {
        mSpinnerAdapter.clear();
        for (String gridNames : grids.values()) {
            mSpinnerAdapter.add(gridNames);
        }
        mSpinnerAdapter.notifyDataSetChanged();
    }
}
