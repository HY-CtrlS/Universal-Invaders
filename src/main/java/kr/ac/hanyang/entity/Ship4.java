package kr.ac.hanyang.entity;

import java.awt.Color;
import java.util.Set;

public class Ship4 extends Ship {

    // Red
    public Ship4(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID, final int ultGauge) {
        super(positionX, positionY, direction, color, shipID, ultGauge);

        ultThreshold = 100;
    }

    /**
     * 관통 총알을 발사하는 메소드
     *
     * @param bullets List of bullets on screen, to add the new bullet.
     * @return Checks if the bullet was shot correctly.
     */
    public final boolean shoot(final Set<Bullet> bullets) {
        if (this.shootingCooldown.checkFinished()) {
            this.shootingCooldown.reset();

            // 관통 총알 발사
            Bullet bullet = BulletPool.getBullet(
                positionX + this.width / 2, positionY + this.height / 2,
                this.bulletSpeed, this.baseDamage, direction, getShipID());
            bullets.add(bullet);

            return true;
        }
        return false;
    }

    /**
     * 자석 (화면상 모든 경험치 끌어오기), 토글형.
     */
    public final void useUlt() {
        super.useUlt();
    }
}
