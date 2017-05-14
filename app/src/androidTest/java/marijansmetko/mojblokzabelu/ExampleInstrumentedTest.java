package marijansmetko.mojblokzabelu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("marijansmetko.mojblokzabelu", appContext.getPackageName());
        BelaBlokHelper dbHelper = new BelaBlokHelper(appContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM GAMES", null);
        while (c.moveToNext()) {
            System.out.print(c.getInt(0));
            System.out.print('\t');
            System.out.print(c.getString(1));
            System.out.print('\t');
            System.out.println(c.getInt(2));
        }
        c = db.rawQuery("SELECT * FROM POINTS", null);
        while (c.moveToNext()) {
            System.out.print(c.getInt(0));
            System.out.print('\t');
            System.out.print(c.getInt(1));
            System.out.print('\t');
            System.out.print(c.getInt(2));
            System.out.print('\t');
            System.out.println(c.getInt(3));
        }
        c.close();
    }
}
