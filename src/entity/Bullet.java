package entity;

import java.awt.Color;

import engine.DrawManager.SpriteType;

/**
 * Implements a bullet that moves vertically up or down.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class Bullet extends Entity {

    /**
     * Speed of the bullet
     */
    private int speed;
    // 총알의 데미지
    private int damage;
    // 총알의 뱡향
    private Direction direction;
    // 아군 또는 적 함선이 발사한 총알을 구분하는 식별자
    private int classify;

    /**
     * Constructor, establishes the bullet's properties.
     *
     * @param positionX Initial position of the bullet in the X axis.
     * @param positionY Initial position of the bullet in the Y axis.
     * @param speed     Speed of the bullet.
     * @param damage    총알의 데미지.
     * @param direction 총알의 방향.
     * @param classify  총알의 진영.
     */
    public Bullet(final int positionX, final int positionY, final int speed, int damage,
        Direction direction, String classify) {
        super(positionX, positionY, 3 * 2, 5 * 2, Color.WHITE);

        this.classify = classify.equals("ENEMY") ? 1 : 0; // 아군 SHIP : 0, 적군 ENEMY : 1
        this.direction = direction;
        this.speed = speed;
        this.damage = damage;
        setSprite();
    }

    /**
     * Sets correct sprite for the bullet, based on speed.
     */
    public final void setSprite() {
        if (this.classify == 0) {
            this.spriteType = SpriteType.Bullet;
        } else {
            this.spriteType = SpriteType.EnemyBullet;
        }
    }

    /**
     * Updates the bullet's position.
     */
    public final void update() {
        switch (direction) {
            case UP:
                this.positionY -= this.speed;
                break;
            case DOWN:
                this.positionY += this.speed;
                break;
            case RIGHT:
                this.positionX += this.speed;
                break;
            case LEFT:
                this.positionX -= this.speed;
                break;
            case UP_RIGHT:
                this.positionY -= (int) (speed / Math.sqrt(2));
                this.positionX += (int) (speed / Math.sqrt(2));
                break;
            case UP_LEFT:
                this.positionY -= (int) (speed / Math.sqrt(2));
                this.positionX -= (int) (speed / Math.sqrt(2));
                break;
            case DOWN_RIGHT:
                this.positionY += (int) (speed / Math.sqrt(2));
                this.positionX += (int) (speed / Math.sqrt(2));
                break;
            case DOWN_LEFT:
                this.positionY += (int) (speed / Math.sqrt(2));
                this.positionX -= (int) (speed / Math.sqrt(2));
                break;
        }
    }

    /**
     * Setter of the speed of the bullet.
     *
     * @param speed New speed of the bullet.
     */
    public final void setSpeed(final int speed) {
        this.speed = speed;
    }

    /**
     * Getter for the speed of the bullet.
     *
     * @return Speed of the bullet.
     */
    public final int getSpeed() {
        return this.speed;
    }

    public final void setDamage(final int damage) {
        this.damage = damage;
    }

    /**
     * Getter for the speed of the bullet.
     *
     * @return Speed of the bullet.
     */
    public final int getDamage() {
        return this.damage;
    }

    /**
     * 총알의 방향을 설정하는 Setter.
     *
     * @param direction 총알의 새로운 방향.
     */
    public final void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * 총알의 방향을 얻는 Getter.
     *
     * @return 총알의 방향.
     */
    public Direction getDirection() {
        return this.direction;
    }

    /**
     * 총알의 진영을 설정하는 Setter.
     *
     * @param classify 총알의 진영
     */
    public void setClassify(String classify) {
        this.classify = classify.equals("ENEMY") ? 1 : 0; // 아군 SHIP : 0, 적군 ENEMY : 1
    }

    /**
     * 총알의 진영을 얻는 Getter.
     *
     * @return 총알의 진영.
     */
    public int getClassify() {
        return this.classify;
    }
}
