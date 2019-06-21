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

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE> {
    private MOVE myMove = MOVE.NEUTRAL;
    private int depthResearch = 4;

    public MOVE getMove(Game game, long timeDue) {
        return expectMinMax(game);
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
        //System.out.println(numberRecursive);

    }

    private float getMin(Game game, int depth) {

        if (depth == 0 || game.gameOver() || game.getNumberOfActivePills() + game.getNumberOfActivePowerPills() == 0) {
            return (float) Utils.InfluenceFunction(game);
            //return (float) Utils.EvaluationFunction(game);
        }

        //long s = System.currentTimeMillis();
        depth = depth - 1;
        //float utility = Float.MAX_VALUE;
        //float minScore = utility;
        Game gameMIN = null;
        float average = 0;

        ArrayList<MOVE[]> allLegalMoves = new ArrayList<MOVE[]>();
        for (GHOST ghost : GHOST.values()) {
			/*if (game.getGhostLairTime(ghost)==0) {
				allLegalMoves.add(game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost)));
			}*/
            allLegalMoves.add(game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost)));
        }

		/*if (allLegalMoves.size()==0){
			return getMax(game, depth);
		}*/
/*		int index = 0;
		for (MOVE[] moves : allLegalMoves) {
			if (moves.length == 0){
				allLegalMoves.remove(index);
				System.out.println(allLegalMoves.size());
			}
			index++;
		}*/

        ArrayList<EnumMap<GHOST, MOVE>> combination = getCombination(allLegalMoves);
        //System.out.println(combination.size());
        for (EnumMap<GHOST, MOVE> ghostState : combination) {
            gameMIN = game.copy();
            gameMIN.updateGhosts(ghostState);
            average += getMax(gameMIN, depth) / combination.size();
        }
		/* //MIN only For ONE GHOST
		EnumMap<GHOST,MOVE> mappa = new EnumMap<GHOST, MOVE>(GHOST.class);
		MOVE[] possibleMOves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(GHOST.BLINKY));
		for(MOVE mossa : possibleMOves){
			mappa.put(GHOST.BLINKY, mossa);
			mappa.put(GHOST.SUE, MOVE.NEUTRAL);
			mappa.put(GHOST.PINKY, MOVE.NEUTRAL);
			mappa.put(GHOST.INKY, MOVE.NEUTRAL);

			gameMIN  = game.copy();
			gameMIN.updateGhosts(mappa);
			average+= getMax(gameMIN, depth)/possibleMOves.length;
		}
		*/

        return average;

    }

    private float getMax(Game game, int depth) {
        long s = System.currentTimeMillis();

        if (depth == 0 || game.gameOver() || game.getNumberOfActivePills() + game.getNumberOfActivePowerPills() == 0) {
            return (float) Utils.InfluenceFunction(game);
            //return (float) Utils.EvaluationFunction(game);
        }

        depth = depth - 1;

        float utility = Float.MIN_VALUE;
        float bestScore = utility;

        MOVE[] legalMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());

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
        } else{

            for (int i = 0; i < legalMoves.get(0).length; i++) {

                if(legalMoves.get(1).length != 0) {

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
                }else{
                    moveGHOST.put(GHOST.BLINKY, legalMoves.get(0)[i]); // BLINKY move
                    combination.add(moveGHOST.clone());
                    moveGHOST.clear();
                }
            }
        }
        return combination;
    }
}
