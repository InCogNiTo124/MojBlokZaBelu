package marijansmetko.mojblokzabelu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

class BelaBlokHelper extends SQLiteOpenHelper {

    private static class GamesTable implements BaseColumns {
        static final String TABLE_NAME = "GAMES";
        static final String COLUMN_START_DATE = "START_DATE";
        static final String COLUMN_DEALER = "DEALER";
    }

    private static class PointsTable implements BaseColumns {
        static final String TABLE_NAME = "POINTS";
        static final String COLUMN_GAME_ID = "GAME_ID";
        static final String COLUMN_BODOVI_MI = "POINTS_MI";
        static final String COLUMN_BODOVI_VI = "POINTS_VI";
    }

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BelaBlok.db";
    private int CURRENT_GAME = 1;

    BelaBlokHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(Constants.TAG, "BelaBlok Helper onCreate");
        String CREATE_TABLE_GAMES = "CREATE TABLE " + GamesTable.TABLE_NAME + " (" +
                GamesTable._ID + " INTEGER PRIMARY KEY, " +
                GamesTable.COLUMN_START_DATE + " TEXT, " +
                GamesTable.COLUMN_DEALER + " INTEGER DEFAULT 0)";

        String CREATE_TABLE_POINTS = "CREATE TABLE " + PointsTable.TABLE_NAME + " (" +
                PointsTable._ID + " INTEGER PRIMARY KEY, " +
                PointsTable.COLUMN_GAME_ID + " INTEGER, " +
                PointsTable.COLUMN_BODOVI_MI + " INTEGER, " +
                PointsTable.COLUMN_BODOVI_VI + " INTEGER, " +
                "FOREIGN KEY (" + PointsTable.COLUMN_GAME_ID + ") " +
                "REFERENCES " + GamesTable.TABLE_NAME + "(" + GamesTable._ID + "))";

        db.execSQL(CREATE_TABLE_GAMES);
        db.execSQL(CREATE_TABLE_POINTS);
        addGame(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Constants.TAG, "Database upgraded");
        db.execSQL("DROP TABLE IF EXISTS " + GamesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PointsTable.TABLE_NAME);
        this.onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d(Constants.TAG, "Database opened");
    }

    private int getLastGame(SQLiteDatabase db) {
        int x = 1;
//        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT max(g." + GamesTable._ID + ") FROM " + GamesTable.TABLE_NAME + " g";
        Cursor c = db.rawQuery(sql, null);
        while(c.moveToNext()) {
            x = c.getInt(0);
        }
        c.close();
        return x;
    }

    int[] getScore() {
        Log.d(Constants.TAG, "getScore");
        int[] a = {0, 0};
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT sum(p." + PointsTable.COLUMN_BODOVI_MI +
                "), sum(p." + PointsTable.COLUMN_BODOVI_VI +
                ") FROM " + PointsTable.TABLE_NAME +
                " p WHERE p." + PointsTable.COLUMN_GAME_ID +
                " = " + this.CURRENT_GAME;
        Cursor cursor = db.rawQuery(sql, null);
//        System.out.println(sql);
        while (cursor.moveToNext()) {
            a[0] = cursor.getInt(0);
            a[1] = cursor.getInt(1);
        }
        cursor.close();
        return a;
    }

    ArrayList<int[]> getPoints() {
        ArrayList<int[]> a = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT p." + PointsTable.COLUMN_BODOVI_MI +
                ", p." + PointsTable.COLUMN_BODOVI_VI +
                " FROM " + PointsTable.TABLE_NAME +
                " p WHERE p." + PointsTable.COLUMN_GAME_ID +
                " = " + this.CURRENT_GAME;
        Cursor c = db.rawQuery(sql, null);
        while(c.moveToNext()) {
            a.add(new int[]{c.getInt(0), c.getInt(1)});
        }
        c.close();
        return a;
    }

    void addPoints(int mi, int vi) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put(PointsTable.COLUMN_BODOVI_MI, mi);
        v.put(PointsTable.COLUMN_BODOVI_VI, vi);
        v.put(PointsTable.COLUMN_GAME_ID, CURRENT_GAME);
        long x = db.insert(PointsTable.TABLE_NAME, null, v);
        String sql = "UPDATE " + GamesTable.TABLE_NAME +
                        " SET " + GamesTable.COLUMN_DEALER +
                        " = (" + GamesTable.COLUMN_DEALER +
                        "+1) % 4 WHERE " + GamesTable._ID +
                        " = " + this.CURRENT_GAME;
        db.execSQL(sql);
        Log.d(Constants.TAG, x > 0 ? "Points added" : "Fckn error");
    }

    void addGame() {
        this.addGame(this.getWritableDatabase());
    }

    private void addGame(SQLiteDatabase db) {
        String SQL_CURR_TIME = "SELECT datetime('now')";
        String sql2 = "INSERT INTO " + GamesTable.TABLE_NAME +
                " (" + GamesTable.COLUMN_START_DATE + ")" +
                " VALUES ((" + SQL_CURR_TIME + "))";

        db.execSQL(sql2);
        CURRENT_GAME = getLastGame(db);
    }

    void deleteLastPoints() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM " + PointsTable.TABLE_NAME +
                " WHERE " + PointsTable._ID +
                " = (SELECT max(" + PointsTable._ID +
                ") FROM " + PointsTable.TABLE_NAME +
                " WHERE " + PointsTable.COLUMN_GAME_ID +
                " = " + this.CURRENT_GAME + ")";
        db.execSQL(sql);
    }

    public void setCurrentGame(int currentGame) {
        CURRENT_GAME = currentGame;
    }

    public Cursor getAllGames() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT g." + GamesTable._ID +
                ", g." + GamesTable.COLUMN_START_DATE +
                ", sum(p." + PointsTable.COLUMN_BODOVI_MI +
                "), sum(p." + PointsTable.COLUMN_BODOVI_VI +
                ") FROM " + GamesTable.TABLE_NAME +
                " g LEFT JOIN " + PointsTable.TABLE_NAME +
                " p ON g." + GamesTable._ID +
                " = p." + PointsTable.COLUMN_GAME_ID +
                " GROUP BY g." + GamesTable.COLUMN_START_DATE;
//        System.out.println(sql);
        return db.rawQuery(sql, null);
    }
}
