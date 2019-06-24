package pacman.game;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.*;
import pacman.entries.pacman.LegacyPacMan;
import pacman.entries.pacman.MyMsPacMan;
import pacman.entries.pacman.MyPacMan;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

import static pacman.game.Constants.DELAY;

public class main {
    public static void main(String args[]) {

        int numTrials = 100;
        //myRunExperiment(new RandomPacMan(), new RandomGhosts(), numTrials);
        //myRunExperiment(new RandomPacMan(), new AggressiveGhosts(), numTrials);
        //myRunExperiment(new RandomPacMan(), new Legacy(), numTrials);
        //myRunExperiment(new RandomNonRevPacMan(), new RandomGhosts(), numTrials);
        //myRunExperiment(new RandomNonRevPacMan(), new AggressiveGhosts(), numTrials);
        //myRunExperiment(new RandomNonRevPacMan(), new Legacy(), numTrials);
        //myRunExperiment(new NearestPillPacMan(), new RandomGhosts(), numTrials);
        //myRunExperiment(new NearestPillPacMan(), new AggressiveGhosts(), numTrials);
        //myRunExperiment(new NearestPillPacMan(), new Legacy(), numTrials);
        //myRunExperiment(new LegacyPacMan(), new Legacy(), numTrials);
        long start = System.currentTimeMillis();
        myRunExperiment(new MyPacMan(RandomGhosts.class.getSimpleName()), new RandomGhosts(), numTrials);
        long tempo = System.currentTimeMillis() - start;
        System.out.println("Tempo totale 50 partite: " + tempo + " , Media tempo singola partita: " + tempo/(long)50);

    }

    /**
     * For running multiple games without visuals. This is useful to get a good idea of how well a controller plays
     * against a chosen opponent: the random nature of the game means that performance can vary from game to game.
     * Running many games and looking at the average score (and standard deviation/error) helps to get a better
     * idea of how well the controller is likely to do in the competition.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController  The Ghosts controller
     * @param trials           The number of trials to be executed
     */
    public static void myRunExperiment(Controller<Constants.MOVE> pacManController, Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController, int trials) {
        double avgScore = 0;
        double score = 0;
        double maxScore = 0;
        double minScore = 100000;

        Random rnd = new Random(0);
        Game game;

        for (int i = 0; i < trials; i++) {
            game = new Game(rnd.nextLong());

            while (!game.gameOver()) {
                game.advanceGame(pacManController.getMove(game.copy(), System.currentTimeMillis() + DELAY),
                        ghostController.getMove(game.copy(), System.currentTimeMillis() + DELAY));
            }
            score = game.getScore();
            avgScore += score;
            if (score > maxScore) {
                maxScore = score;
            }
            if (score < minScore) {
                minScore = score;
            }
            System.out.println(i + "\t" + game.getScore());
        }

        System.out.println("AvgScore: " + avgScore / trials + "\t" + "MaxScore: " + maxScore + "\t" + "MinScore: " + minScore);
    }
}
