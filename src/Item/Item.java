package Item;

import engine.DrawManager.SpriteType;

public interface Item {

    // 아이템에 대한 설명을 반환하는 추상 메소드
    String getItemDescription();
    // 아이템의 적용되는 효과에 대한 설명을 반환하는 추상 메소드
    String getItemEffectDescription();
    // 아이템의 이름을 반환하는 추상 메소드
    String getItemName();
    // 아이템의 레벨을 반환하는 추상 메소드
    int getItemLevel();
    // 해당 아이템을 소유했는지에 대한 정보 setter
    void setOwned(final boolean owned);
    // 해당 아이템을 소유했는지에 대한 정보 getter
    boolean isOwned();
    // 아이템의 SpriteType을 반환하는 추상 메소드
    SpriteType getSpriteType();
    // 아이템의 스프라이트를 설정하는 메소드
    void setSpriteType(SpriteType spriteType);
    // 아이템의 효과를 적용시키는 메소드
    void activateItem();
    // 아이템의 최대 레벨에 도달했는지에 대한 메소드
    boolean isMaxLevel();
    // 아이템의 레벨을 증가시키는 메소드
    void increaseLevel();
}
