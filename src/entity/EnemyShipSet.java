package entity;

import java.util.*;

import screen.Screen;
import engine.Cooldown;
import engine.Core;
import engine.DrawManager;
import engine.DrawManager.SpriteType;
import engine.GameSettings;
import java.util.logging.Logger;

public class EnemyShipSet {

    // 생성된 적들을 관리하기 위한 Set
    private Set<EnemyShip> enemies;
    // 적 생성 쿨타임
    private Cooldown spawnCooldown;
    // DrawManager 인스턴스
    private DrawManager drawManager;
    // 게임 화면 정보
    private Screen screen;
    // 적 랜덤 생성을 위한 랜덤 객체
    private Random random;
    // 아군 함선 참조를 위한 변수
    private Ship ship;
    // 적 함선의 이동속도
    private int movementSpeed;
    // 적 함선의 X 방향 속도
    private double X_SPEED = 1.0;
    // 적 함선의 Y 방향 속도
    private double Y_SPEED = 1.0;
    // 적 함선 끼리의 최소 거리
    private final int MIN_DISTANCE = 5;
    // 로그 출력기
    private Logger logger;
    // 적 수 카운터
    private int enemyCounter;

    /**
     * 생성자 - 기본 set 초기화 및 스폰 준비
     */
    public EnemyShipSet(GameSettings gameSettings, Ship ship) {
        this.enemies = new HashSet<>();
        this.spawnCooldown = Core.getCooldown(gameSettings.getEnemySpawnInterval());
        this.drawManager = Core.getDrawManager();
        this.random = new Random();
        this.ship = ship;
        this.movementSpeed = gameSettings.getBaseSpeed();
        this.logger = Core.getLogger();
        this.enemyCounter = 0;
    }

    /**
     * 화면에 적 생성 후 이동하는 것 업데이트
     */
    public void update() {
        // 스폰 쿨타임이 다 돌았으면 생성
        if (this.spawnCooldown.checkFinished()) {
            this.spawnCooldown.reset();
            spawnEnemy();
            enemyCounter++;
            this.logger.info(enemyCounter + " Enemy Created!");
        }
        cleanup();

        double movement_X;
        double movement_Y;
        int deltaX;
        int deltaY;
        double distance;

        // 각 적 객체에 대해 업데이트
        for (EnemyShip enemy : enemies) {
            // 각 축방향 이동량 0으로 초기화
            enemy.update();
            // X거리와 Y거리 측정
            deltaX = ship.getPositionX() - enemy.getPositionX();
            deltaY = ship.getPositionY() - enemy.getPositionY();
            //플레이어와의 거리 계산
            distance = Math.hypot(deltaX, deltaY);
            // 거리가 0이 아닐때만 플레이어를 향해 이동
            if (distance != 0.0) {
                // X축과 Y축의 거리에 따른 비율을 이용하여 이동량 설정
                movement_X = X_speed * (deltaX / distance);
                movement_Y = Y_speed * (deltaY / distance);
                enemy.move(movement_X, movement_Y);
            }
        }
    }


    /**
     * 적을 생성해주는 메소드
     */
    private void spawnEnemy() {
        int spawnX, spawnY;
        int minDistance = 350; // 플레이어와의 최소 거리 우선 100으로 설정

        // 플레이어로부터 일정 거리 떨어진 위치에서만 생성되도록 설정
        do {
            spawnX = random.nextInt(screen.getWidth());
            spawnY = random.nextInt(screen.getHeight());
        } while (Math.hypot(spawnX - ship.getPositionX(), spawnY - ship.getPositionY()) < minDistance);

        // 적 생성
        EnemyShip newEnemy = new EnemyShip(spawnX, spawnY, SpriteType.EnemyShipA1);
        // 생성된 적 객체를 Set에 추가
        enemies.add(newEnemy);
    }

    /**
     * 생성된 적들을 draw하는 메소드
     */
    public void draw() {
        for (EnemyShip enemy : enemies) {
            drawManager.drawEntity(enemy, enemy.positionX, enemy.positionY);
        }
    }

    public final void attach(final Screen newscreen) {
        screen = newscreen;
    }

    public Set<EnemyShip> getEnemies() {
        return enemies;
    }

    public void destroy(EnemyShip enemyShip) {
        for (EnemyShip enemy : enemies) {
            if (enemy.equals(enemyShip)) {
                enemy.destroy();
            }
        }
    }

    public void cleanup() {
        //제거할 적 개체를 저장
        Set <EnemyShip> toRemove = new HashSet<>();

        for (EnemyShip enemy : enemies) {
            if (enemy.isDestroyed()) {
                toRemove.add(enemy);
            }
        }
        enemies.removeAll(toRemove);
    }
    //현재 화면 상에 생성되어 있는 적의 수를 반환합니다.
    public int getEnemyCount() { return enemies.size(); }

}
