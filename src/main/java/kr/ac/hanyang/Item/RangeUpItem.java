package kr.ac.hanyang.Item;

import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager.SpriteType;
import kr.ac.hanyang.engine.StatusManager;
import java.awt.Color;
import java.util.logging.Logger;

public class RangeUpItem implements Item {

    // 사거리 증가 아이템의 최대 레벨
    private static final int MAX_LEVEL = 5;
    // 사거리 증가 아이템의 현재 레벨
    private int level = 0;
    // 사거리 증가 아이템의 스프라이트 타입
    private SpriteType spriteType = SpriteType.AttackSpeedUpItem;
    // 현재 아이템을 보유중인지에 대한 변수
    private boolean isowned = false;
    // Status 싱글톤 객체를 위한 변수
    private StatusManager status;
    // Logger
    private Logger logger;
    // 아이템의 색깔
    private Color color;

    // 사거리 증가 아이템 객체 생성자
    public RangeUpItem(final int level) {
        logger = Core.getLogger();
        this.level = level;
        status = Core.getStatusManager();
        this.color = Color.RED;
    }

    @Override
    public String getItemDescription() {
        return "Upgrade your Weapon";
    }

    @Override
    public String getItemEffectDescription() {
        return "Range : + 1";
    }

    @Override
    public String getItemName() {
        return "Heavy Missile";
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
            status.setRange(status.getRange() + 1);
            this.logger.info("Range Up!! + 1");
            increaseLevel();
            this.logger.info("Range Level : " + getLevel());
        }
        return 0;
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

    @Override
    // 미사용
    public int getChangedValue() {
        return 0;
    }
}
