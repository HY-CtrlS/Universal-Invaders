package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import screen.*;
import Item.*;

/**
 * Implements core game logic.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public final class Core {

    /** Width of current screen. */
    private static final int WIDTH = 720;
    /** Height of current screen. */
    private static final int HEIGHT = WIDTH + 80;
    /** Max fps of current screen. */
    private static final int FPS = 60;
    /** 게임 난이도 설정 */
    private static final GameSettings GAME_SETTING =
        new GameSettings(5, 4, 60, 2000, 500, 30);

    /** Frame to draw the screen on. */
    private static Frame frame;
    /** Screen currently shown. */
    private static Screen currentScreen;
    /** Application logger. */
    private static final Logger LOGGER = Logger.getLogger(Core.class
        .getSimpleName());
    /** Logger handler for printing to disk. */
    private static Handler fileHandler;
    /** Logger handler for printing to console. */
    private static ConsoleHandler consoleHandler;

    /** quit로 라운드라 종료되었는지 확인하는 변수 */
    private static int isQuit;

    /**
     * Test implementation.
     *
     * @param args Program args, ignored.
     */
    public static void main(final String[] args) {
        try {
            LOGGER.setUseParentHandlers(false);

            fileHandler = new FileHandler("log");
            fileHandler.setFormatter(new MinimalFormatter());

            consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new MinimalFormatter());

            LOGGER.addHandler(fileHandler);
            LOGGER.addHandler(consoleHandler);
            LOGGER.setLevel(Level.ALL);

        } catch (Exception e) {
            // TODO handle exception
            e.printStackTrace();
        }

        frame = new Frame(WIDTH, HEIGHT);
        DrawManager.getInstance().setFrame(frame);
        int width = frame.getWidth();
        int height = frame.getHeight();

        GameState gameState;

        int returnCode = 1;
        do {
            gameState = new GameState(1, 0, getStatusManager().getMaxHp(), 0, 0);
            switch (returnCode) {
                case 1:
                    // Main menu.
                    currentScreen = new TitleScreen(width, height, FPS);
                    LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
                        + " title screen at " + FPS + " fps.");
                    returnCode = frame.setScreen(currentScreen);
                    LOGGER.info("Closing title screen.");
                    // 게임 진입 전에 gameState 현재 체력을 다시 최대 체력으로 설정
                    break;
                case 2:
                    // 게임 시작 시 StatusManager의 status 객체를 res/status 의 값으로 초기화
                    getStatusManager().resetDefaultStatus();

                    // 게임 시작 전 함선 선택
                    currentScreen = new shipSelectScreen(width, height, FPS);
                    LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
                        + " ship select screen at " + FPS + " fps.");
                    int shipID = frame.setScreen(currentScreen);
                    LOGGER.info("Closing ship select screen.");

                    // 게임 화면 시작
                    currentScreen = new GameScreen(gameState,
                        GAME_SETTING,
                        width, height, FPS, shipID);
                    LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
                        + " game screen at " + FPS + " fps.");
                    isQuit = frame.setScreen(currentScreen);
                    LOGGER.info("Closing game screen.");
                    if (isQuit == 0) {
                        break;
                    }

                    // 플레이한 게임의 정보를 gameState에 저장
                    gameState = ((GameScreen) currentScreen).getGameState();

                    getSoundManager().stopBackgroundMusic();
                    if (isQuit == 0) {
                        returnCode = 1;
                        break;
                    }

                    // 게임 종료 후 gameState의 정보를 이용하여 scoreScreen 생성
                    LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
                        + " score screen at " + FPS + " fps, with a score of "
                        + gameState.getScore() + ", "
                        + gameState.getHp() + " lives remaining, "
                        + gameState.getBulletsShot() + " bullets shot and "
                        + gameState.getShipsDestroyed() + " ships destroyed.");
                    currentScreen = new ScoreScreen(width, height, FPS, gameState);
                    returnCode = frame.setScreen(currentScreen);
                    LOGGER.info("Closing score screen.");
                    break;
                case 3:
                    // High scores.
                    currentScreen = new HighScoreScreen(width, height, FPS);
                    LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
                        + " high score screen at " + FPS + " fps.");
                    returnCode = frame.setScreen(currentScreen);
                    LOGGER.info("Closing high score screen.");
                    break;
                case 4:
                    // 설정 화면
                    currentScreen = new SettingScreen(width, height, FPS);
                    LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
                        + " settings screen at " + FPS + " fps.");
                    returnCode = frame.setScreen(currentScreen);
                    LOGGER.info("Closing settings screen.");
                    break;
                default:
                    break;
            }

        } while (returnCode != 0);

        fileHandler.flush();
        fileHandler.close();
        System.exit(0);
    }

    /**
     * Constructor, not called.
     */
    private Core() {

    }

    /**
     * Controls access to the logger.
     *
     * @return Application logger.
     */
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * Controls access to the drawing manager.
     *
     * @return Application draw manager.
     */
    public static DrawManager getDrawManager() {
        return DrawManager.getInstance();
    }

    /**
     * Controls access to the input manager.
     *
     * @return Application input manager.
     */
    public static InputManager getInputManager() {
        return InputManager.getInstance();
    }

    /**
     * Controls access to the file manager.
     *
     * @return Application file manager.
     */
    public static FileManager getFileManager() {
        return FileManager.getInstance();
    }

    /**
     * Controls creation of new cooldowns.
     *
     * @param milliseconds Duration of the cooldown.
     * @return A new cooldown.
     */
    public static Cooldown getCooldown(final int milliseconds) {
        return new Cooldown(milliseconds);
    }

    /**
     * Controls creation of new cooldowns with variance.
     *
     * @param milliseconds Duration of the cooldown.
     * @param variance     Variation in the cooldown duration.
     * @return A new cooldown with variance.
     */
    public static Cooldown getVariableCooldown(final int milliseconds,
        final int variance) {
        return new Cooldown(milliseconds, variance);
    }

    /**
     * Controls access to the status manager.
     *
     * @return Application status manager.
     */
    public static StatusManager getStatusManager() {
        return StatusManager.getInstance();
    }

    /**
     * Controls access to the sound manager.
     *
     * @return Application sound manager.
     */
    public static SoundManager getSoundManager() {
        return SoundManager.getInstance();
    }
}