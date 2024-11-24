package kr.ac.hanyang.Item;

import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager.SpriteType;
import kr.ac.hanyang.engine.StatusManager;
import java.awt.Color;
import java.util.logging.Logger;

public class HealthUpItem implements Item {

    // 이속증가 아이템의 최대 레벨
    private static final int MAX_LEVEL = 5;
    // 이속증가 아이템의 현재 레벨
    private int level = 0;
    // 이속증가 아이템의 스프라이트 타입
    private SpriteType spriteType = SpriteType.AttackSpeedUpItem;
    // 현재 아이템을 보유중인지에 대한 변수
    private boolean isowned = false;
    // Status 싱글톤 객체를 위한 변수
    private StatusManager status;
    // Logger
    private Logger logger;
    // 아이템의 색깔
    private Color color;

    // 공속증가 아이템 객체 생성자
    public HealthUpItem(final int level) {
        logger = Core.getLogger();
        this.level = level;
        status = Core.getStatusManager();
        this.color = Color.PINK;
    }

    @Override
    public String getItemDescription() {
        return "Upgrade your Ship's hull.";
    }

    @Override
    public String getItemEffectDescription() {
        return "Max HP : + 10";
    }

    @Override
    public String getItemName() {
        return "Iron";
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setOwned(final boolean owned) {
        this.isowned = owned;
    }

    @Override
    public boolean isOwned() {
        return this.isowned;
    }

    @Override
    public SpriteType getSpriteType() {
        return this.spriteType;
    }

    @Override
    public void setSpriteType(SpriteType spriteType) {
        this.spriteType = spriteType;
    }

    @Override
    public int activateItem() {
        if (level < MAX_LEVEL) {
            status.setMaxHp(status.getMaxHp() + 10);
            this.logger.info("Health Up!! + 10");
            increaseLevel();
            this.logger.info("HP Level : " + getLevel());
        }
        return 1;
    }

    @Override
    public boolean isMaxLevel() {
        return (this.level == MAX_LEVEL);
    }

    @Override
    public void increaseLevel() {
        if (this.level < MAX_LEVEL) {
            this.level++;
        }
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    //방금 적용된 아이템이 최대체력을 얼마나 증가시켰는지에 대한 정보
    @Override
    public int getChangedValue() {
        // 이후에 level에 따라서도 얼마나 증가됬는지 반환 가능
        // 현재는 고정적으로 10씩 증가하기에 10 반환
        return 10;
    }
}
