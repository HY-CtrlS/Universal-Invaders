package kr.ac.hanyang.screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.GameState;
import kr.ac.hanyang.engine.Score;

/**
 * Implements the score screen.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class ScoreScreen extends Screen {

    /** Milliseconds between changes in user selection. */
    private static final int SELECTION_TIME = 200;
    /** Maximum number of high scores. */
    private static final int MAX_HIGH_SCORE_NUM = 7;
    /** Code of first mayus character. */
    private static final int FIRST_CHAR = 65;
    /** Code of last mayus character. */
    private static final int LAST_CHAR = 90;

    /** Total ships destroyed by the player. */
    private int shipsDestroyed;
    /** List of past high scores. */
    private List<Score> highScores;
    /** Total seconds of survival. */
    private int survivalTime;
    /** Checks if current score is a new high score. */
    private boolean isNewRecord;
    /** Checks if the game was cleared. */
    private boolean isGameClear;
    /** Player name for record input. */
    private char[] name;
    /** Character of players name selected for change. */
    private int nameCharSelected;
    /** Time between changes in user selection. */
    private Cooldown selectionCooldown;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width     Screen width.
     * @param height    Screen height.
     * @param fps       Frames per second, frame rate at which the game is run.
     * @param gameState Current game state.
     */
    public ScoreScreen(final int width, final int height, final int fps,
        final GameState gameState) {
        super(width, height, fps);

        this.shipsDestroyed = gameState.getShipsDestroyed();
        this.survivalTime = gameState.getSurvivalTime();
        this.isNewRecord = false;
        this.isGameClear = false;
        this.name = "AAA".toCharArray();
        this.nameCharSelected = 0;
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();

        try {
            this.highScores = Core.getFileManager().loadHighScores();
            if (highScores.size() < MAX_HIGH_SCORE_NUM
                || highScores.get(highScores.size() - 1).getSurvivalTime()
                < this.survivalTime) {
                this.isNewRecord = true;
            }

        } catch (IOException e) {
            logger.warning("Couldn't load high scores!");
        }

        if (gameState.getHp() > 0) {
            isGameClear = true;
        }
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public final int run() {
        super.run();
        Core.getSoundManager().stopBackgroundMusic();
        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        draw();
        if (this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                // Return to main menu.
                this.returnCode = 1;
                this.isRunning = false;
                if (this.isNewRecord) {
                    saveScore();
                }
            } else if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                // Play again.
                this.returnCode = 2;
                this.isRunning = false;
                if (this.isNewRecord) {
                    saveScore();
                }
            }

            if (this.isNewRecord && this.selectionCooldown.checkFinished()) {
                if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
                    this.nameCharSelected = this.nameCharSelected == 2 ? 0
                        : this.nameCharSelected + 1;
                    this.selectionCooldown.reset();
                }
                if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
                    this.nameCharSelected = this.nameCharSelected == 0 ? 2
                        : this.nameCharSelected - 1;
                    this.selectionCooldown.reset();
                }
                if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
                    this.name[this.nameCharSelected] =
                        (char) (this.name[this.nameCharSelected]
                            == LAST_CHAR ? FIRST_CHAR
                            : this.name[this.nameCharSelected] + 1);
                    this.selectionCooldown.reset();
                }
                if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
                    this.name[this.nameCharSelected] =
                        (char) (this.name[this.nameCharSelected]
                            == FIRST_CHAR ? LAST_CHAR
                            : this.name[this.nameCharSelected] - 1);
                    this.selectionCooldown.reset();
                }
            }
        }
    }

    /**
     * Saves the score as a high score.
     */
    private void saveScore() {
        highScores.add(new Score(new String(this.name), survivalTime));
        Collections.sort(highScores);
        if (highScores.size() > MAX_HIGH_SCORE_NUM) {
            highScores.remove(highScores.size() - 1);
        }

        try {
            Core.getFileManager().saveHighScores(highScores);
        } catch (IOException e) {
            logger.warning("Couldn't load high scores!");
        }
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawGameEnd(this, this.inputDelay.checkFinished(),
            this.isNewRecord, this.isGameClear);
        drawManager.drawResults(this,
            this.shipsDestroyed, this.survivalTime, this.isNewRecord);

        if (this.isNewRecord) {
            drawManager.drawNameInput(this, this.name, this.nameCharSelected);
        }

        drawManager.completeDrawing(this);
    }
}
