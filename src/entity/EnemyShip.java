package entity;

import java.awt.Color;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;

/**
 * Implements a enemy ship, to be destroyed by the player.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class EnemyShip extends Entity {

    /** Point value of a type A enemy. */
    private static final int A_TYPE_POINTS = 10;
    /** Point value of a type B enemy. */
    private static final int B_TYPE_POINTS = 20;
    /** Point value of a type C enemy. */
    private static final int C_TYPE_POINTS = 30;
    /** Point value of a bonus enemy. */
    private static final int BONUS_TYPE_POINTS = 100;

    /** Cooldown between sprite changes. */
    private Cooldown animationCooldown;
    /** Checks if the ship has been hit by a bullet. */
    private boolean isDestroyed;
    /** Values of the ship, in points, when destroyed. */
    private int pointValue;
    // 플레이어를 지속적으로 추적하는데 필요한 변수
    private double remainingMovementX = 0;
    private double remainingMovementY = 0;

    /**
     * Constructor, establishes the ship's properties.
     *
     * @param positionX  Initial position of the ship in the X axis.
     * @param positionY  Initial position of the ship in the Y axis.
     * @param spriteType Sprite type, image corresponding to the ship.
     */
    public EnemyShip(final int positionX, final int positionY,
        final SpriteType spriteType) {
        super(positionX, positionY, 12 * 2, 8 * 2, Color.WHITE);

        this.spriteType = spriteType;
        this.animationCooldown = Core.getCooldown(500);
        this.isDestroyed = false;

        switch (this.spriteType) {
            case EnemyShipA1:
            case EnemyShipA2:
                this.pointValue = A_TYPE_POINTS;
                break;
            case EnemyShipB1:
            case EnemyShipB2:
                this.pointValue = B_TYPE_POINTS;
                break;
            case EnemyShipC1:
            case EnemyShipC2:
                this.pointValue = C_TYPE_POINTS;
                break;
            default:
                this.pointValue = 0;
                break;
        }
    }

    /**
     * Constructor, establishes the ship's properties for a special ship, with known starting
     * properties.
     */
    public EnemyShip() {
        super(-32, 60, 16 * 2, 7 * 2, Color.RED);

        this.spriteType = SpriteType.EnemyShipSpecial;
        this.isDestroyed = false;
        this.pointValue = BONUS_TYPE_POINTS;
    }

    /**
     * Getter for the score bonus if this ship is destroyed.
     *
     * @return Value of the ship.
     */
    public final int getPointValue() {
        return this.pointValue;
    }

    /**
     * Moves the ship the specified distance.
     *
     * @param distanceX Distance to move in the X axis.
     * @param distanceY Distance to move in the Y axis.
     */
    // 이동 잔량을 남기어 최소 단위인 1 이상만큼이 누적되면 누적된 정수만큼 이동후 이동 잔량에서 뺄셈.
    public final void move(final double distanceX, final double distanceY) {
        this.remainingMovementX += distanceX;
        this.remainingMovementY += distanceY;

        int intMoveX = (int) remainingMovementX;
        int intMoveY = (int) remainingMovementY;

        remainingMovementX -= intMoveX;
        remainingMovementY -= intMoveY;

        this.positionX += intMoveX;
        this.positionY += intMoveY;
    }

    /**
     * Updates attributes, mainly used for animation purposes.
     */
    public final void update() {
        if (this.animationCooldown.checkFinished()) {
            this.animationCooldown.reset();

            switch (this.spriteType) {
                case EnemyShipA1:
                    this.spriteType = SpriteType.EnemyShipA2;
                    break;
                case EnemyShipA2:
                    this.spriteType = SpriteType.EnemyShipA1;
                    break;
                case EnemyShipB1:
                    this.spriteType = SpriteType.EnemyShipB2;
                    break;
                case EnemyShipB2:
                    this.spriteType = SpriteType.EnemyShipB1;
                    break;
                case EnemyShipC1:
                    this.spriteType = SpriteType.EnemyShipC2;
                    break;
                case EnemyShipC2:
                    this.spriteType = SpriteType.EnemyShipC1;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Destroys the ship, causing an explosion.
     */
    public final void destroy() {
        this.isDestroyed = true;
        this.spriteType = SpriteType.Explosion;
    }

    /**
     * Checks if the ship has been destroyed.
     *
     * @return True if the ship has been destroyed.
     */
    public final boolean isDestroyed() {
        return this.isDestroyed;
    }

    // 적 함선의 위치 Setter
    public void setPosition(int positionX, int positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }
}
