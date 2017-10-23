package com.calebtrevino.tallystacker.views.fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.calebtrevino.tallystacker.R;
import com.calebtrevino.tallystacker.models.Game;
import com.calebtrevino.tallystacker.views.adaptors.GridDialogPagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ritesh Shakya
 */
@SuppressWarnings("ConstantConditions") public class GridViewDialog extends DialogFragment {

    private static final String DATA_ARRAY = "data_array";
    private static final String POSITION = "position";
    @BindView(R.id.container) protected ViewPager mViewPager;
    @BindView(R.id.tab_layout) protected TabLayout mTabLayout;
    private List<Game> data;
    private int position;

    public static DialogFragment newInstance(ArrayList<Game> data, int position) {
        GridViewDialog fragment = new GridViewDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(DATA_ARRAY, data);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_grid_view, container);
        ButterKnife.bind(this, view);

        GridDialogPagerAdapter adapter =
                new GridDialogPagerAdapter(getChildFragmentManager(), getContext());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        adapter.setData(data);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(adapter.getTabView(i));
            }
        }
        mViewPager.setCurrentItem(position);
        //A little space between pages
        mViewPager.setPageMargin(15);

        //If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.
        mViewPager.setClipChildren(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow()
                .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return view;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            data = getArguments().getParcelableArrayList(DATA_ARRAY);
            position = getArguments().getInt(POSITION);
        }
    }
}
