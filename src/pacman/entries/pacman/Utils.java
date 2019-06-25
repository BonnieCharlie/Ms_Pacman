package pacman.entries.pacman;

import org.omg.PortableInterceptor.INACTIVE;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.DM;
import pacman.game.Game;
import pacman.game.internal.Ghost;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static ArrayList<EnumMap<GHOST, MOVE>> legacyMin(Game g) {

        ArrayList<EnumMap<GHOST, MOVE>> listMoves = new ArrayList<EnumMap<GHOST, MOVE>>();
        EnumMap<GHOST, MOVE> ghostMove = new EnumMap<GHOST, MOVE>(GHOST.class);
        int targetNode = g.getPacmanCurrentNodeIndex();

        if (g.isGhostEdible(GHOST.BLINKY)) {
            ghostMove.put(GHOST.BLINKY, g.getApproximateNextMoveAwayFromTarget(g.getGhostCurrentNodeIndex(GHOST.BLINKY), targetNode, g.getGhostLastMoveMade(GHOST.BLINKY), DM.PATH));
        } else {
            ghostMove.put(GHOST.BLINKY, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(GHOST.BLINKY), targetNode, g.getGhostLastMoveMade(GHOST.BLINKY), DM.PATH));
        }
        if (g.isGhostEdible(GHOST.INKY)) {
            ghostMove.put(GHOST.INKY, g.getApproximateNextMoveAwayFromTarget(g.getGhostCurrentNodeIndex(GHOST.INKY), targetNode, g.getGhostLastMoveMade(GHOST.INKY), DM.MANHATTAN));
        } else {
            ghostMove.put(GHOST.INKY, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(GHOST.INKY), targetNode, g.getGhostLastMoveMade(GHOST.INKY), DM.MANHATTAN));
        }
        if (g.isGhostEdible(GHOST.PINKY)) {
            ghostMove.put(GHOST.PINKY, g.getApproximateNextMoveAwayFromTarget(g.getGhostCurrentNodeIndex(GHOST.PINKY), targetNode, g.getGhostLastMoveMade(GHOST.PINKY), DM.EUCLID));
        } else {
            ghostMove.put(GHOST.PINKY, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(GHOST.PINKY), targetNode, g.getGhostLastMoveMade(GHOST.PINKY), DM.EUCLID));
        }

        if (g.isGhostEdible(GHOST.SUE)) {
            ghostMove.put(GHOST.SUE, g.getGhostLastMoveMade(GHOST.SUE));
        } else {
            MOVE[] possibleMoves = g.getPossibleMoves(g.getGhostCurrentNodeIndex(GHOST.SUE));
            if (possibleMoves.length < 3) {
                possibleMoves = g.getPossibleMoves(g.getGhostCurrentNodeIndex(GHOST.SUE), g.getGhostLastMoveMade(GHOST.SUE));
            }
            for (MOVE move : possibleMoves) {
                ghostMove.put(GHOST.SUE, move);
                listMoves.add(ghostMove.clone());
            }
        }
        if (listMoves.size() == 0) {
            ghostMove.put(GHOST.SUE, MOVE.NEUTRAL);
            listMoves.add(ghostMove.clone());
        }

        ghostMove.clear();
        return listMoves;
    }

    /*
    ****************************** Evaluation function ********************************
    * It contains some rules to define a good behavior for PacMan movements.
    * Keep in input an enemy Controller because it is differente for RandomGhosts and Legacy.
    * Return a float value that represents move's utility.
    *
    */
    public static float evaluationFunction(Game game, String enemyController) {

        int posPacman = game.getPacmanCurrentNodeIndex();
        int[] powerPillIndices = game.getPowerPillIndices();
        int nearestPowerPillDistance = game.getShortestPathDistance(posPacman, game.getClosestNodeIndexFromNodeIndex(posPacman, powerPillIndices, DM.PATH));
        int nearestGhostDistance = Integer.MAX_VALUE;
        int nearestEdibleGhostDistance = Integer.MAX_VALUE;
        GHOST nearestEdibleGhost = GHOST.BLINKY;
        float utility = 0;
        int n_c = 0; // number of edible ghosts
        int n_d = game.getNumberOfActivePills();// number of uneated pills
        int n_totalPills = game.getNumberOfPills(); // Total number of pills;
        int n_d_signed = n_totalPills - n_d; // number of eated pills
        int nearestPillDistance = 0;

        float overallDistanceEdibleGhost = 0;
        float overallDistanceUnedibleGhost = 0;
        EnumMap<GHOST, Float> ghostsDistances = new EnumMap<GHOST, Float>(GHOST.class);
        ghostsDistances.put(GHOST.BLINKY, (float) 0);
        ghostsDistances.put(GHOST.INKY, (float) 0);
        ghostsDistances.put(GHOST.PINKY, (float) 0);
        ghostsDistances.put(GHOST.SUE, (float) 0);

        int nearestUnedibleGhostDistance = Integer.MAX_VALUE;

        for (GHOST ghostType : GHOST.values()) {
            // Save a map of ghosts distances and the minimum distance
            int dist = game.getShortestPathDistance(posPacman, game.getGhostCurrentNodeIndex(ghostType));
            if (dist == -1) {
                dist = 1000;
            }
            if (dist < nearestGhostDistance) {
                nearestGhostDistance = dist;
            }
            if (!game.isGhostEdible(ghostType) && game.getGhostLairTime(ghostType) == 0) {
                int ghostUnedibleDistance = game.getShortestPathDistance(posPacman, game.getGhostCurrentNodeIndex(ghostType));

                ghostsDistances.put(ghostType, 1 / (float) ghostUnedibleDistance);
                overallDistanceUnedibleGhost += 1 / (float) ghostUnedibleDistance;
                if (ghostUnedibleDistance < nearestUnedibleGhostDistance) {
                    nearestUnedibleGhostDistance = ghostUnedibleDistance;
                }
            } else if (game.isGhostEdible(ghostType)) {
                int posGhost = game.getGhostCurrentNodeIndex(ghostType);
                int distanceBetweenGhostAndPacman = game.getShortestPathDistance(posPacman, posGhost);
                overallDistanceEdibleGhost += (ghostType.initialLairTime / (float) 20) * (1 / (float) distanceBetweenGhostAndPacman);
                if (distanceBetweenGhostAndPacman < nearestEdibleGhostDistance) {
                    nearestEdibleGhostDistance = distanceBetweenGhostAndPacman;
                    nearestEdibleGhost = ghostType;
                }
                n_c++;
            }
        }

        //get all active pills
        int[] activePills = game.getActivePillsIndices();
        //get all active power pills
        int[] activePowerPills = game.getActivePowerPillsIndices();

        //create a target array that includes all ACTIVE pills and power pills
        int[] targetNodeIndices = new int[activePills.length + activePowerPills.length];
        for (int i = 0; i < activePills.length; i++)
            targetNodeIndices[i] = activePills[i];
        for (int i = 0; i < activePowerPills.length; i++)
            targetNodeIndices[activePills.length + i] = activePowerPills[i];

        try {
            if (targetNodeIndices.length == 0) {
                nearestPillDistance = 1;
            } else {
                nearestPillDistance = game.getShortestPathDistance(posPacman, game.getClosestNodeIndexFromNodeIndex(posPacman, targetNodeIndices, DM.PATH));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage() + "  targetNodeIndices: " + Arrays.toString(targetNodeIndices) + "    posPacman: " + posPacman);
        }
        int nearestGhostToTheNearestPowerPillDistance = game.getShortestPathDistance(nearestGhostDistance, game.getClosestNodeIndexFromNodeIndex(posPacman, powerPillIndices, DM.PATH));

        // *********************** RULES *************************

        if (enemyController.equals("RandomGhosts")) {

            // RULE 1: move to the nearest power pill
            if (game.getNumberOfActivePowerPills() > 1 && nearestGhostDistance <= 8 && nearestPowerPillDistance <= nearestGhostToTheNearestPowerPillDistance) {
                utility += 1 / (float) nearestPowerPillDistance + game.getScore();
            }

            // RULE 2: move to the nearest edible ghost if exists at least one edible ghost
            if (n_c >= 1 && nearestUnedibleGhostDistance > 20 && nearestEdibleGhostDistance < 20) {
                if ((game.getGhostEdibleTime(nearestEdibleGhost) > 2)) {
                    utility = utility + n_c * (overallDistanceEdibleGhost) + game.getScore();
                }
            }

            // RULE 3: move to the nearest pill
            if (nearestUnedibleGhostDistance >= 20) {
                utility = utility + (1 / (float) nearestPillDistance) + n_d_signed + game.getScore();
            }

            // RULE 4: move away from ghosts
            if (nearestUnedibleGhostDistance <= 10) {
                utility -= 1000;
            }

            // RULE 5: considering overall distance from ghosts
            if (nearestUnedibleGhostDistance < 20) {
                utility -= overallDistanceUnedibleGhost;
            }

        } else { // Legacy controller

            // LOST CONDITION
            if (game.gameOver() || game.wasPacManEaten()) {
                utility -= 1000;
            }

            // RULE 1: move to the nearest power pill
            if (game.getNumberOfActivePowerPills() >= 1 && (1 / ghostsDistances.get(GHOST.BLINKY) <= 10 && 1 / ghostsDistances.get(GHOST.PINKY) <= 10 && 1 / ghostsDistances.get(GHOST.INKY) <= 10)) {
                utility += 1 / (float) nearestPowerPillDistance + game.getScore();
            }

            // RULE 2: move to the nearest edible ghost if exists at least one edible ghost
            if (n_c >= 1 && nearestUnedibleGhostDistance > 20 && nearestEdibleGhostDistance < 20) {
                if ((game.getGhostEdibleTime(nearestEdibleGhost) > 4)) {
                    utility = utility + 4 * overallDistanceEdibleGhost + game.getScore();
                }
            }

            // RULE 3: move to the nearest pill
            utility = utility + (1 / (float) nearestPillDistance) + n_d_signed + game.getScore();

            // RULE 4: move away from all ghosts
            if (!game.isGhostEdible(GHOST.BLINKY) && 1 / ghostsDistances.get(GHOST.BLINKY) < 20) {
                utility -= 1000;
            }
            if (!game.isGhostEdible(GHOST.PINKY) && 1 / ghostsDistances.get(GHOST.PINKY) < 20) {
                utility -= 1000;
            }
            if (!game.isGhostEdible(GHOST.INKY) && 1 / ghostsDistances.get(GHOST.INKY) < 20) {
                utility -= 1000;
            }
            if (!game.isGhostEdible(GHOST.SUE) && 1 / ghostsDistances.get(GHOST.SUE) <= 10) {
                utility -= 500;
            }

            // RULE 5: considering overall distance from ghosts
            if (nearestUnedibleGhostDistance < 20) {
                utility = utility - (float) (2 * ghostsDistances.get(GHOST.BLINKY) + ghostsDistances.get(GHOST.PINKY) + 0.5 * ghostsDistances.get(GHOST.INKY) + 0.25 * ghostsDistances.get(GHOST.SUE));
            }

        }

        //System.out.println("nearestEdibleGhostDistance: " + nearestEdibleGhostDistance + ", nearestGhostDistance: " + nearestGhostDistance + ", utility = " + utility);
        return utility;
    }
}
