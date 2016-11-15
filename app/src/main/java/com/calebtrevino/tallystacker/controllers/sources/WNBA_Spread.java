package com.calebtrevino.tallystacker.controllers.sources;

import android.os.Parcel;

import com.calebtrevino.tallystacker.controllers.sources.bases.WNBA;
import com.calebtrevino.tallystacker.models.enums.ScoreType;


/**
 * @author Ritesh Shakya
 */

public class WNBA_Spread extends WNBA {
    private final ScoreType BID_SCORE_TYPE = ScoreType.SPREAD;

    public WNBA_Spread() {
    }

    @SuppressWarnings("UnusedParameters")
    private WNBA_Spread(Parcel in) {
    }

    public static final Creator<WNBA_Spread> CREATOR = new Creator<WNBA_Spread>() {
        @Override
        public WNBA_Spread createFromParcel(Parcel in) {
            return new WNBA_Spread(in);
        }

        @Override
        public WNBA_Spread[] newArray(int size) {
            return new WNBA_Spread[size];
        }
    };

    @Override
    public ScoreType getScoreType() {
        return BID_SCORE_TYPE;
    }

}
