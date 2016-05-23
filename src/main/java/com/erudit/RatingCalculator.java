package com.erudit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zakharov_ga on 23.05.2016.
 */
public class RatingCalculator {

    private static final RatingCalculator instance = new RatingCalculator();

    private RatingCalculator(){ };

    public static RatingCalculator getInstance() {
        return instance;
    }

    private int computeProvRating() {
        return 0;
    }

    public Map<Player, Integer> getNewRatings(List<Player> players) {
        Map<Player, Integer> newRatings = new HashMap<>();

        int s = 0;
        for(Player player : players) {
            s += player.getRating();
        }
        double avgRating = ((double)s) / players.size();

        for(Player player : players) {
            if(player.getGames() <= 8) {

            }
        }


        return newRatings;
    }
}