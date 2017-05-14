package marijansmetko.mojblokzabelu;

import java.util.ArrayList;

/**
 * Created by Smetko on 1.5.2017..
 */

class Utility {

    private Utility(){}

    static String stringScoreFromDatabase(int[] score) {
        return new StringBuilder()
                .append(score[0] < 10 ? "  " : (score[0] < 100 ? " " : ""))
                .append(score[0])
                .append(" | ")
                .append(score[1] < 10 ? "  " : (score[1] < 100 ? " " : ""))
                .append(score[1]).toString();
    }

    static String stringPointsFromDatabase(ArrayList<int[]> points) {
        String bodovi = "";
        for (int[] a : points) {
            bodovi += (a[0] < 10 ? "  " : (a[0] < 100 ? " " : "")) +
                    a[0] +
                    " | " +
                    (a[1] < 10 ? "  " : (a[1] < 100 ? " " : "")) +
                    a[1] +
                    "\n";
        }
        return bodovi;
    }
}
