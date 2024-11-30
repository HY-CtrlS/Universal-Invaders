package kr.ac.hanyang.screen;


import static kr.ac.hanyang.engine.Core.getStatusManager;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.GameState;
import kr.ac.hanyang.engine.DrawManager.SpriteType;
import kr.ac.hanyang.engine.StatusManager;
import kr.ac.hanyang.entity.Bullet;
import kr.ac.hanyang.entity.BulletPool;
import kr.ac.hanyang.entity.Entity;
import kr.ac.hanyang.entity.Entity.Direction;
import kr.ac.hanyang.entity.boss.Crystal;
import kr.ac.hanyang.entity.boss.Laser;
import kr.ac.hanyang.entity.boss.LaserPool;
import kr.ac.hanyang.entity.boss.Missile;
import kr.ac.hanyang.entity.boss.MissilePool;
import kr.ac.hanyang.entity.ship.Ship;
import kr.ac.hanyang.entity.boss.Boss;

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
    // gameState
    private GameState gameState;
    private Cooldown createLaserCooldown;
    private LaserPool laserPool;
    private Set<Laser> lasers;
    private Cooldown createMissileCooldown;
    private MissilePool missilePool;
    private Set<Missile> missiles;
    private Cooldown createCrystalCooldown;
    private Crystal crystal;

    //임시 쿨다운 변수
    private Cooldown bossBasicBullet;
    private int basicAttackCount;

    Random random = new Random();

    /**
     * 생성자, 화면의 속성을 설정
     */
    public BossScreen(final GameState gameState,
        final int width, final int height, final int fps, final Ship ship) {
        super(width, height, fps);

        this.gameState = gameState;
        this.hp = gameState.getHp();
        this.status = gameState.getStatus();
        this.ship = ship;

        this.returnCode = 1;
        this.status = status;
    }

    /**
     * 화면의 기본 속성을 초기화하고 필요한 요소를 추가
     */
    public final void initialize() {
        super.initialize();

        this.boss = new Boss(this.width / 2 - 40, SEPARATION_LINE_HEIGHT + 50);
        this.boss.setDirection(Direction.RIGHT);

        this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
        this.bullets = new HashSet<Bullet>();
        this.laserPool = new LaserPool(this.ship);
        this.lasers = laserPool.getLasers();
        this.missilePool = new MissilePool(this.ship);
        this.missiles = missilePool.getMissiles();
        this.crystal = new Crystal(0, 0);

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
        // 보스의 기본 공격 쿨타임
        this.bossBasicBullet = Core.getVariableCooldown(5000, 2500);
        this.bossBasicBullet.reset();

        this.basicAttackCount = 0;

        this.createLaserCooldown = Core.getCooldown(4000);
        this.createLaserCooldown.reset();

        this.createMissileCooldown = Core.getCooldown(10000);
        this.createMissileCooldown.reset();

        this.createCrystalCooldown = Core.getCooldown(25000);
        this.createCrystalCooldown.reset();
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
            this.laserPool.update();
            this.missilePool.update();
            if (this.boss.getCurrentHp() <= 0) {
                this.boss.changeBossState();
            }

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
            if (this.boss.isPattern()) {
                // 패턴별 공격 구현 예정
            } else {
                // 보스 패턴A 발동 메소드
                if (bossBasicBullet.checkFinished()) {
                    int randomKey = random.nextInt(7) + 6;
                    double range = randomKey * 5.0;
                    int bulletNum = randomKey - 2;
                    // 공격이 완료되면 false 반환, 아닌 경우 true 반환
                    basicAttackCount += this.boss.spreadBullet(this.bullets, getBulletDirection(),
                        range, bulletNum);
                    // 3발을 발사하면 보스 기본공격 쿨타임 시작
                    if (basicAttackCount == 3) {
                        bossBasicBullet.reset();
                        // 다시 공격 횟수를 0으로 초기화
                        basicAttackCount = 0;
                    }
                }

                if (this.createMissileCooldown.checkFinished()) {
                    missilePool.createMissile(this.boss.getPositionX() + this.boss.getWidth() / 2,
                        this.boss.getPositionY() + this.boss.getHeight() / 2);
                    this.createMissileCooldown.reset();
                }

                if (this.boss.getPhase() > 1 && this.createLaserCooldown.checkFinished()) {
                    laserPool.createLaser();
                    this.createLaserCooldown.reset();
                }
                if (this.boss.getPhase() > 2) {
                    if (!this.boss.isInvincible()) {
                        if (this.createCrystalCooldown.checkFinished()) {
                            createCrystal();
                            this.boss.setInvincible(true);
                            this.createCrystalCooldown.reset();
                        }
                    } else {
                        if (this.crystal.isBroken()) {
                            this.boss.setInvincible(false);
                            this.createCrystalCooldown.reset();
                        }
                    }
                }

                if (this.boss.getPhase() > 2) {
                    this.boss.move();
                }
            }
        }
        manageCollisions();
        cleanBullets();
        draw();
    }

    private void draw() {
        drawManager.initDrawing(this);

        // Countdown to game start.
        if (!this.inputDelay.checkFinished()) {
            int countdown = (int) ((INPUT_DELAY
                - (System.currentTimeMillis()
                - this.gameStartTime)) / 1000);
            drawManager.drawCountDown(this, countdown);
        }
        if (this.inputDelay.checkFinished()) {
            drawManager.drawEntity(this.ship, this.ship.getPositionX(),
                this.ship.getPositionY());
            drawManager.drawEntity(this.boss, this.boss.getPositionX(), this.boss.getPositionY());

            for (Bullet bullet : this.bullets) {
                drawManager.drawEntity(bullet, bullet.getPositionX(),
                    bullet.getPositionY());
            }

            if (this.boss.isInvincible()) {
                drawManager.drawEntity(this.crystal, this.crystal.getPositionX(),
                    this.crystal.getPositionY());
            }

            laserPool.draw();

            missilePool.draw();

            // 보스의 체력바 그리기
            drawManager.drawBossHp(this, boss.getCurrentHp(), this.boss);
            // 아군 함선의 체력바 그리기
            drawManager.drawLives(this, 10, this.getHeight() - 50, this.hp);

        }

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
                if (this.boss.isInvincible()) {
                    if (checkCollision(bullet, this.crystal) && this.crystal.getHp() > 0) {
                        recyclable.add(bullet);
                        this.crystal.getDamaged(status.getBaseDamage());
                    }
                    if (checkCollision(bullet, this.boss) && this.boss.getCurrentHp() > 0) {
                        recyclable.add(bullet);
                    }
                } else {
                    if (checkCollision(bullet, this.boss) && this.boss.getCurrentHp() > 0) {
                        recyclable.add(bullet);
                        this.boss.getDamaged(status.getBaseDamage());
                    }
                }
            } else {
                // 적 총알인 경우
                // 아군 3번 함선이 궁극기 킨 경우 충돌 무시
                if (this.ship.getShipID() != 3 || !this.ship.isUltActivated()) {
                    // 적 총알과 충돌하고 아직 보스가 안죽은 경우
                    if (checkCollision(bullet, this.ship) && this.boss.getCurrentHp() > 0) {
                        // 아군 함선이 안부서져 있는 상태인 경우
                        if (!this.ship.isDestroyed()) {
                            //아군 함선 파괴로 업데이트
                            this.ship.destroy();
                            //아군 함선의 체력 보스 총알의 데미지 만큼 감소
                            this.hp = (this.hp - bullet.getDamage() > 0) ? this.hp
                                - bullet.getDamage() : 0;
                            this.logger.info(
                                "Hit on BossBullet, -" + bullet.getDamage() + " Hp");
                            //충돌한 총알 재활용할 총알로 추가
                            recyclable.add(bullet);
                            // 맞으면 효과음 출력
                            Core.getSoundManager().playDamageSound();
                            if (this.hp <= 0 && !this.isDestroyed) {
                                Core.getSoundManager().playExplosionSound();
                                this.isDestroyed = true;
                            }
                        }
                    }
                }
            }
        }
        this.bullets.removeAll(recyclable);
        BulletPool.recycle(recyclable);

        // 아군 3번 함선이 궁극기 킨 경우 충돌 무시
        if (this.ship.getShipID() != 3 || !this.ship.isUltActivated()) {
            // 레이저의 경우
            for (Laser laser : this.lasers) {
                if (checkCollision(this.ship, laser) && laser.getSpriteType() == SpriteType.Laser) {
                    if (!this.ship.isDestroyed()) {
                        //아군 함선 파괴로 업데이트
                        this.ship.destroy();
                        //아군 함선의 체력을 레이저의 데미지 만큼 감소
                        this.hp =
                            (this.hp - laser.getDamage() > 0) ? this.hp - laser.getDamage() : 0;
                        this.logger.info("Hit on player ship, -" + laser.getDamage() + " Hp");
                        // 맞으면 효과음 출력
                        Core.getSoundManager().playDamageSound();
                        if (this.hp <= 0 && !this.isDestroyed) {
                            Core.getSoundManager().playExplosionSound();
                            this.isDestroyed = true;
                        }
                    }
                }
            }
        }

        // 미사일의 경우
        for (Missile missile : this.missiles) {
            // 아군 3번 함선이 궁극기 킨 경우 충돌 무시
            if (this.ship.getShipID() != 3 || !this.ship.isUltActivated()) {
                // 폭발 중이고 아직 완료되지 않은 미사일 처리
                if (missile.hasExploded() && !missile.isDestroyed()) {
                    // 아군 함선이 폭발 반경 내에 있는지 확인
                    if (isWithinExplosionRadius(this.ship, missile)) {
                        if (!this.ship.isDestroyed()) {
                            //아군 함선 파괴로 업데이트
                            this.ship.destroy();

                            // 거리 기반 데미지 계산
                            int damage = missile.calculateDamage(this.ship);

                            // 아군 체력 감소 처리
                            this.hp = (this.hp - damage > 0) ? this.hp - damage : 0;
                            this.logger.info("Missile explosion hit! -" + damage + " Hp");

                            // 맞으면 효과음 출력
                            Core.getSoundManager().playDamageSound();

                            if (this.hp <= 0 && !this.isDestroyed) {
                                // 체력이 0 이하로 떨어지면 파괴 처리
                                Core.getSoundManager().playExplosionSound();
                                this.isDestroyed = true;
                            }
                        }
                    }
                }
            }

            // 미사일이 완료되었으면 삭제
            if (missile.isDestroyed()) {
                this.missiles.remove(missile);
            }
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

    private double getBulletDirection() {
        int dx =
            (this.ship.getPositionX() + this.ship.getWidth() / 2) - (this.boss.getPositionX()
                + this.boss.getWidth() / 2);
        int dy =
            (this.ship.getPositionY() + this.ship.getHeight() / 2) - (this.boss.getPositionY()
                + this.boss.getHeight());
        // 빗변 길이 계산
        double length = Math.sqrt(dx * dx + dy * dy);
        // 코사인 값 계산 (인접변 / 빗변)
        double cosTheta = dx / length;
        // 역코사인으로 각도 계산 (라디안 값을 반환)
        double thetaRad = Math.acos(cosTheta);
        // 라디안을 각도로 변환
        double thetaDeg = Math.toDegrees(thetaRad);
        // 각도를 보정하여 180에서 뺀 값으로 반환
        return (thetaDeg) % 360;
    }

    /**
     * 주어진 엔티티가 미사일의 폭발 반경 내에 있는지 확인.
     *
     * @param entity  폭발 반경 내에 있는지 확인할 대상 엔티티 (예: 아군 함선).
     * @param missile 폭발을 발생시킨 미사일 객체.
     * @return 대상 엔티티가 폭발 반경 내에 있으면 true, 아니면 false.
     */
    private boolean isWithinExplosionRadius(Entity entity, Missile missile) {
        // 대상 엔티티의 중심 좌표와 미사일의 중심 좌표 간의 거리 계산
        double distance = Math.hypot(
            entity.getPositionX() + entity.getWidth() / 2 - missile.getPositionX(),
            entity.getPositionY() + entity.getHeight() / 2 - missile.getPositionY()
        );
        // 계산된 거리가 미사일의 폭발 반경 이내인지 확인
        return distance <= missile.getExplosionRadius();
    }

    public void createCrystal() {
        int positionX;
        int positionY;
        boolean up = this.ship.getPositionY() <= this.height - (this.height - 110) / 2;
        boolean right = this.ship.getPositionX() >= this.width / 2;

        if (up && right) {
            positionX = 40;
            positionY = this.height - 100;
        } else if (up && !right) {
            positionX = this.width - 20 - 40;
            positionY = this.height - 100;
        } else if (!up && right) {
            positionX = 40;
            positionY = SEPARATION_LINE_HEIGHT + 110;
        } else {
            positionX = this.width - 20 - 40;
            positionY = SEPARATION_LINE_HEIGHT + 110;
        }

        this.crystal = new Crystal(positionX, positionY);
    }
}
