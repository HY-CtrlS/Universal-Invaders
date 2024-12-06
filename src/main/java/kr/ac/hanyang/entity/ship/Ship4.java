package kr.ac.hanyang.entity.ship;

import java.awt.Color;
import java.util.Set;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.entity.Bullet;
import kr.ac.hanyang.entity.BulletPool;

public class Ship4 extends Ship {

    // Red
    public Ship4(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID) {
        super(positionX, positionY, direction, color, shipID);

        ultThreshold = 100;
    }

    /**
     * 관통 총알을 발사하는 메소드
     *
     * @param bullets List of bullets on screen, to add the new bullet.
     */
    public final void shoot(final Set<Bullet> bullets) {
        if (this.shootingCooldown.checkFinished()) {
            this.shootingCooldown.reset();

            // 관통 총알 발사
            Bullet bullet = BulletPool.getBullet(
                positionX + this.width / 2, positionY + this.height / 2,
                this.bulletSpeed, this.baseDamage, this.range, direction, getShipID());
            bullets.add(bullet);
            Core.getSoundManager().playBasicAttack();
        }
    }

    /**
     * 일반 스테이지 - 경험치 자석.
     * <p>
     * 보스 스테이지 - 체력 회복.
     */
    public final void useUlt() {
        super.useUlt();
    }
}
