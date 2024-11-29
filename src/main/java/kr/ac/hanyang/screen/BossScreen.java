package kr.ac.hanyang.screen;


import static kr.ac.hanyang.engine.Core.getStatusManager;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager.SpriteType;
import kr.ac.hanyang.engine.StatusManager;
import kr.ac.hanyang.entity.Bullet;
import kr.ac.hanyang.entity.BulletPool;
import kr.ac.hanyang.entity.EnemyShip;
import kr.ac.hanyang.entity.Entity;
import kr.ac.hanyang.entity.Entity.Direction;
import kr.ac.hanyang.entity.Experience;
import kr.ac.hanyang.entity.ExperiencePool;
import kr.ac.hanyang.entity.Ship;
import kr.ac.hanyang.entity.Boss;

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
    /** 보스 객체 */
    private Boss boss;
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
    /** 궁극기 게이지 자동 상승 쿨타임 생성 **/
    private Cooldown increUltCooldown;
    /** 궁극기 활성화 시간 */
    protected Cooldown ultActivatedTime;
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

        this.boss = new Boss(this.width / 2, SEPARATION_LINE_HEIGHT + 50);
        this.boss.setDirection(Direction.RIGHT);

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

        this.increUltCooldown = Core.getCooldown(1000);
        this.increUltCooldown.reset();

        // 아군 함선 궁극기 기능 연결
        switch (this.ship.getShipID()) {
            case 1:
                this.ultActivatedTime = Core.getCooldown(1000);
                this.ultActivatedTime.reset();
                break;
            case 2:
                this.ultActivatedTime = Core.getCooldown(4000);
                this.ultActivatedTime.reset();
                break;
            case 3:
                this.ultActivatedTime = Core.getCooldown(4000);
                this.ultActivatedTime.reset();
                break;
            case 4:
                this.ultActivatedTime = Core.getCooldown(3000);
                this.ultActivatedTime.reset();
                break;
        }
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
            // 보스의 공격 처리 & 궁극기 효과 적용
            if (this.ship.getShipID() == 2 && this.ship.isUltActivated()) {
                // Ship2 궁극기 활성화 여부에 따라 보스 공격 무력화 결정
            } else {
                this.boss.attack();
            }
            this.boss.checkPhase();

            // WASD - 함선 이동
            boolean moveRight, moveLeft, moveUp, moveDown;
            // 방향키 - 에임
            boolean aimRight, aimLeft, aimUp, aimDown;

            // Ship1 궁극기 활성화 여부에 따라 이동 및 발사 가능 여부 결정
            if (this.ship.getShipID() == 1 && this.ship.isUltActivated()) {
                moveRight = false;
                moveLeft = false;
                moveUp = false;
                moveDown = false;

                aimRight = false;
                aimLeft = false;
                aimUp = false;
                aimDown = false;
            } else {
                moveRight = inputManager.isKeyDown(KeyEvent.VK_D);
                moveLeft = inputManager.isKeyDown(KeyEvent.VK_A);
                moveUp = inputManager.isKeyDown(KeyEvent.VK_W);
                moveDown = inputManager.isKeyDown(KeyEvent.VK_S);

                aimRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT);
                aimLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT);
                aimUp = inputManager.isKeyDown(KeyEvent.VK_UP);
                aimDown = inputManager.isKeyDown(KeyEvent.VK_DOWN);
            }

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
                if (this.ship.getShipID() == 3) {
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
            //궁극기 기능 추가
            if (inputManager.isKeyDown(KeyEvent.VK_F)) {
                if (this.ship.isUltReady()) {
                    this.ultActivatedTime.reset();
                    this.ship.useUlt();
                    this.logger.info("Ultimate Skill!");
                }
            }

            // Ship4 궁극기 활성화 여부에 따라 체력 회복량 결정
            if (this.ship.getShipID() == 4 && this.ship.isUltActivated()) {
                hpRegen(status.getRegenHp() * 10.0);
            } else {
                hpRegen(status.getRegenHp());
            }
            increaseUltGauge();

            this.ship.update();

            // 1초마다 생존 시간 1씩 증가
            if (this.clockCooldown.checkFinished()) {
                this.survivalTime += 1;
                this.clockCooldown.reset();
            }

            if (this.ship.isUltActivated() && this.ultActivatedTime.checkFinished()) {
                this.ship.stopUlt();
                this.ultActivatedTime.reset();
                if (this.ship.getShipID() == 1) {
                    // TODO: 현재 모든 보스의 탄환, 미사일 파괴 + (보스에게 일정 데미지)
                }
            }
        }

        manageCollisions();
        cleanBullets();
        draw();
    }

    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawEntity(this.ship, this.ship.getPositionX(),
            this.ship.getPositionY());
        drawManager.drawEntity(this.boss, this.boss.getPositionX(), this.boss.getPositionY());

        for (Bullet bullet : this.bullets) {
            drawManager.drawEntity(bullet, bullet.getPositionX(),
                bullet.getPositionY());
        }

        // Countdown to game start.
        if (!this.inputDelay.checkFinished()) {
            int countdown = (int) ((INPUT_DELAY
                - (System.currentTimeMillis()
                - this.gameStartTime)) / 1000);
            drawManager.drawCountDown(this, 12);
        }

        // 보스의 체력바 그리기
        drawManager.drawBossHp(this, boss.getCurrentHp(), this.boss);


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
        for (Bullet bullet : this.bullets) {
            // 아군 함선의 총알인 경우
            if (bullet.getClassify() != 0) {
                if (checkCollision(bullet, this.boss) && this.boss.getCurrentHp() > 0) {
                    recyclable.add(bullet);
                    this.boss.getDamaged(status.getBaseDamage());
                }
            }
        }
        this.bullets.removeAll(recyclable);
        BulletPool.recycle(recyclable);
        // 아군 3번 함선의 궁극기
        if (this.ship.getShipID() == 3 && this.ship.isUltActivated()) {
            // 아군 Ship은 무적이라 모든 공격과 충돌 무시
        }
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

    /**
     * increUltCooldown이 끝날 때마다 궁극기 게이지 1씩 증가시킴.
     */
    private void increaseUltGauge() {
        if (this.increUltCooldown.checkFinished() && !this.ship.isUltReady()) {
            this.ship.increaseUltGauge();
            this.increUltCooldown.reset();
        }
    }
}
