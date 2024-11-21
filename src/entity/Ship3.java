package entity;

import engine.Cooldown;
import engine.Core;
import java.awt.Color;
import java.util.Set;

public class Ship3 extends Ship {

    /** 점사 간 딜레이를 위한 쿨다운. */
    private Cooldown burstCooldown;
    /** 점사 중 현재 발사 상태. */
    private int burstShotCount;
    /** 총 점사 횟수 (삼점사). */
    private static final int maxBurstShots = 3;

    public Ship3(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID) {
        super(positionX, positionY, direction, color, shipID);
        this.burstCooldown = Core.getCooldown(100);
        this.burstCooldown.reset();
        this.burstShotCount = 0;
    }

    /**
     * Moves the ship right until the right screen border is reached.
     */
    public final void moveRight() {
        super.moveRight();
    }

    /**
     * Moves the ship left until the left screen border is reached.
     */
    public final void moveLeft() {
        super.moveLeft();
    }

    /**
     * Moves the ship up until the top screen border is reached.
     */
    public final void moveUp() {
        super.moveUp();
    }

    /**
     * Moves the ship down until the bottom screen border is reached.
     */
    public final void moveDown() {
        super.moveDown();
    }

    /**
     * Moves the ship up the right until the top and right screen border is reached.
     */
    public final void moveUpRight() {
        super.moveUpRight();
    }

    /**
     * Moves the ship up the left until the top and left screen border is reached.
     */
    public final void moveUpLeft() {
        super.moveUpLeft();
    }

    /**
     * Moves the ship down the right until the bottom and right screen border is reached.
     */
    public final void moveDownRight() {
        super.moveDownRight();
    }

    /**
     * Moves the ship down the left until the bottom and left screen border is reached.
     */
    public final void moveDownLeft() {
        super.moveDownLeft();
    }

    /**
     * 축 방향 이동속도에서 소수점 아래 부분 누적 및 정수 부분 구분.
     */
    public void calculateMovement() {
        super.calculateMovement();
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
            this.shootingCooldown.reset();
            this.burstCooldown.reset(); // 점사 간 쿨타임 초기화
            this.burstShotCount++;
            shootBullet(bullets); // 첫 번째 총알 발사
            return true;

        } else if (this.burstShotCount > 0 && this.burstShotCount < maxBurstShots
            && this.burstCooldown.checkFinished()) {
            // 점사 진행
            this.burstCooldown.reset(); // 점사 간 쿨타임 초기화
            shootBullet(bullets); // 다음 총알 발사
            this.burstShotCount++;

            if (this.burstShotCount >= maxBurstShots) {
                this.burstShotCount = 0; // 점사 종료
            }
            return true;
        }

        return false;
    }

    /**
     * 총알 발사 로직.
     *
     * @param bullets 총알 Set에 새로 발사된 총알 추가.
     */
    private void shootBullet(Set<Bullet> bullets) {
        Bullet bullet = BulletPool.getBullet(
            this.positionX + this.width / 2, // 실시간 X 위치
            this.positionY,                  // 실시간 Y 위치
            this.bulletSpeed, this.baseDamage, this.direction, 3);
        bullets.add(bullet);
    }

    /**
     * Updates status of the ship, based on direction.
     */
    public final void update() {
        super.update();
    }


    /**
     * Switches the ship to its destroyed state.
     */
    public final void destroy() {
        this.destructionCooldown.reset();
    }

    /**
     * Checks if the ship is destroyed.
     *
     * @return True if the ship is currently destroyed.
     */
    public final boolean isDestroyed() {
        return !this.destructionCooldown.checkFinished();
    }

    /**
     * 함선이 대각선 방향을 바라보고 있는지 체크
     *
     * @return 함선의 방향이 대각선 방향이면 True
     */
    public final boolean isDiagonal() {
        return switch (direction) {
            case UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT -> true;
            default -> false;
        };
    }

    /**
     * Getter for the ship's speed.
     *
     * @return Speed of the ship.
     */
    public final int getSpeed() {
        return this.speed;
    }

    public final int getBaseDamage() {
        return this.baseDamage;
    }

    /**
     * 함선의 방향을 얻는 Getter
     *
     * @return 함선의 방향.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Getter for the ship's X position.
     *
     * @return The X coordinate of the ship.
     */
    public int getX() {
        return this.positionX;
    }

    /**
     * Getter for the ship's Y position.
     *
     * @return The Y coordinate of the ship.
     */
    public int getY() {
        return this.positionY;
    }

    /**
     * 함선의 ID를 얻는 Getter
     *
     * @return 함선의 ID.
     */
    public int getShipID() {
        return this.shipID;
    }
}
