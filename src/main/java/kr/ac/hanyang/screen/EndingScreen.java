package kr.ac.hanyang.screen;

import java.awt.Color;
import java.awt.event.KeyEvent;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.entity.Entity.Direction;
import kr.ac.hanyang.entity.ship.Ship;

public class EndingScreen extends Screen {

    private static final int BACKGROUND_SCROLL_SPEED = 2; // 배경화면 이동 속도 (픽셀/프레임)
    private static final int BACKGROUND_MOVE_DISTANCE = 170; // 배경화면 이동 거리 (픽셀)

    private static Ship superShip;
    private int backgroundOffsetY = 0; // 배경화면 Y축 오프셋
    private boolean hasPlayedSound = false; // 사운드 호출 여부
    private boolean readyToStart = false; // 시작 준비 여부

    public EndingScreen(final int width, final int height, final int fps, final int shipID) {
        super(width, height, fps);
        this.returnCode = 1; // 다음 화면 코드 설정

        initializeShip(shipID);
        initializeSounds();

        if (Core.getSoundManager().isBackgroundMusicPlaying()) {
            Core.getSoundManager().stopBackgroundMusic();
        }
        Core.getSoundManager().playEndingBGM();
    }

    /**
     * 플레이어의 함선을 초기화합니다.
     */
    private void initializeShip(final int shipID) {
        Core.getStatusManager().setSpeed(10.0);
        superShip = Ship.createShipByID(shipID, this.width / 2, 0);
        // direction을 down으로 Ship 생성
        superShip.setDirection(Direction.DOWN);
        superShip.setPositionY(-superShip.getHeight());
        drawManager.resetGameTitleAnimation();
    }

    /**
     * 스플래시 화면의 배경음악을 초기화합니다.
     */
    private void initializeSounds() {
        Core.getSoundManager().playLobbyBGM();
    }

    /**
     * 스플래시 화면 동작을 시작합니다.
     *
     * @return 다음 화면 코드
     */
    public final int run() {
        super.run();
        Core.getSoundManager().stopBackgroundMusic();
        return this.returnCode;
    }

    /**
     * 스플래시 화면의 로직과 요소를 업데이트합니다.
     */
    protected final void update() {
        super.update();

        updateBackground();
        updateShipMovement();
        handleInput();

        draw();
    }

    /**
     * 배경화면 스크롤을 업데이트합니다.
     */
    private void updateBackground() {
        if (backgroundOffsetY < BACKGROUND_MOVE_DISTANCE) {
            backgroundOffsetY += BACKGROUND_SCROLL_SPEED;
        }
    }

    /**
     * 플레이어의 함선 움직임을 업데이트하고, 사운드 효과를 한 번만 재생합니다.
     */
    private void updateShipMovement() {
        if (backgroundOffsetY >= BACKGROUND_MOVE_DISTANCE) {
            superShip.moveDown();

            if (!hasPlayedSound) {
                Core.getSoundManager().playPlaySound();
                hasPlayedSound = true;
            }
        }
    }

    /**
     * 게임 시작 입력을 처리합니다.
     */
    private void handleInput() {
        if (inputDelay.checkFinished() && readyToStart &&
            (inputManager.isKeyDown(KeyEvent.VK_SPACE) || inputManager.isKeyDown(
                KeyEvent.VK_ENTER))) {
            this.isRunning = false;
            Core.getSoundManager().playButtonSound();
        }
    }

    /**
     * 스플래시 화면과 관련된 요소를 그립니다.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawBackground();
        drawTitleAndMessages();
        drawShip();

        drawManager.completeDrawing(this);
    }

    /**
     * 배경화면을 스크롤을 적용하여 그립니다.
     */
    private void drawBackground() {
        drawManager.setSplashImage();
        drawManager.drawBackgroundImage(this, backgroundOffsetY);
    }

    /**
     * 타이틀과 메시지를 그립니다.
     */
    private void drawTitleAndMessages() {
        if (superShip.getPositionY() > this.height) {
            readyToStart = drawManager.drawGameTitle(this, "You Saved the Earth !!");
        }

        if (readyToStart) {
            drawManager.drawStartMessage(this, "Press SPACE or ENTER to restart");
        }
    }

    /**
     * 플레이어의 함선을 그립니다.
     */
    private void drawShip() {
        drawManager.drawEntity(superShip, superShip.getPositionX(), superShip.getPositionY());
    }
}