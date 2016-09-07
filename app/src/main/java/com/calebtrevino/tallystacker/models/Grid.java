package com.calebtrevino.tallystacker.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fatal on 9/6/2016.
 */
public class Grid implements Parcelable {
    private long _id;
    private String gridName;
    private int rowNo;
    private int columnNo;
    private List<Game> gameList;
    private boolean keepUpdates;
    private boolean forceAdd;
    private List<GridLeagues> gridLeagues;


    public Grid() {
    }

    protected Grid(Parcel in) {
        _id = in.readLong();
        gridName = in.readString();
        rowNo = in.readInt();
        columnNo = in.readInt();
        gameList = in.createTypedArrayList(Game.CREATOR);
        keepUpdates = in.readByte() != 0;
        forceAdd = in.readByte() != 0;
        gridLeagues = in.createTypedArrayList(GridLeagues.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeString(gridName);
        dest.writeInt(rowNo);
        dest.writeInt(columnNo);
        dest.writeTypedList(gameList);
        dest.writeByte((byte) (keepUpdates ? 1 : 0));
        dest.writeByte((byte) (forceAdd ? 1 : 0));
        dest.writeTypedList(gridLeagues);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Grid> CREATOR = new Creator<Grid>() {
        @Override
        public Grid createFromParcel(Parcel in) {
            return new Grid(in);
        }

        @Override
        public Grid[] newArray(int size) {
            return new Grid[size];
        }
    };

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public void set_id() {
        this._id = hashCode();
    }

    public String getGridName() {
        return gridName;
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public int getRowNo() {
        return rowNo;
    }

    public void setRowNo(int rowNo) {
        this.rowNo = rowNo;
    }

    public int getColumnNo() {
        return columnNo;
    }

    public void setColumnNo(int columnNo) {
        this.columnNo = columnNo;
    }

    public List<Game> getGameList() {
        return gameList;
    }

    public void setGameList(List<Game> gameList) {
        this.gameList = gameList;
    }

    public boolean isKeepUpdates() {
        return keepUpdates;
    }

    public void setKeepUpdates(boolean keepUpdates) {
        this.keepUpdates = keepUpdates;
    }

    public boolean isForceAdd() {
        return forceAdd;
    }

    public void setForceAdd(boolean forceAdd) {
        this.forceAdd = forceAdd;
    }

    public List<GridLeagues> getGridLeagues() {
        return gridLeagues;
    }

    public void setGridLeagues(List<GridLeagues> gridLeagues) {
        this.gridLeagues = gridLeagues;
    }


}