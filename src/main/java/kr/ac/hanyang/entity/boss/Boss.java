package kr.ac.hanyang.entity.boss;

import java.awt.Color;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager.SpriteType;
import kr.ac.hanyang.entity.Bullet;
import kr.ac.hanyang.entity.BulletPool;
import kr.ac.hanyang.entity.Entity;

/**
 * 보스를 구현하는 클래스 보스는 페이즈에 따라 여러 패턴으로 공격
 */
public class Boss extends Entity {

    /** 각 페이즈 별 최대 체력 */
    private static final int PHASE_1_HP = 300;
    private static final int PHASE_2_HP = 500;
    private static final int PHASE_3_HP = 700;
    private static final int PHASE_4_HP = 1000;

    protected static final Color PHASE_1_HPCOLOR = new Color(255, 150, 150);
    protected static final Color PHASE_2_HPCOLOR = new Color(255, 170, 0);
    protected static final Color PHASE_3_HPCOLOR = new Color(255, 85, 0);
    protected static final Color PHASE_4_HPCOLOR = new Color(255, 0, 0);

    private static final int MIN_SPEED = 2; // 최소 이동 속도
    private static final int MAX_SPEED = 5; // 최대 이동 속도

    private int maxHp;
    private int currentHp;
    private int phase;
    private boolean isInvincible;
    private boolean isPattern;

    private Cooldown basicBulletInterval;
    private Cooldown horizontalBulletCooldown;
    private Cooldown verticalBulletCooldown;
    private Cooldown horizontalBulletTwoCooldown;
    private Cooldown verticalBulletTwoCooldown;
    private Cooldown shootInterval;

    private Logger logger;

    private int speed; // 현재 이동 속도
    private boolean movingRight; // 이동 방향 (true: 오른쪽, false: 왼쪽)
    private Random random; // 랜덤 속도 생성기

    // 1페이즈 패턴 변수
    private boolean isPhaseOnePattern;
    private boolean isPhaseMoveFinished;

    // 2페이즈 패턴 변수
    private boolean isPhaseTwoPattern;

    /**
     * Constructor for Boss entity. Initializes the Boss with the first phase and sets the initial
     * health.
     *
     * @param positionX Initial X position.
     * @param positionY Initial Y position.
     */
    public Boss(int positionX, int positionY) {
        super(positionX, positionY, 46 * 2, 40 * 2,
            new Color[]{Color.YELLOW,
                Color.GRAY,
                Color.MAGENTA});

        this.maxHp = PHASE_1_HP;
        this.currentHp = maxHp;
        this.phase = 1;
        this.spriteType = SpriteType.Boss;
        this.isInvincible = false;
        this.isPattern = false;

        // 각 패턴 발동중인지에 대한 부분 false로 초기화
        this.isPhaseOnePattern = false;
        this.isPhaseMoveFinished = false;

        //보스의 탄막 기본공격 간격
        this.basicBulletInterval = Core.getVariableCooldown(1400, 500);
        this.basicBulletInterval.reset();

        // 보스의 단일 탄막 공격 간격
        this.shootInterval = Core.getCooldown(200);
        this.shootInterval.reset();

        //보스의 가로 탄막 공격 쿨타임
        this.horizontalBulletCooldown = Core.getCooldown(1000);
        this.horizontalBulletCooldown.reset();
        this.horizontalBulletTwoCooldown = Core.getCooldown(1000);
        this.horizontalBulletTwoCooldown.reset();

        //보스의 세로 탄막 공격 쿨타임
        this.verticalBulletCooldown = Core.getCooldown(1000);
        this.verticalBulletCooldown.reset();
        this.verticalBulletTwoCooldown = Core.getCooldown(1000);
        this.verticalBulletTwoCooldown.reset();

        this.logger = Core.getLogger();

        this.random = new Random();
        this.movingRight = true; // 초기 방향은 오른쪽
        setRandomSpeed(); // 초기 속도 설정
    }

    /**
     * 총알을 지정한 파라미터에 맞게 부채꼴 모양으로 공격
     *
     * @param bullets     스크린에서 관리하는 총알 Set
     * @param direction   부채꼴 공격을 발사할 방향 (90 = 90도(아래방향))
     * @param spreadAngle 부채꼴의 폭을 설정하는 변수, 각도를 정수로 제시(120 = 120도)
     * @param bulletNum   설정한 부채꼴에서 발사할 총알의 개수
     **/
    public int spreadBullet(final Set<Bullet> bullets, final double direction,
        final double spreadAngle, final int bulletNum) {
        // Phase 1 attack logic (부채꼴 모양의 총알 발사)
        if (this.basicBulletInterval.checkFinished()) {
            this.basicBulletInterval.reset();
            // 부채꼴 중심 각도와 각도 간격
            double centerAngle = direction; // 중심 각도 (90도 = 아래쪽)
            double angleSpread = spreadAngle; // 부채꼴 범위
            int bulletCount = bulletNum;     // 발사할 총알 개수

            // 각도 계산 후 총알 발사
            for (int i = 0; i < bulletCount; i++) {
                double angle =
                    centerAngle - (angleSpread / 2) + (i * (angleSpread / (bulletCount - 1)));
                bullets.add(
                    BulletPool.getBossBullet(positionX + getWidth() / 2, positionY + getHeight(), 1,
                        10, angle));
                this.logger.info("Bullet fired at angle: " + angle);
            }
            return 1;
        } else {
            return 0;
        }
    }

