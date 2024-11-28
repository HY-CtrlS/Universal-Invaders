package kr.ac.hanyang.entity;

import java.awt.Color;
import java.util.Set;

import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager.SpriteType;
import kr.ac.hanyang.engine.StatusManager;

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
    /** 함선의 에임 뱡향 */
    protected Direction direction;
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
    /** 점사 여부 확인 변수 */
    public boolean isBurstShooting;

    /**
     * Constructor, establishes the ship's properties.
     *
     * @param positionX Initial position of the ship in the X axis.
     * @param positionY Initial position of the ship in the Y axis.
     * @param direction 함선의 초기 에임 방향.
     */
    public Ship(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID) {
        super(positionX, positionY, 13 * 2, 13 * 2, new Color[] {color, Color.WHITE}, direction);

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
        this.positionX += speed;
    }

    /**
     * Moves the ship left until the left screen border is reached.
     */
    public void moveLeft() {
        this.positionX -= speed;
    }

    /**
     * Moves the ship up until the top screen border is reached.
     */
    public void moveUp() {
        this.positionY -= speed;
    }

    /**
     * Moves the ship down until the bottom screen border is reached.
     */
    public void moveDown() {
        this.positionY += speed;
    }

    /**
     * Moves the ship up the right until the top and right screen border is reached.
     */
    public void moveUpRight() {
        calculateMovement();
        this.positionY -= movement;
        this.positionX += movement;
    }

    /**
     * Moves the ship up the left until the top and left screen border is reached.
     */
    public void moveUpLeft() {
        calculateMovement();
        this.positionY -= movement;
        this.positionX -= movement;
    }

    /**
     * Moves the ship down the right until the bottom and right screen border is reached.
     */
    public void moveDownRight() {
        calculateMovement();
        this.positionY += movement;
        this.positionX += movement;
    }

    /**
     * Moves the ship down the left until the bottom and left screen border is reached.
     */
    public void moveDownLeft() {
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
                positionY + this.height / 2, this.bulletSpeed, this.baseDamage, this.direction,
                getShipID()));
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
     * 함선의 에임이 대각선 방향인지 체크.
     *
     * @return 에임 방향이 대각선 방향이면 True.
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
     * 함선의 에임 방향을 설정하는 Setter.
     *
     * @param direction 설정할 에임의 방향.
     */
    public final void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * 함선의 에임 방향을 얻는 Getter.
     *
     * @return 함선의 에임 방향.
     */
    public Direction getDirection() {
        return direction;
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

    public static Ship createShipByID(int shipID, int positionX, int positionY) {
        switch (shipID) {
            case 1:
                return new Ship1(positionX, positionY, Entity.Direction.UP, Color.GREEN, 1);
            case 2:
                return new Ship2(positionX, positionY, Entity.Direction.UP, Color.BLUE, 2);
            case 3:
                return new Ship3(positionX, positionY, Entity.Direction.UP, Color.YELLOW, 3);
            case 4:
                return new Ship4(positionX, positionY, Entity.Direction.UP, Color.RED, 4);
            default:
                throw new IllegalArgumentException("Invalid shipID: " + shipID);
        }
    }

    /**
     * Ship의 Stat을 StatusManager에서 업데이트.
     */
    public void updateStatsFromStatusManager() {
        StatusManager statusManager = Core.getStatusManager();
        this.shootingInterval = statusManager.getShootingInterval();
        this.bulletSpeed = statusManager.getBulletSpeed();
        this.baseDamage = statusManager.getBaseDamage();
        this.speed = statusManager.getSpeed();
    }
}
