package entity;

import engine.DrawManager.SpriteType;
import java.awt.Color;
import java.util.Set;

public class Ship3 extends Ship {

    public Ship3(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID) {
        super(positionX, positionY, direction, color, shipID);
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
        return super.shoot(bullets);
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
