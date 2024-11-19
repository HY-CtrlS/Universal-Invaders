package Item;

import engine.DrawManager.SpriteType;
import java.awt.Color;

public interface Item {

  // 아이템에 대한 설명을 반환하는 추상 메소드
  String getItemDescription();

  // 아이템의 적용되는 효과에 대한 설명을 반환하는 추상 메소드
  String getItemEffectDescription();

  // 아이템의 이름을 반환하는 추상 메소드
  String getItemName();

  // 아이템의 레벨을 반환하는 추상 메소드 (아이템의 레벨 정보는 여러곳에 사용 가능성이 높아 추가)
  int getLevel();

  // 해당 아이템을 소유했는지에 대한 정보 setter (나중에 ShipStatus에 아이템 보유 현황도 추가하는데 사용할 수 있을 듯해서 넣어둠)
  void setOwned(final boolean owned);

  // 해당 아이템을 소유했는지에 대한 정보 getter (위와 동일한 이유)
  boolean isOwned();

  // 아이템의 SpriteType을 반환하는 추상 메소드 (draw하기 위해서 필요)
  SpriteType getSpriteType();

  // 아이템의 스프라이트를 설정하는 메소드 (레벨에 따른 스프라이트 변화를 주고 싶다면 사용 가능)
  void setSpriteType(SpriteType spriteType);

  // 아이템의 효과를 적용시키는 메소드
  int activateItem();

  // 아이템의 최대 레벨에 도달했는지에 대한 메소드 (최대 레벨 확인 체크)
  boolean isMaxLevel();

  // 아이템의 레벨을 증가시키는 메소드 (아이템을 한번 쓰면 레벨을 증가시켜야 함)
  void increaseLevel();

  // 아이템의 색깔 getter (아이템 draw하는데 필요)
  Color getColor();

  // 아이템의 최대 레벨 getter (이 아이템의 최대 레벨 제한은 어디까지인지에 대한 정보)
  int getMaxLevel();
}
