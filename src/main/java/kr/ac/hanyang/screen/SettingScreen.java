package kr.ac.hanyang.screen;

import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.SoundManager;
import java.awt.event.KeyEvent;
import kr.ac.hanyang.engine.Core;

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
    /** 사운드 관리 매니저 */
    private final SoundManager soundManager;

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
        this.soundManager = Core.getSoundManager(); // 필드로 저장
    }

    /**
     * 설정 화면을 실행합니다.
     *
     * @return 다음 화면 코드
     */
    @Override
    public final int run() {
        super.run();
        return this.returnCode;
    }

    /**
     * 화면 요소를 업데이트하고 사용자 입력 이벤트를 처리합니다.
     */
    @Override
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
            soundManager.playButtonSound(); // 필드를 통해 접근
            this.selectionCooldown.reset();
        }
        if (inputManager.isKeyDown(KeyEvent.VK_DOWN) || inputManager.isKeyDown(KeyEvent.VK_S)) {
            nextMenuItem();
            soundManager.playButtonSound(); // 필드를 통해 접근
            this.selectionCooldown.reset();
        }

        // 볼륨 조정 (좌우 키 사용 시)
        if (adjustCooldown.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_LEFT) || inputManager.isKeyDown(KeyEvent.VK_A)) {
                adjustVolumeDown();
                soundManager.playButtonSound(); // 필드를 통해 접근
                this.adjustCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) || inputManager.isKeyDown(
                KeyEvent.VK_D)) {
                adjustVolumeUp();
                soundManager.playButtonSound(); // 필드를 통해 접근
                this.adjustCooldown.reset();
            }
        }

        // 설정 화면 종료
        if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
            soundManager.playButtonSound(); // 필드를 통해 접근
            this.isRunning = false;
        }
    }

    /**
     * 선택된 메뉴 항목의 볼륨을 증가시킵니다.
     */
    private void adjustVolumeUp() {
        if (selectionCode == 0) {
            soundManager.BGMUp(); // 필드를 통해 접근
        } else if (selectionCode == 1) {
            soundManager.SFXUp(); // 필드를 통해 접근
        }
    }

    /**
     * 선택된 메뉴 항목의 볼륨을 감소시킵니다.
     */
    private void adjustVolumeDown() {
        if (selectionCode == 0) {
            soundManager.BGMDown(); // 필드를 통해 접근
        } else if (selectionCode == 1) {
            soundManager.SFXDown(); // 필드를 통해 접근
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