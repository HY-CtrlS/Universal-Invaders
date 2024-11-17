package screen;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.GameSettings;
import engine.GameState;
import entity.*;


/**
 * Implements the game screen, where the action happens.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class GameScreen extends Screen {

    /** Milliseconds until the screen accepts user input. */
    private static final int INPUT_DELAY = 6000;
    /** Bonus score for each life remaining at the end of the level. */
    private static final int LIFE_SCORE = 100;
    /** 함선이 체력을 자동으로 회복하는 쿨타임. 기본값은 5000 밀리세컨드로 설정됨.*/
    private Cooldown hpRegenCooldown;
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
    /** Current score. */
    private int score;
    /** 플레이어의 최대 Hp. 기본값은 100. */
    private int maxHp = 100;
    /** Player hp left. */
    private int hp;
    /** Total bullets shot by the player. */
    private int bulletsShot;
    /** Total ships destroyed by the player. */
    private int shipsDestroyed;
    /** Moment the game starts. */
    private long gameStartTime;
    /** Checks if the level is finished. */
    private boolean levelFinished;
    /** Checks if a bonus life is received. */
    private boolean bonusLife;
    /** level 경과 시간 */
    private int levelTime;
    /** level 이 시작 되었는지 여부 */
    private boolean levelStarted;
    /** 1초를 새는 Cooldown */
    private Cooldown clockCooldown;
    /** 함선이 완전히 파괴되었는지 여부 */
    private boolean isDestroyed = false;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param gameState    Current game state.
     * @param gameSettings Current game settings.
     * @param bonusLife    Checks if a bonus life is awarded this level.
     * @param width        Screen width.
     * @param height       Screen height.
     * @param fps          Frames per second, frame rate at which the game is run.
     */
    public GameScreen(final GameState gameState,
        final GameSettings gameSettings, final boolean bonusLife,
        final int width, final int height, final int fps) {
        super(width, height, fps);

        this.gameSettings = gameSettings;
        this.bonusLife = bonusLife;

        this.level = gameState.getLevel();
        this.score = gameState.getScore();

        this.hp = gameState.getHp();
        if (this.bonusLife) {
            this.hp++;

        }
        this.bulletsShot = gameState.getBulletsShot();
        this.shipsDestroyed = gameState.getShipsDestroyed();

        Core.getSoundManager().playInGameBGM();
    }

    /**
     * Initializes basic screen properties, and adds necessary elements.
     */
    public final void initialize() {
        super.initialize();

        this.ship = new Ship(this.width / 2, this.height / 2, Entity.Direction.UP);
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

        // Special input delay / countdown.
        this.gameStartTime = System.currentTimeMillis();
        this.inputDelay = Core.getCooldown(INPUT_DELAY);
        this.inputDelay.reset();

        // GameScreen 이 시작될 땐 카운트 다운이 시작되므로
        this.levelStarted = false;
        this.levelTime = 0;
        this.clockCooldown = Core.getCooldown(1000);
        this.clockCooldown.reset();

        this.hpRegenCooldown = Core.getCooldown(5000);
        this.hpRegenCooldown.reset();
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public final int run() {
        super.run();

        this.score += LIFE_SCORE * (this.hp - 20);
        this.logger.info("Screen cleared with a score of " + this.score);

        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        // level 이 처음 시작될 때 clockCooldown, hpRegenCooldown reset
        if (this.inputDelay.checkFinished() && !this.levelStarted) {
            this.clockCooldown.reset();
            this.hpRegenCooldown.reset();
            this.levelStarted = true;
        }


        if (this.inputDelay.checkFinished() && !this.levelFinished) {

            boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
                || inputManager.isKeyDown(KeyEvent.VK_D);
            boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
                || inputManager.isKeyDown(KeyEvent.VK_A);
            boolean moveUp = inputManager.isKeyDown(KeyEvent.VK_UP)
                || inputManager.isKeyDown(KeyEvent.VK_W);
            boolean moveDown = inputManager.isKeyDown(KeyEvent.VK_DOWN)
                || inputManager.isKeyDown(KeyEvent.VK_S);

            boolean isRightBorder = this.ship.getPositionX()
                + this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
            boolean isLeftBorder = this.ship.getPositionX()
                - this.ship.getSpeed() < 1;
            boolean isTopBorder = this.ship.getPositionY()
                - this.ship.getSpeed() < 1 + SEPARATION_LINE_HEIGHT;
            boolean isBottomBorder = this.ship.getPositionY()
                + this.ship.getHeight() + this.ship.getSpeed() > this.height - 1;

            if (moveRight && !isRightBorder) {
                this.ship.moveRight();
            }
            if (moveLeft && !isLeftBorder) {
                this.ship.moveLeft();
            }
            if (moveUp && !isTopBorder) {
                this.ship.moveUp();
            }
            if (moveDown && !isBottomBorder) {
                this.ship.moveDown();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                if (this.ship.shoot(this.bullets)) {
                    this.bulletsShot++;
                    Core.getSoundManager().playBulletShotSound();
                }
            }

            //if (this.enemyShipSpecial != null) {
            //	if (!this.enemyShipSpecial.isDestroyed())
            //		this.enemyShipSpecial.move(2, 0);
            //else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
            //	this.enemyShipSpecial = null;

            //}
            //if (this.enemyShipSpecial == null
            //		&& this.enemyShipSpecialCooldown.checkFinished()) {
            //	this.enemyShipSpecial = new EnemyShip();
            //	this.enemyShipSpecialCooldown.reset();
            //	this.logger.info("A special ship appears");
            //	}
            //	if (this.enemyShipSpecial != null
            //		&& this.enemyShipSpecial.getPositionX() > this.width) {
            //	this.enemyShipSpecial = null;
            //		this.logger.info("The special ship has escaped");
            //}

            // 5초마다 체력 1씩 회복
            hpRegen();

            this.ship.update();
            this.enemyShipSet.update();
            // 1초마다 levelTime 1씩 증가
            if (this.clockCooldown.checkFinished()) {
                this.levelTime += 1;
                this.clockCooldown.reset();
            }
        }

        manageCollisions();
        cleanBullets();
        draw();
        // 현재 진행된 시간이 라운드에서 정한 시간과 같으면 클리어로 판단 후 라운드 종료
        if ((levelTime == this.gameSettings.getRoundTime() || this.hp <= 0)
            && !this.levelFinished) {
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

        enemyShipSet.draw();

        for (Bullet bullet : this.bullets) {
            drawManager.drawEntity(bullet, bullet.getPositionX(),
                bullet.getPositionY());
        }

        // Interface.
        drawManager.drawScore(this, this.score);
        drawManager.drawLives(this, this.hp);
        drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);

        // Countdown to game start.
        if (!this.inputDelay.checkFinished()) {
            int countdown = (int) ((INPUT_DELAY
                - (System.currentTimeMillis()
                - this.gameStartTime)) / 1000);
            drawManager.drawCountDown(this, this.level, countdown,
                this.bonusLife);
        }

        // 현재 levelTime 그리기
        drawManager.drawTime(this, this.gameSettings.getRoundTime() - levelTime);

        drawManager.completeDrawing(this);
    }

    /**
     * Cleans bullets that go off screen.
     */
    private void cleanBullets() {
        Set<Bullet> recyclable = new HashSet<Bullet>();
        for (Bullet bullet : this.bullets) {
            bullet.update();
            if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
                || bullet.getPositionY() > this.height) {
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

            if (bullet.getClassify() == 1) {
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
                        this.score += enemyShip.getPointValue();
                        this.shipsDestroyed++;

                        this.enemyShipSet.damage_Enemy(enemyShip, bullet.getDamage());
                        recyclable.add(bullet);
                        Core.getSoundManager().playBulletHitSound();


                    }
                }
                if (this.enemyShipSpecial != null
                    && !this.enemyShipSpecial.isDestroyed()
                    && checkCollision(bullet, this.enemyShipSpecial)) {
                    this.score += this.enemyShipSpecial.getPointValue();
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
                    this.hp -= 5;
                    this.logger.info("Hit on player ship, -5 HP");
                    Core.getSoundManager().playDamageSound();
                    if (this.hp <= 0 && !this.isDestroyed) {
                        Core.getSoundManager().playExplosionSound();
                        this.isDestroyed = true;
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

    /** hpRegenCooldown이 끝날 때마다 자동으로 체력을 회복함.*/
    private void hpRegen() {
        if (this.hpRegenCooldown.checkFinished() && this.hp < this.maxHp) {
            this.hp++;
            this.hpRegenCooldown.reset();

        }
    }


    /**
     * Returns a GameState object representing the status of the game.
     *
     * @return Current game state.
     */
    public final GameState getGameState() {
        return new GameState(this.level, this.score, this.hp,
            this.bulletsShot, this.shipsDestroyed);
    }
}