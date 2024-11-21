package entity;

import java.awt.Color;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;
import engine.StatusManager;

/**
 * Implements a ship, to be controlled by the player.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class Ship extends Entity {

    /** Time between shots. */
    protected int shootingInterval;
    /** Speed of the bullets shot by the ship. */
    protected int bulletSpeed;
    /** Movement of the ship for each unit of time. */
    protected int speed;
    /** 함선의 기본 데미지 */
    protected int baseDamage;
    /** 함선이 바라보고 있는 뱡향 */
    protected static Direction direction;
    /** 축 방향 속도의 소수 부분을 저장 및 누적 */
    protected double remainingMovement = 0;
    /** 축 방향 속도의 정수 부분 (실제 이동량) */
    protected int movement = 0;
    /** Minimum time between shots. */
    protected Cooldown shootingCooldown;
    /** Time spent inactive between hits. */
    protected Cooldown destructionCooldown;
    /** 함선의 ID */
    protected int shipID;
    /** 점사 여부 확읹 변수 */
    public boolean isBurstShooting;

    /**
     * Constructor, establishes the ship's properties.
     *
     * @param positionX Initial position of the ship in the X axis.
     * @param positionY Initial position of the ship in the Y axis.
     * @param direction 함선의 초기 방향.
     */
    public Ship(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID) {
        super(positionX, positionY, 13 * 2, 8 * 2, color, direction);

        this.spriteType = SpriteType.Ship;

        StatusManager statusManager = Core.getStatusManager();
        this.shootingInterval = statusManager.getShootingInterval();
        this.bulletSpeed = statusManager.getBulletSpeed();
        this.baseDamage = statusManager.getBaseDamage();
        this.speed = statusManager.getSpeed();

        this.shootingCooldown = Core.getCooldown(this.shootingInterval);
        this.destructionCooldown = Core.getCooldown(200);

        this.direction = direction;
        this.shipID = shipID;
        this.isBurstShooting = false;
    }

    /**
     * Moves the ship right until the right screen border is reached.
     */
    public void moveRight() {
        this.direction = Direction.RIGHT;
        this.positionX += speed;
    }

    /**
     * Moves the ship left until the left screen border is reached.
     */
    public void moveLeft() {
        this.direction = Direction.LEFT;
        this.positionX -= speed;
    }

    /**
     * Moves the ship up until the top screen border is reached.
     */
    public void moveUp() {
        this.direction = Direction.UP;
        this.positionY -= speed;
    }

    /**
     * Moves the ship down until the bottom screen border is reached.
     */
    public void moveDown() {
        this.direction = Direction.DOWN;
        this.positionY += speed;
    }

    /**
     * Moves the ship up the right until the top and right screen border is reached.
     */
    public void moveUpRight() {
        this.direction = Direction.UP_RIGHT;
        calculateMovement();
        this.positionY -= movement;
        this.positionX += movement;
    }

    /**
     * Moves the ship up the left until the top and left screen border is reached.
     */
    public void moveUpLeft() {
        this.direction = Direction.UP_LEFT;
        calculateMovement();
        this.positionY -= movement;
        this.positionX -= movement;
    }

    /**
     * Moves the ship down the right until the bottom and right screen border is reached.
     */
    public void moveDownRight() {
        this.direction = Direction.DOWN_RIGHT;
        calculateMovement();
        this.positionY += movement;
        this.positionX += movement;
    }

    /**
     * Moves the ship down the left until the bottom and left screen border is reached.
     */
    public void moveDownLeft() {
        this.direction = Direction.DOWN_LEFT;
        calculateMovement();
        this.positionY += movement;
        this.positionX -= movement;
    }

    /**
     * 축 방향 이동속도에서 소수점 아래 부분 누적 및 정수 부분 구분.
     */
    public void calculateMovement() {
        remainingMovement += speed / Math.sqrt(2);
        movement = (int) remainingMovement; // 정수 부분
        remainingMovement -= movement; // 소수 부분
    }

    /**
     * Shoots a bullet upwards.
     *
     * @param bullets List of bullets on screen, to add the new bullet.
     * @return Checks if the bullet was shot correctly.
     */
    public boolean shoot(final Set<Bullet> bullets) {
        if (this.shootingCooldown.checkFinished()) {
            this.shootingCooldown.reset();
            bullets.add(BulletPool.getBullet(positionX + this.width / 2,
                positionY, this.bulletSpeed, this.baseDamage, direction, 1));
            return true;
        }
        return false;
    }

    /**
     * Updates status of the ship, based on direction.
     */
    public void update() {
        if (!this.destructionCooldown.checkFinished()) {
            if (isDiagonal()) {
                this.spriteType = SpriteType.ShipDiagonalDestroyed;
            } else {
                this.spriteType = SpriteType.ShipDestroyed;
            }
        } else {

            if (isDiagonal()) {
                this.spriteType = SpriteType.ShipDiagonal;
            } else {
                this.spriteType = SpriteType.Ship;
            }

        }
    }


    /**
     * Switches the ship to its destroyed state.
     */
    public void destroy() {
        this.destructionCooldown.reset();
    }

    /**
     * Checks if the ship is destroyed.
     *
     * @return True if the ship is currently destroyed.
     */
    public boolean isDestroyed() {
        return !this.destructionCooldown.checkFinished();
    }

    /**
     * 함선이 대각선 방향을 바라보고 있는지 체크
     *
     * @return 함선의 방향이 대각선 방향이면 True
     */
    public boolean isDiagonal() {
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
    public int getSpeed() {
        return this.speed;
    }

    public int getBaseDamage() {
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

    /**
     * 힘선이 점사를 시작하는 메소드
     */
    public void startBurstShooting() {
        // 기본 Ship은 점사 기능 없음
    }
}
