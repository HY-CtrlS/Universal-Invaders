package kr.ac.hanyang.engine;

/**
 * Implements an object that stores a single game's difficulty settings.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class GameSettings {

    /** 적 생성 주기 시간 */
    private int enemySpawnInterval;

    /**
     * Constructor.
     *
     * @param baseSpeed Speed of the enemies.
     */
    public GameSettings(final int baseSpeed, final int enemySpawnInterval) {
        this.enemySpawnInterval = enemySpawnInterval;
    }

    /**
     * @return the enemySpawnInterval
     */
    public final int getEnemySpawnInterval() {
        return enemySpawnInterval;
    }

}
