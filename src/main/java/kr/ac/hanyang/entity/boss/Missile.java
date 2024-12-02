package kr.ac.hanyang.entity.boss;

import java.awt.Color;
import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager.SpriteType;
import kr.ac.hanyang.entity.Entity;
import kr.ac.hanyang.entity.ship.Ship;

public class Missile extends Entity {

    private static final int EXPLOSION_RADIUS = 50; // 폭발 반경
    private static final int EXPLOSION_DAMAGE = 10; // 최대 데미지
    private static final double INITIAL_SPEED = 3.0; // 초기 속도
    private boolean isDestroyed; // 미사일 파괴 여부
    private boolean hasExploded; // 미사일 폭발 여부
    private double speedX; // X축 속도
    private double speedY; // Y축 속도
    private Ship targetShip;

    private Cooldown explosionCooldown; // 폭발 지속 시간
    private Cooldown trackingCooldown; // 추적 지속 시간

    public Missile(final int positionX, final int positionY,
        final Ship targetShip) {
        super(positionX, positionY, 9 * 2, 7 * 2,
            new Color[]{
                new Color(0, 255, 255),
                new Color(255, 165, 0)},
            Direction.DOWN);
        this.spriteType = SpriteType.Missile;

        this.targetShip = targetShip;

        this.isDestroyed = false;
        this.hasExploded = false;

        this.explosionCooldown = Core.getCooldown(2000);// 폭발 지속 시간: 2초
        this.trackingCooldown = Core.getCooldown(8000); // 추적 지속 시간: 3초
        this.trackingCooldown.reset();
    }

    /**
     * 미사일 상태 업데이트.
     */
    public void update() {
        if (!this.hasExploded) {
            if (this.trackingCooldown.checkFinished()) {
                explode(); // 추적 시간이 끝나면 폭발
            } else {
                trackTarget(); // 목표 추적
                move(); // 이동
            }
        } else {
            if (this.explosionCooldown.checkFinished()) {
                this.isDestroyed = true; // 폭발 지속 시간이 끝나면 제거
            }
        }
    }

    /**
     * 아군 함선을 추적하여 속도를 설정.
     */
    private void trackTarget() {
        // 아군 함선의 현재 위치 계산
        int targetX = targetShip.getPositionX() + targetShip.getWidth() / 2;
        int targetY = targetShip.getPositionY() + targetShip.getHeight() / 2;

        // 목표와의 거리 계산
        int deltaX = targetX - (this.positionX + this.width / 2);
        int deltaY = targetY - (this.positionY + this.height / 2);
        double distance = Math.hypot(deltaX, deltaY);

        // 부드러운 추적을 위해 거리 비율 기반으로 속도 계산
        if (distance != 0) {
            this.speedX = INITIAL_SPEED * (deltaX / distance);
            this.speedY = INITIAL_SPEED * (deltaY / distance);
        }
    }

    /**
     * 미사일 이동.
     */
    private void move() {
        this.positionX += (int) this.speedX;
        this.positionY += (int) this.speedY;
    }

    /**
     * 폭발 처리.
     */
    private void explode() {
        this.hasExploded = true;
        this.explosionCooldown.reset();
        Core.getLogger()
            .info("Missile exploded at (" + this.positionX + ", " + this.positionY + ")");
    }

    /**
     * 폭발 중심에서의 거리 기반 데미지 계산.
     *
     * @param entity 대상 엔티티.
     * @return 데미지 값.
     */
    public int calculateDamage(Entity entity) {
        double distance = Math.hypot(
            entity.getPositionX() + entity.getWidth() / 2 - this.positionX,
            entity.getPositionY() + entity.getHeight() / 2 - this.positionY
        );

        // 폭발 반경 내 거리 기반 데미지 감소 처리
        if (distance <= 30) {
            return EXPLOSION_DAMAGE; // 폭발 중심부
        } else if (distance <= EXPLOSION_RADIUS) {
            return (int) (EXPLOSION_DAMAGE * (1 - (distance - 30) / (EXPLOSION_RADIUS - 30)));
        } else {
            return 0; // 폭발 반경 외부
        }
    }

    /**
     * 미사일 파괴 여부 반환.
     *
     * @return true if destroyed.
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }

    /**
     * 미사일 폭발 여부 반환.
     *
     * @return true if exploded.
     */
    public boolean hasExploded() {
        return hasExploded;
    }

    /**
     * 미사일 폭발 반경 반환
     *
     * @return EXPLOSION_RADIUS
     */
    public int getExplosionRadius() {
        return EXPLOSION_RADIUS;
    }
}