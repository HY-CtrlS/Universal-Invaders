package kr.ac.hanyang.screen;


import static kr.ac.hanyang.engine.Core.getStatusManager;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.StatusManager;
import kr.ac.hanyang.entity.Bullet;
import kr.ac.hanyang.entity.BulletPool;
import kr.ac.hanyang.entity.Entity;
import kr.ac.hanyang.entity.Entity.Direction;
import kr.ac.hanyang.entity.Ship;

/**
 * 보스 스테이지의 화면을 정의하는 클래스
 */
public class BossScreen extends Screen {
    // For fields
    /** Milliseconds until the screen accepts user input. */
    private static final int INPUT_DELAY = 6000;
    /** Height of the interface separation line. */
    private static final int SEPARATION_LINE_HEIGHT = 40;
    /** Time from finishing the level to screen change. */
    private static final int SCREEN_CHANGE_INTERVAL = 1500;
    /** 보스의 최대 페이즈 */
    private static final int maxPhase = 3;

    /** 보스의 페이즈 */
    private int phase;
    /** Player's ship. */
    private Ship ship;
    /** Time from finishing the level to screen change. */
    private Cooldown screenFinishedCooldown;
    /** Set of all bullets fired by on screen ships. */
    private Set<Bullet> bullets;
    /** 플레이어의 최대 Hp. 기본값은 100. */
    private int maxHp = getStatusManager().getMaxHp();
    /** Player hp left. */
    private int hp;
    /** HP 자동 재생되는 누적량 체크 **/
    private double remainingRegenHp;
    /** HP 리젠되는 쿨타임 생성 **/
    private Cooldown regenHpCooldown;
    /** Total bullets shot by the player. */
    private int bulletsShot;
    /** Moment the game starts. */
    private long gameStartTime;
    /** 페이즈가 끝났는지 여부 */
    private boolean phaseFinished;
    /** 페이즈가 시작 되었는지 여부 */
    private boolean phaseStarted;
    /** 1초를 새는 Cooldown */
    private Cooldown clockCooldown;
    /** 함선이 완전히 파괴되었는지 여부 */
    private boolean isDestroyed = false;
    /** Total survival time in milliseconds. */
    private int survivalTime;
    /** 현재 함선의 status **/
    private StatusManager status;
    /** 현재 함선의 ID */
    private int shipID;

    /**
     * 생성자, 화면의 속성을 설정
     */
    public BossScreen(final StatusManager status,
        final int width, final int height, final int fps, final Ship ship) {
        super(width, height, fps);

        this.ship = ship;

        this.returnCode = 1;
        this.status = status;
    }

    /**
     * 화면의 기본 속성을 초기화하고 필요한 요소를 추가
     */
    public final void initialize() {
        super.initialize();

        this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
        this.bullets = new HashSet<Bullet>();

        // Special input delay / countdown.
        this.gameStartTime = System.currentTimeMillis();
        this.inputDelay = Core.getCooldown(INPUT_DELAY);
        this.inputDelay.reset();

        this.phaseStarted = false;
        this.survivalTime = 0;
        this.clockCooldown = Core.getCooldown(1000);
        this.clockCooldown.reset();

        this.regenHpCooldown = Core.getCooldown(1000);
        this.regenHpCooldown.reset();
    }

    /**
     * 화면을 실행
     *
     * @return 화면의 현재 리턴코드
     */
    public final int run() {
        super.run();

        return this.returnCode;
    }

