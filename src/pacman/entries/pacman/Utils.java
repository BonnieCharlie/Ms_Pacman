package pacman.entries.pacman;

import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

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
        float p_dots = 1;
        float p_run = 1;
        float p_chase = 20;
        int n_d = game.getNumberOfActivePills()+game.getNumberOfActivePowerPills(); // number of uneated pills
        int n_totalPills = game.getNumberOfPills()+game.getNumberOfPowerPills();
        int n_d_signed = n_totalPills - n_d; // number of eated pills

        int n_r = 0; // number of unedible ghosts
        int n_c = 0; // number of edible ghosts

        int[] ghostUnedibleIndices = new int[4];
        int[] ghostEdibleIndices = new int[4];
        for (GHOST ghostType: GHOST.values()){
            if (!game.isGhostEdible(ghostType) && game.getGhostLairTime(ghostType)==0) { //
                ghostUnedibleIndices[n_r] = game.getGhostCurrentNodeIndex(ghostType);
                n_r++;
            }else if(game.isGhostEdible(ghostType)){
                ghostEdibleIndices[n_c] = game.getGhostCurrentNodeIndex(ghostType);
                n_c++;
            }
        }


        //p_dots = p_dots/n_totalPills;

        float pill_influence = 0;
        float unedibleGhost_influence = 0;
        float edibleGhost_influence = 0;

        int[] pillIndices = game.getPillIndices();
        int[] powerPillIndices = game.getPowerPillIndices();

        ArrayList<Integer> targets=new ArrayList<Integer>(Arrays.stream(pillIndices).boxed().collect(Collectors.toList()));
        targets.addAll(Arrays.stream(powerPillIndices).boxed().collect(Collectors.toList()));

        //System.out.println(targets.size());

        for(int k=0; k<n_d; k++){
            double den = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), targets.get(k));

            if (den == 0) // correzione se +inf
                den = 1;


            pill_influence += (float)p_dots*n_d_signed/den;
            //System.out.println("pill_influence " + k + ": " + pill_influence);
        }
        //System.out.println("pill_influence totale" + pill_influence);


        for(int k=0; k<n_r; k++) {
            //System.out.println(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), ghostUnedibleIndices[k]));
            unedibleGhost_influence += p_run * n_d / game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), ghostUnedibleIndices[k]);
        }

        for(int k=0; k<n_c; k++) {
            //System.out.println(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), ghostEdibleIndices[k]));
            edibleGhost_influence += p_chase / game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), ghostEdibleIndices[k]);
        }

        influence = pill_influence  - unedibleGhost_influence + edibleGhost_influence;
        //System.out.println("Influence: " + influence + ", " + pill_influence +", " + unedibleGhost_influence+", "+  edibleGhost_influence );
        return influence;
    }
}
