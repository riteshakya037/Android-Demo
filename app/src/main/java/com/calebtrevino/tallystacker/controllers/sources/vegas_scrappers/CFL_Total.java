package com.calebtrevino.tallystacker.controllers.sources.vegas_scrappers;

import android.os.Parcel;
import com.calebtrevino.tallystacker.controllers.sources.vegas_scrappers.bases.CFL;
import com.calebtrevino.tallystacker.models.enums.ScoreType;

/**
 * @author Ritesh Shakya
 */

public class CFL_Total extends CFL {
    public static final Creator<CFL_Total> CREATOR = new Creator<CFL_Total>() {
        @Override public CFL_Total createFromParcel(Parcel in) {
            return new CFL_Total(in);
        }

        @Override public CFL_Total[] newArray(int size) {
            return new CFL_Total[size];
        }
    };
    private final ScoreType BID_SCORE_TYPE = ScoreType.TOTAL;

    public CFL_Total() {
    }

    @SuppressWarnings("UnusedParameters") private CFL_Total(Parcel in) {
        // Empty Block
    }

    @Override public ScoreType getScoreType() {
        return BID_SCORE_TYPE;
    }

    @Override public String getContraryPackageName() {
        return CFL_Spread.class.getName();
    }
}
