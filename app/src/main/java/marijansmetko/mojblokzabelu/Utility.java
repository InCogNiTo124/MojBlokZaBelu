package marijansmetko.mojblokzabelu;

import java.util.ArrayList;

/**
 * Created by Smetko on 1.5.2017..
 */

public class Utility {

    private Utility(){}

    public static String stringScoreFromDatabase(int[] score) {
        String txt = new StringBuilder()
                .append(score[0] < 10 ? "  " : (score[0] < 100 ? " " : ""))
                .append(score[0])
                .append(" | ")
                .append(score[1] < 10 ? "  " : (score[1] < 100 ? " " : ""))
                .append(score[1]).toString();
        return txt;
    }

    public static String stringPointsFromDatabase(ArrayList<int[]> points) {
        String bodovi = "";
        for (int[] a : points) {
            bodovi += new StringBuilder()
                    .append(a[0] < 10 ? "  " : (a[0] < 100 ? " " : ""))
                    .append(a[0])
                    .append(" | ")
                    .append(a[1] < 10 ? "  " : (a[1] < 100 ? " " : ""))
                    .append(a[1])
                    .append("\n").toString();
        }
        return bodovi;
    }
}
