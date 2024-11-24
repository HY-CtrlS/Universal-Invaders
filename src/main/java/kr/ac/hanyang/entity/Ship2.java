package kr.ac.hanyang.entity;

import java.awt.Color;
import java.util.Set;
import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.screen.GameScreen;

public class Ship2 extends Ship {

    /** 적 프리징 시간 */
    private Cooldown freezingTime;

    // Blue
    public Ship2(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID, final int ultGauge) {
        super(positionX, positionY, direction, color, shipID, ultGauge);

        this.freezingTime = Core.getCooldown(5000);
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

    /**
     * 현재 모든 적 함선들을 얼리고 적 생성을 멈추는 궁극기 사용.
     */
    public final void useUlt() {
        ultGauge = 0;
        isUltActv = true;
        freezingTime.reset();
        while (!freezingTime.checkFinished()) {
            if (freezingTime.checkFinished()) {
                isUltActv = false;
                break;
            }
        }
    }
}
