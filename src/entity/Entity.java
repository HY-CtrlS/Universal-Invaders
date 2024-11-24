package entity;

import java.awt.Color;

import engine.DrawManager.SpriteType;

/**
 * Implements a generic game entity.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class Entity {

    /** Position in the x-axis of the upper left corner of the entity. */
    protected int positionX;
    /** Position in the y-axis of the upper left corner of the entity. */
    protected int positionY;
    /** Width of the entity. */
    protected int width;
    /** Height of the entity. */
    protected int height;
    /** Color of the entity. */
    private Color[] color;
    /** Sprite type assigned to the entity. */
    protected SpriteType spriteType;
    /** 엔티티 스프라이트의 방향 (기본 위) */
    protected Direction direction;

    /**
     * Constructor, establishes the entity's generic properties.
     *
     * @param positionX Initial position of the entity in the X axis.
     * @param positionY Initial position of the entity in the Y axis.
     * @param width     Width of the entity.
     * @param height    Height of the entity.
     * @param color     Color of the entity.
     */
    public Entity(final int positionX, final int positionY, final int width,
        final int height, final Color color) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.color = new Color[] { color };
        this.direction = Entity.Direction.UP;
    }

    /**
     * Constructor, establishes the entity's generic properties.
     *
     * @param positionX Initial position of the entity in the X axis.
     * @param positionY Initial position of the entity in the Y axis.
     * @param width     Width of the entity.
     * @param height    Height of the entity.
     * @param color     Color of the entity.
     */
    public Entity(final int positionX, final int positionY, final int width,
        final int height, final Color[] color) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.color = color;
        this.direction = Entity.Direction.UP;
    }

    /**
     * Constructor, establishes the entity's generic properties.
     *
     * @param positionX Initial position of the entity in the X axis.
     * @param positionY Initial position of the entity in the Y axis.
     * @param width     Width of the entity.
     * @param height    Height of the entity.
     * @param color     Color of the entity.
     */
    public Entity(final int positionX, final int positionY, final int width,
        final int height, final Color color, final Direction direction) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.color = new Color[] { color };
        this.direction = direction;
    }

    /**
     * Constructor, establishes the entity's generic properties.
     *
     * @param positionX Initial position of the entity in the X axis.
     * @param positionY Initial position of the entity in the Y axis.
     * @param width     Width of the entity.
     * @param height    Height of the entity.
     * @param color     Color of the entity.
     */
    public Entity(final int positionX, final int positionY, final int width,
        final int height, final Color[] color, final Direction direction) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.color = color;
        this.direction = direction;
    }

    /** 방향 표시를 위한 열거형 */
    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        UP_RIGHT,
        UP_LEFT,
        DOWN_RIGHT,
        DOWN_LEFT;

        /**
         * 현재 방향에서 각도 오프셋에 따라 새로운 방향을 반환.
         *
         * @param currentDirection 현재 방향.
         * @param angleOffset      각도 오프셋 (양수: 시계방향, 음수: 반시계방향).
         * @return 새 방향.
         */
        public static Direction getOffsetDirection(Direction currentDirection, int angleOffset) {
            // 기존 방향을 기준으로 각도별 새로운 방향 매핑
            switch (currentDirection) {
                case UP:
                    if (angleOffset > 0) {
                        return UP_RIGHT;
                    } else {
                        return UP_LEFT;
                    }
                case DOWN:
                    if (angleOffset > 0) {
                        return DOWN_RIGHT;
                    } else {
                        return DOWN_LEFT;
                    }
                case LEFT:
                    if (angleOffset > 0) {
                        return UP_LEFT;
                    } else {
                        return DOWN_LEFT;
                    }
                case RIGHT:
                    if (angleOffset > 0) {
                        return DOWN_RIGHT;
                    } else {
                        return UP_RIGHT;
                    }
                case UP_RIGHT:
                    if (angleOffset > 0) {
                        return RIGHT;
                    } else {
                        return UP;
                    }
                case UP_LEFT:
                    if (angleOffset > 0) {
                        return UP;
                    } else {
                        return LEFT;
                    }
                case DOWN_RIGHT:
                    if (angleOffset > 0) {
                        return DOWN;
                    } else {
                        return RIGHT;
                    }
                case DOWN_LEFT:
                    if (angleOffset > 0) {
                        return LEFT;
                    } else {
                        return DOWN;
                    }
                default:
                    return currentDirection; // 기본적으로 변경되지 않은 방향 반환
            }
        }
    }

    /**
     * Getter for the color of the entity.
     *
     * @return Color of the entity, used when drawing it.
     */
    public final Color[] getColor() {
        return color;
    }

    public final void setColor(Color[] color) { this.color = color; }

    /**
     * Getter for the X axis position of the entity.
     *
     * @return Position of the entity in the X axis.
     */
    public final int getPositionX() {
        return this.positionX;
    }

    /**
     * Getter for the Y axis position of the entity.
     *
     * @return Position of the entity in the Y axis.
     */
    public final int getPositionY() {
        return this.positionY;
    }

    /**
     * Setter for the X axis position of the entity.
     *
     * @param positionX New position of the entity in the X axis.
     */
    public final void setPositionX(final int positionX) {
        this.positionX = positionX;
    }

    /**
     * Setter for the Y axis position of the entity.
     *
     * @param positionY New position of the entity in the Y axis.
     */
    public final void setPositionY(final int positionY) {
        this.positionY = positionY;
    }

    /**
     * Getter for the sprite that the entity will be drawn as.
     *
     * @return Sprite corresponding to the entity.
     */
    public final SpriteType getSpriteType() {
        return this.spriteType;
    }

    /**
     * Getter for the width of the image associated to the entity.
     *
     * @return Width of the entity.
     */
    public final int getWidth() {
        return this.width;
    }

    /**
     * Getter for the height of the image associated to the entity.
     *
     * @return Height of the entity.
     */
    public final int getHeight() {
        return this.height;
    }

    /**
     * 엔티티의 방향을 얻는 Getter. Ship의 경우 에임의 방향, Bullet의 경우 발사 방향.
     *
     * @return 엔티티의 방향.
     */
    public Direction getDirection() {
        return this.direction;
    }

    /**
     * 엔티티의 방향을 설정하는 Setter. Ship의 경우 에임의 방향, Bullet의 경우 발사 방향.
     *
     * @param direction 설정할 엔티티의 방향.
     */
    public void setDirection(final Direction direction) {
        this.direction = direction;
    }
}
