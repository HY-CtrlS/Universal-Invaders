package kr.ac.hanyang.engine;

import kr.ac.hanyang.engine.StatusManager;
import kr.ac.hanyang.entity.Ship;

/**
 * Implements an object that stores the state of the game between levels.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class GameState {

    /** Lives currently remaining. */
    private int hp;
    /** Bullets shot until now. */
    private int bulletsShot;
    /** Ships destroyed until now. */
    private int shipsDestroyed;
    /** Total seconds of survival. */
    private int survivalTime;

    /**
     * Constructor.
     *
     * @param hp             Lives currently remaining.
     * @param bulletsShot    Bullets shot until now.
     * @param shipsDestroyed Ships destroyed until now.
     * @param survivalTime   Total seconds of survival.
     */
    public GameState(
        final int hp, final int bulletsShot,
        final int shipsDestroyed, final int survivalTime) {
        this.hp = hp;
        this.bulletsShot = bulletsShot;
        this.shipsDestroyed = shipsDestroyed;
        this.survivalTime = survivalTime;
    }

    /**
     * @return the livesRemaining
     */
    public final int getHp() {
        return hp;
    }

    /**
     * 체력 설정
     *
     * @param hp 설정할 체력
     */
    public void setHP(final int hp) {
        this.hp = hp;
    }

    /**
     * @return the bulletsShot
     */
    public final int getBulletsShot() {
        return bulletsShot;
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
}
