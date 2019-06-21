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
    private int depthResearch = 5;

    public MOVE getMove(Game game, long timeDue)
    {
        float bestScore = Float.MIN_VALUE;

        MOVE[] legalMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());

        for (MOVE move : legalMoves){
            Game g = game.copy();
            g.updatePacMan(move);
            float utility = expectMinMax(g, 1, depthResearch);
            //System.out.println(utility);
            if (utility>bestScore){
                bestScore = utility;
                myMove = move;
            }
        }
        return myMove;
    }

    private float expectMinMax (Game g, int typeNode, int depth){
        if(depth == 0 || g.gameOver() || g.getNumberOfActivePills()+ g.getNumberOfActivePowerPills() ==0){
            //return (float)Utils.InfluenceFunction(g);
            long t1 = System.currentTimeMillis();
            float eval = (float) Utils.eval(g);

            //System.out.println("Tempo Eval Function "+ (System.currentTimeMillis()-t1));
            return eval;
        }
        if (typeNode ==0){
            return getMax(g, typeNode, depth);
        }else{
            return getMin(g, typeNode, depth);
        }

    }

    private float getMin(Game g, int typeNode, int depth) {
        float utility = Float.MAX_VALUE;
        Game gameMIN = null;
        depth = depth-1;
        ArrayList<EnumMap<GHOST,MOVE>> listMoves = legacyMin(g);
        for(int i=0; i<listMoves.size(); i++){
            gameMIN = g.copy();
            gameMIN.updateGhosts(listMoves.get(i));
            utility = expectMinMax(gameMIN, 0, depth);
            //utility += utility/listMoves.size();
        }
        utility = utility/listMoves.size();
        return utility;

    }

    private float getMax(Game g, int typeNode, int depth) {
        float utility = Float.MIN_VALUE;
        float bestScore = utility;
        MOVE[] legalMoves = g.getPossibleMoves(g.getPacmanCurrentNodeIndex());
        Game gameMAX = null;
        for (MOVE move : legalMoves){
            gameMAX = g.copy();
            gameMAX.updatePacMan(move);
            utility = expectMinMax(gameMAX, 1, depth);
            if (utility>bestScore){
                bestScore = utility;
            }
        }
        return bestScore;

    }
}
