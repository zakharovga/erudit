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

    public Map<Player, Double> computeNewRatings(List<Player> players) {

        Map<Player, Double> ratingPoints = computeRatingPoints(players);

        Map<Player, Double> newRatings = new HashMap<>();

        double summ = 0;
        for(Player player : players) {
            summ += player.getRating();
        }
        double avgRating = summ / players.size();

        for(Player player : players) {
            if(player.isGuest()) {
                newRatings.put(player, User.DEFAULT_RATING);
                continue;
            }
            int nGames = player.getGames();
            int size = players.size();
            double s = ratingPoints.get(player);
            double newRating;
            if(nGames <= 8) {
                newRating = (nGames * player.getRating() + (size - 1) * avgRating + (2 * ratingPoints.get(player) - (size - 1)) * 400) / (nGames + (size - 1));
            }
            else {
                double effGames = computeEffGames(player.getRating(), nGames);
                double k = 800.0 / (effGames + (size - 1));
                double sumExp = 0.0;
                for(Player each : players)
                    if(!each.equals(player))
                        sumExp += computeWinExp(player.getRating(), each.getRating());
                newRating = player.getRating() + k * (s - sumExp);
            }
            newRatings.put(player, newRating);
        }
        return newRatings;
    }

    private Map<Player, Double> computeRatingPoints(List<Player> players) {
        Map<Player, Double> ratingPoints = new HashMap<>();
        for(Player player : players)
            ratingPoints.put(player, 0.0);
        int n = players.size();
        for(int i = 0; i < n; i++) {
            for(int j = i + 1; j < n; j++) {
                Player iPlayer = players.get(i);
                Player jPlayer = players.get(j);
                Double iOldPoints = ratingPoints.get(iPlayer);
                Double jOldPoints = ratingPoints.get(jPlayer);
                if(players.get(i).getTotalPoints() > players.get(j).getTotalPoints())
                    ratingPoints.put(iPlayer, iOldPoints + 1);
                else if(players.get(i).getTotalPoints() == players.get(j).getTotalPoints()) {
                    ratingPoints.put(iPlayer, iOldPoints + 0.5);
                    ratingPoints.put(jPlayer, jOldPoints + 0.5);
                }
                else
                    ratingPoints.put(jPlayer, jOldPoints + 1);
            }
        }
        return ratingPoints;
    }

    private double computeEffGames(double oldRating, int nGames) {
        double n1;
        if(oldRating <= 2355)
            n1 = 50.0 / Math.sqrt(0.662 + 0.00000739 * (2569.0 - oldRating) * (2569.0 - oldRating));
        else
            n1 = 50.0;
        return Math.min(nGames, n1);
    }

    private double computeWinExp(double oldRating, double oppRating) {
        return 1 / (Math.pow(10, -(oldRating - oppRating) / 400) + 1);
    }
}