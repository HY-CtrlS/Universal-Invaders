package kr.ac.hanyang.entity.ship;

import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import java.awt.Color;
import java.util.Set;
import kr.ac.hanyang.entity.Bullet;
import kr.ac.hanyang.entity.BulletPool;

public class Ship3 extends Ship {

    /** 점사 간 딜레이를 위한 쿨다운. */
    private Cooldown burstCooldown;
    /** 점사 중 현재 발사 상태. */
    private int burstShotCount;
    /** 총 점사 횟수 (삼점사). */
    private static final int maxBurstShots = 3;

    // Yellow
    public Ship3(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID) {
        super(positionX, positionY, direction, color, shipID);
        this.burstCooldown = Core.getCooldown(100);
        this.burstShotCount = 0;

        ultThreshold = 150;
    }

    /**
     * Shoots a bullet upwards.
     *
     * @param bullets List of bullets on screen, to add the new bullet.
     */
    public final void shoot(final Set<Bullet> bullets) {
        if (this.burstShotCount == 0 && this.shootingCooldown.checkFinished()) {
            // 첫 번째 발사
            this.burstCooldown.reset();    // 점사 내 간격 초기화
            this.burstShotCount++;
            shootBullet(bullets); // 첫 번째 총알 발사
            Core.getSoundManager().playBasicAttack();
        } else if (this.burstShotCount > 0 && this.burstShotCount < maxBurstShots
            && this.burstCooldown.checkFinished()) {
            // 점사 진행
            this.burstCooldown.reset(); // 점사 내 간격 초기화
            shootBullet(bullets); // 다음 총알 발사
            this.burstShotCount++;

            if (this.burstShotCount >= maxBurstShots) {
                this.burstShotCount = 0; // 점사 완료
                this.isBurstShooting = false; // 점사 상태 종료
                this.shootingCooldown.reset(); // 점사 시작 간격 초기화
            }
            Core.getSoundManager().playBasicAttack();
        }
    }

    private void shootBullet(Set<Bullet> bullets) {
        Bullet bullet = BulletPool.getBullet(
            this.positionX + this.width / 2, this.positionY + this.height / 2, this.bulletSpeed,
            this.baseDamage, this.range, this.direction, getShipID());
        bullets.add(bullet);
    }

    public void startBurstShooting() {
        if (!isBurstShooting) {
            isBurstShooting = true;
            this.burstShotCount = 0; // 점사 초기화
        }
    }

    /**
     * 일반, 보스 스테이지 - 짧은 시간 동안 무적.
     */
    public final void useUlt() {
        super.useUlt();
    }
}
