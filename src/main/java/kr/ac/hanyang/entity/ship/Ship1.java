package kr.ac.hanyang.entity.ship;

import java.awt.Color;

public class Ship1 extends Ship {

    // Green
    public Ship1(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID) {
        super(positionX, positionY, direction, color, shipID);

        ultThreshold = 150;
    }

    /**
     * 일반 스테이지 - 현재 모든 적 함선들을 파괴.
     * <p>
     * 보스 스테이지 - 현재 보스의 모든 총알과 미사일 파괴, 크리스탈 파괴, 보스에게 데미지.
     */
    public final void useUlt() {
        super.useUlt();
    }
}
