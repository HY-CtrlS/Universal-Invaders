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
    private final Color[] color;
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
        DOWN_LEFT
    }

    /**
     * Getter for the color of the entity.
     *
     * @return Color of the entity, used when drawing it.
     */
    public final Color[] getColor() {
        return color;
    }

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
