package kr.ac.hanyang.screen;

import static kr.ac.hanyang.engine.Core.getStatusManager;

import kr.ac.hanyang.Item.Item;
import kr.ac.hanyang.Item.ItemList;

import kr.ac.hanyang.engine.DrawManager;
import kr.ac.hanyang.engine.ShipStatus;
import kr.ac.hanyang.engine.StatusManager;

import kr.ac.hanyang.entity.Entity.Direction;
import kr.ac.hanyang.entity.Ship;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.GameSettings;
import kr.ac.hanyang.engine.GameState;
import kr.ac.hanyang.entity.*;


/**
 * Implements the game screen, where the action happens.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class GameScreen extends Screen {

    /** Milliseconds until the screen accepts user input. */
    private static final int INPUT_DELAY = 6000;
    /** 경험치 바의 높이 */
    public static final int EXPERIENCE_BAR_HEIGHT = 40;
    /** Minimum time between bonus ship's appearances. */
    private static final int BONUS_SHIP_INTERVAL = 20000;
    /** Maximum variance in the time between bonus ship's appearances. */
    private static final int BONUS_SHIP_VARIANCE = 10000;
    /** Time until bonus ship explosion disappears. */
    private static final int BONUS_SHIP_EXPLOSION = 500;
    /** Time from finishing the level to screen change. */
    private static final int SCREEN_CHANGE_INTERVAL = 1500;
    /** Height of the interface separation line. */
    private static final int SEPARATION_LINE_HEIGHT = 40;
    /** 아이템 선택 화면으로 넘어가는 경험치 기준 양 */
    private static final int EXPERIENCE_THRESHOLD = 100;

    /** Current game difficulty settings. */
    private GameSettings gameSettings;
    /** Current difficulty level number. */
    private int level;
    /** Formation of enemy ships. */
    private EnemyShipSet enemyShipSet;
    /** 적을 갖고 있는 set */
    private Set<EnemyShip> enemis;
    /** Player's ship. */
    private Ship ship;
    /** Bonus enemy ship that appears sometimes. */
    private EnemyShip enemyShipSpecial;
    /** Minimum time between bonus ship appearances. */
    private Cooldown enemyShipSpecialCooldown;
    /** Time until bonus ship explosion disappears. */
    private Cooldown enemyShipSpecialExplosionCooldown;
    /** Time from finishing the level to screen change. */
    private Cooldown screenFinishedCooldown;
    /** Set of all bullets fired by on screen ships. */
    private Set<Bullet> bullets;
    /** 화면에 존재하는 경험치들의 집합 */
    private Set<Experience> experiences;
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
    /** Total ships destroyed by the player. */
    private int shipsDestroyed;
    /** Moment the game starts. */
    private long gameStartTime;
    /** Checks if the level is finished. */
    private boolean levelFinished;
    /** level 이 시작 되었는지 여부 */
    private boolean levelStarted;
    /** 1초를 새는 Cooldown */
    private Cooldown clockCooldown;
    /** 함선이 완전히 파괴되었는지 여부 */
    private boolean isDestroyed = false;
    /** 현재 함선의 status **/
    private StatusManager status;
    /** 현재까지 획득한 경험치 */
    private int currentExperience = 0;
    /** 플레이어의 현재 레벨 */
    private int playerLevel = 1;


    /** Total survival time in milliseconds. */
    private int survivalTime;

    private int shipID;

    // 아이템 리스트 객체 생성
    private static ItemList items = new ItemList();
    // 아이템 리스트 참조하기 위한 배열
    private static List<Item> itemList;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param gameState    Current game state.
     * @param gameSettings Current game settings.
     * @param width        Screen width.
     * @param height       Screen height.
     * @param fps          Frames per second, frame rate at which the game is run.
     */
    public GameScreen(final GameState gameState,
        final GameSettings gameSettings,
        final int width, final int height, final int fps, final int shipID) {
        super(width, height, fps);

        this.gameSettings = gameSettings;
        this.shipID = shipID;
        this.level = gameState.getLevel();

        this.hp = gameState.getHp();
        this.bulletsShot = gameState.getBulletsShot();
        this.shipsDestroyed = gameState.getShipsDestroyed();

        // 배경음악 중지 후 인게임 배경음악 재생
        if (Core.getSoundManager().isBackgroundMusicPlaying()) {
            Core.getSoundManager().stopBackgroundMusic();
        }
        Core.getSoundManager().playInGameBGM();

        this.returnCode = 1;

        // 현재 게임에 사용되는 Ship의 status 정보
        this.status = getStatusManager();
    }

    /**
     * Initializes basic screen properties, and adds necessary elements.
     */
    public final void initialize() {
        super.initialize();

        this.ship = Ship.createShipByID(this.shipID, this.width / 2, this.height / 2);
        enemyShipSet = new EnemyShipSet(this.gameSettings, this.level, this.ship);
        enemyShipSet.attach(this);

        this.enemis = enemyShipSet.getEnemies();
        // Appears each 10-30 seconds.
        this.enemyShipSpecialCooldown = Core.getVariableCooldown(
            BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
        this.enemyShipSpecialCooldown.reset();
        this.enemyShipSpecialExplosionCooldown = Core
            .getCooldown(BONUS_SHIP_EXPLOSION);
        this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
        this.bullets = new HashSet<Bullet>();
        this.experiences = new HashSet<Experience>(); // 경험치 집합 초기화

        // 게임 시작 시 StatusManager의 status 객체를 res/status 의 값으로 초기화
        getStatusManager().resetDefaultStatus();

        // 게임 시작 시 초기 아이템 리스트 생성
        itemList = items.initializedItems();
        // 게임 시작 시 함선의 체력을 기본으로 초기화
        this.hp = (getStatusManager().getMaxHp());
        items.initializedItems();

        // Special input delay / countdown.
        this.gameStartTime = System.currentTimeMillis();
        this.inputDelay = Core.getCooldown(INPUT_DELAY);
        this.inputDelay.reset();

        // GameScreen 이 시작될 땐 카운트 다운이 시작되므로
        this.levelStarted = false;
        this.survivalTime = 0;
        this.clockCooldown = Core.getCooldown(1000);
        this.clockCooldown.reset();

        // HP 리젠 쿨타임 생성 및 시작
        this.regenHpCooldown = Core.getCooldown(1000);
        this.regenHpCooldown.reset();
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public final int run() {
        super.run();

        this.logger.info("Screen cleared with a survival time " + this.survivalTime);

        return this.returnCode;
    }

    /**
     * Updates the elem ents on screen and checks for events.
     */
    protected final void update() {
        super.update();

        // level 이 처음 시작될 때 clockCooldown, hpRegenCooldown reset
        if (this.inputDelay.checkFinished() && !this.levelStarted) {
            this.clockCooldown.reset();
            this.levelStarted = true;
        }

        if (this.inputDelay.checkFinished() && !this.levelFinished) {

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
                > this.height - 1 - EXPERIENCE_BAR_HEIGHT;

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

            // esc키를 눌렀을 때 일시정지 화면으로 전환
            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                this.logger.info("Starting " + this.getWidth() + "x" + this.getHeight()
                    + " pause screen at " + this.fps + " fps.");
                Screen pause = new PauseScreen(this.getWidth(), this.getHeight(), this.fps);
                int check = pause.run();
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

            // hp 자동 재생 기능 실행
            hpRegen(status.getRegenHp());

            this.ship.update();
            this.enemyShipSet.update();
            ExperiencePool.update(this.experiences);
            // 1초마다 생존 시간 1씩 증가
            if (this.clockCooldown.checkFinished()) {
                this.survivalTime += 1;
                this.clockCooldown.reset();
            }

        }

        // Quit시에(!isRunning) GameScreen 그려지지 않도록 함
        if (isRunning) {
            manageCollisions();
            cleanBullets();
            draw();
        }

        // 체력이 0 이하로 내려가면 게임 종료
        if ((this.hp <= 0) && !this.levelFinished) {
            this.levelFinished = true;
            this.screenFinishedCooldown.reset();
        }

        if (this.levelFinished && this.screenFinishedCooldown.checkFinished()) {
            this.isRunning = false;
        }


    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawEntity(this.ship, this.ship.getPositionX(),
            this.ship.getPositionY());
        if (this.enemyShipSpecial != null) {
            drawManager.drawEntity(this.enemyShipSpecial,
                this.enemyShipSpecial.getPositionX(),
                this.enemyShipSpecial.getPositionY());
        }

        for (Bullet bullet : this.bullets) {
            drawManager.drawEntity(bullet, bullet.getPositionX(),
                bullet.getPositionY());
        }

        // 경험치 그리기
        for (Experience experience : this.experiences) {
            drawManager.drawEntity(experience, experience.getPositionX(),
                experience.getPositionY());
        }

        enemyShipSet.draw();

        // Interface.
        drawManager.drawLives(this, this.hp);
        drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);
        drawManager.drawLevel(this, this.playerLevel); // 현재 레벨 그리기
        drawManager.drawHorizontalLine(this, this.height - EXPERIENCE_BAR_HEIGHT - 1);
        drawManager.drawExperienceBar(this, this.currentExperience,
            EXPERIENCE_THRESHOLD, EXPERIENCE_BAR_HEIGHT); // 경험치 바 그리기

        // Countdown to game start.
        if (!this.inputDelay.checkFinished()) {
            int countdown = (int) ((INPUT_DELAY
                - (System.currentTimeMillis()
                - this.gameStartTime)) / 1000);
            drawManager.drawCountDown(this, this.level, countdown);
        }

        // 현재 levelTime 그리기
        drawManager.drawSurvivalTime(this, survivalTime);

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

            // 적 총알인 경우 실행되는 부분 ( 현재는 적 총알이 나오는 곳이 없음 )

            if (bullet.getClassify() == 0) {
                if (checkCollision(bullet, this.ship) && !this.levelFinished) {
                    if (!this.ship.isDestroyed()) {
                        recyclable.add(bullet);
                        this.ship.destroy();

                        this.hp--;
                        this.logger.info("Hit on player ship, " + this.hp
                            + " reamining.");
                    }
                }
            } else { //아군 총알인 경우 실행되는 부분

                for (EnemyShip enemyShip : enemis) {
                    if (!enemyShip.isDestroyed()
                        && checkCollision(bullet, enemyShip)) {
                        this.shipsDestroyed++;

                        this.enemyShipSet.damage_Enemy(enemyShip, bullet.getDamage());

                        // 관통 여부 확인
                        if (!bullet.getisPiercing()) {
                            recyclable.add(bullet); // 관통 아닌 경우 제거
                        }
                        Core.getSoundManager().playBulletHitSound();

                        // 적 함선이 파괴되었을 때 경험치 생성
                        if (enemyShip.isDestroyed()) {
                            this.experiences.add(
                                ExperiencePool.getExperience(enemyShip.getPositionX() + 3 * 2,
                                    // enemyShip의 너비는 13, 경험치의 너비는 7이므로 3을 더해줌
                                    enemyShip.getPositionY(), enemyShip.getPointValue()));
                        }

                    }
                }
                if (this.enemyShipSpecial != null
                    && !this.enemyShipSpecial.isDestroyed()
                    && checkCollision(bullet, this.enemyShipSpecial)) {
                    this.shipsDestroyed++;
                    this.enemyShipSpecial.destroy();
                    this.enemyShipSpecialExplosionCooldown.reset();
                    recyclable.add(bullet);
                }

            }
        }
        this.bullets.removeAll(recyclable);
        BulletPool.recycle(recyclable);
        // 적과 아군 함선의 충돌 체크
        for (EnemyShip enemyShip : enemis) {
            if (checkCollision(this.ship, enemyShip)) {
                if (!this.ship.isDestroyed() && !enemyShip.isDestroyed() && !levelFinished) {
                    //this.enemyShipSet.damage_Enemy(enemyShip, this.ship.getBaseDamage());
                    this.ship.destroy();
                    this.hp = (this.hp - 5 > 0) ? this.hp - 5 : 0;
                    this.logger.info("Hit on player ship, -5 HP");
                    Core.getSoundManager().playDamageSound();
                    if (this.hp <= 0 && !this.isDestroyed) {
                        Core.getSoundManager().playExplosionSound();
                        this.isDestroyed = true;
                    }
                }
            }
        }

        // 아군 함선과 경험치 객체의 충돌 처리
        Set<Experience> collectedExperiences = new HashSet<>();
        for (Experience experience : this.experiences) {
            if (checkCollision(this.ship, experience)) {
                collectedExperiences.add(experience);

                this.currentExperience += experience.getValue(); // 획득한 경험치 누적
                this.logger.info("획득한 경험치: " + experience.getValue() + " EXP");
                Core.getSoundManager().playExpCollectSound();

                // 임계점 도달 시 레벨 증가
                while (currentExperience >= EXPERIENCE_THRESHOLD) {
                    playerLevel++;
                    this.logger.info("플레이어 레벨 업! 현재 레벨: " + playerLevel);
                    Core.getSoundManager().playLevelUpSound();
                    currentExperience -= EXPERIENCE_THRESHOLD;
                    // 선택한 아이템 없는 것으로 초기화
                    int selectedItem = -1;
                    this.logger.info(
                        "Starting " + this.width + "X" + this.height + " ItemSelectingScreen at "
                            + this.fps + " fps.");
                    ItemSelectedScreen currentScreen = new ItemSelectedScreen(
                        items.getSelectedItemList(), width, height, this.fps, playerLevel);
                    selectedItem = currentScreen.run();
                    this.logger.info("Closing Item Selecting Screen.");
                    // 최대 체력 증가 아이템을 선택한 경우, 현재 체력 또한 증가된 체력만큼 올려줌.
                    if (selectedItem == 1) {
                        // 가지고 있던 체력의 비율 계산
                        double portionHp =
                            (double) this.hp / (getStatusManager().getMaxHp() - itemList.get(1)
                                .getChangedValue());
                        // 늘어난 체력에 맞게 현재 체력의 비율 조정
                        this.hp = ((int) (getStatusManager().getMaxHp() * portionHp));
                    }
                }
            }
        }

        // 충돌한 경험치 제거 및 반환
        this.experiences.removeAll(collectedExperiences);
        ExperiencePool.recycle(collectedExperiences);
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
     * Returns a GameState object representing the status of the game.
     *
     * @return Current game state.
     */
    public final GameState getGameState() {
        return new GameState(this.level, this.hp,
            this.bulletsShot, this.shipsDestroyed, this.survivalTime);
    }
}