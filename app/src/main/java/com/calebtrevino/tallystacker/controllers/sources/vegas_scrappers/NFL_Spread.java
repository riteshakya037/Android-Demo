package com.calebtrevino.tallystacker.controllers.sources.vegas_scrappers;

import android.os.Parcel;

import com.calebtrevino.tallystacker.controllers.sources.vegas_scrappers.bases.NFL;
import com.calebtrevino.tallystacker.models.enums.ScoreType;


/**
 * @author Ritesh Shakya
 */

public class NFL_Spread extends NFL {
    public static final Creator<NFL_Spread> CREATOR = new Creator<NFL_Spread>() {
        @Override
        public NFL_Spread createFromParcel(Parcel in) {
            return new NFL_Spread(in);
        }

        @Override
        public NFL_Spread[] newArray(int size) {
            return new NFL_Spread[size];
        }
    };
    private final ScoreType BID_SCORE_TYPE = ScoreType.SPREAD;

    public NFL_Spread() {
    }

    @SuppressWarnings("UnusedParameters")
    private NFL_Spread(Parcel in) {
        // Empty Block
    }

    @Override
    public ScoreType getScoreType() {
        return BID_SCORE_TYPE;
    }


}
