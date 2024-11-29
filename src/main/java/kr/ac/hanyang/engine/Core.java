package kr.ac.hanyang.engine;

import java.awt.Color;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.ac.hanyang.entity.Entity.Direction;
import kr.ac.hanyang.entity.ship.Ship;
import kr.ac.hanyang.screen.*;

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

    /** Frame to draw the screen on. */
    private static Frame frame;
    /** Screen currently shown. */
    private static Screen currentScreen;
    /** Application logger. */
    private static final Logger LOGGER = Logger.getLogger(Core.class.getSimpleName());
    /** Logger handler for printing to disk. */
    private static Handler fileHandler;
    /** Logger handler for printing to console. */
    private static ConsoleHandler consoleHandler;

    /**
     * Test implementation.
     *
     * @param args Program args, ignored.
     */
    public static void main(final String[] args) {
        initializeLogger();
        initializeFrame();

        int width = frame.getWidth();
        int height = frame.getHeight();

        int returnCode = 1;
        GameState gameState = new GameState(0, getStatusManager().getMaxHp(), 0, 0, getStatusManager(), new Ship(0,0, Direction.DOWN, Color.GREEN, 1));

        do {
            switch (returnCode) {
                case 1:
                    // Main menu.
                    currentScreen = new TitleScreen(width, height, FPS);
                    returnCode = handleScreen(currentScreen, "title screen");
                    break;
                case 2:
                    // 게임 시작 전 함선 선택
                    currentScreen = new shipSelectScreen(width, height, FPS);
                    int shipID = handleScreen(currentScreen, "ship select screen");

                    // 게임 화면 시작
                    currentScreen = new GameScreen(gameState, width, height, FPS,
                        shipID);
                    int isQuit = handleScreen(currentScreen, "game screen");

                    // 플레이한 게임의 정보를 gameState에 저장
                    gameState = ((GameScreen) currentScreen).getGameState();

                    getSoundManager().stopBackgroundMusic();
                    if (isQuit == 0) {
                        returnCode = 1;
                        break;
                    }

                    // 보스 스크린 시작
                    if (isQuit == 2) {
                        currentScreen = new BossScreen(gameState.getStatus(), width, height, FPS,
                            gameState.getShip());
                        returnCode = handleScreen(currentScreen, "boss screen");
                    }

                    // 게임 종료 후 gameState의 정보를 이용하여 scoreScreen 생성
                    currentScreen = new ScoreScreen(width, height, FPS, gameState);
                    returnCode = handleScreen(currentScreen, "score screen");
                    break;
                case 3:
                    // High scores.
                    currentScreen = new HighScoreScreen(width, height, FPS);
                    returnCode = handleScreen(currentScreen, "high score screen");
                    break;
                case 4:
                    // 설정 화면
                    currentScreen = new SettingScreen(width, height, FPS);
                    returnCode = handleScreen(currentScreen, "setting screen");
                    break;
                default:
                    break;
            }
        } while (returnCode != 0);

        close();
    }

    /**
     * 로거를 초기화합니다.
     */
    private static void initializeLogger() {
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
            LOGGER.severe("An error occurred during logger initialization: " + e.getMessage());
        }
    }

    /**
     * 메인 게임 프레임을 초기화합니다.
     */
    private static void initializeFrame() {
        frame = new Frame(WIDTH, HEIGHT);
        DrawManager.getInstance().setFrame(frame);
    }

    /**
     * 화면을 처리합니다.
     *
     * @param screen     화면
     * @param screenName 화면 이름
     * @return 화면 종료 코드
     */
    private static int handleScreen(Screen screen, String screenName) {
        LOGGER.info("Starting " + WIDTH + "x" + HEIGHT + " " + screenName + " at " + FPS + " fps.");
        int returnCode = frame.setScreen(screen);
        LOGGER.info("Closing " + screenName + ".");
        return returnCode;
    }

    /**
     * 리소스를 정리하고 애플리케이션을 종료합니다.
     */
    private static void close() {
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
}