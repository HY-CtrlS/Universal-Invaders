package screen;

import engine.Cooldown;
import java.awt.event.KeyEvent;
import engine.Core;

/**
 * 설정 화면을 구현하여 플레이어가 볼륨 등의 설정을 조정할 수 있도록 합니다.
 */
public class SettingScreen extends Screen {

    /** 메뉴 선택 간 시간 간격 (밀리초). */
    private static final int SELECTION_TIME = 200;
    /** 볼륨 조정 간 시간 간격 (밀리초). */
    private static final int ADJUST_TIME = 100;

    /** 사용자 선택 변경 시간 간격 관리용. */
    private Cooldown selectionCooldown;
    /** 볼륨 조정 시 간격 관리용. */
    private Cooldown adjustCooldown;
    /** 현재 선택된 메뉴 항목 (0: BGM, 1: SFX). */
    private int selectionCode;

    /**
     * 생성자, 화면 속성 설정.
     *
     * @param width  화면 너비
     * @param height 화면 높이
     * @param fps    초당 프레임 수
     */
    public SettingScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
        this.returnCode = 1;
        this.selectionCode = 0;
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.adjustCooldown = Core.getCooldown(ADJUST_TIME);
        this.selectionCooldown.reset();
        this.adjustCooldown.reset();
    }

    /**
     * 설정 화면을 실행합니다.
     *
     * @return 다음 화면 코드
     */
    public final int run() {
        super.run();
        return this.returnCode;
    }

    /**
     * 화면 요소를 업데이트하고 사용자 입력 이벤트를 처리합니다.
     */
    protected final void update() {
        super.update();

        draw();
        if (this.selectionCooldown.checkFinished() && this.inputDelay.checkFinished()) {
            handleInput();
        }
    }

    /**
     * 사용자 입력을 처리하여 메뉴 이동 및 볼륨 조정을 수행합니다.
     */
    private void handleInput() {
        // 메뉴 이동
        if (inputManager.isKeyDown(KeyEvent.VK_UP) || inputManager.isKeyDown(KeyEvent.VK_W)) {
            previousMenuItem();
            Core.getSoundManager().playSoundEffect("/Users/suhynnoh/IdeaProjects/Universal-Invaders/res/sounds/button.wav");
            this.selectionCooldown.reset();
        }
        if (inputManager.isKeyDown(KeyEvent.VK_DOWN) || inputManager.isKeyDown(KeyEvent.VK_S)) {
            nextMenuItem();
            Core.getSoundManager().playSoundEffect("/Users/suhynnoh/IdeaProjects/Universal-Invaders/res/sounds/button.wav");
            this.selectionCooldown.reset();
        }

        // 볼륨 조정 (좌우 키 사용 시)
        if (adjustCooldown.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_LEFT) || inputManager.isKeyDown(KeyEvent.VK_A)) {
                adjustVolumeDown();
                Core.getSoundManager().playSoundEffect("/Users/suhynnoh/IdeaProjects/Universal-Invaders/res/sounds/button.wav");
                this.adjustCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) || inputManager.isKeyDown(KeyEvent.VK_D)) {
                adjustVolumeUp();
                Core.getSoundManager().playSoundEffect("/Users/suhynnoh/IdeaProjects/Universal-Invaders/res/sounds/button.wav");
                this.adjustCooldown.reset();
            }
        }

        // 설정 화면 종료
        if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
            Core.getSoundManager().playSoundEffect("/Users/suhynnoh/IdeaProjects/Universal-Invaders/res/sounds/button.wav");
            this.isRunning = false;
        }
    }

    /**
     * 선택된 메뉴 항목의 볼륨을 증가시킵니다.
     */
    private void adjustVolumeUp() {
        if (selectionCode == 0) {
            Core.getSoundManager().BGMUp();
        } else if (selectionCode == 1) {
            Core.getSoundManager().SFXUp();
        }
    }

    /**
     * 선택된 메뉴 항목의 볼륨을 감소시킵니다.
     */
    private void adjustVolumeDown() {
        if (selectionCode == 0) {
            Core.getSoundManager().BGMDown();
        } else if (selectionCode == 1) {
            Core.getSoundManager().SFXDown();
        }
    }

    /**
     * 다음 메뉴 항목으로 선택을 이동합니다.
     */
    private void nextMenuItem() {
        selectionCode = (selectionCode + 1) % 2;
    }

    /**
     * 이전 메뉴 항목으로 선택을 이동합니다.
     */
    private void previousMenuItem() {
        selectionCode = (selectionCode == 0) ? 1 : 0;
    }

    /**
     * 설정 화면의 구성 요소를 그립니다.
     */
    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawSettingsMenu(this, selectionCode);
        drawManager.completeDrawing(this);
    }
}