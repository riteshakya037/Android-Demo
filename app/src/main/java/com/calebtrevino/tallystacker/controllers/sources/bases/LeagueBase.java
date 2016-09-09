package com.calebtrevino.tallystacker.controllers.sources.bases;

import android.content.Context;
import android.util.Log;

import com.calebtrevino.tallystacker.controllers.factories.DefaultFactory;
import com.calebtrevino.tallystacker.controllers.sources.League;
import com.calebtrevino.tallystacker.controllers.sources.ProBaseball;
import com.calebtrevino.tallystacker.models.Game;
import com.calebtrevino.tallystacker.models.database.DatabaseContract;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fatal on 9/4/2016.
 */

public abstract class LeagueBase implements League {
    private static final String TAG = ProBaseball.class.getSimpleName();

    @Override
    public List<Game> pullGamesFromNetwork(Context context) {
        Log.e(TAG, "Started " + getName());
        List<Game> updatedGameList = new ArrayList<>();
        Document parsedDocument = null;
        try {
            parsedDocument = Jsoup.connect(getBaseUrl()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updatedGameList = scrapeUpdateGamesFromParsedDocument(updatedGameList, parsedDocument);

        updateLibraryInDatabase(updatedGameList, context);
        return updatedGameList;
    }

    private List<Game> scrapeUpdateGamesFromParsedDocument(List<Game> updatedGameList, Document parsedDocument) {
        Elements updatedHtmlBlocks = parsedDocument.select(getCSSQuery());
        for (Element currentHtmlBlock : updatedHtmlBlocks) {
            Game currentGame = constructGameFromHtmlBlock(currentHtmlBlock);
            updatedGameList.add(currentGame);
        }

        return updatedGameList;
    }

    private Game constructGameFromHtmlBlock(Element currentHtmlBlock) {
        Game gameFromHtmlBlock = DefaultFactory.Game.constructDefault();
        gameFromHtmlBlock.setScoreType(getScoreType());
        gameFromHtmlBlock.setLeagueType(this);
        gameFromHtmlBlock.setGameAdded(new Date().getTime());
        Elements updatedHtmlBlocks = currentHtmlBlock.select("td");
        boolean once = true;
        for (Element currentColumnBlock : updatedHtmlBlocks) {
            if (once) {
                once = false;
                createGameInfo(currentColumnBlock.text(), gameFromHtmlBlock);

            } else {
                createBidInfo(currentColumnBlock.text(), gameFromHtmlBlock);
            }
        }
        gameFromHtmlBlock.createID();
        return gameFromHtmlBlock;
    }

    protected abstract void createGameInfo(String text, Game gameFromHtmlBlock);

    protected abstract void createBidInfo(String text, Game gameFromHtmlBlock);


    private void updateLibraryInDatabase(List<Game> updatedGameList, Context context) {
        DatabaseContract.DbHelper dbHelper = new DatabaseContract.DbHelper(context);
        dbHelper.onInsertGame(updatedGameList);
        dbHelper.selectAll("game_table");
    }

    @Override
    public String toString() {
        return "League {" +
                " name = \"" + getName() +
                "\"}";
    }
}
