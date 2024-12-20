package kr.ac.hanyang.entity.ship;

import java.awt.Color;
import java.util.Set;

import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager.SpriteType;
import kr.ac.hanyang.engine.StatusManager;
import kr.ac.hanyang.entity.Bullet;
import kr.ac.hanyang.entity.BulletPool;
import kr.ac.hanyang.entity.Entity;

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
    protected double speed;
    /** 저속모드에서의 함선 속도 */
    protected double slowSpeed = 1.3;
    /** 저속모드 여부 */
    protected boolean slowMode = false;
    /** 함선의 기본 데미지 */
    protected int baseDamage;
    /** 총알 사거리 */
    protected int range;
    /** 함선의 에임 뱡향 */
    protected Direction direction;
    /** 축 방향 속도의 소수 부분을 저장 및 누적 */
    protected double remainingMovement = 0;
    /** 축 방향 속도의 정수 부분 (실제 이동량) */
    protected int movement = 0;
    /** 궁극기 게이지 */
    protected int ultGauge;
    /** 궁극기 차는 양 */
    protected double regenUltra;
    /** Minimum time between shots. */
    protected Cooldown shootingCooldown;
    /** Time spent inactive between hits. */
    protected Cooldown destructionCooldown;
    /** 함선의 ID */
    protected int shipID;
    /** 점사 여부 확인 변수 */
    protected boolean isBurstShooting;
    /** 토글형 궁극기 활성화 여부 */
    protected boolean isUltActv;
    /** 궁극기를 사용할 수 있는 게이지 기준 양 */
    protected int ultThreshold;
    /** 궁극기 게이지의 소수 부분 누적 */
    private double ultRemainder = 0.0;
    /** 보스 스테이지 페이즈4에서 함선을 강제로 중앙으로 옮길때 사용 */
    protected boolean isCenter;
    /** 이전 궁극기 게이지가 최대치였는지 추적 */
    private boolean wasUltFull = false;

    /**
     * Constructor, establishes the ship's properties.
     *
     * @param positionX Initial position of the ship in the X axis.
     * @param positionY Initial position of the ship in the Y axis.
     * @param direction 함선의 초기 에임 방향.
     * @param color     함선의 색상.
     * @param shipID    함선의 ID.
     */
    public Ship(final int positionX, final int positionY, final Direction direction, Color color,
        final int shipID) {
        super(positionX, positionY, 13 * 2, 13 * 2, color, direction);

        this.spriteType = SpriteType.Ship;

        StatusManager statusManager = Core.getStatusManager();
        this.shootingInterval = statusManager.getShootingInterval();
        this.bulletSpeed = statusManager.getBulletSpeed();
        this.baseDamage = statusManager.getBaseDamage();
        this.range = statusManager.getRange();
        this.speed = statusManager.getSpeed();
        this.regenUltra = statusManager.getRegenUltra();

        this.shootingCooldown = Core.getCooldown(this.shootingInterval);
        this.destructionCooldown = Core.getCooldown(200);

        this.ultGauge = 0;
        this.isUltActv = false;
        this.direction = direction;
        this.shipID = shipID;
        this.isBurstShooting = false;
        this.isCenter = false;
    }

    /**
     * Moves the ship right until the right screen border is reached.
     */
    public void moveRight() {
        calculateMovement();
        this.positionX += movement;
    }

    /**
     * Moves the ship left until the left screen border is reached.
     */
    public void moveLeft() {
        calculateMovement();
        this.positionX -= movement;
    }

    /**
     * Moves the ship up until the top screen border is reached.
     */
    public void moveUp() {
        calculateMovement();
        this.positionY -= movement;
    }

    /**
     * Moves the ship down until the bottom screen border is reached.
     */
    public void moveDown() {
        calculateMovement();
        this.positionY += movement;
    }

    /**
     * Moves the ship up the right until the top and right screen border is reached.
     */
    public void moveUpRight() {
        calculateDiagonalMovement();
        this.positionY -= movement;
        this.positionX += movement;
    }

    /**
     * Moves the ship up the left until the top and left screen border is reached.
     */
    public void moveUpLeft() {
        calculateDiagonalMovement();
        this.positionY -= movement;
        this.positionX -= movement;
    }

    /**
     * Moves the ship down the right until the bottom and right screen border is reached.
     */
    public void moveDownRight() {
        calculateDiagonalMovement();
        this.positionY += movement;
        this.positionX += movement;
    }

    /**
     * Moves the ship down the left until the bottom and left screen border is reached.
     */
    public void moveDownLeft() {
        calculateDiagonalMovement();
        this.positionY += movement;
        this.positionX -= movement;
    }

    public void moveCenter() {
        int centerX = 338;
        int centerY = 451;

        if (this.positionX < centerX) {
            this.positionX += 1;
        } else if (this.positionX > centerX) {
            this.positionX -= 1;
        }

        if (this.positionY < centerY) {
            this.positionY += 1;
        } else if (this.positionY > centerY) {
            this.positionY -= 1;
        }

        if (this.positionX == centerX && this.positionY == centerY) {
            this.isCenter = true;
        }
    }

    /**
     * 축 방향 이동속도에서 소수점 아래 부분 누적 및 정수 부분 구분.
     */
    public void calculateMovement() {
        remainingMovement += (slowMode ? slowSpeed : speed);
        movement = (int) remainingMovement;
        remainingMovement -= movement;
    }

    /**
     * 대각선 이동속도에서 소수점 아래 부분 누적 및 정수 부분 구분.
     */
    public void calculateDiagonalMovement() {
        remainingMovement += (slowMode ? slowSpeed : speed) / Math.sqrt(2);
        movement = (int) remainingMovement;
        remainingMovement -= movement;
    }

    /**
     * Shoots a bullet upwards.
     *
     * @param bullets List of bullets on screen, to add the new bullet.
     */
    public void shoot(final Set<Bullet> bullets) {
        if (this.shootingCooldown.checkFinished()) {
            this.shootingCooldown.reset();
            bullets.add(BulletPool.getBullet(positionX + this.width / 2,
                positionY + this.height / 2, this.bulletSpeed, this.baseDamage, this.range,
                this.direction,
                getShipID()));
            Core.getSoundManager().playBasicAttack();
        }
    }

    /**
     * 궁극기 사용.
     */
    public void useUlt() {
        isUltActv = true;
        ultGauge = 0;
        Core.getSoundManager().playUltUseSound();
    }

    /**
     * 궁극기 게이지 1 + regenUltra + ultRemainder 증가.
     */
    public void increaseUltGauge() {
        if (ultGauge < ultThreshold) {
            // shipStatus에서 궁극기 게이지 증가량을 가져와서 증가
            double totalRegen = 1 + regenUltra + ultRemainder;
            ultGauge += (int) totalRegen; // 정수 부분만 증가
            ultRemainder = totalRegen - (int) totalRegen; // 남은 실수 부분 저장

            if (ultGauge >= ultThreshold) {
                ultGauge = ultThreshold; // 최대치를 초과하지 않도록 제한

                // 궁극기 게이지가 처음으로 100%에 도달했을 때 효과음 재생
                if (!wasUltFull) {
                    Core.getSoundManager().playUltChargeSound(); // 효과음 재생
                    wasUltFull = true; // 100% 상태로 표시
                }
            } else {
                // 게이지가 100% 이하로 떨어지면 상태 리셋
                wasUltFull = false;
            }
        }
    }

    /**
     * 궁극기 게이지가 모두 차 사용 가능한 상태인지 체크.
     *
     * @return 궁극기 게이지가 ultThreshold면 True.
     */
    public boolean isUltReady() {
        return ultGauge == ultThreshold;
    }

    /**
     * 토글형 궁극기가 현재 활성화 중인지 체크.
     *
     * @return 토글형 궁극기 스킬이 실행 중이면 True.
     */
    public final boolean isUltActivated() {
        return isUltActv;
    }

    /**
     * 궁극기 효과 중지.
     */
    public final void stopUlt() {
        isUltActv = false;
    }

    /**
     * 현재 궁극기 게이지 값을 얻는 Getter.
     *
     * @return 현재 궁극기 게이지.
     */
    public final int getUltGauge() {
        return ultGauge;
    }

    /**
     * 궁극기 사용 가능 게이지 기준을 얻는 Getter.
     *
     * @return 궁극기 사용 가능 기준.
     */
    public final int getUltThreshold() {
        return ultThreshold;
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

    public boolean isCenter() {
        return this.isCenter;
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
    public double getSpeed() {
        return this.speed;
    }

    public int getBaseDamage() {
        return this.baseDamage;
    }

    public int getRange() {
        return this.range;
    }

    public int getShootingInterval() {
        return this.shootingInterval;
    }

    public int getBulletSpeed() {
        return this.bulletSpeed;
    }

    public Cooldown getDestructionCooldown() {
        return this.destructionCooldown;
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

    public boolean isBurstShooting() {
        return this.isBurstShooting;
    }

    public void setSlowMode(boolean isSlowMode) {
        this.slowMode = isSlowMode;
    }

    public boolean isSlowMode() {
        return this.slowMode;
    }

    public static Ship createShipByID(int shipID, int positionX, int positionY) {
        switch (shipID) {
            case 1:
                return new Ship1(positionX, positionY, Entity.Direction.UP, new Color(80, 200, 120),
                    1);
            case 2:
                return new Ship2(positionX, positionY, Entity.Direction.UP, new Color(15, 82, 186),
                    2);
            case 3:
                return new Ship3(positionX, positionY, Entity.Direction.UP, new Color(255, 215, 0),
                    3);
            case 4:
                return new Ship4(positionX, positionY, Entity.Direction.UP, new Color(224, 17, 95),
                    4);
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
        this.range = statusManager.getRange();
        this.speed = statusManager.getSpeed();
        this.regenUltra = statusManager.getRegenUltra();
    }
}
