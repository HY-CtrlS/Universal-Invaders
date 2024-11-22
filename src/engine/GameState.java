package engine;

/**
 * Implements an object that stores the state of the game between levels.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class GameState {

    /** Current game level. */
    private int level;
    /** Current score. */
    private int score;
    /** Lives currently remaining. */
    private int hp;
    /** Bullets shot until now. */
    private int bulletsShot;
    /** Ships destroyed until now. */
    private int shipsDestroyed;

    /**
     * Constructor.
     *
     * @param level          Current game level.
     * @param score          Current score.
     * @param hp Lives currently remaining.
     * @param bulletsShot    Bullets shot until now.
     * @param shipsDestroyed Ships destroyed until now.
     */
    public GameState(final int level, final int score,
        final int hp, final int bulletsShot,
        final int shipsDestroyed) {
        this.level = level;
        this.score = score;
        this.hp = hp;
        this.bulletsShot = bulletsShot;
        this.shipsDestroyed = shipsDestroyed;
    }

    /**
     * @return the level
     */
    public final int getLevel() {
        return level;
    }

    /**
     * @return the score
     */
    public final int getScore() {
        return score;
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

}