    /**
     * 화면의 요소를 업데이트하고 이벤트를 확인
     */
    protected final void update() {
        super.update();

        // phase가 처음 시작될 때 clockCooldown, hpRegenCooldown reset
        if (this.inputDelay.checkFinished() && !this.phaseStarted) {
            this.clockCooldown.reset();
            this.phaseStarted = true;
        }

        if (this.inputDelay.checkFinished() && !this.phaseFinished) {

            // WASD - 함선 이동
            boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_D);
            boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_A);
            boolean moveUp = inputManager.isKeyDown(KeyEvent.VK_W);
            boolean moveDown = inputManager.isKeyDown(KeyEvent.VK_S);
            // 방향키 - 에임
            boolean aimRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT);
            boolean aimLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT);
            boolean aimUp = inputManager.isKeyDown(KeyEvent.VK_UP);
            boolean aimDown = inputManager.isKeyDown(KeyEvent.VK_DOWN);

            boolean isRightBorder = this.ship.getPositionX()
                + this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
            boolean isLeftBorder = this.ship.getPositionX()
                - this.ship.getSpeed() < 1;
            boolean isTopBorder = this.ship.getPositionY()
                - this.ship.getSpeed() < 1 + SEPARATION_LINE_HEIGHT;
            boolean isBottomBorder = this.ship.getPositionY()
                + this.ship.getHeight() + this.ship.getSpeed()
                > this.height - 1;

            if (moveUp && moveRight && !isTopBorder && !isRightBorder) {
                this.ship.moveUpRight();
            } else if (moveUp && moveLeft && !isTopBorder && !isLeftBorder) {
                this.ship.moveUpLeft();
            } else if (moveDown && moveRight && !isBottomBorder && !isRightBorder) {
                this.ship.moveDownRight();
            } else if (moveDown && moveLeft && !isBottomBorder && !isLeftBorder) {
                this.ship.moveDownLeft();
            } else if (moveRight && !isRightBorder) {
                this.ship.moveRight();
            } else if (moveLeft && !isLeftBorder) {
                this.ship.moveLeft();
            } else if (moveUp && !isTopBorder) {
                this.ship.moveUp();
            } else if (moveDown && !isBottomBorder) {
                this.ship.moveDown();
            }

            if (aimUp && aimRight) {
                this.ship.setDirection(Direction.UP_RIGHT);
            } else if (aimUp && aimLeft) {
                this.ship.setDirection(Direction.UP_LEFT);
            } else if (aimDown && aimRight) {
                this.ship.setDirection(Direction.DOWN_RIGHT);
            } else if (aimDown && aimLeft) {
                this.ship.setDirection(Direction.DOWN_LEFT);
            } else if (aimUp) {
                this.ship.setDirection(Direction.UP);
            } else if (aimDown) {
                this.ship.setDirection(Direction.DOWN);
            } else if (aimRight) {
                this.ship.setDirection(Direction.RIGHT);
            } else if (aimLeft) {
                this.ship.setDirection(Direction.LEFT);
            }

            if (aimUp || aimDown || aimRight || aimLeft) {
                if (this.shipID == 3) {
                    this.ship.startBurstShooting();
                } else {
                    if (this.ship.shoot(this.bullets)) {
                        this.bulletsShot++;
                    }
                }
            }

            if (this.ship.isBurstShooting) {
                if (this.ship.shoot(this.bullets)) {
                    this.bulletsShot++;
                }
            }

            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                // 추후 궁극기 추가
            }

            // hp 자동 재생 기능 실행
            hpRegen(status.getRegenHp());
            
            this.ship.update();

            // 1초마다 생존 시간 1씩 증가
            if (this.clockCooldown.checkFinished()) {
                this.survivalTime += 1;
                this.clockCooldown.reset();
            }
        }
    }

    private void draw() {
        drawManager.initDrawing(this);

        drawManager.completeDrawing(this);
    }

    /**
     * Cleans bullets that go off-screen.
     */
    private void cleanBullets() {
        Set<Bullet> recyclable = new HashSet<Bullet>();
        for (Bullet bullet : this.bullets) {
            bullet.update();
            if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
                || bullet.getPositionY() > this.height || bullet.getPositionX() < 0
                || bullet.getPositionX() > this.width) {
                recyclable.add(bullet);
            }
        }
        this.bullets.removeAll(recyclable);
        BulletPool.recycle(recyclable);
    }

    /**
     * Manages collisions between bullets and ships.
     */
    private void manageCollisions() {
        Set<Bullet> recyclable = new HashSet<Bullet>();
    }

    /**
     * Checks if two entities are colliding.
     *
     * @param a First entity, the bullet.
     * @param b Second entity, the ship.
     * @return Result of the collision test.
     */
    private boolean checkCollision(final Entity a, final Entity b) {
        // Calculate center point of the entities in both axis.
        int centerAX = a.getPositionX() + a.getWidth() / 2;
        int centerAY = a.getPositionY() + a.getHeight() / 2;
        int centerBX = b.getPositionX() + b.getWidth() / 2;
        int centerBY = b.getPositionY() + b.getHeight() / 2;
        // Calculate maximum distance without collision.
        int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
        int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
        // Calculates distance.
        int distanceX = Math.abs(centerAX - centerBX);
        int distanceY = Math.abs(centerAY - centerBY);

        return distanceX < maxDistanceX && distanceY < maxDistanceY;
    }

    /** hpRegenCooldown이 끝날 때마다 자동으로 체력을 회복함. */
    private void hpRegen(final double regenHp) {
        // 체력이 최대체력보다 낮을 경우에만 regen
        if (this.regenHpCooldown.checkFinished() && this.hp < maxHp) {
            this.remainingRegenHp += regenHp;
            // 1 이상으로 쌓이면 hp 1만큼을 int_regenHp로 이동
            int int_regenHp = (int) remainingRegenHp;
            remainingRegenHp -= int_regenHp;

            // HP 리젠율이 최대체력을 초과하는 경우, 최대체력을 초과해서 회복되지 않도록 설정
            this.hp = (this.maxHp - this.hp < int_regenHp) ? maxHp : this.hp + int_regenHp;
            this.regenHpCooldown.reset();
        }
    }
}
