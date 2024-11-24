package screen;

import java.awt.event.KeyEvent;

import engine.Cooldown;
import engine.Core;

/**
 * Implements the title screen.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class TitleScreen extends Screen {

    /** Milliseconds between changes in user selection. */
    private static final int SELECTION_TIME = 200;

    /** Time between changes in user selection. */
    private Cooldown selectionCooldown;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width  Screen width.
     * @param height Screen height.
     * @param fps    Frames per second, frame rate at which the game is run.
     */
    public TitleScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        // Defaults to play.
        this.returnCode = 2;
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();

        // 메인 메뉴 배경음악 재생
        if (!Core.getSoundManager().isBackgroundMusicPlaying()) {
            Core.getSoundManager().playTitleScreenBGM();
        }
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    @Override
    public final int run() {
        super.run();

        return this.returnCode;
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
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                this.isRunning = false;
                if (returnCode == 2) {
                    Core.getSoundManager().playButtonSound();
                } else {
                    Core.getSoundManager().playButtonSound();
                }
            }
        }
    }

    /**
     * Shifts the focus to the next menu item.
     */
    private void nextMenuItem() {
        if (this.returnCode == 4) {
            this.returnCode = 0;
        } else if (this.returnCode == 0) {
            this.returnCode = 2;
        } else {
            this.returnCode++;
        }
    }

    /**
     * Shifts the focus to the previous menu item.
     */
    private void previousMenuItem() {
        if (this.returnCode == 0) {
            this.returnCode = 4;
        } else if (this.returnCode == 2) {
            this.returnCode = 0;
        } else {
            this.returnCode--;
        }
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawTitle(this);
        drawManager.drawMenu(this, this.returnCode);

        drawManager.completeDrawing(this);
    }
}
