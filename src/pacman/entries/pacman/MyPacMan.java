package pacman.entries.pacman;

import com.sun.webkit.Timer;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;

import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Ghost;

import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import static pacman.entries.pacman.Utils.legacyMin;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE> {
    private MOVE myMove = MOVE.NEUTRAL;
    private int depthResearch;
    private String enemyController;

    public MyPacMan(String enemyController) {
        super();
        this.enemyController = enemyController;
        //System.out.println(enemyController);
        if (enemyController.equals("Legacy")){
            this.depthResearch=12;
        }else{
            this.depthResearch=8;
        }
    }
    public MyPacMan(String enemyController, int depth) {
        super();
        this.enemyController = enemyController;
        //System.out.println(enemyController);
        this.depthResearch = depth;
    }

    public MOVE getMove(Game game, long timeDue) {

        long t = System.currentTimeMillis();
        MOVE bestMove = expectMinMax(game);
        //System.err.println("TIME EXEC " + (System.currentTimeMillis() - t));
        return bestMove;
    }

    private MOVE expectMinMax(Game game) {
        float bestScore = Float.MIN_VALUE;

        MOVE[] legalMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
        MOVE bestMOVE = MOVE.NEUTRAL;

        for (MOVE move : legalMoves) {
            Game g = game.copy();
            g.updatePacMan(move);
            float utility = getMin(g, depthResearch);
            if (utility > bestScore) {
                bestScore = utility;
                bestMOVE = move;
            }
        }
        return bestMOVE;
    }


    private float getMin(Game game, int depth) {

        if (depth == 0 || game.gameOver() || game.getNumberOfActivePills() + game.getNumberOfActivePowerPills() == 0) {
            //return (float) Utils.InfluenceFunction(game);
            //return (float) Utils.EvaluationFunction(game);
            return Utils.utilityFunction(game, enemyController);
        }

        //long s = System.currentTimeMillis();
        depth = depth - 1;
        Game gameMIN = null;
        float average = 0;

        ArrayList<EnumMap<GHOST, MOVE>> combination = null;
        if (enemyController.equals("RandomGhosts")) {
            ArrayList<MOVE[]> allLegalMoves = new ArrayList<MOVE[]>();
            for (GHOST ghost : GHOST.values()) {

                int positionGHOST = game.getGhostCurrentNodeIndex(ghost);
                //UPSTREAM HEURISTIC
                // THE REVERSE MOVE HAS A LOW PROBABILITY (0.0015) SO THE REVERSAL MOVE IS NOT CONSIDERED
                allLegalMoves.add(game.getPossibleMoves(positionGHOST, game.getGhostLastMoveMade(ghost)));
            }
            combination = getCombination(allLegalMoves);
            //System.out.println("COMBINATION SIZE ----------- " + combination.size());
        } else if (enemyController.equals("Legacy")) {
            combination = legacyMin(game);
        }

        for (EnumMap<GHOST, MOVE> ghostState : combination) {
            gameMIN = game.copy();
            gameMIN.updateGhosts(ghostState);
            average += getMax(gameMIN, depth) / combination.size();
        }

        return average;

    }

    private float getMax(Game game, int depth) {
        long s = System.currentTimeMillis();

        if (depth == 0 || game.gameOver() || game.getNumberOfActivePills() + game.getNumberOfActivePowerPills() == 0) {
            //return (float) Utils.InfluenceFunction(game);
            //return (float) Utils.EvaluationFunction(game);
            return Utils.utilityFunction(game, enemyController);
        }

        depth = depth - 1;

        float utility = Float.MIN_VALUE;
        float bestScore = utility;

        //DON'T CONSIDER THE OPPOSITE OF THE LAST MOVE MADE BECAUSE IT EXPANDS A SOLUTION ALREADY CALCULATED
        MOVE[] legalMoves= game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());

        for (MOVE move : legalMoves) {
            Game gameMAX = game.copy();
            gameMAX.updatePacMan(move);
            utility = getMin(gameMAX, depth);
            if (utility > bestScore) {
                bestScore = utility;
            }
        }

        return bestScore;

    }

    private ArrayList<EnumMap<GHOST, MOVE>> getCombination(ArrayList<MOVE[]> legalMoves) {
        //long time = System.currentTimeMillis();
        ArrayList<EnumMap<GHOST, MOVE>> combination = new ArrayList<EnumMap<GHOST, MOVE>>();
        EnumMap<GHOST, MOVE> moveGHOST = new EnumMap<GHOST, MOVE>(GHOST.class);

        //System.out.println(legalMoves.size());
        if (legalMoves.get(0).length == 0) {
            moveGHOST.put(GHOST.BLINKY, MOVE.NEUTRAL); // BLINKY move
            moveGHOST.put(GHOST.PINKY, MOVE.NEUTRAL); // PINKY move
            moveGHOST.put(GHOST.INKY, MOVE.NEUTRAL); // INKY move
            moveGHOST.put(GHOST.SUE, MOVE.NEUTRAL); // SUE move

            combination.add(moveGHOST.clone());

            return combination;
        } else {

            for (int i = 0; i < legalMoves.get(0).length; i++) {

                if (legalMoves.get(1).length != 0) {

                    for (int j = 0; j < legalMoves.get(1).length; j++) {

                        if (legalMoves.get(2).length != 0) {

                            for (int k = 0; k < legalMoves.get(2).length; k++) {

                                if (legalMoves.get(3).length != 0) {
                                    for (int t = 0; t < legalMoves.get(3).length; t++) {
                                        moveGHOST.put(GHOST.BLINKY, legalMoves.get(0)[i]); // BLINKY move
                                        moveGHOST.put(GHOST.PINKY, legalMoves.get(1)[j]); // PINKY move
                                        moveGHOST.put(GHOST.INKY, legalMoves.get(2)[k]); // INKY move
                                        moveGHOST.put(GHOST.SUE, legalMoves.get(3)[t]); // SUE move
                                        combination.add(moveGHOST.clone()); //Add combination
                                        moveGHOST.clear(); //Remove Elements
                                    }
                                } else {
                                    moveGHOST.put(GHOST.BLINKY, legalMoves.get(0)[i]); // BLINKY move
                                    moveGHOST.put(GHOST.PINKY, legalMoves.get(1)[j]); // PINKY move
                                    moveGHOST.put(GHOST.INKY, legalMoves.get(2)[k]); // INKY move
                                    combination.add(moveGHOST.clone()); //Add combination
                                    moveGHOST.clear(); //Remove Elements
                                }
                            }
                        } else {
                            moveGHOST.put(GHOST.BLINKY, legalMoves.get(0)[i]); // BLINKY move
                            moveGHOST.put(GHOST.PINKY, legalMoves.get(1)[j]); // PINKY move
                            combination.add(moveGHOST.clone()); //Add combination
                            moveGHOST.clear(); //Remove Elements
                        }
                    }
                } else {
                    moveGHOST.put(GHOST.BLINKY, legalMoves.get(0)[i]); // BLINKY move
                    combination.add(moveGHOST.clone());
                    moveGHOST.clear();
                }
            }
        }
        return combination;
    }
}
