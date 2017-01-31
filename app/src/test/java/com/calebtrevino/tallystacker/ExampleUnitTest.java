package com.calebtrevino.tallystacker;

import com.calebtrevino.tallystacker.controllers.factories.DefaultFactory;
import com.calebtrevino.tallystacker.controllers.sources.vegas_scrappers.NCAA_BK_Total;
import com.calebtrevino.tallystacker.controllers.sources.vegas_scrappers.NFL_Spread;
import com.calebtrevino.tallystacker.controllers.sources.vegas_scrappers.Soccer_Total;
import com.calebtrevino.tallystacker.controllers.sources.vegas_scrappers.bases.League;
import com.calebtrevino.tallystacker.models.Bid;
import com.calebtrevino.tallystacker.models.Game;
import com.calebtrevino.tallystacker.models.GridLeagues;
import com.calebtrevino.tallystacker.models.database.DatabaseContract;
import com.calebtrevino.tallystacker.models.enums.BidCondition;
import com.calebtrevino.tallystacker.models.espn.EspnJson;
import com.calebtrevino.tallystacker.utils.ParseUtils;
import com.calebtrevino.tallystacker.utils.TeamPreference;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.tz.UTCProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@SuppressWarnings("unused")
public class ExampleUnitTest {
    @Test
    public void team_info() {
        String bodyText = "09/04 1:10 PM 901 St. Louis 902 Cincinnati";
        Pattern pattern = Pattern.compile("([0-9]{2}/[0-9]{2})" + "\\s+" + "([0-9]{1,2}:[0-9]{2}" + "\\s+" + "[A|P]M)" + "\\s+" + "([0-9]{3})" + ".?(\\w.*)" + "([0-9]{3})" + ".?(\\w.*)");
        Matcher m = pattern.matcher(bodyText);
        if (m.matches()) {
            System.out.println("m.group(1) = " + m.group(1));
            System.out.println("m.group(1) = " + m.group(2));
            System.out.println("m.group(1) = " + m.group(3));
            System.out.println("m.group(1) = " + m.group(4));
            System.out.println("m.group(1) = " + m.group(5));
            System.out.println("m.group(1) = " + m.group(6));
        }
    }

    @Test
    public void bidInfo() {
        String text = "162½u-05 " +
                "-1 -05";
        Pattern pattern = Pattern.compile(".*(\\d+[\\p{N}]?)([uUoO]).*");
        Matcher m = pattern.matcher(text);
        if (m.matches()) {
            System.out.println("m.group(1) = " + m.group(1));
            System.out.println("m.group(1) = " + BidCondition.match(m.group(2)));
        }

    }

    @Test
    public void total_check() {
        String bodyText = "162½u-05 " +
                "-1 -05";
        assertEquals(true, bodyText.matches(".*[\\d]+.*?[oOuU][+|-]?[\\d]+.*"));
    }

    @Test
    public void dateParser() {
        System.out.println("date = " + new Date(ParseUtils.parseDate("09/08 8:30 PM")));
    }

    @Test
    public void packageTest() throws Exception {
        League league = new NCAA_BK_Total();
        DateTimeZone.setProvider(new UTCProvider());

        for (Game game : league.pullGamesFromNetwork(null)) {
            System.out.println(DatabaseContract.DbHelper.checkBid(game) + " for " + game);
        }
    }

    @Test
    public void LeagueStatusCheck() throws Exception {
        Document doc = Jsoup.connect("http://www.espn.com/womens-college-basketball/scoreboard?date=20170127")
                .get();
        Elements scriptElements = doc.getElementsByTag("script");
        Pattern pattern = Pattern.compile("window.espn.scoreboardData[\\s\t]*= (.*);.*window.espn.scoreboardSettings.*");
        for (Element element : scriptElements) {
            for (DataNode node : element.dataNodes()) {
                if (node.getWholeData().startsWith("window.espn.scoreboardData")) {
                    Matcher matcher = pattern.matcher(node.getWholeData());
                    if (matcher.matches()) {
                        Gson gson = new Gson();
                        EspnJson espnJson = new Gson().fromJson(matcher.group(1), EspnJson.class);
                        espnJson.getTeams();
                    }
                }
            }
        }
    }

