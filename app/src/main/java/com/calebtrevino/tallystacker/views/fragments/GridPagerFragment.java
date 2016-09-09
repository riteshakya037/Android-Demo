package com.calebtrevino.tallystacker.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    public static final String TAG = GridPagerFragment.class.getSimpleName();

    private GridPagePresenter gridPagePresenter;


    @OnClick(R.id.fab)
    public void createNewGrid() {
        gridPagePresenter.createNewGrid();
    }

    @BindView(R.id.emptyRelativeLayout)
    RelativeLayout mEmptyRelativeLayout;

    public GridPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gridPagePresenter = new GridPagePresenterImpl(this, this);
    }

    @BindView(R.id.container)
    NonScrollableViewPager mViewPager;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View gridFrag = inflater.inflate(R.layout.fragment_grid, container, false);
        ButterKnife.bind(this, gridFrag);

        return gridFrag;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            gridPagePresenter.restoreState(savedInstanceState);
        }

        gridPagePresenter.initializeViews();
        gridPagePresenter.initializeDatabase();
        gridPagePresenter.initializeDataFromPreferenceSource();
        gridPagePresenter.initializeTabLayoutFromAdaptor();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        gridPagePresenter.saveState(outState);
    }

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
        }
    }

    @Override
    public void showEmptyRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            mEmptyRelativeLayout.setVisibility(View.VISIBLE);
            mTabLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void registerAdapter(FragmentStatePagerAdapter adapter) {
        if (mViewPager != null) {
            mViewPager.setAdapter(adapter);
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
            // Set up the ViewPager with the sections adapter.

        }
    }

    @Override
    public void registerTabs(GridFragmentPagerAdapter mCatalogueAdapter) {
        if (mTabLayout != null && mViewPager != null) {
            mTabLayout.setupWithViewPager(mViewPager);
            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                tab.setCustomView(mCatalogueAdapter.getTabView(i));
                if (mTabLayout.getSelectedTabPosition() == -1) {
                    mTabLayout.getTabAt(0).getCustomView().setSelected(true);
                } else {
                    mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).getCustomView().setSelected(true);
                }
            }
        }
    }


}
