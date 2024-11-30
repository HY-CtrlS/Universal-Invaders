package kr.ac.hanyang.screen;

import java.awt.Color;
import java.awt.event.KeyEvent;

import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.entity.Entity.Direction;
import kr.ac.hanyang.entity.Ship;

public class SplashScreen extends Screen {

    private static Ship superShip;

    public SplashScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        this.returnCode = 1;
        superShip = new Ship(this.width / 2, this.height, Direction.UP, Color.GREEN, 1);
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

        superShip.moveUp();
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
        drawManager.initDrawing(this);

        drawManager.setSplashImage();
        drawManager.drawBackgroundImage(this);

        drawManager.drawGameTitle(this);

        drawManager.drawEntity(superShip, superShip.getPositionX(), superShip.getPositionY());

        drawManager.completeDrawing(this);
    }
}
