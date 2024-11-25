package kr.ac.hanyang.entity;

import java.awt.Color;
import java.util.Set;
import kr.ac.hanyang.screen.GameScreen;

public class Ship1 extends Ship {

    // Green
    public Ship1(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID, final int ultGauge) {
        super(positionX, positionY, direction, color, shipID, ultGauge);

        ultThreshold = 200;
    }

    public final void useUlt() {
        super.useUlt();
    }

    /**
     * 현재 모든 적 함선들을 파괴, 토글형.
     */
    public final void useUlt(final GameScreen gameScreen, final Set<EnemyShip> enemies) {
        for (EnemyShip enemyShip : enemies) {
            enemyShip.destroy();
            gameScreen.increaseShipsDestroyed();
            gameScreen.createExp(enemyShip);
        }
    }
}
