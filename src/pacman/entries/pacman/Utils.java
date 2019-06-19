package pacman.entries.pacman;

import pacman.game.Constants;
import pacman.game.Constants.GHOST;
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
        nearestghost = null;
        for (Constants.GHOST ghost: Constants.GHOST.values()) {
            if(game.getGhostLairTime(ghost)==0){
                distance = game.getShortestPathDistance(posPacman, game.getGhostCurrentNodeIndex(ghost));
                //distance = game.getManhattanDistance(posPacman,game.getGhostCurrentNodeIndex(ghost));
                //distance = (float) game.getEuclideanDistance(posPacman, game.getGhostCurrentNodeIndex(ghost));
                if(distance < oldDistance){
                    oldDistance = distance;
                    nearestghost = ghost;
                }
            }
        }
        /*
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

        float minDistanceToNextPill =  game.getShortestPathDistance(posPacman, game.getClosestNodeIndexFromNodeIndex(posPacman,targetsArray, Constants.DM.MANHATTAN));
        */
        return oldDistance;
    }

    public static double InfluenceFunction(Game game){
        float influence = 0;
        float p_dots = 1/(float)20;
        float p_run = 1;
        float p_chase = 1;
        int n_d = game.getNumberOfActivePills()+game.getNumberOfActivePowerPills(); // number of uneated pills
        int n_totalPills = game.getNumberOfPills()+game.getNumberOfPowerPills();
        int n_d_signed = n_totalPills - n_d; // number of eated pills
        int n_r = 0; // number of unedible ghosts
        for (GHOST ghostType: GHOST.values()){
            if (game.getGhostEdibleTime(ghostType)==0)
                n_r++;
        }
        int n_c = GHOST.values().length - n_r; // number of edible ghosts
        float pill_influence = 0;
        float unedibleGhost_influence = 0;
        float edibleGhost_influence = 0;

        for(int k=0; k<n_d; k++){
            float den = game.getManhattanDistance(game.getPacmanCurrentNodeIndex(), k);
            if (den == 0) // correzione se +inf
                den = 1;
            pill_influence += (float)p_dots*n_d_signed/den;
            //System.out.println("pill_influence " + k + ": " + pill_influence);
        }
        //System.out.println("pill_influence totale" + pill_influence);

        for(int k=0; k<n_r; k++)
            unedibleGhost_influence += (float)p_run*n_d/game.getManhattanDistance(game.getPacmanCurrentNodeIndex(), k);

        for(int k=0; k<n_c; k++)
            edibleGhost_influence += (float)p_chase/game.getManhattanDistance(game.getPacmanCurrentNodeIndex(), k);

        influence = pill_influence + edibleGhost_influence - unedibleGhost_influence;
        System.out.println("Influence: " + influence + ", " + pill_influence +", "+  edibleGhost_influence +", " + unedibleGhost_influence);
        return influence;
    }
}
