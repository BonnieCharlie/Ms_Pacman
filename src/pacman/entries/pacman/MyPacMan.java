package pacman.entries.pacman;

import com.sun.webkit.Timer;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;

import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Ghost;

import java.sql.Time;
import java.util.EnumMap;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;
	private int depthResearch = 2;
	
	public MOVE getMove(Game game, long timeDue) 
	{
		Long start = System.currentTimeMillis();

		float bestScore = Float.MIN_VALUE;

		MOVE[] legalMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());

		for (MOVE move : legalMoves){
			Game g = game.copy();
			g.updatePacMan(move);
			float utility = expectMinMax(g, 1, depthResearch);
			if (utility>bestScore){
				bestScore = utility;
				myMove = move;
			}
		}
		if(System.currentTimeMillis()-start >40 ){
			System.out.println("TIME EXEC TO MOVE "+ (System.currentTimeMillis()-start));
		}
		return myMove;
	}

	private float expectMinMax (Game g, int typeNode, int depth){
		if(depth == 0 || g.gameOver() || g.getNumberOfActivePills()+ g.getNumberOfActivePowerPills() ==0){
			//return (float)Utils.InfluenceFunction(g);
			long t1 = System.currentTimeMillis();
			float eval = (float) Utils.InfluenceFunction(g);

			System.out.println("Tempo Eval Function "+ (System.currentTimeMillis()-t1));
			return eval;
		}
		if (typeNode ==0){
			return getMax(g, typeNode, depth);
		}else{
			return getMin(g, typeNode, depth);
		}

	}

	private float getMin(Game g, int typeNode, int depth) {
		Long s = System.currentTimeMillis();

		float utility = Float.MAX_VALUE;
		float minScore = utility;
		Game gameMIN = null;
		float average = 0;

		for(GHOST ghostType : GHOST.values()) {
			MOVE[] legalMoves = g.getPossibleMoves(g.getGhostCurrentNodeIndex(ghostType));
			for (MOVE move : legalMoves) {
				gameMIN = g.copy();
				EnumMap<GHOST,MOVE> ghostMove= new EnumMap<GHOST, MOVE>(GHOST.class);
				ghostMove.put(ghostType,move);
				gameMIN.updateGhosts(ghostMove);
				utility = expectMinMax(gameMIN, 0, depth);
				if (utility < minScore) {
					minScore = utility;
				}
			}
			average += minScore/GHOST.values().length;
		}
		if(System.currentTimeMillis()-s >40 ){
			System.out.println("TIME EXEC MIN "+ (System.currentTimeMillis()-s));
		}

		return average;

	}

	private float getMax(Game g, int typeNode, int depth) {
		Long s = System.currentTimeMillis();
		depth=depth-1;

		float utility = Float.MIN_VALUE;
		float bestScore = utility;

		MOVE[] legalMoves = g.getPossibleMoves(g.getPacmanCurrentNodeIndex());

		for (MOVE move : legalMoves){
			Game gameMAX = g.copy();
			gameMAX.updatePacMan(move);
			utility = expectMinMax(gameMAX, 1, depth);
			if (utility>bestScore){
				bestScore = utility;
			}
		}
		if(System.currentTimeMillis()-s >40 ){
			System.out.println("TIME EXEC MAX "+ (System.currentTimeMillis()-s));
		}
		return bestScore;

	}
}
