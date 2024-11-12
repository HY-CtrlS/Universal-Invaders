package Item;

import engine.DrawManager.SpriteType;
import engine.Core;

import engine.StatusManager;
import java.awt.Color;
import java.util.Map;
import java.util.logging.Logger;

public class AttackSpeedUpItem implements Item {
    // 공속증가 아이템의 최대 레벨
    private static final int MAX_LEVEL = 5;
    // 공속증가 아이템의 현재 레벨
    private int level = 0;
    // 공속증가 아이템의 스프라이트 타입
    private SpriteType spriteType = SpriteType.AttackSpeedUpItem;
    // 현재 아이템을 보유중인지에 대한 변수
    private boolean isowned = false;
    // 현재 아이템이 최대 레벨인지에 대한 변수
    // Status 싱글톤 객체를 위한 변수
    private StatusManager status;
    // Logger
    private Logger logger;
    // 아이템의 색깔
    private Color color;

    // 공속증가 아이템 객체 생성자
    public AttackSpeedUpItem(final int level) {
        logger = Core.getLogger();
        this.level = level;
        status = Core.getStatusManager();
        this.color = Color.BLUE;
    }

    @Override
    public String getItemDescription() {
        return "Upgrade your weapon system.";
    }

    @Override
    public String getItemEffectDescription() {
        return "Attack Speed : + 1";
    }

    @Override
    public String getItemName() {
        return "Monkey Spanner";
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
    public void activateItem() {
        if (level < MAX_LEVEL) {
            status.setShootingInterval(status.getShootingInterval() - 100);
            this.logger.info("AttackSpeed Up!! + 1");
            increaseLevel();
            this.logger.info("AttackSpeed Level : " + getLevel());
        }
    }

    @Override
    public boolean isMaxLevel() {
        return (this.level == MAX_LEVEL);
    }

    @Override
    public void increaseLevel() {
        if (this.level < MAX_LEVEL) {this.level++;}
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }
}
