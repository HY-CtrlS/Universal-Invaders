package screen;

import engine.Cooldown;
import engine.Core;
import java.awt.event.KeyEvent;

public class PauseScreen extends Screen {

    private static final int SELECTION_TIME = 200;

    private Cooldown selectionCooldown;

    public PauseScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
        
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
        this.returnCode = 1;
    }

    public final int run() {
        super.run();

        return this.returnCode;
    }

    protected final void update() {
        super.update();

        draw();
        if (this.selectionCooldown.checkFinished()
            && this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_UP)
                || inputManager.isKeyDown(KeyEvent.VK_W)) {
                changeMenu();
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
                || inputManager.isKeyDown(KeyEvent.VK_S)) {
                changeMenu();
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                this.isRunning = false;
            }
        }
    }

    private void changeMenu() {
        if (this.returnCode == 1) {
            this.returnCode = 0;
        } else {
            this.returnCode = 1;
        }
    }

    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawPauseTitle(this);
        drawManager.drawPauseMenu(this, this.returnCode);
        drawManager.completeDrawing(this);
    }


}
