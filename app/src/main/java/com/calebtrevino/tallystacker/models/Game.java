package com.calebtrevino.tallystacker.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.calebtrevino.tallystacker.controllers.sources.League;
import com.calebtrevino.tallystacker.models.base.BaseModel;
import com.calebtrevino.tallystacker.models.enums.BidResult;
import com.calebtrevino.tallystacker.models.enums.ScoreType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fatal on 9/3/2016.
 */

public class Game extends BaseModel implements Parcelable {
    public static final String TAG = Game.class.getSimpleName();

    private long _id;
    private Team firstTeam;
    private Team SecondTeam;
    private League leagueType;
    private long gameDateTime;
    private long gameAdded;
    private ScoreType scoreType;
    private List<Bid> bidList;
    private BidResult bidResult;
    private long firstTeamScore;
    private long secondTeamScore;

    public Game() {
    }

    protected Game(Parcel in) {
        _id = in.readLong();
        firstTeam = in.readParcelable(Team.class.getClassLoader());
        SecondTeam = in.readParcelable(Team.class.getClassLoader());
        leagueType = in.readParcelable(League.class.getClassLoader());
        gameDateTime = in.readLong();
        gameAdded = in.readLong();
        scoreType = in.readParcelable(ScoreType.class.getClassLoader());
        bidList = in.createTypedArrayList(Bid.CREATOR);
        bidResult = in.readParcelable(BidResult.class.getClassLoader());
        firstTeamScore = in.readLong();
        secondTeamScore = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeParcelable(firstTeam, flags);
        dest.writeParcelable(SecondTeam, flags);
        dest.writeParcelable(leagueType, flags);
        dest.writeLong(gameDateTime);
        dest.writeLong(gameAdded);
        dest.writeParcelable(scoreType, flags);
        dest.writeTypedList(bidList);
        dest.writeParcelable(bidResult, flags);
        dest.writeLong(firstTeamScore);
        dest.writeLong(secondTeamScore);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    public Long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    @Override
    public void createID() {
        this._id = (long) hashCode();
    }

    public List<Bid> getBidList() {
        return bidList;
    }

    public void setBidList(List<Bid> bidList) {
        this.bidList = bidList;
    }

    public ScoreType getScoreType() {
        return scoreType;
    }

    public void setScoreType(ScoreType scoreType) {
        this.scoreType = scoreType;
    }

    public long getGameAdded() {
        return gameAdded;
    }

    public void setGameAdded(long gameAdded) {
        this.gameAdded = gameAdded;
    }

    public long getGameDateTime() {
        return gameDateTime;
    }

    public void setGameDateTime(long gameDate) {
        this.gameDateTime = gameDate;
    }

    public League getLeagueType() {
        return leagueType;
    }

    public void setLeagueType(League leagueType) {
        this.leagueType = leagueType;
    }

    public Team getSecondTeam() {
        return SecondTeam;
    }

    public void setSecondTeam(Team secondTeam) {
        SecondTeam = secondTeam;
    }

    public Team getFirstTeam() {
        return firstTeam;
    }

    public void setFirstTeam(Team firstTeam) {
        this.firstTeam = firstTeam;
    }

    public BidResult getBidResult() {
        return bidResult;
    }

    public void setBidResult(BidResult bidResult) {
        this.bidResult = bidResult;
    }

    public long getFirstTeamScore() {
        return firstTeamScore;
    }

    public void setFirstTeamScore(long firstTeamScore) {
        this.firstTeamScore = firstTeamScore;
    }

    public long getSecondTeamScore() {
        return secondTeamScore;
    }

    public void setSecondTeamScore(long secondTeamScore) {
        this.secondTeamScore = secondTeamScore;
    }


    @Override
    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", get_id());
            jsonObject.put("first_team", getFirstTeam().get_id());
            jsonObject.put("second_team", getSecondTeam().get_id());
            jsonObject.put("league_type", getLeagueType().getPackageName());
            jsonObject.put("game_date_type", getGameDateTime());
            jsonObject.put("score_type", getScoreType().getValue());
            jsonObject.put("bid_list", Bid.createJsonArray(getBidList()));
            jsonObject.put("bid_result", getBidResult().getValue());
            jsonObject.put("first_team_score", getFirstTeamScore());
            jsonObject.put("second_team_score", getSecondTeamScore());
            jsonObject.put("game_added", getGameAdded());
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getIDArrayToJSSON(List<Game> gameList) {
        JSONArray jsonArray = new JSONArray();
        for (Game game : gameList) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", game.get_id());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }

    public static List<String> getIdArrayFromJSON(String idListJson) {
        List<String> idList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(idListJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                idList.add(jsonArray.get(0).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return idList;
    }
}
