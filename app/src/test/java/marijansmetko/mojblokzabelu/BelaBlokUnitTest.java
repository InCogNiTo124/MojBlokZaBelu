package marijansmetko.mojblokzabelu;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.junit.Assert.*;

/**
 * Created by Smetko on 29.4.2017..
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, packageName = "marijansmetko.mojblokzabelu", sdk = 19)
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
}
