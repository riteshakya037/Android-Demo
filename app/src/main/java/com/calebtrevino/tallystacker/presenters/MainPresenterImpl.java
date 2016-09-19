package com.calebtrevino.tallystacker.presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.calebtrevino.tallystacker.utils.NavigationUtils;
import com.calebtrevino.tallystacker.views.MainView;
import com.calebtrevino.tallystacker.views.activities.MainActivity;
import com.calebtrevino.tallystacker.views.fragments.DashFragment;
import com.calebtrevino.tallystacker.views.fragments.GridPagerFragment;
import com.calebtrevino.tallystacker.views.fragments.LeagueFragment;


public class MainPresenterImpl implements MainPresenter {
    private static final String TAG = MainPresenterImpl.class.getSimpleName();

    private static final String MAIN_FRAGMENT_PARCELABLE_KEY = TAG + ":" + "MainFragmentParcelableKey";

    private final MainView mMainView;

    private Fragment mFragment;

    private int mInitialPosition;


    public MainPresenterImpl(MainView mainView) {
        mMainView = mainView;
    }

    @Override
    public void initializeViews() {
        mMainView.initializeToolbar();
        mMainView.initializeDrawerLayout();
    }

    @Override
    public void initializeMainLayout(Intent argument) {
        if (argument != null) {
            if (argument.hasExtra(MainActivity.POSITION_ARGUMENT_KEY)) {
                mInitialPosition = argument.getIntExtra(MainActivity.POSITION_ARGUMENT_KEY, NavigationUtils.POSITION_DASHBOARD);

                if (mInitialPosition == NavigationUtils.POSITION_DASHBOARD) {
                    mFragment = new DashFragment();
                } else if (mInitialPosition == NavigationUtils.POSITION_GRID) {
                    mFragment = new GridPagerFragment();
                } else if (mInitialPosition == NavigationUtils.POSITION_LEAGUE) {
                    mFragment = new LeagueFragment();
                }
//                else if (mInitialPosition == NavigationUtils.POSITION_SETTING) {
//                    Intent intent = new Intent(mMainView.getActivity(), SettingsActivity.class);
//                    mMainView.getActivity().startActivity(intent);
//                }
                argument.removeExtra(MainActivity.POSITION_ARGUMENT_KEY);
            }
        }

        if (mFragment == null) {
            mInitialPosition = NavigationUtils.POSITION_DASHBOARD;

            mFragment = new DashFragment();
        }

        ((FragmentActivity) mMainView.getActivity()).getSupportFragmentManager().beginTransaction()
                .add(mMainView.getMainLayoutId(), mFragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == NavigationUtils.POSITION_DASHBOARD) {
            onPositionDashBoard();
        } else if (id == NavigationUtils.POSITION_GRID) {
            onPositionGrid();
        } else if (id == NavigationUtils.POSITION_LEAGUE) {
            onPositionLeague();
        }
//        else if (id == NavigationUtils.POSITION_SETTING) {
//            Intent intent = new Intent(mMainView.getActivity(), SettingsActivity.class);
//            mMainView.getActivity().startActivity(intent);
//        }

        mMainView.closeDrawerLayout();
        return true;
    }


    @Override
    public void saveState(Bundle outState) {
        if (mFragment != null) {
            ((FragmentActivity) mMainView.getActivity()).getSupportFragmentManager().putFragment(outState, MAIN_FRAGMENT_PARCELABLE_KEY, mFragment);
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(MAIN_FRAGMENT_PARCELABLE_KEY)) {
            mFragment = ((FragmentActivity) mMainView.getActivity()).getSupportFragmentManager().getFragment(savedState, MAIN_FRAGMENT_PARCELABLE_KEY);

            savedState.remove(MAIN_FRAGMENT_PARCELABLE_KEY);
        }
    }


    private void onPositionDashBoard() {
        if (mFragment instanceof DashFragment) {
            return;
        }
        mFragment = new DashFragment();
        mMainView.isSpinnerVisible(false);

        replaceMainFragment();
    }

    private void onPositionGrid() {
        if (mFragment instanceof GridPagerFragment) {
            return;
        }
        mFragment = new GridPagerFragment();
        mMainView.isSpinnerVisible(true);
        replaceMainFragment();
    }

    private void onPositionLeague() {
        if (mFragment instanceof LeagueFragment) {
            return;
        }
        mFragment = new LeagueFragment();
        mMainView.isSpinnerVisible(false);

        replaceMainFragment();
    }


    private void replaceMainFragment() {
        ((FragmentActivity) mMainView.getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(mMainView.getMainLayoutId(), mFragment)
                .commit();
    }

}
