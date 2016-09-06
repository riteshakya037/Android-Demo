package com.calebtrevino.tallystacker.views.adaptors;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.calebtrevino.tallystacker.R;
import com.calebtrevino.tallystacker.views.fragments.GridCalanderFragment;
import com.calebtrevino.tallystacker.views.fragments.GridSettingFragment;
import com.calebtrevino.tallystacker.views.fragments.GridViewFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fatal on 9/6/2016.
 */

public class GridFragmentPagerAdapter extends FragmentPagerAdapter {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.icon)
    ImageView icon;


    private final String[] mTabsTitle = {"Grids", "Calendar", "Setting"};

    private int[] mTabsIcons = {
            R.drawable.ic_view_module_white_24px,
            R.drawable.ic_date_range_white_24px,
            R.drawable.ic_settings_white_24px};
    private FragmentManager fm;
    private Context mContext;

    public GridFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.fm = fm;
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return GridViewFragment.newInstance();
            case 1:
                return GridCalanderFragment.newInstance(position + 1);
            case 2:
                return GridSettingFragment.newInstance(position + 1);
            default:
                return GridViewFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabsTitle[position];
    }


    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
        ButterKnife.bind(this, view);
        title.setText(mTabsTitle[position]);
        icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageResource(mTabsIcons[position]);
        return view;
    }
}