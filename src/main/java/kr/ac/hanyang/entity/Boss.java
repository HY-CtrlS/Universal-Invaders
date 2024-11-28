package kr.ac.hanyang.entity;

import java.awt.Color;
import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager.SpriteType;

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

    /**
     * Constructor for Boss entity. Initializes the Boss with the first phase and sets the initial
     * health.
     *
     * @param positionX Initial X position.
     * @param positionY Initial Y position.
     */
    public Boss(int positionX, int positionY) {
        super(positionX, positionY, 54 * 2, 25 * 2, PHASE_1_COLOR, Direction.DOWN);

        this.maxHp = PHASE_1_HP;
        this.currentHp = maxHp;
        this.phase = 1;
        this.spriteType = SpriteType.Boss;
        this.attackCooldown = Core.getCooldown(ATTACK_COOLDOWN_1);
    }

    public void attack() {
        if (attackCooldown.checkFinished()) {
            switch (phase) {
                case 1:
                    attackPhase1();
                    break;
                case 2:
                    attackPhase1();
                    attackPhase2();
                    break;
                case 3:
                    attackPhase1();
                    attackPhase2();
                    attackPhase3();
                    break;
            }
            attackCooldown.reset();
        }
    }

    private void attackPhase1() {
        // Phase 1 attack logic (bullet barrage)
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
            attackCooldown = Core.getCooldown(PHASE_2_HP);
            setColor(PHASE_2_COLOR);
        } else if (currentHp <= 0 && phase == 2) {
            phase = 3;
            maxHp = PHASE_3_HP;
            currentHp = maxHp;
            attackCooldown = Core.getCooldown(PHASE_3_HP);
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
}
