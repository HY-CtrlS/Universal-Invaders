package kr.ac.hanyang.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ItemList {

    private List<Item> items;
    Random random = new Random();

    public ItemList() {
        initializedItems();
    }

    // 아이템 리스트에 아이템 객체들 추가
    public List<Item> initializedItems() {
        // 아이템을 빈 리스트로 초기화
        items = new ArrayList<>();
        // 아이템 새로 추가시에 items에 초기 레벨로 추가하는 코드를 추가해주세요.
        // 0번 아이템
        items.add(new RangeUpItem(0));
        // 1번 아이템
        items.add(new HealthUpItem(0));
        // 2번 아이템
        items.add(new AttackSpeedUpItem(0));
        // 3번 아이템
        items.add(new BulletSpeedUpItem(0));
        // 4번 아이템
        items.add(new MoveSpeedUpItem(0));
        // 5번 아이템
        items.add(new HpRegenItem(0));
        // 6번 아이템
        items.add(new UltRegenItem(0));

        return items;
    }

    // 선택화면에 표시하기 위해 무작위로 선택된 아이템 리스트를 얻어오는 메소드
    public List<Item> getSelectedItemList() {
        // 상점에 나갈 물품 선정 전에 상점에 나갈 수 있는 최대 레벨이 아닌 물품의 개수 세기
        int numSelectingItems = 0;
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isMaxLevel()) {
                numSelectingItems++;
            }
        }
        Set<Item> tempItemList = new HashSet<>();

        // 나올 수 있는 아이템의 개수가 3개를 초과하는 경우에만 무작위 선정
        if (numSelectingItems > 3) {
            // 아이템을 중복을 허용하지 않고 3개를 선택할 때까지 반복(Set이므로 중복은 허용되지 않음)
            while (tempItemList.size() < 3) {
                // items 아이템 개수에 맞는 범위에서 인덱스 랜덤 선정
                int randomIndex = random.nextInt(items.size());
                // 뽑힌 아이템이 최대 레벨이 아닌 경우에 추가
                if (!items.get(randomIndex).isMaxLevel()) {
                    tempItemList.add(items.get(randomIndex));
                }
            }
        } else {
            // 뽑힐 수 있는 아이템이 3개 미만인 경우에는
            for (int i = 0; i < items.size(); i++) {
                if (!items.get(i).isMaxLevel()) {
                    // 최대 레벨이 아닌 아이템만 넣는다.
                    tempItemList.add(items.get(i));
                }
            }
            return new ArrayList<>(tempItemList);
        }
        return new ArrayList<>(tempItemList);
    }

    public List<Item> getItems() {
        return items;
    }
}
