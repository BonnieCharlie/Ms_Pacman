package pacman.game;

import pacman.controllers.Controller;
import pacman.controllers.examples.RandomGhosts;
import pacman.entries.pacman.MyPacMan;

import java.util.EnumMap;
import java.util.Random;

import static pacman.game.Constants.DELAY;

public class DepthBund {

    public static void main(String args[]) {

        int numTrials = 50;
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
        System.out.println("Tempo totale 50 partite: " + tempo + " , Media tempo singola partita: " + tempo / (long) 50);

    }

    public static void myRunExperiment(Controller<Constants.MOVE> pacManController, Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController, int trials) {
        double avgScore = 0;
        double score = 0;
        double maxScore = 0;
        double minScore = 100000;

        Random rnd = new Random(0);
        Game game;


        for (int d = 1; d <= 20; d++) {
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
}
