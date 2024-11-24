package kr.ac.hanyang.entity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import java.awt.Color;
import java.util.Set;

public class Ship3 extends Ship {

    /** 점사 간 딜레이를 위한 쿨다운. */
    private Cooldown burstCooldown;
    /** 점사 중 현재 발사 상태. */
    private int burstShotCount;
    /** 총 점사 횟수 (삼점사). */
    private static final int maxBurstShots = 3;

    // Yellow
    public Ship3(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID, final int ultGauge) {
        super(positionX, positionY, direction, color, shipID, ultGauge);
        this.burstCooldown = Core.getCooldown(100);
        this.burstShotCount = 0;
    }

    /**
     * Shoots a bullet upwards.
     *
     * @param bullets List of bullets on screen, to add the new bullet.
     * @return Checks if the bullet was shot correctly.
     */
    public final boolean shoot(final Set<Bullet> bullets) {
        if (this.burstShotCount == 0 && this.shootingCooldown.checkFinished()) {
            // 첫 번째 발사
            this.burstCooldown.reset();    // 점사 내 간격 초기화
            this.burstShotCount++;
            shootBullet(bullets); // 첫 번째 총알 발사
            return true;

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
            return true;
        }

        return false;
    }

    private void shootBullet(Set<Bullet> bullets) {
        Bullet bullet = BulletPool.getBullet(
            this.positionX + this.width / 2, // 함선의 중앙
            this.positionY + this.height / 2,                   // 함선의 상단
            this.bulletSpeed, this.baseDamage, this.direction, getShipID());
        bullets.add(bullet);
    }

    public void startBurstShooting() {
        if (!isBurstShooting) {
            isBurstShooting = true;
            this.burstShotCount = 0; // 점사 초기화
        }
    }

    /**
     * 5초간 무적.
     */
    public final void useUlt() {
        // TODO: 무적 스프라이트로 변경
        isUltActv = true;
        ultGauge = 0;
        // 궁극기 발동 시간을 위한 타이머
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        timer.schedule(() -> {
            isUltActv = false;
        }, 5, TimeUnit.SECONDS);

        timer.shutdown();
    }
}
