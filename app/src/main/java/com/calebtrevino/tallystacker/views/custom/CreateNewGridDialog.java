package com.calebtrevino.tallystacker.views.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.calebtrevino.tallystacker.R;
import com.calebtrevino.tallystacker.controllers.factories.DefaultFactory;
import com.calebtrevino.tallystacker.models.Game;
import com.calebtrevino.tallystacker.models.enums.GridMode;
import com.calebtrevino.tallystacker.models.listeners.FinishedListener;
import com.calebtrevino.tallystacker.presenters.DialogPresenter;
import com.calebtrevino.tallystacker.presenters.DialogPresenterImpl;
import com.calebtrevino.tallystacker.presenters.mapper.AddGridMapper;
import com.calebtrevino.tallystacker.utils.Utils;
import com.calebtrevino.tallystacker.views.DialogView;
import java.util.List;

/**
 * @author Ritesh Shakya
 */
@SuppressWarnings("WeakerAccess") public class CreateNewGridDialog extends Dialog
        implements DialogView, AddGridMapper {

    private final Activity mActivity;
    private final List<Game> gameList;
    @BindView(R.id.gridName) protected TextInputEditText gridName;
    @BindView(R.id.rowNo) protected TextInputEditText rowNo;
    @BindView(R.id.columnNo) protected TextInputEditText columnNo;
    @BindView(R.id.gridModeSwitch) protected Switch gridModeSwitch;
    @BindView(R.id.leagueRecycler) protected RecyclerView mLeagueRecycler;
    @BindView(R.id.tallyCountSeekBar) protected SeekBarWithValues tallyCountSeek;
    private DialogPresenter dialogPresenter;
    private FinishedListener listener;

    public CreateNewGridDialog(Activity activity, List<Game> gameList) {
        super(activity);
        mActivity = activity;
        this.gameList = gameList;
    }

    @OnCheckedChanged(R.id.gridModeSwitch) void onCheckChange(boolean isChecked) {
        tallyCountSeek.setEnabled(!isChecked);
    }

    @OnClick(R.id.fab) protected void createGrid() {
        listener.onFinished(dialogPresenter.getGrid());
    }

    @OnClick(R.id.addLeague) protected void createLeague() {
        dialogPresenter.createLeague();
    }

    @OnClick(R.id.backButton) protected void dispose() {
        dismiss();
    }

    public void setFinishedListener(FinishedListener listener) {
        this.listener = listener;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_grid_dialog);
        Utils utils = new Utils(getContext());
        //noinspection ConstantConditions
        getWindow().setLayout(utils.getScreenWidth(), WindowManager.LayoutParams.MATCH_PARENT);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        dialogPresenter = new DialogPresenterImpl(this, this);
        ButterKnife.bind(this);

        dialogPresenter.initializeViews();
        dialogPresenter.initializeDatabase();
        dialogPresenter.initializeDataFromPreferenceSource();

        setDefaultValues();
    }

    private void setDefaultValues() {
        rowNo.setText(String.valueOf(DefaultFactory.Grid.ROW_NO));
        columnNo.setText(String.valueOf(DefaultFactory.Grid.COLUMN_NO));
        tallyCountSeek.updateSeekMaxValue(DefaultFactory.Grid.MAX_TALLY_COUNT);
        tallyCountSeek.setProgress(DefaultFactory.Grid.GRID_TOTAL_COUNT);
    }

    @Override public void hide() {
        super.hide();
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @Override public void dismiss() {
        super.dismiss();
        dialogPresenter.releaseAllResources();
    }

    @Override public void registerAdapter(RecyclerView.Adapter<?> adapter) {
        if (mLeagueRecycler != null) {
            mLeagueRecycler.setAdapter(adapter);
        }
    }

    @Override public Activity getActivity() {
        return mActivity;
    }

    @Override
    public void initializeRecyclerLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (mLeagueRecycler != null) {
            mLeagueRecycler.setLayoutManager(layoutManager);
        }
    }

    @Override public String getRowNo() {
        return rowNo.getText().toString();
    }

    @Override public String getColumnNo() {
        return columnNo.getText().toString();
    }

    @Override public String getName() {
        return gridName.getText().toString();
    }

    @Override public void setName(String name) {
        gridName.setText(name);
    }

    @Override public GridMode getGridMode() {
        return gridModeSwitch.isChecked() ? GridMode.GROUPED : GridMode.TALLY_COUNT;
    }

    @Override public List<Game> getGames() {
        return gameList;
    }

    @Override public int getSeekValue() {
        return tallyCountSeek.getProgress();
    }
}
