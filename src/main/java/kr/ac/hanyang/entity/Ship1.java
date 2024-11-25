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

    /**
     * 현재 모든 적 함선들을 파괴, 토글형.
     */
    public final void useUlt() {
        super.useUlt();
    }
}
