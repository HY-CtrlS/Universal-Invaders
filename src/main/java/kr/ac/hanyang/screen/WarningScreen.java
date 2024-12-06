package kr.ac.hanyang.screen;

import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import java.awt.event.KeyEvent;

/**
 * 게임 종료 시 경고창을 구현하는 클래스
 */
public class WarningScreen extends Screen {

    /** 사용자 선택의 변경 사이의 시간(밀리초) */
    private static final int SELECTION_TIME = 200;

    /** 사용자 선택이 변경될 때까지의 시간 */
    private Cooldown selectionCooldown;

    /**
     * 생성자, 경고 화면의 속성을 설정
     *
     * @param width  Screen width.
     * @param height Screen height.
     * @param fps    Frames per second, frame rate at which the game is run.
     */
    public WarningScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
        this.returnCode = 0;
    }

    /**
     * 화면 실행 메소드
     *
     * @return 어떤 메뉴가 선택되었는지 리턴코드 반환
     */
    public final int run() {
        super.run();

        return this.returnCode;
    }

    /**
     * 경고 화면의 요소를 업데이트하고 이벤트를 확인
     */
    protected final void update() {
        super.update();

        draw();
        if (this.selectionCooldown.checkFinished()
            && this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_UP)
                || inputManager.isKeyDown(KeyEvent.VK_W)) {
                previousMenuItem();
                this.selectionCooldown.reset();
                Core.getSoundManager().playButtonSound();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
                || inputManager.isKeyDown(KeyEvent.VK_S)) {
                nextMenuItem();
                this.selectionCooldown.reset();
                Core.getSoundManager().playButtonSound();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                this.isRunning = false;
                Core.getSoundManager().playButtonSound();
            }
        }
    }

    /**
     * 다음 메뉴 전환 메소드
     */
    private void nextMenuItem() {
        if (this.returnCode == 1) {
            this.returnCode = 0;
        } else {
            this.returnCode++;
        }
    }

    /**
     * 이전 메뉴 전환 메소드
     */
    private void previousMenuItem() {
        if (this.returnCode == 0) {
            this.returnCode = 1;
        } else {
            this.returnCode--;
        }
    }

    /**
     * 일시정지 화면에 표시되는 문구들을 그리는 메소드
     */
    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawWarningTitle(this);
        drawManager.drawWarningMenu(this, this.returnCode);
        drawManager.completeDrawing(this);
    }
}
