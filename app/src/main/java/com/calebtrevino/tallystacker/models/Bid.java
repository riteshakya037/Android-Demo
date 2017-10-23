package com.calebtrevino.tallystacker.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.calebtrevino.tallystacker.controllers.factories.DefaultFactory;
import com.calebtrevino.tallystacker.models.base.BaseModel;
import com.calebtrevino.tallystacker.models.enums.BidCondition;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Ritesh Shakya
 */
@SuppressWarnings("SameParameterValue") public class Bid extends BaseModel implements Parcelable {
    public static final Creator<Bid> CREATOR = new Creator<Bid>() {
        @Override public Bid createFromParcel(Parcel in) {
            return new Bid(in);
        }

        @Override public Bid[] newArray(int size) {
            return new Bid[size];
        }
    };
    private float bidAmount;
    private float vigAmount;
    private BidCondition condition;
    private boolean VI_column;

    public Bid() {
    }

    private Bid(Parcel in) {
        bidAmount = in.readFloat();
        vigAmount = in.readFloat();
        condition = in.readParcelable(BidCondition.class.getClassLoader());
        VI_column = in.readByte() != 0;
    }

    private static Bid createFromJSON(String jsonString) {
        Bid bid = DefaultFactory.Bid.constructDefault();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            bid.setBidAmount(jsonObject.getString("bid_amount"));
            bid.setVigAmount(jsonObject.getString("vig_amount"));
            bid.setCondition(BidCondition.match(jsonObject.getString("bid_condition")));
            bid.setVIColumn(jsonObject.getBoolean("vi_column"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bid;
    }

    public static String createJsonArray(List<Bid> bidList) {
        JSONArray jsonArray = new JSONArray();
        for (Bid bid : bidList) {
            try {
                JSONObject jsonObject = new JSONObject(bid.toJSON());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }

    public static List<Bid> createArrayFromJson(String jsonString) {
        List<Bid> bids = new LinkedList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                bids.add(createFromJSON(jsonArray.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bids;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(bidAmount);
        dest.writeFloat(vigAmount);
        dest.writeParcelable(condition, flags);
        dest.writeByte((byte) (VI_column ? 1 : 0));
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void createID() {
        // Empty method
    }

    public boolean isVIColumn() {
        return VI_column;
    }

    public void setVIColumn(boolean VI_column) {
        this.VI_column = VI_column;
    }

    @Override protected String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bid_amount", getBidAmount());
            jsonObject.put("vig_amount", getVigAmount());
            jsonObject.put("bid_condition", getCondition().getValue());
            jsonObject.put("vi_column", isVIColumn());
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public float getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(float bidAmount) {
        this.bidAmount = bidAmount;
    }

    public void setBidAmount(String bidAmount) {
        String tempAmount = bidAmount.replaceAll("\\u00BD", ".5")
                .replaceAll("\\u00BC", ".25")
                .replaceAll("\\u00BE", ".75");
        this.bidAmount = Float.parseFloat(tempAmount);
    }

    public void setVigAmount(String vigAmount) {
        String tempAmount = vigAmount;
        if (vigAmount != null && vigAmount.equals("EV")) {
            tempAmount = "-1";
        }
        this.vigAmount = Float.parseFloat(tempAmount);
    }

    public BidCondition getCondition() {
        return condition;
    }

    public void setCondition(BidCondition condition) {
        this.condition = condition;
    }

    public float getVigAmount() {
        return vigAmount;
    }

    public void setVigAmount(float vigAmount) {
        this.vigAmount = vigAmount;
    }

    public void setBidAmount(String bidAmount, boolean reverse) {
        String tempAmount = bidAmount.replaceAll("\\u00BD", ".5")
                .replaceAll("\\u00BC", ".25")
                .replaceAll("\\u00BE", ".75");
        this.bidAmount = Float.parseFloat(tempAmount) * (reverse ? -1 : 1);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bid bid = (Bid) o;
        return Float.compare(bid.bidAmount, bidAmount) == 0
                && Float.compare(bid.vigAmount, vigAmount) == 0
                && VI_column == bid.VI_column
                && condition == bid.condition;
    }

    @Override public int hashCode() {
        int result = (bidAmount != +0.0f ? Float.floatToIntBits(bidAmount) : 0);
        result = 31 * result + (vigAmount != +0.0f ? Float.floatToIntBits(vigAmount) : 0);
        result = 31 * result + condition.hashCode();
        result = 31 * result + (VI_column ? 1 : 0);
        return result;
    }
}
