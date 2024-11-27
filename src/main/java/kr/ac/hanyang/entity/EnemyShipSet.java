package kr.ac.hanyang.entity;

import java.util.*;

import kr.ac.hanyang.screen.Screen;
import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager;
import kr.ac.hanyang.engine.DrawManager.SpriteType;
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

    // 로그 출력기
    private Logger logger;
    // 적 수 카운터
    private int enemyCounter;

    // 게임 진행 시간에 대한 정보를 위한 변수
    private boolean isLevelStarted;
    private int survivalTime;
    private Cooldown clockCooldown;

    // 게임 패턴 카운터
    private int waveOneCount = 0;
    // 게임 패턴 지속시간에 대한 변수
    private Cooldown waveOneCooldown;

    private int enemySpawnInterval;
    /**
     * 생성자 - 기본 set 초기화 및 스폰 준비
     */
    public EnemyShipSet(final int enemySpawnInterval, Ship ship) {
        this.enemies = new HashSet<>();
        this.drawManager = Core.getDrawManager();
        this.random = new Random();
        this.ship = ship;
        this.logger = Core.getLogger();
        this.enemyCounter = 0;
        this.enemySpawnInterval = enemySpawnInterval;
        this.spawnCooldown = Core.getCooldown(this.enemySpawnInterval);

        // 게임 진행 시간 정보 0초로 초기화
        this.survivalTime = 0;
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

        // 첫 번째 웨이브 생성
        if (this.survivalTime == 250 && waveOneCount == 0) {
            spawnWaveOne(250, 5000);
            waveOneCount++;
        }

        // 적의 기본적인 이동방식 계산
        double movement_X;
        double movement_Y;
        int deltaX;
        int deltaY;
        double distance;

        // 각 적 객체에 대해 업데이트
        for (EnemyShip enemy : enemies) {
            // 죽지 않고 살아있는 적에 대해서만 적용
            if (!enemy.isDestroyed()) {
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
                    movement_X = enemy.getXSpeed() * (deltaX / distance);
                    movement_Y = enemy.getYSpeed() * (deltaY / distance);
                    enemy.move(movement_X, movement_Y);
                }
            }
        }
    }

    /**
     * Ship2의 궁극기 발동시 적 업데이트.
     */
    public void noUpdate() {
        cleanup();
        // 각 적 객체에 대해 업데이트
        for (EnemyShip enemy : enemies) {
            // 죽지 않고 살아있는 적에 대해서만 적용
            if (!enemy.isDestroyed()) {
                // 각 축방향 이동량 0으로 초기화
                enemy.update();
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
        } while (Math.hypot(spawnX - ship.getPositionX(), spawnY - ship.getPositionY())
            < minDistance);
        // 적 생성
        if (survivalTime < 100) {
            EnemyShip newEnemy = new EnemyShip(spawnX, spawnY, SpriteType.EnemyShipA1);
            enemies.add(newEnemy);
        } else if (survivalTime < 200) {
            int randomKey = random.nextInt(1000);
            // 10분의 1의 확률로 탱커 생성 이외의 경우는 기본 적 생성
            EnemyShip newEnemy =
                (randomKey > 900) ? new EnemyShip(spawnX, spawnY, SpriteType.EnemyShipB1)
                    : new EnemyShip(spawnX, spawnY, SpriteType.EnemyShipA1);
            enemies.add(newEnemy);
        }
        // 생존 시간이 150을 넘은 경우
        else {
            int randomKey = random.nextInt(2000);
            // 0.7의 확률로 기본 적 생성, 0.2의 확률로 탱커 생성, 0.1의 확률로 속도 빠른 적 생성.
            EnemyShip newEnemy =
                (randomKey <= 1400) ? new EnemyShip(spawnX, spawnY, SpriteType.EnemyShipA1)
                    : ((randomKey <= 1800) ? new EnemyShip(spawnX, spawnY, SpriteType.EnemyShipB1)
                        : new EnemyShip(spawnX, spawnY, SpriteType.EnemyShipC1));
            enemies.add(newEnemy);
        }
    }

    private void spawnWaveOne(final int radius, final int time) {
        // 패턴 지속 시간 설정
        waveOneCooldown = Core.getCooldown(time);

        // 아군 함선 위치 파악
        int shipX = ship.getPositionX();
        int shipY = ship.getPositionY();
        // 스프라이트 생성 위치
        int spriteX;
        int spriteY;

        double theta; // 라디안

        // 원의 방정식을 사용해 함선의 위치가 x와 y일때, 반지름이 r인 원으로 그리기 위해서 x+rcos(theta), y+rsin(theta) 임을 이용
        for (theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 18.0) {
            // 원에 맞는 스프라이트 생성 위치 계산
            spriteX = shipX + (int)(radius*Math.cos(theta));
            spriteY = shipY + (int)(radius*Math.sin(theta));

            // 위치에 장애물 생성 후 추가
            EnemyShip newObstacle = new EnemyShip(spriteX, spriteY, SpriteType.Obstacle);
            enemies.add(newObstacle);
        }
        // 패턴 시작
        waveOneCooldown.reset();
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

    public void damage_Enemy(EnemyShip enemyShip, int damage) {
        for (EnemyShip enemy : enemies) {
            if (enemy.equals(enemyShip)) {
                enemy.decreaseHp(damage);
                logger.info("-1 enemy's hp");
            }
        }
    }

    public void cleanup() {
        //제거할 적 개체를 저장
        Set<EnemyShip> toRemove = new HashSet<>();

        for (EnemyShip enemy : enemies) {
            // 파괴된 적 객체를 제거
            if (enemy.isDestroyed() && enemy.isFinishedCleanCooldown()) {
                toRemove.add(enemy);
            }
            // 패턴이 끝난 장애물 객체를 제거
            if (enemy.getSpriteType() == SpriteType.Obstacle && waveOneCooldown.checkFinished()) {
                toRemove.add(enemy);
            }
        }
        enemies.removeAll(toRemove);
    }


    // 게임이 시작했음을 알립니다.
    public void setLevelStarted(boolean isLevelStarted) {
        this.isLevelStarted = isLevelStarted;
    }

    // 스폰 주기 삭제
    public void decreaseSpawnInterval(int amount) {
        int check = this.enemySpawnInterval - amount;
        this.spawnCooldown = (check > 500) ? Core.getCooldown(check) : spawnCooldown;
    }

    public void updateTime() {
        this.survivalTime++;
        this.logger.info("Time : " + survivalTime);
    }
}
