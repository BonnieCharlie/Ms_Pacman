package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.EnumMap;

import static pacman.entries.pacman.Utils.legacyMin;

public class LegacyPacMan extends Controller<MOVE>  {

    private MOVE myMove=MOVE.NEUTRAL;
    private int depthResearch = 10;

    public MOVE getMove(Game game, long timeDue)
    {
        return expectMinMax(game);
    }

    private MOVE expectMinMax (Game game){

        float bestScore = Float.MIN_VALUE;

        MOVE[] legalMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
        MOVE bestMOVE = MOVE.NEUTRAL;
        for (MOVE move : legalMoves){
            Game g = game.copy();
            g.updatePacMan(move);
            float utility = getMin(g, depthResearch);
            if (utility>bestScore){
                bestScore = utility;
                bestMOVE = move;
            }
        }

        return bestMOVE;
        //System.out.println(numberRecursive);

    }

    private float getMin(Game game, int depth ) {

        if(depth == 0 || game.gameOver() || game.getNumberOfActivePills()+ game.getNumberOfActivePowerPills() ==0){
            return (float) Utils.InfluenceFunction(game);
            //return (float) Utils.EvaluationFunction(game);
        }

        //long s = System.currentTimeMillis();
        depth=depth-1;
        //float utility = Float.MAX_VALUE;
        //float minScore = utility;
        Game gameMIN = null;
        float average = 0;

        ArrayList<EnumMap<GHOST,MOVE>> listMoves = legacyMin(game);
        for(int i=0; i<listMoves.size(); i++){
            gameMIN = game.copy();
            gameMIN.updateGhosts(listMoves.get(i));
            average+= getMax(gameMIN, depth);
            //utility += utility/listMoves.size();
        }
        average = average/listMoves.size();
        return average;

    }

    private float getMax(Game game, int depth) {
        long s = System.currentTimeMillis();

        if(depth == 0 || game.gameOver() || game.getNumberOfActivePills()+ game.getNumberOfActivePowerPills() ==0){
            return (float) Utils.InfluenceFunction(game);
            //return (float) Utils.EvaluationFunction(game);
        }

        depth=depth-1;

        float utility = Float.MIN_VALUE;
        float bestScore = utility;

        MOVE[] legalMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());

        for (MOVE move : legalMoves){
            Game gameMAX = game.copy();
            gameMAX.updatePacMan(move);
            utility = getMin(gameMAX, depth);
            if (utility>bestScore){
                bestScore = utility;
            }
        }

        return bestScore;

    }

    private ArrayList<EnumMap<GHOST,MOVE>> getCombination(ArrayList<MOVE[]> legalMoves){
        //long time = System.currentTimeMillis();
        ArrayList<EnumMap<GHOST,MOVE>> combination  = new ArrayList<EnumMap<GHOST, MOVE>>();
        EnumMap<GHOST,MOVE>  moveGHOST = new EnumMap<GHOST, MOVE>(GHOST.class);

        for (int i = 0; i<legalMoves.get(0).length ; i++){
            for(int j=0; j<legalMoves.get(1).length; j++){
                for(int k = 0; k<legalMoves.get(2).length; k++){
                    for(int t=0; t<legalMoves.get(3).length; t++){

                        moveGHOST.put(GHOST.values()[0], legalMoves.get(0)[i]); // BLINKY move
                        moveGHOST.put(GHOST.values()[1], legalMoves.get(1)[j]); // PINKY move
                        moveGHOST.put(GHOST.values()[2], legalMoves.get(2)[k]); // INKY move
                        moveGHOST.put(GHOST.values()[3], legalMoves.get(3)[t]); // SUE move

                        combination.add(moveGHOST.clone()); //Add combination

                        moveGHOST.clear(); //Remove Elements

                    }
                }
            }
        }
        //System.out.println("COMBINATION TIME " + (System.currentTimeMillis()-time));
        return combination;
    }
}
