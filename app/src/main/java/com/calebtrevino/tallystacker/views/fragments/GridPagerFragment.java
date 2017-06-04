package com.calebtrevino.tallystacker.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.calebtrevino.tallystacker.R;
import com.calebtrevino.tallystacker.presenters.GridPagePresenter;
import com.calebtrevino.tallystacker.presenters.GridPagePresenterImpl;
import com.calebtrevino.tallystacker.presenters.mapper.GridPagerMapper;
import com.calebtrevino.tallystacker.views.GridPagerView;
import com.calebtrevino.tallystacker.views.adaptors.GridFragmentPagerAdapter;
import com.calebtrevino.tallystacker.views.custom.NonScrollableViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class GridPagerFragment extends Fragment implements GridPagerView, GridPagerMapper {
    @SuppressWarnings("unused")
    public static final String TAG = GridPagerFragment.class.getSimpleName();
    @BindView(R.id.emptyRelativeLayout)
    protected RelativeLayout mEmptyRelativeLayout;
    @BindView(R.id.fab)
    protected FloatingActionButton floatingActionButton;
    @BindView(R.id.toolbar_shadow)
    protected View mBottomShadow;
    @BindView(R.id.container)
    protected NonScrollableViewPager mViewPager;
    @BindView(R.id.tab_layout)
    protected TabLayout mTabLayout;
    private GridPagePresenter gridPagePresenter;
    private Handler mUIHandler;
    private Spinner mSpinner;

    @OnClick(R.id.fab)
    protected void createNewGrid() {
        gridPagePresenter.createNewGrid();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUIHandler = new Handler();

        gridPagePresenter = new GridPagePresenterImpl(this, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View gridFrag = inflater.inflate(R.layout.fragment_grid, container, false);
        ButterKnife.bind(this, gridFrag);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        MenuItem item = toolbar.getMenu().findItem(R.id.spinner);
        mSpinner = (Spinner) MenuItemCompat.getActionView(item);
        return gridFrag;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gridPagePresenter.initializeViews();
        gridPagePresenter.initializePrefs();
        gridPagePresenter.initializeSpinner();
        if (savedInstanceState != null) {
            gridPagePresenter.restoreState(savedInstanceState);
        }

        gridPagePresenter.initializeDatabase();
        gridPagePresenter.initializeDataFromPreferenceSource();
//        gridPagePresenter.initializeTabLayoutFromAdaptor();

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        gridPagePresenter.saveState(outState);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void initializeToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.fragment_grid);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gridPagePresenter.releaseAllResources();
    }

    @Override
    public void initializeEmptyRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            ((ImageView) mEmptyRelativeLayout.findViewById(R.id.emptyImageView)).setImageResource(R.drawable.empty_grid);
            ((TextView) mEmptyRelativeLayout.findViewById(R.id.emptyTextView)).setText(R.string.no_grid);
            ((TextView) mEmptyRelativeLayout.findViewById(R.id.instructionsTextView)).setText(R.string.please_create_a_new_one);
        }
    }

    @Override
    public void hideEmptyRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            mEmptyRelativeLayout.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.VISIBLE);
            mBottomShadow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showEmptyRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            mEmptyRelativeLayout.setVisibility(View.VISIBLE);
            mTabLayout.setVisibility(View.GONE);
            mBottomShadow.setVisibility(View.GONE);
        }
    }


    @Override
    public void registerAdapter(FragmentStatePagerAdapter adapter) {
        if (mViewPager != null) {
            mViewPager.setAdapter(adapter);
        }
    }

    @Override
    public void registerSpinner(ArrayAdapter adapter) {
        if (mSpinner != null) {
            mSpinner.setAdapter(adapter);
        }
    }


    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public Parcelable getPositionState() {
        if (mViewPager != null) {
            return mViewPager.onSaveInstanceState();
        } else {
            return null;
        }
    }

    @Override
    public void setPositionState(Parcelable state) {
        if (mViewPager != null) {
            mViewPager.onRestoreInstanceState(state);
        }
    }

    @Override
    public void initializeBasePageView() {
        if (mViewPager != null) {
            mViewPager.setPagingEnabled(false);
            mViewPager.setOffscreenPageLimit(3);
        }
    }

    @Override
    public void initializeSpinnerListener() {
        if (mSpinner != null) {
            SpinnerInteractionListener interactionListener = new SpinnerInteractionListener();
            mSpinner.setOnTouchListener(interactionListener);
            mSpinner.setOnItemSelectedListener(interactionListener);
        }
    }

    @Override
    public void registerTabs(GridFragmentPagerAdapter mCatalogueAdapter) {
        if (mTabLayout != null && mViewPager != null) {
            mTabLayout.setupWithViewPager(mViewPager);
            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setCustomView(mCatalogueAdapter.getTabView(i));
                }
            }
        }
    }

    @Override
    public void setSpinnerLast() {
        mSpinner.setSelection(mSpinner.getCount(), true);
    }


    @Override
    public void showLoadingRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            ((ImageView) mEmptyRelativeLayout.findViewById(R.id.emptyImageView)).setImageResource(R.drawable.empty_grid);
            ((TextView) mEmptyRelativeLayout.findViewById(R.id.emptyTextView)).setText(R.string.loading_from_database);
            ((TextView) mEmptyRelativeLayout.findViewById(R.id.instructionsTextView)).setText(R.string.please_wait);
            showEmptyRelativeLayout();
        }
    }

    @Override
    public void setCurrentSpinner(int itemPosition) {
        if (mSpinner != null) {
            mSpinner.setSelection(itemPosition, true);
        }
    }

    @Override
    public void fabVisibility(final boolean isVisible) {
        handleInMainUI(new Runnable() {
            @Override
            public void run() {
                floatingActionButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void handleInMainUI(Runnable runnable) {
        if (mUIHandler != null) {
            mUIHandler.post(runnable);
        }
    }


    private class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {
        private boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (userSelect) {
                gridPagePresenter.spinnerClicked(position);
            }
            userSelect = false;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Empty method
        }
    }
}