package kr.ac.hanyang.screen;

import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.GameState;
import kr.ac.hanyang.Item.*;

import java.awt.event.KeyEvent;
import java.util.List;


public class ItemSelectedScreen extends Screen {

    // 화면 표시항 아이템셋 (3개)
    List<Item> items;
    // 선택 쿨다운
    private Cooldown selectionCooldown;
    // 선택 전환 가능 쿨타임
    private static final int SELECTION_TIME = 200;
    // 현재 선택된 아이템
    private int selectedItem = 0;
    // 플레이어 레벨
    private int playerLevel;
    // 선택한 아이템에 대한 정보
    private int item_num;

    // 생성자 - 아이템셋, width, height, fps 받아옴
    public ItemSelectedScreen(List<Item> items, final int width,
        final int height, final int fps, final int playerLevel) {
        super(width, height, fps);

        //아이템 리스트 받아옴
        this.items = items;

        this.playerLevel = playerLevel;
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
    }

    // 화면 가동 메소드
    @Override
    public final int run() {
        super.run();

        return item_num;
    }
    // 화면 업데이트 메소드
    @Override
    protected final void update() {
        super.update();

        draw();
        if (this.selectionCooldown.checkFinished()
            && this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_A)
                || inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
                previousMenuItem();
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_D)
                || inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
                nextMenuItem();
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                // items가 존재하는 경우에만 적용
                if (items.size() != 0) {
                    // 해당 아이템 효과 적용
                    item_num = items.get(selectedItem).activateItem();
                    // 화면 종료
                    this.isRunning = false;
                } //items에 아이템이 없으면 스페이스바 누르면 그냥 넘어가도록 지정
                else {
                    this.isRunning = false;
                }
            }
        }
    }

    /**
     * Shifts the focus to the next menu item.
     */
    private void nextMenuItem() {
        if (items.size() == 3) {
            if (this.selectedItem == 2) {
                this.selectedItem = 0;
            } else {
                this.selectedItem++;
            }
        } else if (items.size() == 2) {
            if (this.selectedItem == 1) {
                this.selectedItem = 0;
            } else {
                this.selectedItem++;
            }
        } else {
            this.selectedItem = 0;
        }
    }

    /**
     * Shifts the focus to the previous menu item.
     */
    private void previousMenuItem() {
        if (items.size() == 3) {
            if (this.selectedItem == 0) {
                this.selectedItem = 2;
            } else {
                this.selectedItem--;
            }
        } else if (items.size() == 2) {
            if (this.selectedItem == 0) {
                this.selectedItem = 1;
            } else {
                this.selectedItem--;
            }
        } else {
            this.selectedItem = 0;
        }
    }


    // 화면 draw 메소드
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawItemSelectingTitle(this, this.playerLevel);
        drawManager.drawSelectedItem(this, items, this.selectedItem);
        drawManager.drawItems(this, items);

        drawManager.completeDrawing(this);
    }

}
