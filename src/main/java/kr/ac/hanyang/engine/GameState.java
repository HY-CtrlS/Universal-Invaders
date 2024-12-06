package kr.ac.hanyang.engine;

import kr.ac.hanyang.entity.ship.Ship;

/**
 * Implements an object that stores the state of the game between levels.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class GameState {

    /** Lives currently remaining. */
    private int hp;
    /** Ships destroyed until now. */
    private int shipsDestroyed;
    /** Total seconds of survival. */
    private int survivalTime;
    // 함선의 Status
    private StatusManager status;
    // 아군함선
    private Ship ship;

    /**
     * Constructor.
     *
     * @param hp             Lives currently remaining.
     * @param shipsDestroyed Ships destroyed until now.
     * @param survivalTime   Total seconds of survival.
     * @param status         Current status.
     * @param ship           Player Ship.
     */
    public GameState(
        final int hp, final int shipsDestroyed, final int survivalTime, final StatusManager status,
        final Ship ship) {
        this.hp = hp;
        this.shipsDestroyed = shipsDestroyed;
        this.survivalTime = survivalTime;
        this.status = status;
        this.ship = ship;
    }

    /**
     * @return the livesRemaining
     */
    public final int getHp() {
        return hp;
    }

    /**
     * @return the shipsDestroyed
     */
    public final int getShipsDestroyed() {
        return shipsDestroyed;
    }

    /**
     * @return the survivalTime
     */
    public final int getSurvivalTime() {
        return survivalTime;
    }

    /**
     * @return the status
     */
    public final StatusManager getStatus() {
        return status;
    }

    /**
     * @return the ship
     */
    public final Ship getShip() {
        return ship;
    }
}
