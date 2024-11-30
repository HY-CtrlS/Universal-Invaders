package kr.ac.hanyang.screen;

import java.awt.Color;
import java.awt.event.KeyEvent;

import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.entity.Entity.Direction;
import kr.ac.hanyang.entity.Ship;

public class SplashScreen extends Screen {

    private static Ship superShip;
    private int backgroundOffsetY = 0; // 배경화면 Y축 오프셋
    private final int backgroundScrollSpeed = 2; // 배경화면 이동 속도 (픽셀/프레임)
    private long startTime; // 시작 시간 기록
    private boolean hasPlayedSound; // 사운드 호출 여부

    public SplashScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        this.returnCode = 1; // 다음 화면 코드를 1로 설정
        this.startTime = System.currentTimeMillis(); // 화면 시작 시간 기록
        this.hasPlayedSound = false; // 사운드 호출 여부를 false로 초기화

        Core.getStatusManager().setSpeed(10);
        superShip = new Ship(this.width / 2, this.height, Direction.UP, Color.GREEN, 1);

        Core.getSoundManager().playLobbyBGM(); // 로비 배경음악 재생
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public final int run() {
        super.run();

        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        // 1초 동안 배경화면 아래로 이동
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime <= 1500) { // 1.5초 이내
            backgroundOffsetY += backgroundScrollSpeed; // 배경화면 이동
        }
        else{
            superShip.moveUp();

            // 최초 1회만 사운드 호출
            if (!hasPlayedSound) {
                Core.getSoundManager().playPlaySound();
                hasPlayedSound = true; // 사운드 호출 여부를 true로 변경
            }
        }

        draw();

        if (inputManager.isKeyDown(KeyEvent.VK_SPACE)
            && this.inputDelay.checkFinished()) {
            this.isRunning = false;
            Core.getSoundManager().playButtonSound();
        }
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        boolean isTitleComplete = false;
        drawManager.initDrawing(this);

        drawManager.setSplashImage();
        drawManager.drawBackgroundImage(this, backgroundOffsetY); // 오프셋 적용

        if (superShip.getPositionY() < 0) {
            isTitleComplete = drawManager.drawGameTitle(this);
        }

        // 타이틀이 완료되었으면 메시지 출력
        if (isTitleComplete) {
            drawManager.drawStartMessage(this);
        }

        drawManager.drawEntity(superShip, superShip.getPositionX(), superShip.getPositionY());

        drawManager.completeDrawing(this);
    }
}
