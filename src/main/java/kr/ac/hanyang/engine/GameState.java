package kr.ac.hanyang.engine;

/**
 * Implements an object that stores the state of the game between levels.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class GameState {

    /** Current game level. */
    private int level;
    /** Lives currently remaining. */
    private int hp;
    /** Bullets shot until now. */
    private int bulletsShot;
    /** Ships destroyed until now. */
    private int shipsDestroyed;
    /** Total seconds of survival. */
    private int survivalTime;
    /** 현재 궁극기 게이지 */
    private int ultGauge;

    /**
     * Constructor.
     *
     * @param level          Current game level.
     * @param hp             Lives currently remaining.
     * @param bulletsShot    Bullets shot until now.
     * @param shipsDestroyed Ships destroyed until now.
     * @param survivalTime   Total seconds of survival.
     * @param ultGauge       현재 궁극기 게이지.
     */
    public GameState(final int level,
        final int hp, final int bulletsShot,
        final int shipsDestroyed, final int survivalTime, final int ultGauge) {
        this.level = level;
        this.hp = hp;
        this.bulletsShot = bulletsShot;
        this.shipsDestroyed = shipsDestroyed;
        this.survivalTime = survivalTime;
        this.ultGauge = ultGauge;
    }

    /**
     * @return the level
     */
    public final int getLevel() {
        return level;
    }

    /**
     * @return the livesRemaining
     */
    public final int getHp() {
        return hp;
    }

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

    /**
     * @return 현재 궁극기 게이지
     */
    public final int getUltGauge() {
        return ultGauge;
    }
}