    // 화면 위에 가로로 총알을 생성하는 메소드
    public int createHorizontalBullets(final Set<Bullet> bullets) {
        if (this.horizontalBulletCooldown.checkFinished()) {
            this.horizontalBulletCooldown.reset();

            for (int i = 1; i <= 15; i++) { // 1부터 시작하여 일정 간격 배치
                int bulletX = i * 50; // 각 총알의 X 좌표
                // 총알 생성
                bullets.add(BulletPool.getBossBullet(bulletX, 50, 2.5, 10, 90));
                this.logger.info("Horizontal Bullet Creation!");
            }

            return 1; // 성공적으로 발사
        } else {
            return 0; // 쿨타임 중
        }
    }

    // 화면 아래에 가로로 총알을 생성하는 메소드
    public int createHorizontalBulletsTwo(final Set<Bullet> bullets) {
        if (this.horizontalBulletTwoCooldown.checkFinished()) {
            this.horizontalBulletTwoCooldown.reset();

            for (int i = 0; i < 15; i++) { // 1부터 시작하여 일정 간격 배치
                int bulletX = i * 50 + 25; // 각 총알의 X 좌표
                // 총알 생성
                bullets.add(BulletPool.getBossBullet(bulletX, 750, 2.5, 10, 270));
                this.logger.info("Horizontal Bullet two Creation!");
            }

            return 1; // 성공적으로 발사
        } else {
            return 0; // 쿨타임 중
        }
    }

    // 화면 왼쪽에 세로로 총알을 22개 생성하는 메소드
    public int createVerticalBullets(final Set<Bullet> bullets) {
        if (this.verticalBulletCooldown.checkFinished()) {
            this.verticalBulletCooldown.reset();

            for (int i = 1; i <= 16; i++) { // 1부터 시작하여 일정 간격 배치
                int bulletY = i * 50; // 각 총알의 Y 좌표
                // 총알 생성
                bullets.add(BulletPool.getBossBullet(20, bulletY, 2, 10, 0));
                this.logger.info("Vertical Bullet Creation!");
            }

            return 1; // 성공적으로 발사
        } else {
            return 0; // 쿨타임 중
        }
    }

    // 화면 오른쪽에 세로로 총알을 22개 생성하는 메소드
    public int createVerticalBulletsTwo(final Set<Bullet> bullets) {
        if (this.verticalBulletTwoCooldown.checkFinished()) {
            this.verticalBulletTwoCooldown.reset();

            for (int i = 0; i < 16; i++) { // 1부터 시작하여 일정 간격 배치
                int bulletY = i * 50 + 25; // 각 총알의 Y 좌표
                // 총알 생성
                bullets.add(BulletPool.getBossBullet(700, bulletY, 2, 10, 180));
                this.logger.info("Vertical Bullet Creation!");
            }

            return 1; // 성공적으로 발사
        } else {
            return 0; // 쿨타임 중
        }
    }

    public int shootBullet(final Set<Bullet> bullets, final double direction) {
        if (this.shootInterval.checkFinished()) {
            this.shootInterval.reset();

            bullets.add(
                BulletPool.getBossBullet(positionX + getWidth() / 2, positionY + getHeight(), 4, 10,
                    direction));
            return 1;
        } else {
            return 0;
        }
    }

    // 1페이즈 -> 2페이즈 패턴 시작시 보스가 목적로 한 번 이동
    public void phaseOneMove(final int positionX, final int positionY) {
        // 목표 이동 지점
        // X축 방향 보스 이동 먼저 맞추기
        if (this.positionX < positionX) {
            this.positionX += 1;
        } else if (this.positionX > positionX) {
            this.positionX -= 1;
        }

        // Y축 방향 보스 이동 맞추기
        if (this.positionY < positionY) {
            this.positionY += 1;
        } else if (this.positionY > positionY) {
            this.positionY -= 1;
        }
    }