    @Test
    public void GameStatusCheck() throws Exception {
        Document parsedDocument = Jsoup.connect("http://www.espn.com/nba/game?gameId=400928501").timeout(60 * 1000).get();
        Elements element = parsedDocument.select("div#gamepackage-linescore-wrap");
        Elements teams = element.select("td.team-name");
        Elements scores = element.select("td.final-score");
        for (int i = 0; i < teams.size(); i++) {
            System.out.println(teams.get(i).text() + " " + scores.get(i).text());
        }
    }

    @Test
    public void CheckBidTest() throws Exception {
        Game game = DefaultFactory.Game.constructDefault();
        game.setLeagueType(new Soccer_Total());
        Bid bid1 = DefaultFactory.Bid.constructDefault();
        bid1.setBidAmount(2.5F);
        bid1.setVI_column(true);
        bid1.setCondition(BidCondition.UNDER);
        bid1.setVigAmount(-2F);
        game.getBidList().add(bid1);
        game.setVI_bid();
        System.out.println(DatabaseContract.DbHelper.checkBid(game) + " for " + game);
    }

    @Test
    public void tryJsoup() throws Exception {
        Document parsedDocument = Jsoup.connect("http://www.espn.com/wnba/teams").timeout(60 * 1000).get();
        Elements elements = parsedDocument.select("div.mod-content>ul>li>div>b");
        for (Element element : elements) {
            String[] urlSplit = element.select("b>a").get(0).attr("href").split("/");
            System.out.println(element.text() + "," + urlSplit[urlSplit.length - 2].toUpperCase());
        }
    }

    @Test
    public void InternalTeamTest() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("nfl_teams.txt");
        File file = new File(resource.getPath());
        String line;
        List<TeamPreference.TeamsWrapper> teamList = new ArrayList<>();
        try (InputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
             BufferedReader br = new BufferedReader(isr)
        ) {
            while ((line = br.readLine()) != null) {
                String[] lineMap = line.split(",");
                teamList.add(new TeamPreference.TeamsWrapper(lineMap[0], lineMap[1], lineMap[2], lineMap[3]));
            }
        }
        if (teamList.contains(new TeamPreference.TeamsWrapper("New England"))) {
            System.out.println("Success");
        }
    }

    @Test
    public void JsonTest() {
        List<GridLeagues> bidList = new LinkedList<>();
        bidList.add(DefaultFactory.GridLeagues.constructDefault());
        bidList.add(DefaultFactory.GridLeagues.constructDefault());
        bidList.add(DefaultFactory.GridLeagues.constructDefault());
        bidList.add(DefaultFactory.GridLeagues.constructDefault());
        bidList = bidList.subList(0, 2);
        System.out.println(bidList);

    }

    @Test
    public void SpaceTrim() {
        String s = " Philadelphia Tat";
        System.out.println("s = " + s.trim().replaceAll(" ", "") + "s");

    }

    @Test
    public void dateTest() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        System.out.println(new Date(cal.getTimeInMillis()));
        DateTime dateTime = new DateTime();

        System.out.println("dateTime.withTimeAtStartOfDay().getMillis() = " + new Date(dateTime.minusDays(1).withTimeAtStartOfDay().getMillis()));
    }

    @Test
    public void checkTime() {
        Date date = new Date(1478851200000L);
        System.out.println(date);

    }

    @Test
    public void Download() throws Exception {
        League league = new NFL_Spread();
        Document parsedDocument = Jsoup.connect(league.getBaseUrl()).timeout(60 * 1000).get();

        try {
            String root = "";
            File myDir = new File("Tallystacker/" + new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            myDir.mkdirs();
            final File f = new File(myDir, league.getAcronym() + "-" + league.getScoreType() + ".html");
            FileUtils.writeStringToFile(f, parsedDocument.select("table.frodds-data-tbl").outerHtml(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}