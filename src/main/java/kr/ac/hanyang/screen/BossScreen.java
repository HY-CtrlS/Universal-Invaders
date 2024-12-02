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
import kr.ac.hanyang.entity.boss.AsteroidPool;
import kr.ac.hanyang.entity.boss.Crystal;
import kr.ac.hanyang.entity.boss.CrystalPool;
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
    private CrystalPool crystalPool;
    private Set<Crystal> crystals;
    private AsteroidPool asteroidPool;
    private boolean isPhase4Ready;

    //임시 쿨다운 변수
    private Cooldown bossBasicBullet;
    private int basicAttackCount;

    // 보스 페이즈 1 공격 카운터
    private int phaseOneCounter;
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
        // 아군 함선의 위치 변경 (화면 중앙 아래 부분)
        this.ship.setPositionX(this.getWidth() / 2 - this.ship.getWidth() / 2);
        this.ship.setPositionY(this.getHeight() * 3 / 4 - this.ship.getHeight() / 2);

        this.returnCode = 1;
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
        this.laserPool = new LaserPool();
        this.lasers = laserPool.getLasers();
        this.missilePool = new MissilePool(this.ship);
        this.missiles = missilePool.getMissiles();
        this.crystalPool = new CrystalPool(this.ship);
        this.crystals = crystalPool.getCrystals();
        this.asteroidPool = new AsteroidPool();

        // Special input delay / countdown.
        this.gameStartTime = System.currentTimeMillis();
        this.inputDelay = Core.getCooldown(INPUT_DELAY);
        this.inputDelay.reset();

        this.phaseStarted = false;
        this.phaseFinished = false;
        this.isPhase4Ready = false;
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

        this.createMissileCooldown = Core.getCooldown(5000);
        this.createMissileCooldown.reset();

        this.createCrystalCooldown = Core.getCooldown(25000);
        this.createCrystalCooldown.reset();

        // 페이즈 카운터 초기화
        this.phaseOneCounter = 0;
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
        /// 화면 기본 업데이트 시작
        // phase가 처음 시작될 때 clockCooldown, hpRegenCooldown reset
        if (this.inputDelay.checkFinished() && !this.phaseStarted) {
            this.clockCooldown.reset();
            this.phaseStarted = true;
        }

        if (this.inputDelay.checkFinished() && !this.phaseFinished) {
            // 보스의 공격 처리 & 궁극기 효과 적용
            if (this.ship.getShipID() == 2 && this.ship.isUltActivated()) {
                // Ship2 궁극기 활성화 여부에 따라 보스 공격 무력화 결정
            }

            // WASD - 함선 이동
            boolean moveRight, moveLeft, moveUp, moveDown;
            // 방향키 - 에임
            boolean aimRight, aimLeft, aimUp, aimDown;

            // Ship1 궁극기 활성화 여부에 따라 이동 및 발사 가능 여부 결정
            if ((this.ship.getShipID() == 1 && this.ship.isUltActivated())
                || (this.boss.getPhase() == 4 && !this.isPhase4Ready)) {
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

            boolean isRightBorder, isLeftBorder, isTopBorder, isBottomBorder;

            if (this.boss.getPhase() == 4) {
                isRightBorder =
                    this.ship.getPositionX() + this.ship.getWidth() + this.ship.getSpeed()
                        > 552 - 1;
                isLeftBorder = this.ship.getPositionX() - this.ship.getSpeed() < 156 + 1;
                isTopBorder = this.ship.getPositionY() - this.ship.getSpeed() < 323 + 1;
                isBottomBorder =
                    this.ship.getPositionY() + this.ship.getHeight() + this.ship.getSpeed()
                        > 615 - 1;
            } else {
                isRightBorder =
                    this.ship.getPositionX() + this.ship.getWidth() + this.ship.getSpeed()
                        > this.width - 1;
                isLeftBorder = this.ship.getPositionX() - this.ship.getSpeed() < 1;
                isTopBorder =
                    this.ship.getPositionY() - this.ship.getSpeed() < 1 + SEPARATION_LINE_HEIGHT;
                isBottomBorder =
                    this.ship.getPositionY() + this.ship.getHeight() + this.ship.getSpeed()
                        > this.height - 1;
            }

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

            // esc키를 눌렀을 때 일시정지 화면으로 전환
            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                this.logger.info("Starting " + this.getWidth() + "x" + this.getHeight()
                    + " pause screen at " + this.fps + " fps.");
                Screen pause = new PauseScreen(this.getWidth(), this.getHeight(), this.fps);
                if (this.ship.isUltActivated()) {
                    this.ultActivatedTime.pause();
                }
                int check = pause.run();
                if (this.ultActivatedTime.isPaused()) {
                    this.ultActivatedTime.resume();
                }
                this.logger.info("Closing pause screen.");
                // 일시정지 화면에서 quit를 누른 경우 현재 라운드 종료
                if (check == 2) {
                    this.returnCode = 0;
                    this.isRunning = false;
                }
                // 일시정지 화면에서 돌아온 후 스페이스바 키 입력을 초기화하여
                // 돌아오자마자 스페이스바가 눌린 상태로 인식되지 않도록 함
                inputManager.resetKeyState(KeyEvent.VK_SPACE);
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
            this.crystalPool.update();

            // 1초마다 생존 시간 1씩 증가
            if (this.clockCooldown.checkFinished()) {
                this.survivalTime += 1;
                this.clockCooldown.reset();
            }

            if (this.ship.isUltActivated() && this.ultActivatedTime.checkFinished()) {
                this.ship.stopUlt();
                this.ultActivatedTime.reset();
                if (this.ship.getShipID() == 1) {
                    // Ship1 궁극기로 보스의 총알과 미사일 파괴, 크리스탈 파괴, 보스에게 일정 데미지
                    destroyAllAmmo();
                    if (!this.isPhase4Ready) {
                        this.boss.getDamaged(30);
                        this.crystals.clear();
                    }
                }
            }
            /// 화면 기본 업데이트 끝

            /// 보스 관련 업데이트 시작
            // 보스가 패턴을 쓸건지 짤패턴 중인지 설정
            if (this.boss.isPattern()) {
                /// 1페이즈 -> 2페이즈 패턴
                if (this.boss.getPhase() == 2) {
                    // 보스가 다 이동을 안했다면
                    if (!this.boss.isPhaseMoveFinished()) {
                        // 보스가 화면 가운데로 이동
                        this.boss.phaseOneMove(this.getWidth() / 2 - this.boss.getWidth() / 2,
                            this.getHeight() / 2 - this.boss.getHeight() / 2);

                        int checkX =
                            (this.getWidth() / 2 - this.boss.getWidth() / 2)
                                - this.boss.getPositionX();
                        int checkY = (this.getHeight() / 2 - this.boss.getHeight() / 2)
                            - this.boss.getPositionY();
                        if (checkX < 2 && checkX > -2 && checkY < 2 && checkY > -2) {
                            // 보스가 다 이동했으면 이동한 것으로 설정
                            this.boss.setPhaseMoveFinished(true);
                        }
                    }
                    // 보스의 이동이 완료된 경우 실행되는 부분
                    else {
                        this.logger.info("" + phaseOneCounter);
                        // 보스가 첫 번째 패턴중인 상태면 실행되는 부분
                        if (this.boss.isPhaseOnePattern()) {
                            if (this.phaseOneCounter == 0 || this.phaseOneCounter == 2
                                || this.phaseOneCounter == 4) {
                                // 페이즈 카운터가 0일때 실행이 되는 부분
                                int result = this.boss.spreadBullet(this.bullets, 0, 360, 18);
                                if (result == 1) {
                                    // 공격을 시행했다면 페이즈 카운터 증가
                                    phaseOneCounter++;
                                }
                                // phaseCounter에 따른 가로 세로 총알 생성
                                if (phaseOneCounter == 5) {
                                    // 가로 세로 방향 총알 생성
                                    this.boss.createHorizontalBullets(this.bullets);
                                    this.boss.createVerticalBullets(this.bullets);
                                }
                            } else if (this.phaseOneCounter == 1 || this.phaseOneCounter == 3
                                || this.phaseOneCounter == 5) {
                                // 페이즈 카운터가 1일때 실행이 되는 부분
                                int result = this.boss.spreadBullet(this.bullets, 10, 360, 18);
                                if (result == 1) {
                                    // 공격을 시행했다면 페이즈 카운터 증가
                                    phaseOneCounter++;
                                }
                            } else {
                                // phaseOneCounter가 6 이상이 된 경우 실행되는 부분
                                if (phaseOneCounter == 6) {
                                    // 한번만 쿨타임을 설정
                                    this.boss.setShootInterval(200, 0);
                                    phaseOneCounter++;
                                }
                                if (phaseOneCounter < 50) {
                                    // 플레이어를 향해 총알을 난사
                                    phaseOneCounter += this.boss.shootBullet(this.bullets,
                                        getBulletDirection());
                                } else {
                                    // 페이즈1에서 2페이즈 패턴 종료
                                    this.boss.setPhaseOnePattern(false);
                                }
                            }
                        }
                        // 보스가 첫 번째 패턴을 끝난 직후 실행되는 부분
                        else {
                            //보스 원위치로 이동
                            int checkX =
                                (this.width / 2 - this.boss.getWidth() / 2)
                                    - this.boss.getPositionX();
                            int checkY = (SEPARATION_LINE_HEIGHT + 50)
                                - this.boss.getPositionY();

                            // 아직 원위치로 다 이동 안했으면
                            if (!(checkX < 2 && checkX > -2 && checkY < 2 && checkY > -2)) {
                                // 보스가 원위치로 이동
                                this.boss.phaseOneMove(this.width / 2 - this.boss.getWidth() / 2,
                                    SEPARATION_LINE_HEIGHT + 50);
                            } else {
                                // 원위치로 모두 이동했으면 패턴 상태 종료
                                this.boss.setPattern(false);
                                // 보스 무적 상태 해제
                                this.boss.setInvincible(false);
                                // PhaseCounter 초기화
                                this.phaseOneCounter = 0;
                                // 보스의 패턴위치 설정 메소드는 다시 false로
                                this.boss.setPhaseMoveFinished(false);
                            }
                        }
                    }
                } /// 1페이즈 -> 2페이즈 패턴 종료

                /// 2페이즈 -> 3페이즈 패턴 시작
                else if (this.boss.getPhase() == 3) {
                    // 보스가 다 이동을 안했다면
                    if (!this.boss.isPhaseMoveFinished()) {
                        // 보스가 화면 가운데로 이동
                        this.boss.phaseOneMove(this.getWidth() / 2 - this.boss.getWidth() / 2,
                            this.getHeight() / 2 - this.boss.getHeight() / 2);

                        int checkX =
                            (this.getWidth() / 2 - this.boss.getWidth() / 2)
                                - this.boss.getPositionX();
                        int checkY = (this.getHeight() / 2 - this.boss.getHeight() / 2)
                            - this.boss.getPositionY();
                        if (checkX < 2 && checkX > -2 && checkY < 2 && checkY > -2) {
                            // 보스가 다 이동했으면 이동한 것으로 설정
                            this.boss.setPhaseMoveFinished(true);
                        }
                    }
                    // 보스의 이동이 완료된 경우 실행되는 부분
                    else {
                        this.logger.info("" + phaseOneCounter);
                        // 보스가 두 번째 패턴중인 상태면 실행되는 부분
                        if (this.boss.isPhaseTwoPattern()) {
                            if (phaseOneCounter == 0) {
                                // 패턴중 기본공격 속도 설정
                                this.boss.setShootInterval(2100, 1000);
                                // 페이즈 카운터 증가
                                phaseOneCounter++;
                            }

                            //각 조건마다 격자로 총알 생성
                            if (phaseOneCounter == 7) {
                                this.boss.createHorizontalBullets(this.bullets);
                                phaseOneCounter += this.boss.createHorizontalBulletsTwo(
                                    this.bullets);
                                // 마지막 공격 발사 했으면 페이즈 카운터 증가
                            } else if (phaseOneCounter == 26) {
                                this.boss.createHorizontalBulletsTwo(this.bullets);
                                phaseOneCounter += this.boss.createVerticalBullets(this.bullets);
                                // 마지막 공격 발사 했으면 페이즈 카운터 증가
                            } else if (phaseOneCounter == 21) {
                                this.boss.createVerticalBulletsTwo(this.bullets);
                                phaseOneCounter += this.boss.createHorizontalBullets(this.bullets);
                                // 마지막 공격 발사 했으면 페이즈 카운터 증가
                            } else if (phaseOneCounter == 14) {
                                this.boss.createVerticalBullets(this.bullets);
                                phaseOneCounter += this.boss.createHorizontalBullets(this.bullets);
                                // 마지막 공격 발사 했으면 페이즈 카운터 증가
                            } else if (phaseOneCounter < 27 && phaseOneCounter % 3 == 0) {
                                // 페이즈 카운터가 3의 배수일 때, 원형 공격 발사
                                phaseOneCounter += this.boss.spreadBullet(this.bullets,
                                    phaseOneCounter, 360, random.nextInt(4) + 16);
                                // 발사했으면 페이즈 카운터 증가
                            } else {
                                // 위 경우가 아닌 경우에는 기본 공격함 0.7초에서 2.7초 간격으로 빠른 공격을 발사함
                                phaseOneCounter += this.boss.shootBullet(this.bullets,
                                    getBulletDirection());
                            }

                            if (phaseOneCounter == 27) {
                                // 페이즈 카운터가 27일 때, 공격속도 조정
                                this.boss.setBasicBulletInterval(600, 300);
                            }
                            if (phaseOneCounter < 40) {
                                // 페이즈 카운터가 40이 될때까지 원형공격 시작
                                phaseOneCounter += this.boss.spreadBullet(this.bullets,
                                    phaseOneCounter, 360, random.nextInt(12) + 8);
                            } else {
                                // 2번 큰 패턴 종료
                                this.boss.setPhaseTwoPattern(false);
                            }
                        }
                        // 보스가 두 번째 패턴을 끝난 직후 실행되는 부분
                        else {
                            //보스 원위치로 이동
                            int checkX =
                                (this.width / 2 - this.boss.getWidth() / 2)
                                    - this.boss.getPositionX();
                            int checkY = (SEPARATION_LINE_HEIGHT + 50)
                                - this.boss.getPositionY();

                            // 아직 원위치로 다 이동 안했으면
                            if (!(checkX < 2 && checkX > -2 && checkY < 2 && checkY > -2)) {
                                // 보스가 원위치로 이동
                                this.boss.phaseOneMove(this.width / 2 - this.boss.getWidth() / 2,
                                    SEPARATION_LINE_HEIGHT + 50);
                            } else {
                                // 원위치로 모두 이동했으면 패턴 상태 종료
                                this.boss.setPattern(false);
                                // 기본공격속도 정상화
                                this.boss.setBasicBulletInterval();
                                // 보스 무적 상태 해제
                                this.boss.setInvincible(false);
                                // PhaseCounter 초기화
                                this.phaseOneCounter = 0;
                                // 보스의 패턴위치 설정 메소드는 다시 false로
                                this.boss.setPhaseMoveFinished(false);
                            }
                        }
                    }
                } /// 2페이즈 -> 3페이즈 패턴 종료

                /// 3페이즈 -> 4페이즈 패턴 시작
                else if (this.boss.getPhase() == 4) {
                    // 페이즈4 전 함선이 화면 중앙으로 강제 이동되지 않은 경우
                    if (!this.isPhase4Ready) {
                        // 함선 중앙으로 이동
                        this.ship.moveCenter();
                        //보스 원위치로 이동
                        int checkX =
                            (this.width / 2 - this.boss.getWidth() / 2)
                                - this.boss.getPositionX();
                        int checkY = (SEPARATION_LINE_HEIGHT + 50)
                            - this.boss.getPositionY();

                        if (!(checkX < 2 && checkX > -2 && checkY < 2 && checkY > -2)) {
                            // 보스가 원위치로 이동
                            this.boss.phaseOneMove(this.width / 2 - this.boss.getWidth() / 2,
                                SEPARATION_LINE_HEIGHT + 50);
                        } else if (this.ship.isCenter()) {
                            // 이동 끝나면 소행성과 크리스탈 생성
                            this.crystalPool.createFinalCrystal();
                            // 레이저 생성 쿨타임 1초로 변경
                            this.createLaserCooldown = changeCooldown(1000);
                            this.createLaserCooldown.reset();
                            this.isPhase4Ready = true;
                        }
                    } else {
                        // 발악패턴 시작
                        // 레이저 발사, 크리스탈 회전
                        this.boss.setInvincible(true);
                        this.crystalPool.move();
                        if (this.createLaserCooldown.checkFinished()) {
                            int randomY = (int) (Math.random() * 262) + 323;
                            laserPool.createHorizontalLaser(randomY);
                            this.createLaserCooldown.reset();
                        }
                    }

                    if (this.boss.getCurrentHp() <= 0) {
                        // 보스 최종 클리어
                        this.phaseFinished = true;
                        this.screenFinishedCooldown.reset();
                    }
                }

            } else {
                // 보스 패턴A 발동 메소드
                if (bossBasicBullet.checkFinished()) {
                    int randomKey = random.nextInt(8) + 10;
                    double range = randomKey * 5.0;
                    int bulletNum = randomKey - (random.nextInt(4) + 5);
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
                    int randomY = (int) (Math.random() * 76) - 40;
                    laserPool.createHorizontalLaser(this.ship.getPositionY() + randomY);
                    this.createLaserCooldown.reset();
                }

                if (this.boss.getPhase() > 2) {
                    if (!this.boss.isInvincible()) {
                        if (this.createCrystalCooldown.checkFinished()) {
                            this.crystalPool.createCrystal();
                            this.boss.setInvincible(true);
                            this.createCrystalCooldown.reset();
                        }
                    } else {
                        if (this.crystalPool.isAllBroken()) {
                            this.boss.setInvincible(false);
                            this.createCrystalCooldown.reset();
                        }
                    }
                }

                if (this.boss.getPhase() > 2) {
                    this.boss.move();
                }

                // 보스 체력에 따른 페이즈 설정(맨 마지막 부분에 설정하여 페이즈가 변하면서 바로 짤패턴이 적용되는 경우를 제외_)
                if (this.boss.getCurrentHp() <= 0) {
                    // 보스 페이즈 변환
                    this.boss.changeBossPhase();
                    if (this.boss.getPhase() == 4) {
                        destroyAllAmmo();
                    }
                    // 보스 패턴 시행
                    this.boss.setPattern(true);
                    // 보스 무적
                    this.boss.setInvincible(true);
                    if (this.boss.getPhase() == 2) {
                        // 2페이즈면 첫번째 큰 패턴 시작
                        this.boss.setPhaseOnePattern(true);
                    } else if (this.boss.getPhase() == 3) {
                        // 3페이즈면 두번째 큰 패턴 시작
                        this.boss.setPhaseTwoPattern(true);
                    }
                }
            }
        }

        if (this.isRunning) {
            manageCollisions();
            cleanBullets();
            draw();
        }

        if (this.phaseFinished && this.screenFinishedCooldown.checkFinished()) {
            this.isRunning = false;
        }
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

            for (Bullet bullet : this.bullets) {
                drawManager.drawEntity(bullet, bullet.getPositionX(),
                    bullet.getPositionY());
            }

            drawManager.drawEntity(this.ship, this.ship.getPositionX(),
                this.ship.getPositionY());
            drawManager.drawEntity(this.boss, this.boss.getPositionX(), this.boss.getPositionY());

            if (this.boss.getPhase() >= 3) {
                if (!(this.boss.getPhase() == 3 && this.boss.isPattern())) {
                    this.crystalPool.draw();
                }
            }

            laserPool.draw();
            missilePool.draw();
            if (this.isPhase4Ready) {
                asteroidPool.draw();
            }

            // 보스의 체력바 그리기
            drawManager.drawBossHp(this, boss.getCurrentHp(), this.boss);
            // 아군 함선의 체력바 그리기
            drawManager.drawLives(10, this.getHeight() - 30, this.hp);
            // 아군 함선의 궁극기바 그리기
            drawManager.drawUltGauge(this.ship, this.getWidth() - 210, this.getHeight() - 30);

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
                // 보스와의 충돌
                if (checkCollision(bullet, this.boss) && this.boss.getCurrentHp() > 0) {
                    recyclable.add(bullet);
                    if (!this.boss.isInvincible()) {
                        this.boss.getDamaged(status.getBaseDamage());
                    }
                }
                // 크리스탈과의 충돌
                for (Crystal crystal : crystals) {
                    if (checkCollision(bullet, crystal) && crystal.getHp() > 0) {
                        recyclable.add(bullet);
                        crystal.getDamaged(status.getBaseDamage());
                        // 페이즈4 에서는 크리스탈 하나씩 부술때마다 보스 체력 1/4 감소
                        if (this.isPhase4Ready && crystal.isBroken()) {
                            this.boss.getDamaged(250);
                        }
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

        // 라디안 단위로 방향 계산
        double thetaRad = Math.atan2(dy, dx);

        // 라디안을 각도로 변환
        double thetaDeg = Math.toDegrees(thetaRad);

        // 각도를 0~360도 범위로 변환
        if (thetaDeg < 0) {
            thetaDeg += 360;
        }

        return thetaDeg;
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

    public Cooldown changeCooldown(int ms) {
        return Core.getCooldown(ms);
    }

    public void destroyAllAmmo() {
        this.bullets.clear();
        this.missiles.clear();
    }
}
