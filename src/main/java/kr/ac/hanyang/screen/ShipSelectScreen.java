package kr.ac.hanyang.screen;

import kr.ac.hanyang.entity.*;
import java.awt.Color;
import java.awt.event.KeyEvent;

import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.entity.ship.Ship;

/**
 * 함선 선택 화면을 구현하는 클래스
 */
public class ShipSelectScreen extends Screen {

    /** 사용자 선택의 변경 사이의 시간(밀리초) */
    private static final int SELECTION_TIME = 200;

    /** 사용자 선택이 변경될 때까지의 시간 */
    private Cooldown selectionCooldown;

    /** 함선 ID */
    private int shipID;

    private Color[] shipColors = {Color.GREEN, Color.BLUE, Color.YELLOW, Color.RED};

    /**
     * 생성자, 함선 선택 화면의 속성을 설정
     *
     * @param width  Screen width.
     * @param height Screen height.
     * @param fps    Frames per second, frame rate at which the game is run.
     */
    public ShipSelectScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        // Defaults to play.
        this.returnCode = 1;
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
        this.shipID = 1;

    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    @Override
    public final int run() {
        super.run();

        return this.shipID;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    @Override
    protected final void update() {
        super.update();

        draw();
        if (this.selectionCooldown.checkFinished()
            && this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_UP)
                || inputManager.isKeyDown(KeyEvent.VK_W)) {
                previousMenuItem();
                Core.getSoundManager().playButtonSound();
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
                || inputManager.isKeyDown(KeyEvent.VK_S)) {
                nextMenuItem();
                Core.getSoundManager().playButtonSound();
                this.selectionCooldown.reset();
            }
            if (this.returnCode == 0) {
                if (this.selectionCooldown.checkFinished()
                    && this.inputDelay.checkFinished()) {
                    if (inputManager.isKeyDown(KeyEvent.VK_LEFT)
                        || inputManager.isKeyDown(KeyEvent.VK_A)) {
                        previousShip();
                        Core.getSoundManager().playButtonSound();
                        this.selectionCooldown.reset();
                    }
                    if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)
                        || inputManager.isKeyDown(KeyEvent.VK_D)) {
                        nextShip();
                        Core.getSoundManager().playButtonSound();
                        this.selectionCooldown.reset();
                    }
                }
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                if (this.returnCode == 0) {
                    this.isRunning = false;
                }
                // SoundManager에서 음악 재생 중인지 확인 후 정지
                if (Core.getSoundManager().isBackgroundMusicPlaying()) {
                    Core.getSoundManager().stopBackgroundMusic();
                }
                Core.getSoundManager().playPlaySound();
            }
        }
    }

    /**
     * Shifts the focus to the next menu item.
     */
    private void nextMenuItem() {
        if (this.returnCode == 1) {
            this.returnCode = 0;
        } else {
            this.returnCode++;
        }
    }

    /**
     * Shifts the focus to the previous menu item.
     */
    private void previousMenuItem() {
        if (this.returnCode == 0) {
            this.returnCode = 1;
        } else {
            this.returnCode--;
        }
    }

    /**
     * Shifts the focus to the next ship.
     */
    private void nextShip() {
        if (this.shipID == 4) {
            this.shipID = 1;
        } else {
            this.shipID++;
        }
    }

    /**
     * Shifts the focus to the next ship.
     */
    private void previousShip() {
        if (this.shipID == 1) {
            this.shipID = 4;
        } else {
            this.shipID--;
        }
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        Entity dummy = Ship.createShipByID(this.shipID, 0, 0);

        drawManager.initDrawing(this);
        drawManager.drawEntity(dummy, this.width / 2 - (dummy.getWidth() / 2),
            this.height / 3 * 2 + 115);
        drawManager.drawShipSelectTitle(this);
        drawManager.drawShipSelectMenu(this, this.returnCode, this.shipID);
        drawManager.completeDrawing(this);
    }
}