    // 보스의 좌우 이동을 구현하는 메소드
    public void move() {
        // 보스가 화면의 오른쪽 경계에 도달했는지 확인
        boolean isRightBorder = this.positionX + this.width + this.speed > 708;
        // 보스가 화면의 왼쪽 경계에 도달했는지 확인
        boolean isLeftBorder = this.positionX - this.speed < 0;

        if (movingRight && !isRightBorder) {
            this.positionX += speed; // 오른쪽으로 이동
        } else if (!movingRight && !isLeftBorder) {
            this.positionX -= speed; // 왼쪽으로 이동
        } else {
            movingRight = !movingRight; // 경계에 도달하면 방향 전환
            setRandomSpeed(); // 새로운 속도로 이동
        }

    }

    //보스가 1페이즈 큰 패턴에서 다 이동했는지에 대한 메소드
    public boolean isPhaseMoveFinished() {
        return this.isPhaseMoveFinished;
    }

    public void setPhaseMoveFinished(boolean value) {
        this.isPhaseMoveFinished = value;
    }

    public void setPhase(final int phase) {
        this.phase = phase;
        switch (phase) {
            case 2:
                this.maxHp = PHASE_2_HP;
                this.currentHp = PHASE_2_HP;
                getNextHpColor();
                break;
            case 3:
                this.maxHp = PHASE_3_HP;
                this.currentHp = PHASE_3_HP;
                getNextHpColor();
                break;
            case 4:
                this.maxHp = PHASE_4_HP;
                this.currentHp = PHASE_4_HP;
                getNextHpColor();
                break;
        }
    }

    public int getPhase() {
        return phase;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void getDamaged(final int value) {
        if (value >= 0) {
            this.currentHp -= value;
            this.logger.info("Boss get damaged! : -" + value + "Hp");
        }
    }

    public Color getHpColor() {
        if (phase == 1) {
            return PHASE_1_HPCOLOR;
        } else if (phase == 2) {
            return PHASE_2_HPCOLOR;
        } else if (phase == 3) {
            return PHASE_3_HPCOLOR;
        } else {
            return PHASE_4_HPCOLOR;
        }
    }

    public Color getNextHpColor() {
        Color hpColor = null;
        if (phase == 1) {
            hpColor = PHASE_2_HPCOLOR;
        } else if (phase == 2) {
            hpColor = PHASE_3_HPCOLOR;
        }
        return hpColor;
    }

    public final boolean isInvincible() {
        return this.isInvincible;
    }

    public final void setInvincible(final boolean invincible) {
        this.isInvincible = invincible;
    }

    public final boolean isPattern() {
        return this.isPattern;
    }

    // 1페이즈 -> 2페이즈 패턴 중인지 판단
    public final boolean isPhaseOnePattern() {
        return this.isPhaseOnePattern;
    }

    // 1페이즈 -> 2페이즈 패턴중인지를 설정
    public void setPhaseOnePattern(final boolean value) {
        this.isPhaseOnePattern = value;
    }

    public final boolean isPhaseTwoPattern() {
        return this.isPhaseTwoPattern;
    }

    public void setPhaseTwoPattern(final boolean value) {
        this.isPhaseTwoPattern = value;
    }

    public void setPattern(boolean value) {
        this.isPattern = value;
    }

    public final void changeBossPhase() {
        // 보스가 현재 1페이즈라면
        if (this.phase == 1) {
            // 2페이즈로 변경
            setPhase(2);
        }
        // 보스가 현재 2페이즈 라면
        else if (this.phase == 2) {
            // 3페이즈로 변경
            setPhase(3);
        }
        // 보스가 현재 3페이즈인 경우
        else if (this.phase == 3) {
            // 발악 패턴으로 변경
            setPhase(4);
        }
    }

    /**
     * 이동 속도를 랜덤으로 설정.
     */
    private void setRandomSpeed() {
        this.speed = random.nextInt(MAX_SPEED - MIN_SPEED + 1) + MIN_SPEED;
    }

    /**
     * 현재 이동 속도를 반환.
     *
     * @return 현재 이동 속도.
     */
    public int getSpeed() {
        return this.speed;
    }

    /**
     * 현재 이동 방향을 반환.
     *
     * @return true: 오른쪽, false: 왼쪽.
     */
    public boolean isMovingRight() {
        return this.movingRight;
    }

    // value로 고정된 단일 탄막 쿨타임으로 변경
    public void setShootInterval(int value, int variance) {
        if (variance != 0) {
            this.shootInterval = Core.getVariableCooldown(value, variance);
        } else {
            this.shootInterval = Core.getCooldown(value);
        }
        this.shootInterval.reset();
    }

    // value로 고정된 쿨타임으로 변경
    public void setBasicBulletInterval(int value, int variance) {
        this.basicBulletInterval = Core.getVariableCooldown(value, variance);
        this.basicBulletInterval.reset();
    }

    // 보스 기본공격 시간을 기존 쿨타임으로 변경
    public void setBasicBulletInterval() {
        this.basicBulletInterval = Core.getVariableCooldown(1400, 500);
        this.basicBulletInterval.reset();
    }
}
