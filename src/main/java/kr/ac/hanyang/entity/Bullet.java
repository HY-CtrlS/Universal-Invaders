package kr.ac.hanyang.entity;

import java.awt.Color;

import kr.ac.hanyang.engine.DrawManager.SpriteType;

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
    // 총알의 사정거리 계수
    private int range;
    // 총알의 뱡향
    private Direction direction;
    // 총알의 현재 거리
    private int curDistance = 0;
    // 총알이 날아가는 최대 거리
    private int maxDistance;
    // 총알을 발사한 함선 식별
    private int classify;
    // 축 방향 속도의 소수 부분을 저장 및 누적
    private double remainingMovement = 0;
    // 축 방향 속도의 정수 부분 (실제 이동량)
    private int movement = 0;
    // 총알의 적 관통 여부를 표시
    private boolean isPiercing = false;

    // 보스 총알인 경우의 속도
    private double speedX;
    private double speedY;

    // 보스 총알의 각 축 방향 속도의 소수 부분 저장 및 누적
    private double remainingMovementX;
    private double remainingMovementY;
    // 보스 총알의 각 축 방향 속도의 정수 부분
    private int movementX;
    private int movementY;
    /**
     * Constructor, establishes the bullet's properties.
     *
     * @param positionX Initial position of the bullet in the X axis.
     * @param positionY Initial position of the bullet in the Y axis.
     * @param speed     Speed of the bullet.
     * @param damage    총알의 데미지.
     * @param direction 총알의 방향.
     * @param classify  총알의 종류.
     */
    public Bullet(final int positionX, final int positionY, final int speed, int damage, int range,
        Direction direction, int classify) {
        super(positionX, positionY, 2 * 2, 2 * 2, Color.WHITE);

        this.classify = classify; // 함선 코드로 어느 함선에서 발사한 총알인지 식별
        this.direction = direction;
        this.speed = speed;
        this.damage = damage;
        this.range = range;
        setMaxDistance(this.range);
        setSprite();
    }

    // 보스총알의 생성자
    public Bullet(final int positionX, final int positionY, final double speed, int damage,
        double angle) {
        super(positionX, positionY, 3 * 2, 3 * 2, Color.RED);

        this.classify = 0;
        this.speedX = speed * Math.cos(Math.toRadians(angle));
        this.speedY = speed * Math.sin(Math.toRadians(angle));
        this.damage = damage;
        // 방향은 null 이 안되도록 일단 up으로 설정
        this.direction = Direction.UP;
        setSprite();
    }

    /**
     * 함선이 대각선 방향으로 총알을 발사했는지 체크
     *
     * @return 총알 발사 방향이 대각선 방향이면 True
     */
    public final boolean isDiagonal() {
        return switch (direction) {
            case UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT -> true;
            default -> false;
        };
    }

    /**
     * Sets correct sprite for the bullet, based on classify and direction.
     */
    public final void setSprite() {
        if (this.classify != 0) {
            if (isDiagonal()) {
                this.spriteType = SpriteType.BulletDiagonal;
            } else {
                this.spriteType = SpriteType.Bullet;
            }
        } else {
            this.spriteType = SpriteType.EnemyBullet;
        }
    }

    /**
     * 축 방향 이동속도에서 소수점 아래 부분 누적 및 정수 부분 구분.
     */
    private void calculateMovement() {
        remainingMovement += speed / Math.sqrt(2);
        movement = (int) remainingMovement; // 정수 부분
        remainingMovement -= movement; // 소수 부분
    }

    // 보스 총알의 움직임 계산
    private void calculateBossBulletMovement() {
        remainingMovementX += speedX;
        remainingMovementY += speedY;

        movementX = (int) remainingMovementX;
        movementY = (int) remainingMovementY;

        remainingMovementX -= movementX;
        remainingMovementY -= movementY;
    }
    /**
     * Updates the bullet's position.
     */
    public final void update() {
        if (classify != 0) {
            calculateMovement();
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
                    this.positionY -= movement;
                    this.positionX += movement;
                    break;
                case UP_LEFT:
                    this.positionY -= movement;
                    this.positionX -= movement;
                    break;
                case DOWN_RIGHT:
                    this.positionY += movement;
                    this.positionX += movement;
                    break;
                case DOWN_LEFT:
                    this.positionY += movement;
                    this.positionX -= movement;
                    break;
            }
        } else {
            // Boss의 총알인 경우
            calculateBossBulletMovement();
            this.positionX += movementX;
            this.positionY += movementY;
        }
        this.curDistance += this.speed;
    }

    /**
     * Setter of the speed of the bullet.
     *
     * @param speed New speed of the bullet.
     */
    public final void setSpeed(final int speed) {
        this.speed = speed;
    }

    // 보스 총알인 경우에는 X축 Y축 속도 설정
    public final void setSpeedX(final double speed) {this.speedX = speed;}
    public final void setSpeedY(final double speed) {this.speedY = speed;}
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

    public final void setRange(final int range) {
        this.range = range;
        setMaxDistance(this.range);
    }

    public final int getRange() {
        return this.range;
    }

    /**
     * @return 현재 거리, 최대 거리를 설정 또는 반환함.
     */
    public final void setcurDistance(int distance) {
        this.curDistance = 0;
    }

    public final double getcurDistance() {
        return this.curDistance;
    }

    public final void setMaxDistance(int range) {
        this.maxDistance = range * 20;
    }

    public final double getMaxDistance() {
        return this.maxDistance;
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
    public void setClassify(int classify) {
        this.classify = classify;
    }

    /**
     * 총알의 진영을 얻는 Getter.
     *
     * @return 총알의 진영.
     */
    public int getClassify() {
        return this.classify;
    }

    /**
     * 총알의 관통을 설정하는 Setter.
     */
    public void setPiercing(boolean isPiercing) {
        this.isPiercing = isPiercing;
    }

    /**
     * 총알의 관통 여부를 얻는 Getter.
     *
     * @return 총알의 진영.
     */
    public boolean getisPiercing() {
        return this.isPiercing;
    }
}
