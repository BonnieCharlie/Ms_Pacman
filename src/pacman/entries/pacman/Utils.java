package pacman.entries.pacman;

import pacman.game.Constants;
import pacman.game.Game;

import java.util.ArrayList;

public class Utils {

    public static double EvaluationFunction(Game game){

        if(game.getNumberOfActivePills()+game.getNumberOfActivePowerPills() == 0){
            return Double.POSITIVE_INFINITY;
        }else if(game.gameOver() || game.wasPacManEaten()){
            return Double.NEGATIVE_INFINITY;
        }

        int score = game.getScore();
        int posPacman = game.getPacmanCurrentNodeIndex();
        float distance = 10000;
        float oldDistance = distance;
        Constants.GHOST nearestghost;
        for (Constants.GHOST ghost: Constants.GHOST.values()) {
            if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0){
                distance = game.getShortestPathDistance(posPacman, game.getGhostCurrentNodeIndex(ghost));
                if(distance < oldDistance){
                    oldDistance = distance;
                    nearestghost = ghost;
                }
            }
        }

        int[] pills=game.getPillIndices();
        int[] powerPills=game.getPowerPillIndices();

        ArrayList<Integer> targets=new ArrayList<Integer>();

        for(int i=0;i<pills.length;i++)					//check which pills are available
            if(game.isPillStillAvailable(i))
                targets.add(pills[i]);

        for(int i=0;i<powerPills.length;i++)			//check with power pills are available
            if(game.isPowerPillStillAvailable(i))
                targets.add(powerPills[i]);

        int[] targetsArray=new int[targets.size()];		//convert from ArrayList to array

        for(int i=0;i<targetsArray.length;i++)
            targetsArray[i]=targets.get(i);

        float minDistanceToNextPill =  game.getManhattanDistance(posPacman, game.getClosestNodeIndexFromNodeIndex(posPacman,targetsArray, Constants.DM.MANHATTAN));
        double value= 1/minDistanceToNextPill - 1/oldDistance ;

        return value;
    }
}
