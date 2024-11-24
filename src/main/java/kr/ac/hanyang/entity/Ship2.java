package kr.ac.hanyang.entity;

import java.awt.Color;
import java.util.Set;

public class Ship2 extends Ship {

    public Ship2(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID) {
        super(positionX, positionY, direction, color, shipID);
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
                positionY + this.height / 2, this.bulletSpeed, this.baseDamage,
                Direction.getOffsetDirection(direction, -1), getShipID()));
            bullets.add(BulletPool.getBullet(positionX + this.width / 2,
                positionY + this.height / 2, this.bulletSpeed, this.baseDamage, direction,
                getShipID()));
            bullets.add(BulletPool.getBullet(positionX + this.width / 2,
                positionY + this.height / 2, this.bulletSpeed, this.baseDamage,
                Direction.getOffsetDirection(direction, 1), getShipID()));
            return true;
        }
        return false;
    }
}
