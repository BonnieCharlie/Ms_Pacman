package pacman.game;

import pacman.controllers.Controller;
import pacman.controllers.examples.Legacy;
import pacman.controllers.examples.RandomGhosts;
import pacman.entries.pacman.MyPacMan;
import pacman.game.internal.PacMan;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

import static pacman.game.Constants.DELAY;

public class DepthBound {


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
        myRunExperiment(Legacy.class.getSimpleName(), new Legacy(), numTrials);
        long tempo = System.currentTimeMillis() - start;
        System.out.println("Tempo totale 50 partite: " + tempo + " , Media tempo singola partita: " + tempo / (long) 50);

    }

    public static void myRunExperiment(String enemyController, Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController, int trials) {

        Random rnd = new Random(0);
        Game game;


        for (int d = 1; d <= 100; d++) {
            double avgScore = 0;
            double score = 0;
            double maxScore = 0;
            double minScore = 10000000;
            Controller<Constants.MOVE> pacmanController = new MyPacMan(enemyController, d);
            System.out.println("Pacman Controller with depth  " + d);
            try (PrintWriter pr = new PrintWriter("DepthStats\\Depth"+d+".txt")) {
                pr.println("Depth " + d + ":");
                for (int i = 0; i < trials; i++) {
                    long t = System.currentTimeMillis();
                    game = new Game(rnd.nextLong());

                    while (!game.gameOver()) {
                        game.advanceGame(pacmanController.getMove(game.copy(), System.currentTimeMillis() + DELAY),
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
                    //System.out.println("Tempo singola partita : " + (System.currentTimeMillis()- t));
                    pr.print(score);
                    pr.println();
                }
                //pr.print("AvgScore: " + avgScore / trials + "\t" + "MaxScore: " + maxScore + "\t" + "MinScore: " + minScore);

            } catch (Exception e) {
                System.out.println("Something was wrong");
                e.printStackTrace();
            }
            //System.out.println("AvgScore: " + avgScore / trials + "\t" + "MaxScore: " + maxScore + "\t" + "MinScore: " + minScore);

        }


    }
}
