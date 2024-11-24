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

    /** Levels between extra life. */
    private static final int EXTRA_LIFE_FREQUENCY = 3;
    /** Total number of levels. */
    private static final int NUM_LEVELS = 7;

    /** Difficulty settings for level 1. */
    private static final GameSettings SETTINGS_LEVEL_1 =
        new GameSettings(5, 4, 60, 2000, 500, 30);
    /** Difficulty settings for level 2. */
    private static final GameSettings SETTINGS_LEVEL_2 =
        new GameSettings(5, 5, 50, 2000, 1000, 35);
    /** Difficulty settings for level 3. */
    private static final GameSettings SETTINGS_LEVEL_3 =
        new GameSettings(6, 5, 40, 1500, 1000, 40);
    /** Difficulty settings for level 4. */
    private static final GameSettings SETTINGS_LEVEL_4 =
        new GameSettings(6, 6, 30, 1500, 1000, 45);
    /** Difficulty settings for level 5. */
    private static final GameSettings SETTINGS_LEVEL_5 =
        new GameSettings(7, 6, 20, 1000, 1, 50);
    /** Difficulty settings for level 6. */
    private static final GameSettings SETTINGS_LEVEL_6 =
        new GameSettings(7, 7, 10, 1000, 1, 55);
    /** Difficulty settings for level 7. */
    private static final GameSettings SETTINGS_LEVEL_7 =
        new GameSettings(8, 7, 2, 500, 1, 60);

    /** Frame to draw the screen on. */
    private static Frame frame;
    /** Screen currently shown. */
    private static Screen currentScreen;
    /** Difficulty settings list. */
    private static List<GameSettings> gameSettings;
    /** Application logger. */
    private static final Logger LOGGER = Logger.getLogger(Core.class
        .getSimpleName());
    /** Logger handler for printing to disk. */
    private static Handler fileHandler;
    /** Logger handler for printing to console. */
    private static ConsoleHandler consoleHandler;
    // 아이템 리스트 객체 생성
    private static ItemList items = new ItemList();
    // 아이템 리스트 참조하기 위한 배열
    private static List<Item> itemList;
    /** quit로 라운드라 종료되었는지 확인하는 변수 */
    private static int isQuit;
    /** 아이템 선택화면에서 선택된 아이템 판단**/
    private static int selectedItem;

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

        gameSettings = new ArrayList<GameSettings>();
        gameSettings.add(SETTINGS_LEVEL_1);
        gameSettings.add(SETTINGS_LEVEL_2);
        gameSettings.add(SETTINGS_LEVEL_3);
        gameSettings.add(SETTINGS_LEVEL_4);
        gameSettings.add(SETTINGS_LEVEL_5);
        gameSettings.add(SETTINGS_LEVEL_6);
        gameSettings.add(SETTINGS_LEVEL_7);

        GameState gameState;

        int returnCode = 1;
        do {


            gameState = new GameState(1, 0, getStatusManager().getMaxHp(), 0, 0, 0);

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
                    // 게임 시작 시 초기 아이템 리스트 생성
                    itemList = items.initializedItems();
                    // 게임 시작 시 함선의 체력을 기본으로 초기화
                    gameState.setHP(getStatusManager().getMaxHp());
                    items.initializedItems();
                    // 게임 시작 전 함선 선택
                    currentScreen = new shipSelectScreen(width, height, FPS);
                    LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
                        + " ship select screen at " + FPS + " fps.");
                    int shipID = frame.setScreen(currentScreen);
                    LOGGER.info("Closing ship select screen.");
                    // Game & score.
                    do {
                        // 선택한 아이템 없는 것으로 초기화
                        selectedItem = -1;
                        // One extra live every few levels.
                        boolean bonusLife = gameState.getLevel()
                            % EXTRA_LIFE_FREQUENCY == 0


                            && gameState.getHp() < getStatusManager().getMaxHp();
                        int totalSurvivalTime = 0;
                        totalSurvivalTime += gameState.getSurvivalTime();


                        currentScreen = new GameScreen(gameState,
                            gameSettings.get(gameState.getLevel() - 1),

                            bonusLife, totalSurvivalTime, width, height, FPS, shipID);

                        LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
                            + " game screen at " + FPS + " fps.");
                        isQuit = frame.setScreen(currentScreen);
                        LOGGER.info("Closing game screen.");
                        if (isQuit == 0) {
                            break;
                        }

                        // 현재 플레이한 게임의 정보를 gameState에 저장
                        gameState = ((GameScreen) currentScreen).getGameState();

                        // 아이템 선택화면으로 이동
                        // 아직 HP가 남아있거나 방금 깬 레벨이 마지막 레벨이 아닌 경우
                        if (gameState.getHp() > 0
                            && gameState.getLevel() + 1 <= NUM_LEVELS) {
                            LOGGER.info(
                                "Starting " + WIDTH + "X" + HEIGHT + " ItemSelectingScreen at "
                                    + FPS + " fps.");
                            currentScreen = new ItemSelectedScreen(gameState,
                                items.getSelectedItemList(), width, height, FPS);
                            selectedItem = frame.setScreen(currentScreen);
                            LOGGER.info("Closing Item Selecting Screen.");
                        }
                        // 최대 체력 증가 아이템을 선택한 경우, 현재 체력 또한 증가된 체력만큼 올려줌.
                        if (selectedItem == 1) {
                            // 가지고 있던 체력의 비율 계산
                            double portionHp = (double) gameState.getHp() / (getStatusManager().getMaxHp() - itemList.get(1).getChangedValue());
                            // 늘어난 체력에 맞게 현재 체력의 비율 조정
                            gameState.setHP((int) (getStatusManager().getMaxHp() * portionHp));
                        }
                        gameState = new GameState(gameState.getLevel() + 1,
                            gameState.getScore(),
                            gameState.getHp(),
                            gameState.getBulletsShot(),
                            gameState.getShipsDestroyed(),
                            gameState.getSurvivalTime());

                    } while (gameState.getHp() > 0
                        && gameState.getLevel() <= NUM_LEVELS);
                    getSoundManager().stopBackgroundMusic();
                    if (isQuit == 0) {
                        returnCode = 1;
                        break;
                    }
                    LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
                        + " score screen at " + FPS + " fps, with a score of "
                        + gameState.getScore() + ", "
                        + gameState.getHp() + " lives remaining, "
                        + gameState.getBulletsShot() + " bullets shot and "
                        + gameState.getShipsDestroyed() + " ships destroyed."
                        + "Total survival time: " + (int) gameState.getSurvivalTime());
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