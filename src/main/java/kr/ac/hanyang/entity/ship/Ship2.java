package kr.ac.hanyang.entity.ship;

import java.awt.Color;
import java.util.Set;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.entity.Bullet;
import kr.ac.hanyang.entity.BulletPool;

public class Ship2 extends Ship {

    // Blue
    public Ship2(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID) {
        super(positionX, positionY, direction, color, shipID);

        ultThreshold = 150;
    }

    /**
     * 총알을 산탄으로 발사하는 메소드
     *
     * @param bullets List of bullets on screen, to add the new bullet.
     * @return Checks if the bullet was shot correctly.
     */
    public final boolean shoot(final Set<Bullet> bullets) {
        if (this.shootingCooldown.checkFinished()) {
            this.shootingCooldown.reset();
            bullets.add(BulletPool.getBullet(positionX + this.width / 2,
                positionY + this.height / 2, this.bulletSpeed, this.baseDamage, this.range,
                Direction.getOffsetDirection(direction, -1), getShipID()));
            bullets.add(BulletPool.getBullet(positionX + this.width / 2,
                positionY + this.height / 2, this.bulletSpeed, this.baseDamage, this.range,
                direction,
                getShipID()));
            bullets.add(BulletPool.getBullet(positionX + this.width / 2,
                positionY + this.height / 2, this.bulletSpeed, this.baseDamage, this.range,
                Direction.getOffsetDirection(direction, 1), getShipID()));
            Core.getSoundManager().playBasicAttack();
            return true;
        }
        return false;
    }

    /**
     * 현재 모든 적 함선 프리징 + 적 생성X, 토글형.
     */
    public final void useUlt() {
        super.useUlt();
    }
}
