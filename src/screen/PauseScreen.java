package screen;

import engine.Cooldown;
import engine.Core;
import java.awt.event.KeyEvent;

/**
 * 일시정지 화면을 구현하는 클래스
 */
public class PauseScreen extends Screen {

    /** 사용자 선택의 변경 사이의 시간(밀리초) */
    private static final int SELECTION_TIME = 200;

    /** 사용자 선택이 변경될 때까지의 시간 */
    private Cooldown selectionCooldown;

    /**
     * 생성자, 일시정지 화면의 속성을 설정
     *
     * @param width  Screen width.
     * @param height Screen height.
     * @param fps    Frames per second, frame rate at which the game is run.
     */
    public PauseScreen(final int width, final int height, final int fps) {
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
     * 일시정지 화면의 요소를 업데이트하고 이벤트를 확인
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
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
                || inputManager.isKeyDown(KeyEvent.VK_S)) {
                nextMenuItem();
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                // 일시정지 화면에서 setting 선택 시 setting 화면으로 이동
                if (this.returnCode == 1) {
                    this.logger.info("Starting " + this.getWidth() + "x" + this.getHeight()
                        + " settings screen at " + this.fps + " fps.");
                    Screen setting = new SettingScreen(this.getWidth(), this.getHeight(), this.fps);
                    setting.run();
                    this.logger.info("Closing settings screen.");
                    this.returnCode = 0;
                } else if (this.returnCode == 2) {
                    this.logger.info("Starting " + this.getWidth() + "x" + this.getHeight()
                        + " warning screen at " + this.fps + " fps.");
                    Screen warning = new WarningScreen(this.getWidth(), this.getHeight(), this.fps);
                    int check = warning.run();
                    this.logger.info("Closing warning screen.");
                    if (check == 0) {
                        this.returnCode = 2;
                        this.isRunning = false;
                    } else {
                        this.returnCode = 2;
                    }
                } else {
                    this.isRunning = false;
                }
                // 일시정지 화면에서 돌아온 후 스페이스바 키 입력을 초기화하여
                // 돌아오자마자 스페이스바가 눌린 상태로 인식되지 않도록 함
                inputManager.resetKeyState(KeyEvent.VK_SPACE);
            }
        }
    }

    /**
     * 다음 메뉴 전환 메소드
     */
    private void nextMenuItem() {
        if (this.returnCode == 2) {
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
            this.returnCode = 2;
        } else {
            this.returnCode--;
        }
    }

    /**
     * 일시정지 화면에 표시되는 문구들을 그리는 메소드
     */
    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawPauseTitle(this);
        drawManager.drawPauseMenu(this, this.returnCode);
        drawManager.completeDrawing(this);
    }
}
