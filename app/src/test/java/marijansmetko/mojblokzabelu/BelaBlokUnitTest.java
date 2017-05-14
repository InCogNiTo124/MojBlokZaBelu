package marijansmetko.mojblokzabelu;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Created by Smetko on 29.4.2017..
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, packageName = "marijansmetko.mojblokzabelu", sdk = 21)
public class BelaBlokUnitTest {

    BelaBlokHelper dbHandler;

    @Test
    public void testDatabase() {
        dbHandler = new BelaBlokHelper(RuntimeEnvironment.application);
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        dbHandler.addGame();
        dbHandler.addPoints(100, 62);
        dbHandler.addPoints(0, 252);
        dbHandler.addPoints(82, 80);
        try {
            Thread.sleep(2000);
            dbHandler.addGame();
            Thread.sleep(2000);
            dbHandler.addGame();
            Thread.sleep(2000);
            dbHandler.addGame();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Cursor c = db.rawQuery("SELECT * FROM GAMES", null);
        while (c.moveToNext()) {
            System.out.print(c.getString(0));
            System.out.print("\t");
            System.out.println(c.getString(1));
        }
        dbHandler.addPoints(252, 0);
        dbHandler.addPoints(69, 93);
        dbHandler.addPoints(20, 142);

        c = db.rawQuery("SELECT GAME_ID FROM POINTS ORDER BY _id DESC LIMIT 1", null);
        while (c.moveToNext()) {
            assertEquals(4, c.getInt(0));
        }


        c = db.rawQuery("SELECT max(g._id) FROM GAMES g", null);
        while(c.moveToNext()) {
            System.out.println(c.getInt(0));
        }

        System.out.println(Utility.stringPointsFromDatabase(dbHandler.getPoints()));

        dbHandler.deleteLastPoints();
        System.out.println();
        System.out.println(Utility.stringPointsFromDatabase(dbHandler.getPoints()));
        System.out.println();

        c = dbHandler.getAllGames();
        while(c.moveToNext()) {
            System.out.print(c.getString(0));
            System.out.print('\t');
            System.out.println(Utility.stringScoreFromDatabase(new int[]{c.getInt(1), c.getInt(2)}));
        }
        c.close();
    }

    @Test
    public void testAddGame() {
        dbHandler = new BelaBlokHelper(RuntimeEnvironment.application);
        MainActivity ma = Robolectric.buildActivity(MainActivity.class).create().get();
        ma.addPoints(82, 80, 0, 0, false, false, true);     //  82  80
        ma.addPoints(82, 80, 0, 0, false, false, false);    //  162 0
        ma.addPoints(80, 82, 0, 0, false, false, false);    //  80  82
        ma.addPoints(80, 82, 0, 0, false, false, true);     //  0   162

        ma.addPoints(81, 81, 0, 0, false, false, true);     //  0   162
        ma.addPoints(81, 81, 20, 0, false, true, true);     //  0   202
        ma.addPoints(81, 81, 0, 20, true,  false, true);    //  0   202
        ma.addPoints(81, 81, 0, 0, false, false, false);    //  162 0
        ma.addPoints(81, 81, 20, 0, false, true, false);    //  202 0
        ma.addPoints(81, 81, 0, 20, true, false, false);    //  202 0

        ma.addPoints(252, 0, 20, 0, false, true, true);     //  292 0
        ma.addPoints(252, 0, 0, 20, false, true, true);     //  292 0
        ma.addPoints(252, 0, 20, 0, true, false, true);     //  292 0
        ma.addPoints(252, 0, 0, 20, true, false, true);     //  292 0

        ma.addPoints(0, 252, 20, 0, false, true, true);     //  0   292
        ma.addPoints(0, 252, 0, 20, false, true, true);     //  0   292
        ma.addPoints(0, 252, 20, 0, true, false, true);     //  0   292
        ma.addPoints(0, 252, 0, 20, true, false, true);     //  0   292

        System.out.println(Utility.stringPointsFromDatabase(dbHandler.getPoints()));
    }
}
