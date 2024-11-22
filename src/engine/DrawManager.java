package engine;

import Item.Item;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import screen.Screen;
import entity.Entity;

/**
 * Manages screen drawing.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public final class DrawManager {

    /** Singleton instance of the class. */
    private static DrawManager instance;
    /** Current frame. */
    private static Frame frame;
    /** FileManager instance. */
    private static FileManager fileManager;
    /** Application logger. */
    private static Logger logger;
    /** Graphics context. */
    private static Graphics graphics;
    /** Buffer Graphics. */
    private static Graphics backBufferGraphics;
    /** Buffer image. */
    private static BufferedImage backBuffer;
    /** Normal sized font. */
    private static Font fontRegular;
    /** Normal sized font properties. */
    private static FontMetrics fontRegularMetrics;
    /** Big sized font. */
    private static Font fontBig;
    /** Big sized font properties. */
    private static FontMetrics fontBigMetrics;

    /** Sprite types mapped to their images. */
    private static Map<SpriteType, boolean[][][]> spriteMap;

    /** Sprite types. */
    public static enum SpriteType {
        /** 상단을 향한 플레이어 함선 */
        Ship,
        /** 좌상단을 향한 플레이어 함선 */
        ShipDiagonal,
        /** 파괴된 플레이어 함선 (상향) */
        ShipDestroyed,
        /* 파괴된 플레이어 함선 (좌상향) */
        ShipDiagonalDestroyed,
        /** 상단을 향한 플레이어 탄막 */
        Bullet,
        /** 좌상단을 향한 플레이어 탄막 */
        BulletDiagonal,
        /** Enemy bullet. */
        EnemyBullet,
        /** First enemy ship - first form. */
        EnemyShipA1,
        /** First enemy ship - second form. */
        EnemyShipA2,
        /** Second enemy ship - first form. */
        EnemyShipB1,
        /** Second enemy ship - second form. */
        EnemyShipB2,
        /** Third enemy ship - first form. */
        EnemyShipC1,
        /** Third enemy ship - second form. */
        EnemyShipC2,
        /** Bonus ship. */
        EnemyShipSpecial,
        /** Destroyed enemy ship. */
        Explosion,
        // 공속증가 아이템
        AttackSpeedUpItem
    }

    /**
     * Private constructor.
     */
    private DrawManager() {
        fileManager = Core.getFileManager();
        logger = Core.getLogger();
        logger.info("Started loading resources.");

        try {
            spriteMap = new LinkedHashMap<>();

            spriteMap.put(SpriteType.Ship, new boolean[2][13][13]);
            spriteMap.put(SpriteType.ShipDiagonal, new boolean[1][13][13]);
            spriteMap.put(SpriteType.ShipDestroyed, new boolean[1][16][13]);
            spriteMap.put(SpriteType.ShipDiagonalDestroyed, new boolean[1][15][15]);
            spriteMap.put(SpriteType.Bullet, new boolean[1][2][4]);
            spriteMap.put(SpriteType.BulletDiagonal, new boolean[1][4][4]);
            spriteMap.put(SpriteType.EnemyBullet, new boolean[1][3][5]);
            spriteMap.put(SpriteType.EnemyShipA1, new boolean[1][12][8]);
            spriteMap.put(SpriteType.EnemyShipA2, new boolean[1][12][8]);
            spriteMap.put(SpriteType.EnemyShipB1, new boolean[1][12][8]);
            spriteMap.put(SpriteType.EnemyShipB2, new boolean[1][12][8]);
            spriteMap.put(SpriteType.EnemyShipC1, new boolean[1][12][8]);
            spriteMap.put(SpriteType.EnemyShipC2, new boolean[1][12][8]);
            spriteMap.put(SpriteType.EnemyShipSpecial, new boolean[1][16][7]);
            spriteMap.put(SpriteType.Explosion, new boolean[1][13][7]);
            // 공속 증가 아이템 스프라이트
            spriteMap.put(SpriteType.AttackSpeedUpItem, new boolean[1][10][10]);

            fileManager.loadSprite(spriteMap);
            logger.info("Finished loading the sprites.");

            // Font loading.
            fontRegular = fileManager.loadFont(14f);
            fontBig = fileManager.loadFont(24f);
            logger.info("Finished loading the fonts.");

        } catch (IOException e) {
            logger.warning("Loading failed.");
        } catch (FontFormatException e) {
            logger.warning("Font formating failed.");
        }
    }

    /**
     * Returns shared instance of DrawManager.
     *
     * @return Shared instance of DrawManager.
     */
    protected static DrawManager getInstance() {
        if (instance == null) {
            instance = new DrawManager();
        }
        return instance;
    }

    /**
     * Sets the frame to draw the image on.
     *
     * @param currentFrame Frame to draw on.
     */
    public void setFrame(final Frame currentFrame) {
        frame = currentFrame;
    }

    /**
     * First part of the drawing process. Initializes buffers, draws the background and prepares the
     * images.
     *
     * @param screen Screen to draw in.
     */
    public void initDrawing(final Screen screen) {
        backBuffer = new BufferedImage(screen.getWidth(), screen.getHeight(),
            BufferedImage.TYPE_INT_RGB);

        graphics = frame.getGraphics();
        backBufferGraphics = backBuffer.getGraphics();

        backBufferGraphics.setColor(Color.BLACK);
        backBufferGraphics
            .fillRect(0, 0, screen.getWidth(), screen.getHeight());

        fontRegularMetrics = backBufferGraphics.getFontMetrics(fontRegular);
        fontBigMetrics = backBufferGraphics.getFontMetrics(fontBig);

        // drawBorders(screen);
        // drawGrid(screen);
    }

    /**
     * Draws the completed drawing on screen.
     *
     * @param screen Screen to draw on.
     */
    public void completeDrawing(final Screen screen) {
        graphics.drawImage(backBuffer, frame.getInsets().left,
            frame.getInsets().top, frame);
    }

    /**
     * Draws an entity, using the appropriate image.
     *
     * @param entity    Entity to be drawn.
     * @param positionX Coordinates for the left side of the image.
     * @param positionY Coordinates for the upper side of the image.
     */
    public void drawEntity(final Entity entity, final int positionX,
        final int positionY) {
        boolean[][][] image = spriteMap.get(entity.getSpriteType());

//        backBufferGraphics.setColor(entity.getColor());
        Entity.Direction direction = entity.getDirection();

        // TODO: Ship 이외의 스프라이트들 깨짐 해결
        switch (direction) {
            case UP:
            case UP_LEFT:
                for (int layerNum = 0; layerNum < image.length; layerNum++) {
                    backBufferGraphics.setColor(entity.getColor()[layerNum]);
                    for (int row = 0; row < image[layerNum].length; row++) {
                        for (int column = 0; column < image[layerNum][row].length; column++) {
                            if (image[layerNum][row][column]) {
                                backBufferGraphics.drawRect(positionX + row * 2, positionY
                                    + column * 2, 1, 1);
                            }
                        }
                    }
                }
                break;
            case DOWN:
            case DOWN_RIGHT:
                for (int layerNum = 0; layerNum < image.length; layerNum++) {
                    backBufferGraphics.setColor(entity.getColor()[layerNum]);
                    for (int row = image[layerNum].length - 1; row >= 0; row--) {
                        for (int column = image[layerNum][row].length - 1; column >= 0; column--) {
                            if (image[layerNum][image[layerNum].length - 1 - row][image[layerNum][row].length - 1 - column]) {
                                backBufferGraphics.drawRect(positionX + row * 2, positionY
                                    + column * 2, 1, 1);
                            }
                        }
                    }
                }
                break;
            case LEFT:
            case DOWN_LEFT:
                for (int layerNum = 0; layerNum < image.length; layerNum++) {
                    backBufferGraphics.setColor(entity.getColor()[layerNum]);
                    for (int row = 0; row < image[layerNum].length; row++) {
                        for (int column = 0; column < image[layerNum][row].length; column++) {
                            if (image[layerNum][row][column]) {
                                backBufferGraphics.drawRect(positionX + column * 2, positionY
                                    + (image[layerNum].length - 1 - row) * 2, 1, 1);
                            }
                        }
                    }
                }
                break;
            case RIGHT:
            case UP_RIGHT:
                for (int layerNum = 0; layerNum < image.length; layerNum++) {
                    backBufferGraphics.setColor(entity.getColor()[layerNum]);
                    for (int row = 0; row < image[layerNum].length; row++) {
                        for (int column = 0; column < image[layerNum][row].length; column++) {
                            if (image[layerNum][row][column]) {
                                backBufferGraphics.drawRect(
                                    positionX + (image[layerNum][row].length - 1 - column) * 2,
                                    positionY + row * 2,
                                    1, 1
                                );
                            }
                        }
                    }
                }
                break;
        }
    }

    /**
     * For debugging purposes, draws the canvas borders.
     *
     * @param screen Screen to draw in.
     */
    @SuppressWarnings("unused")
    private void drawBorders(final Screen screen) {
        backBufferGraphics.setColor(Color.GREEN);
        backBufferGraphics.drawLine(0, 0, screen.getWidth() - 1, 0);
        backBufferGraphics.drawLine(0, 0, 0, screen.getHeight() - 1);
        backBufferGraphics.drawLine(screen.getWidth() - 1, 0,
            screen.getWidth() - 1, screen.getHeight() - 1);
        backBufferGraphics.drawLine(0, screen.getHeight() - 1,
            screen.getWidth() - 1, screen.getHeight() - 1);
    }

    /**
     * For debugging purposes, draws a grid over the canvas.
     *
     * @param screen Screen to draw in.
     */
    @SuppressWarnings("unused")
    private void drawGrid(final Screen screen) {
        backBufferGraphics.setColor(Color.DARK_GRAY);
        for (int i = 0; i < screen.getHeight() - 1; i += 2) {
            backBufferGraphics.drawLine(0, i, screen.getWidth() - 1, i);
        }
        for (int j = 0; j < screen.getWidth() - 1; j += 2) {
            backBufferGraphics.drawLine(j, 0, j, screen.getHeight() - 1);
        }
    }

    /**
     * Draws current score on screen.
     *
     * @param screen Screen to draw on.
     * @param score  Current score.
     */
    public void drawScore(final Screen screen, final int score) {
        backBufferGraphics.setFont(fontRegular);
        backBufferGraphics.setColor(Color.WHITE);
        String scoreString = String.format("%04d", score);
        backBufferGraphics.drawString(scoreString, screen.getWidth() - 60, 25);
    }

    /**
     * Draws number of remaining lives on screen.
     *
     * @param screen Screen to draw on.
     * @param lives  Current lives.
     */
    public void drawLives(final Screen screen, final int lives) {
        backBufferGraphics.setFont(fontRegular);

        int barX = 10; // 체력 바의 X 좌표
        int barY = 10; // 체력 바의 Y 좌표
        int barWidth = 200; // 체력 바의 최대 너비
        int barHeight = 20; // 체력 바의 높이
        int hp = Core.getStatusManager().getHp(); // 최대 체력

        // 체력 바의 테두리 그리기
        backBufferGraphics.setColor(Color.GRAY);
        backBufferGraphics.drawRect(barX, barY, barWidth, barHeight);

        // 현재 체력에 따른 바의 너비 계산
        int healthWidth = (int) ((double) lives / hp * barWidth);

        // 체력 바 채우기
        backBufferGraphics.setColor(Color.RED); // 체력 바의 색상
        backBufferGraphics.fillRect(barX + 1, barY + 1, healthWidth - 1, barHeight - 1);

        // 체력 수치 표시
        backBufferGraphics.setColor(Color.WHITE);
        String hpText = +lives + "/" + hp;
        int textX = barX + (barWidth - fontRegularMetrics.stringWidth(hpText)) / 2;
        int textY = barY + ((barHeight - fontRegularMetrics.getHeight()) / 2)
            + fontRegularMetrics.getAscent();
        backBufferGraphics.drawString(hpText, textX, textY);
    }

    /**
     * Draws a thick line from side to side of the screen.
     *
     * @param screen    Screen to draw on.
     * @param positionY Y coordinate of the line.
     */
    public void drawHorizontalLine(final Screen screen, final int positionY) {
        backBufferGraphics.setColor(Color.GREEN);
        backBufferGraphics.drawLine(0, positionY, screen.getWidth(), positionY);
        backBufferGraphics.drawLine(0, positionY + 1, screen.getWidth(),
            positionY + 1);
    }

    /**
     * Draws game title.
     *
     * @param screen Screen to draw on.
     */
    public void drawTitle(final Screen screen) {
        String titleString = "Invaders";
        String instructionsString =
            "select with w+s / arrows, confirm with space";

        backBufferGraphics.setColor(Color.GRAY);
        drawCenteredRegularString(screen, instructionsString,
            screen.getHeight() / 2);

        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredBigString(screen, titleString, screen.getHeight() / 3);
    }

    /**
     * Draws main menu.
     *
     * @param screen Screen to draw on.
     * @param option Option selected.
     */
    public void drawMenu(final Screen screen, final int option) {
        String playString = "Play";
        String highScoresString = "High scores";
        String settingsString = "Settings"; // Added settings menu
        String exitString = "Exit";

        if (option == 2) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, playString, screen.getHeight() / 3 * 2);

        if (option == 3) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, highScoresString,
            screen.getHeight() / 3 * 2 + fontRegularMetrics.getHeight() * 2);

        if (option == 4) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, settingsString,
            screen.getHeight() / 3 * 2 + fontRegularMetrics.getHeight() * 4);

        if (option == 0) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, exitString,
            screen.getHeight() / 3 * 2 + fontRegularMetrics.getHeight() * 6);
    }

    /**
     * Draws game results.
     *
     * @param screen         Screen to draw on.
     * @param score          Score obtained.
     * @param livesRemaining Lives remaining when finished.
     * @param shipsDestroyed Total ships destroyed.
     * @param accuracy       Total accuracy.
     * @param isNewRecord    If the score is a new high score.
     */
    public void drawResults(final Screen screen, final int score,
        final int livesRemaining, final int shipsDestroyed,
        final float accuracy, final boolean isNewRecord) {
        String scoreString = String.format("score %04d", score);
        String livesRemainingString = "lives remaining " + livesRemaining;
        String shipsDestroyedString = "enemies destroyed " + shipsDestroyed;
        String accuracyString = String
            .format("accuracy %.2f%%", accuracy * 100);

        int height = isNewRecord ? 4 : 2;

        backBufferGraphics.setColor(Color.WHITE);
        drawCenteredRegularString(screen, scoreString, screen.getHeight()
            / height);
        drawCenteredRegularString(screen, livesRemainingString,
            screen.getHeight() / height + fontRegularMetrics.getHeight()
                * 2);
        drawCenteredRegularString(screen, shipsDestroyedString,
            screen.getHeight() / height + fontRegularMetrics.getHeight()
                * 4);
        drawCenteredRegularString(screen, accuracyString, screen.getHeight()
            / height + fontRegularMetrics.getHeight() * 6);
    }

    /**
     * Draws interactive characters for name input.
     *
     * @param screen           Screen to draw on.
     * @param name             Current name selected.
     * @param nameCharSelected Current character selected for modification.
     */
    public void drawNameInput(final Screen screen, final char[] name,
        final int nameCharSelected) {
        String newRecordString = "New Record!";
        String introduceNameString = "Introduce name:";

        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredRegularString(screen, newRecordString, screen.getHeight()
            / 4 + fontRegularMetrics.getHeight() * 10);
        backBufferGraphics.setColor(Color.WHITE);
        drawCenteredRegularString(screen, introduceNameString,
            screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 12);

        // 3 letters name.
        int positionX = screen.getWidth()
            / 2
            - (fontRegularMetrics.getWidths()[name[0]]
            + fontRegularMetrics.getWidths()[name[1]]
            + fontRegularMetrics.getWidths()[name[2]]
            + fontRegularMetrics.getWidths()[' ']) / 2;

        for (int i = 0; i < 3; i++) {
            if (i == nameCharSelected) {
                backBufferGraphics.setColor(Color.GREEN);
            } else {
                backBufferGraphics.setColor(Color.WHITE);
            }

            positionX += fontRegularMetrics.getWidths()[name[i]] / 2;
            positionX = i == 0 ? positionX
                : positionX
                    + (fontRegularMetrics.getWidths()[name[i - 1]]
                    + fontRegularMetrics.getWidths()[' ']) / 2;

            backBufferGraphics.drawString(Character.toString(name[i]),
                positionX,
                screen.getHeight() / 4 + fontRegularMetrics.getHeight()
                    * 14);
        }
    }

    /**
     * Draws basic content of game over screen.
     *
     * @param screen       Screen to draw on.
     * @param acceptsInput If the screen accepts input.
     * @param isNewRecord  If the score is a new high score.
     */
    public void drawGameEnd(final Screen screen, final boolean acceptsInput,
        final boolean isNewRecord, final boolean isGameClear) {
        String gameEndString = isGameClear ? "Game Clear" : "Game Over";
        String continueOrExitString =
            "Press Space to play again, Escape to exit";

        int height = isNewRecord ? 4 : 2;

        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredBigString(screen, gameEndString, screen.getHeight()
            / height - fontBigMetrics.getHeight() * 2);

        if (acceptsInput) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.GRAY);
        }
        drawCenteredRegularString(screen, continueOrExitString,
            screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 10);
    }

    /**
     * Draws high score screen title and instructions.
     *
     * @param screen Screen to draw on.
     */
    public void drawHighScoreMenu(final Screen screen) {
        String highScoreString = "High Scores";
        String instructionsString = "Press Space to return";

        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredBigString(screen, highScoreString, screen.getHeight() / 8);

        backBufferGraphics.setColor(Color.GRAY);
        drawCenteredRegularString(screen, instructionsString,
            screen.getHeight() / 5);
    }

    /**
     * Draws high scores.
     *
     * @param screen     Screen to draw on.
     * @param highScores List of high scores.
     */
    public void drawHighScores(final Screen screen,
        final List<Score> highScores) {
        backBufferGraphics.setColor(Color.WHITE);
        int i = 0;
        String scoreString = "";

        for (Score score : highScores) {
            scoreString = String.format("%s        %04d", score.getName(),
                score.getScore());
            drawCenteredRegularString(screen, scoreString, screen.getHeight()
                / 4 + fontRegularMetrics.getHeight() * (i + 1) * 2);
            i++;
        }
    }

    /**
     * Draws a centered string on regular font.
     *
     * @param screen Screen to draw on.
     * @param string String to draw.
     * @param height Height of the drawing.
     */
    public void drawCenteredRegularString(final Screen screen,
        final String string, final int height) {
        backBufferGraphics.setFont(fontRegular);
        backBufferGraphics.drawString(string, screen.getWidth() / 2
            - fontRegularMetrics.stringWidth(string) / 2, height);
    }

    /**
     * Draws a centered string on big font.
     *
     * @param screen Screen to draw on.
     * @param string String to draw.
     * @param height Height of the drawing.
     */
    public void drawCenteredBigString(final Screen screen, final String string,
        final int height) {
        backBufferGraphics.setFont(fontBig);
        backBufferGraphics.drawString(string, screen.getWidth() / 2
            - fontBigMetrics.stringWidth(string) / 2, height);
    }

    /**
     * Countdown to game start.
     *
     * @param screen    Screen to draw on.
     * @param level     Game difficulty level.
     * @param number    Countdown number.
     * @param bonusLife Checks if a bonus life is received.
     */
    public void drawCountDown(final Screen screen, final int level,
        final int number, final boolean bonusLife) {
        int rectWidth = screen.getWidth();
        int rectHeight = screen.getHeight() / 6;
        backBufferGraphics.setColor(Color.BLACK);
        backBufferGraphics.fillRect(0, screen.getHeight() / 2 - rectHeight / 2,
            rectWidth, rectHeight);
        backBufferGraphics.setColor(Color.GREEN);
        drawHorizontalLine(screen, screen.getHeight() / 2 - screen.getHeight()
            / 12);
        drawHorizontalLine(screen, screen.getHeight() / 2 + screen.getHeight()
            / 12);
        if (number >= 4) {
            if (!bonusLife) {
                drawCenteredBigString(screen, "Level " + level,
                    screen.getHeight() / 2
                        + fontBigMetrics.getHeight() / 3);
            } else {
                drawCenteredBigString(screen, "Level " + level
                        + " - Bonus life!",
                    screen.getHeight() / 2
                        + fontBigMetrics.getHeight() / 3);
            }
        } else if (number != 0) {
            drawCenteredBigString(screen, Integer.toString(number),
                screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3);
        } else {
            drawCenteredBigString(screen, "GO!", screen.getHeight() / 2
                + fontBigMetrics.getHeight() / 3);
        }
    }

    /**
     * 레벨 시작 후 경과 시간을 화면 상단 중앙에 표시합니다.
     *
     * @param screen      화면 객체입니다.
     * @param elapsedTime 경과 시간(초)입니다.
     */
    public void drawTime(final Screen screen, final int elapsedTime) {
        backBufferGraphics.setFont(fontRegular);
        backBufferGraphics.setColor(Color.WHITE);
        String timeString = elapsedTime + " S";

        // 문자열의 너비를 계산하여 중앙에 위치시킵니다.
        int xPosition = (screen.getWidth() - fontRegularMetrics.stringWidth(timeString)) / 2;
        // Y 좌표는 원하는 위치로 설정합니다. 여기서는 상단 여백을 25로 설정했습니다.
        int yPosition = 25;

        backBufferGraphics.drawString(timeString, xPosition, yPosition);
    }

    public void drawItemBox(final int position_X, final int position_Y) {
        backBufferGraphics.drawRect(position_X, position_Y, 100, 100);
    }

    public void drawItemSelectingTitle(final Screen screen, final GameState gameState) {
        String titleString = "Level  " + gameState.getLevel() + "  Clear!!";
        String instructionsString1 =
            "Select your Item with A + D / arrows";
        String instructionsString2 =
            "Press spacebar If you want to select.";
        // 아이템 선택 지시 문구 표시
        backBufferGraphics.setColor(Color.GRAY);
        drawCenteredRegularString(screen, instructionsString1,
            100);
        drawCenteredRegularString(screen, instructionsString2, screen.getHeight() - 50);
        // 레벨 클리어 문구 표시
        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredBigString(screen, titleString, 50);

    }

    public void drawSelectedItem(final Screen screen, final List<Item> itemList,
        final int selectedItem) {
        if (itemList.size() < 3) {
            String alarmString = "Only  " + itemList.size() + "  items left you can upgrade!!";
            drawCenteredRegularString(screen, alarmString,
                150);
        }

        // 첫 아이템 선택여부
        if (selectedItem == 0) {
            backBufferGraphics.setColor(Color.WHITE);
            // 아이템 설명 출력
            drawCenteredRegularString(screen, itemList.get(selectedItem).getItemDescription(),
                screen.getHeight() / 3 + 200);
            drawCenteredBigString(screen, itemList.get(selectedItem).getItemEffectDescription(),
                screen.getHeight() / 3 + 300);
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        // 첫 아이템 그리기
        drawItemBox((screen.getWidth() / 4) - 50, screen.getHeight() / 3);

        // 두번째 아이템 선택여부
        if (selectedItem == 1) {
            backBufferGraphics.setColor(Color.WHITE);
            // 아이템 설명 출력
            drawCenteredRegularString(screen, itemList.get(selectedItem).getItemDescription(),
                screen.getHeight() / 3 + 200);
            drawCenteredBigString(screen, itemList.get(selectedItem).getItemEffectDescription(),
                screen.getHeight() / 3 + 300);
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        // 두 번째 아이템 그리기
        drawItemBox((screen.getWidth() * 2 / 4) - 50, screen.getHeight() / 3);

        // 세번째 아이템 선택여부
        if (selectedItem == 2) {
            backBufferGraphics.setColor(Color.WHITE);
            // 아이템 설명 출력
            drawCenteredRegularString(screen, itemList.get(selectedItem).getItemDescription(),
                screen.getHeight() / 3 + 200);
            drawCenteredBigString(screen, itemList.get(selectedItem).getItemEffectDescription(),
                screen.getHeight() / 3 + 300);
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        //세 번째 아이템 그리기
        drawItemBox((screen.getWidth() * 3) / 4 - 50, screen.getHeight() / 3);
    }

    // 각 아이템을 화면에 그리는 메소드
    public void drawItems(final Screen screen, final List<Item> itemList) {
        for (int i = 0; i < itemList.size(); i++) {
            drawBigItem(itemList.get(i), (screen.getWidth() * (i + 1) / 4 - 30),
                screen.getHeight() / 3 + 25);
            String levelInformation = "Level " + itemList.get(i).getLevel();
            backBufferGraphics.setColor(Color.ORANGE);
            backBufferGraphics.setFont(fontRegular);
            backBufferGraphics.drawString(levelInformation, (screen.getWidth() * (i + 1) / 4 - 33),
                screen.getHeight() / 3 + 120);
        }
    }

    // 아이템의 스프라이트를 받아와 스케일 업해서 그리는 메소드
    public void drawBigItem(final Item item, final int position_X, final int position_Y) {
        boolean[][][] image = spriteMap.get(item.getSpriteType());

        backBufferGraphics.setColor(item.getColor());
        for (boolean [][] layer: image) {
            for (int i = 0; i < layer.length; i++) {
                for (int j = 0; j < layer[i].length; j++) {
                    if (layer[i][j]) {
                        backBufferGraphics.drawRect(position_X + i * 6, position_Y
                            + j * 6, 6, 6);
                    }
                }
            }
        }
    }

    /**
     * 설정 메뉴를 그려주며, 사용자에게 배경음악 및 효과음 볼륨을 조정할 수 있도록 합니다.
     *
     * @param screen         설정 화면을 그릴 스크린 객체
     * @param selectedOption 현재 선택된 옵션 (0: 배경음악, 1: 효과음)
     */
    public void drawSettingsMenu(final Screen screen, final int selectedOption) {
        // 설정 화면 제목 및 안내 문자열
        String settingsTitle = "Settings";
        String instructionsString = "Use UP/DOWN to switch, LEFT/RIGHT to adjust, SPACE to exit";

        // 설정 제목과 안내 문자열 표시
        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredBigString(screen, settingsTitle, screen.getHeight() / 8);

        backBufferGraphics.setColor(Color.GRAY);
        drawCenteredRegularString(screen, instructionsString, screen.getHeight() / 5);

        // 볼륨 바에 대한 공통 설정 변수
        int boxSize = 20;
        int spacing = 5;
        int totalBoxes = 10;
        int barX = (screen.getWidth() - (boxSize + spacing) * totalBoxes + spacing) / 2;

        // 배경음악(BGM) 볼륨 레이블 및 바 표시
        String bgmLabel = "Background Music Volume";
        int filledBoxesBGM = (int) (Core.getSoundManager().getBackgroundMusicVolume() * totalBoxes);
        int bgmBarY = screen.getHeight() / 3;

        // 배경음악 볼륨 바 그리기
        drawVolumeBar(screen, bgmLabel, filledBoxesBGM, barX, bgmBarY, selectedOption == 0);

        // 효과음(SFX) 볼륨 레이블 및 바 표시
        String sfxLabel = "Sound Effects Volume";
        int filledBoxesSFX = (int) (Core.getSoundManager().getSoundEffectsVolume() * totalBoxes);
        int sfxBarY = screen.getHeight() / 2;

        // 효과음 볼륨 바 그리기
        drawVolumeBar(screen, sfxLabel, filledBoxesSFX, barX, sfxBarY, selectedOption == 1);
    }

    /**
     * 볼륨 바와 레이블을 그려주며, 현재 볼륨 레벨을 표시합니다.
     *
     * @param screen      볼륨 바를 그릴 스크린 객체
     * @param label       볼륨 바에 대한 레이블 (예: "배경음악 볼륨")
     * @param filledBoxes 현재 볼륨을 나타내는 채워진 박스 개수
     * @param barX        볼륨 바의 X 좌표
     * @param barY        볼륨 바의 Y 좌표
     * @param isSelected  현재 볼륨 바가 선택되어 있는지 여부
     */
    private void drawVolumeBar(final Screen screen, String label, int filledBoxes, int barX,
        int barY, boolean isSelected) {
        // 선택된 항목에 따라 색상 설정
        backBufferGraphics.setColor(isSelected ? Color.GREEN : Color.GRAY);
        drawCenteredRegularString(screen, label, barY - fontRegularMetrics.getHeight());

        // 볼륨 바의 박스들을 그려줌
        for (int i = 0; i < 10; i++) {
            if (i < filledBoxes) {
                backBufferGraphics.fillRect(barX + (20 + 5) * i, barY, 20, 20); // 채워진 박스
            } else {
                backBufferGraphics.drawRect(barX + (20 + 5) * i, barY, 20, 20); // 빈 박스
            }
        }
    }

    // 각 아이템을 그리는 메소드
    public void drawItem(final int item, final int position_X, final int position_Y) {
        // drawEntity로 해당 Item 스프라이트를 그림
        // drawString 으로 해당 Item에 대한 설명 및 정보를 화면 아래 부분에 추가
    }

    /**
     * PauseScreen의 제목을 화면 상단에 위치시키는 메소드
     *
     * @param screen 화면 객체를 받는 매개변수
     */
    public void drawPauseTitle(final Screen screen) {
        String titleString = "Pause";

        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredBigString(screen, titleString, screen.getHeight() / 3);
    }

    /**
     * PauseScreen의 메뉴를 표시하는 메소드
     *
     * @param screen 화면 객체를 받는 매개변수
     * @param option 어떤 메뉴를 선택했는지 구분하는 매개변수
     */
    public void drawPauseMenu(final Screen screen, final int option) {
        String resume = "Resume";
        String quit = "Quit";
        String setting = "Setting";

        if (option == 0) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, resume,
            screen.getHeight() / 3 * 2);
        if (option == 1) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, setting, screen.getHeight()
            / 3 * 2 + fontRegularMetrics.getHeight() * 2);
        if (option == 2) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, quit, screen.getHeight()
            / 3 * 2 + fontRegularMetrics.getHeight() * 4);
    }

    /**
     * WarningScreen의 제목과 경고문을 화면 상단에 위치시키는 메소드
     *
     * @param screen 화면 객체를 받는 매개변수
     */
    public void drawWarningTitle(final Screen screen) {
        String titleString = "Warning";
        String instructionsString1 =
            "When you leave the game,";
        String instructionsString2 = "the information or money you get from this game will disappear.";
        String instructionsString3 =
            "Are you sure you want to exit?";

        backBufferGraphics.setColor(Color.RED);
        drawCenteredBigString(screen, titleString, screen.getHeight() / 3);
        backBufferGraphics.setColor(Color.GRAY);
        drawCenteredRegularString(screen, instructionsString1,
            screen.getHeight() / 3 + fontRegularMetrics.getHeight() * 2);
        drawCenteredRegularString(screen, instructionsString2,
            screen.getHeight() / 3 + fontRegularMetrics.getHeight() * 4);
        drawCenteredRegularString(screen, instructionsString3,
            screen.getHeight() / 2);
    }

    /**
     * WarningScreen의 메뉴를 표시하는 메소드
     *
     * @param screen 화면 객체를 받는 매개변수
     * @param option 어떤 메뉴를 선택했는지 구분하는 매개변수
     */
    public void drawWarningMenu(final Screen screen, final int option) {
        String quit = "Quit";
        String cancel = "Cancel";

        if (option == 0) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, quit,
            screen.getHeight() / 3 * 2);
        if (option == 1) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, cancel, screen.getHeight()
            / 3 * 2 + fontRegularMetrics.getHeight() * 2);
    }
}
