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
    private int shootingInterval;
    /** Speed of the bullets shot by the ship. */
    private int bulletSpeed;
    /** Movement of the ship for each unit of time. */
    private int speed;
    /** 함선의 기본 데미지 */
    private int baseDamage;
    /** 함선이 바라보고 있는 뱡향 */
    private static Direction direction;
    /** Minimum time between shots. */
    private Cooldown shootingCooldown;
    /** Time spent inactive between hits. */
    private Cooldown destructionCooldown;

    /**
     * Constructor, establishes the ship's properties.
     *
     * @param positionX Initial position of the ship in the X axis.
     * @param positionY Initial position of the ship in the Y axis.
     * @param direction 함선의 초기 방향.
     */
    public Ship(final int positionX, final int positionY, final Direction direction) {
        super(positionX, positionY, 13 * 2, 8 * 2, Color.GREEN, direction);

        this.spriteType = SpriteType.Ship;

        StatusManager statusManager = Core.getStatusManager();
        this.shootingInterval = statusManager.getShootingInterval();
        this.bulletSpeed = statusManager.getBulletSpeed();
        this.baseDamage = statusManager.getBaseDamage();
        this.speed = statusManager.getSpeed();

        this.shootingCooldown = Core.getCooldown(this.shootingInterval);
        this.destructionCooldown = Core.getCooldown(1000);

        this.direction = direction;
    }

    /**
     * Moves the ship right until the right screen border is reached.
     */
    public final void moveRight() {
        this.direction = Direction.RIGHT;
        this.positionX += speed;
    }

    /**
     * Moves the ship left until the left screen border is reached.
     */
    public final void moveLeft() {
        this.direction = Direction.LEFT;
        this.positionX -= speed;
    }

    /**
     * Moves the ship up until the top screen border is reached.
     */
    public final void moveUp() {
        this.direction = Direction.UP;
        this.positionY -= speed;
    }

    /**
     * Moves the ship down until the bottom screen border is reached.
     */
    public final void moveDown() {
        this.direction = Direction.DOWN;
        this.positionY += speed;
    }

    /**
     * Moves the ship up the right until the top and right screen border is reached.
     */
    public final void moveUpRight() {
        this.direction = Direction.UP_RIGHT;
        this.positionY -= (int) (speed / Math.sqrt(2));
        this.positionX += (int) (speed / Math.sqrt(2));
    }

    /**
     * Moves the ship up the left until the top and left screen border is reached.
     */
    public final void moveUpLeft() {
        this.direction = Direction.UP_LEFT;
        this.positionY -= (int) (speed / Math.sqrt(2));
        this.positionX -= (int) (speed / Math.sqrt(2));
    }

    /**
     * Moves the ship down the right until the bottom and right screen border is reached.
     */
    public final void moveDownRight() {
        this.direction = Direction.DOWN_RIGHT;
        this.positionY += (int) (speed / Math.sqrt(2));
        this.positionX += (int) (speed / Math.sqrt(2));
    }

    /**
     * Moves the ship down the left until the bottom and left screen border is reached.
     */
    public final void moveDownLeft() {
        this.direction = Direction.DOWN_LEFT;
        this.positionY += (int) (speed / Math.sqrt(2));
        this.positionX -= (int) (speed / Math.sqrt(2));
    }

    /**
     * Shoots a bullet upwards.
     *
     * @param bullets List of bullets on screen, to add the new bullet.
     * @return Checks if the bullet was shot correctly.
     */
    public final boolean shoot(final Set<Bullet> bullets) {
        if (this.shootingCooldown.checkFinished()) {
            this.shootingCooldown.reset();
            bullets.add(BulletPool.getBullet(positionX + this.width / 2,
                positionY, this.bulletSpeed, this.baseDamage, direction, "SHIP"));
            return true;
        }
        return false;
    }

    /**
     * Updates status of the ship.
     */
    public final void update() {
        if (!this.destructionCooldown.checkFinished()) {
            this.spriteType = SpriteType.ShipDestroyed;
        } else {
            this.spriteType = SpriteType.Ship;
        }
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
}
