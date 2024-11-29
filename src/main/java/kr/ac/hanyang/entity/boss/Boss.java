package kr.ac.hanyang.entity.boss;

import java.awt.Color;
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

    /** 각 페이즈 별 공격 쿨다운 */
    private static final int ATTACK_COOLDOWN_1 = 1000;
    private static final int ATTACK_COOLDOWN_2 = 800;
    private static final int ATTACK_COOLDOWN_3 = 600;

    /** 각 페이즈 별 최대 체력 */
    private static final int PHASE_1_HP = 300;
    private static final int PHASE_2_HP = 500;
    private static final int PHASE_3_HP = 700;

    private static final Color[] PHASE_1_COLOR = {new Color(255, 255, 0), Color.WHITE};
    private static final Color[] PHASE_2_COLOR = {new Color(255, 165, 0), Color.WHITE};
    private static final Color[] PHASE_3_COLOR = {new Color(255, 0, 0), Color.WHITE};

    private int maxHp;
    private int currentHp;
    private int phase;

    private Cooldown attackCooldown;
    private Cooldown attackPhaseOne;
    private Logger logger;

    private Set<Bullet> bullets;
    /**
     * Constructor for Boss entity. Initializes the Boss with the first phase and sets the initial
     * health.
     *
     * @param positionX Initial X position.
     * @param positionY Initial Y position.
     */
    public Boss(int positionX, int positionY) {
        super(positionX, positionY, 40 * 2, 46 * 2, PHASE_1_COLOR, Direction.DOWN);

        this.maxHp = PHASE_1_HP;
        this.currentHp = maxHp;
        this.phase = 1;
        this.spriteType = SpriteType.Boss;
        this.attackCooldown = Core.getCooldown(ATTACK_COOLDOWN_1);
        this.attackPhaseOne = Core.getCooldown(1000);
        this.logger = Core.getLogger();
    }

    // 이 부분을 BossScreen에서 구현해야 할 것 같습니다.
    public void attack() {
        if (attackCooldown.checkFinished()) {
            switch (phase) {
                case 1:
                    //attackPhaseOne();
                    break;
                case 2:
                    //attackPhaseOne();
                    attackPhase2();
                    break;
                case 3:
                    //attackPhaseOne();
                    attackPhase2();
                    attackPhase3();
                    break;
            }
            attackCooldown.reset();
        }
    }

    public boolean attackPhaseOne(final Set<Bullet> bullets, final double direction) {
        // Phase 1 attack logic (부채꼴 모양의 총알 발사)
        if (this.attackPhaseOne.checkFinished()) {
            this.attackPhaseOne.reset();
            // 부채꼴 중심 각도와 각도 간격
            double centerAngle = direction; // 중심 각도 (90도 = 아래쪽)
            double angleSpread = 45; // 부채꼴 범위
            int bulletCount = 4;     // 발사할 총알 개수

            // 각도 계산 후 총알 발사
            for (int i = 0; i < bulletCount; i++) {
                double angle =
                    centerAngle - (angleSpread / 2) + (i * (angleSpread / (bulletCount - 1)));
                bullets.add(BulletPool.getBossBullet(positionX, positionY, 5, 10, angle));
                this.logger.info("Bullet fired at angle: " + angle);
            }
            return true;
        } else {
            return false;
        }
    }

    private void attackPhase2() {
        // Phase 2 attack logic (missile launch)
    }

    private void attackPhase3() {
        // Phase 3 attack logic (laser path and shot)
    }

    public void takeDamage(int damage) {
        this.currentHp -= damage;
        if (this.currentHp <= 0) {
            this.currentHp = 0;
        }
    }

    public void checkPhase() {
        if (currentHp <= 0 && phase == 1) {
            phase = 2;
            maxHp = PHASE_2_HP;
            currentHp = maxHp;
            attackCooldown = Core.getCooldown(ATTACK_COOLDOWN_2);
            setColor(PHASE_2_COLOR);
        } else if (currentHp <= 0 && phase == 2) {
            phase = 3;
            maxHp = PHASE_3_HP;
            currentHp = maxHp;
            attackCooldown = Core.getCooldown(ATTACK_COOLDOWN_3);
            setColor(PHASE_3_COLOR);
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
        this.currentHp -= value;
        this.logger.info("Boss get damaged! : -" + value + "Hp");
    }

    public Color getHpColor() {
        Color hpColor = (phase == 1) ? Color.CYAN : ((phase == 2) ? Color.PINK : Color.RED);
        return hpColor;
    }

    public Color getPreviousHpColor() {
        Color hpColor = null;
        if (phase == 1) {hpColor = Color.PINK;}
        else if (phase == 2) {hpColor = Color.RED;}
        else if (phase == 3) {hpColor = null;}
        return hpColor;
    }
}
