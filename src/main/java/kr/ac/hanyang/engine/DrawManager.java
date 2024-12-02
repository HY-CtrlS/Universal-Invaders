package kr.ac.hanyang.engine;

import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import kr.ac.hanyang.Item.Item;
import kr.ac.hanyang.entity.boss.Boss;
import kr.ac.hanyang.entity.boss.Missile;
import kr.ac.hanyang.entity.ship.Ship;
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

import kr.ac.hanyang.screen.Screen;
import kr.ac.hanyang.entity.Entity;

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

    /** 배경 이미지. */
    private BufferedImage backgroundImage;

    // DrawManager 클래스의 전역 변수 추가
    private int titleTypingIndex = 0; // 현재 출력된 글자 수
    private long lastUpdateTime = 0; // 마지막 글자 업데이트 시점
    private static final long TYPING_DELAY = 80; // 글자 간 출력 지연 (밀리초)
    private boolean showStartMessage = true; // 메시지 표시 여부
    private long lastBlinkTime = 0;          // 마지막 깜빡임 시간
    private static final int BLINK_DELAY = 1000; // 메시지 깜빡임 주기 (1초)

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
        /** 경험치 A */
        ExperienceA,
        /** 경험치 B */
        ExperienceB,
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
        AttackSpeedUpItem,
        // 장애물 스프라이트
        Obstacle,
        // 다중 색 레이어 포탈
        Portal,
        // 보스
        Boss,
        // 레이저
        Laser,
        // 레이저 위치 경고용
        WarningLaser,
        // 미사일
        Missile,
        // 무적 크리스탈
        Crystal,
        // 배리어용 소행성
        Asteroid;
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

            spriteMap.put(SpriteType.Ship, new boolean[1][13][13]);
            spriteMap.put(SpriteType.ShipDiagonal, new boolean[1][13][13]);
            spriteMap.put(SpriteType.ShipDestroyed, new boolean[1][16][13]);
            spriteMap.put(SpriteType.ShipDiagonalDestroyed, new boolean[1][15][15]);
            spriteMap.put(SpriteType.Bullet, new boolean[1][2][4]);
            spriteMap.put(SpriteType.BulletDiagonal, new boolean[1][4][4]);
            spriteMap.put(SpriteType.ExperienceA, new boolean[1][7][7]);
            spriteMap.put(SpriteType.ExperienceB, new boolean[1][7][7]);
            spriteMap.put(SpriteType.EnemyBullet, new boolean[1][3][3]);
            spriteMap.put(SpriteType.EnemyShipA1, new boolean[1][12][8]);
            spriteMap.put(SpriteType.EnemyShipA2, new boolean[1][12][8]);
            spriteMap.put(SpriteType.EnemyShipB1, new boolean[2][24][16]);
            spriteMap.put(SpriteType.EnemyShipB2, new boolean[2][24][16]);
            spriteMap.put(SpriteType.EnemyShipC1, new boolean[1][8][8]);
            spriteMap.put(SpriteType.EnemyShipC2, new boolean[1][8][8]);
            spriteMap.put(SpriteType.EnemyShipSpecial, new boolean[1][16][7]);
            spriteMap.put(SpriteType.Explosion, new boolean[1][13][7]);
            // 공속 증가 아이템 스프라이트
            spriteMap.put(SpriteType.AttackSpeedUpItem, new boolean[1][10][10]);
            // 장애물 스프라이트
            spriteMap.put(SpriteType.Obstacle, new boolean[2][10][10]);
            // 포탈 스프라이트
            spriteMap.put(SpriteType.Portal, new boolean[3][21][30]);
            // 보스 스프라이트
            spriteMap.put(SpriteType.Boss, new boolean[1][40][46]);
            spriteMap.put(SpriteType.Laser, new boolean[2][10][720]);
            spriteMap.put(SpriteType.WarningLaser, new boolean[1][10][720]);
            spriteMap.put(SpriteType.Missile, new boolean[2][7][9]);
            spriteMap.put(SpriteType.Crystal, new boolean[2][20][20]);
            spriteMap.put(SpriteType.Asteroid, new boolean[2][10][10]);

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
                            if (image[layerNum][image[layerNum].length - 1 - row][
                                image[layerNum][row].length - 1 - column]) {
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
     * Draws current ultimate skill gauge on screen.
     *
     * @param ship Current player ship.
     */
    public void drawUltGauge(final Ship ship, final int X, final int Y) {
        backBufferGraphics.setFont(fontRegular);

        int barX = X; // 궁극기 바의 X 좌표
        int barY = Y; // 궁극기 바의 Y 좌표
        int barWidth = 200; // 궁극기 바의 최대 너비
        int barHeight = 20; // 궁극기 바의 높이
        int ult = ship.getUltThreshold(); // 궁극기 가능 기준치

        // 궁극기 바의 테두리 그리기
        backBufferGraphics.setColor(Color.GRAY);
        backBufferGraphics.drawRect(barX, barY, barWidth, barHeight);

        // 현재 궁극기 게이지에 따른 바의 너비 계산
        int ultWidth = (int) ((double) ship.getUltGauge() / ult * barWidth);

        // 궁극기 바 채우기
        backBufferGraphics.setColor(Color.BLUE); // 궁극기 바의 색상
        backBufferGraphics.fillRect(barX + 1, barY + 1, ultWidth - 1, barHeight - 1);

        // 궁극기 게이지 표시
        backBufferGraphics.setColor(Color.WHITE);
        String ultText = +ship.getUltGauge() + "/" + ult;
        int textX = barX + (barWidth - fontRegularMetrics.stringWidth(ultText)) / 2;
        int textY = barY + ((barHeight - fontRegularMetrics.getHeight()) / 2)
            + fontRegularMetrics.getAscent();
        backBufferGraphics.drawString(ultText, textX, textY);
    }

    /**
     * Draws number of remaining lives on screen.
     *
     * @param lives Current lives.
     */
    public void drawLives(final int X, final int Y, final int lives) {
        backBufferGraphics.setFont(fontRegular);

        int barX = X; // 체력 바의 X 좌표
        int barY = Y; // 체력 바의 Y 좌표
        int barWidth = 200; // 체력 바의 최대 너비
        int barHeight = 20; // 체력 바의 높이
        int hp = Core.getStatusManager().getMaxHp(); // 최대 체력

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
        String titleString = "Universal Invaders";
        String instructionsString =
            "select with w+s / arrows, confirm with space";

        backBufferGraphics.setColor(Color.GRAY);
        drawCenteredRegularString(screen, instructionsString,
            screen.getHeight() / 4);

        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredBigString(screen, titleString, screen.getHeight() / 6);
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
        drawCenteredRegularString(screen, playString, screen.getHeight() / 5 * 2);

        if (option == 3) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, highScoresString,
            screen.getHeight() / 5 * 2 + fontRegularMetrics.getHeight() * 2);

        if (option == 4) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, settingsString,
            screen.getHeight() / 5 * 2 + fontRegularMetrics.getHeight() * 4);

        if (option == 0) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, exitString,
            screen.getHeight() / 5 * 2 + fontRegularMetrics.getHeight() * 6);
    }

    /**
     * Draws game results.
     *
     * @param screen         Screen to draw on.
     * @param shipsDestroyed Total ships destroyed.
     * @param isNewRecord    If the score is a new high score.
     */
    public void drawResults(final Screen screen,
        final int shipsDestroyed, final int survivalTime, final boolean isNewRecord) {

        String shipsDestroyedString = "enemies destroyed " + shipsDestroyed;
        String survivalTimeString = "Survival Time: " + survivalTime + " s";

        int height = isNewRecord ? 4 : 2;

        drawCenteredRegularString(screen, survivalTimeString,
            screen.getHeight() / height + fontRegularMetrics.getHeight()
                * 2);
        backBufferGraphics.setColor(Color.WHITE);

        drawCenteredRegularString(screen, shipsDestroyedString,
            screen.getHeight() / height + fontRegularMetrics.getHeight()
                * 4);

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
            scoreString = String.format("%s        %d sec",
                score.getName(),
                score.getSurvivalTime());

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
     * @param screen Screen to draw on.
     * @param number Countdown number.
     */
    public void drawCountDown(final Screen screen, final int number) {
        int rectWidth = screen.getWidth();
        int rectHeight = screen.getHeight() / 6;
        backBufferGraphics.setColor(Color.BLACK);
        backBufferGraphics.fillRect(0, screen.getHeight() / 2 - rectHeight / 2,
            rectWidth, rectHeight);
        backBufferGraphics.setColor(Color.GREEN);
        drawHorizontalLine(screen, screen.getHeight() / 2 - screen.getHeight() / 12);
        drawHorizontalLine(screen, screen.getHeight() / 2 + screen.getHeight() / 12);
        if (number >= 4) {
            drawCenteredBigString(screen, "Are you ready?",
                screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3);
        } else if (number != 0) {
            drawCenteredBigString(screen, Integer.toString(number),
                screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3);
        } else {
            drawCenteredBigString(screen, "GO!", screen.getHeight() / 2
                + fontBigMetrics.getHeight() / 3);
        }
    }

    /**
     * 현재 생존 시간을 화면에 그립니다.
     *
     * @param screen       화면 객체
     * @param survivalTime 현재 생존 시간
     */
    public void drawSurvivalTime(final Screen screen, final int survivalTime) {
        backBufferGraphics.setFont(fontRegular);
        backBufferGraphics.setColor(Color.WHITE);
        String survivalTimeString = String.format("%d S", survivalTime);
        backBufferGraphics.drawString(survivalTimeString, screen.getWidth() - 60, 25);
    }

    public void drawItemBox(final int position_X, final int position_Y) {
        backBufferGraphics.drawRect(position_X, position_Y, 100, 100);
    }

    public void drawItemSelectingTitle(final Screen screen, final int playerLevel) {
        String titleString = "Level  " + playerLevel + "  !!";
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
        if (itemList.size() != 0) {
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
        } else {
            String alarmString = "There are no items left";
            drawCenteredRegularString(screen, alarmString,
                150);
        }
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
        for (boolean[][] layer : image) {
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
        backBufferGraphics.setColor(Color.WHITE);
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

    /**
     * Draws the player's current level on screen.
     *
     * @param screen Screen to draw on.
     * @param level  Current level of the player.
     */
    public void drawLevel(final Screen screen, final int level) {
        backBufferGraphics.setFont(fontRegular);
        String levelText = "LV. " + level; // 표시할 텍스트

        // 텍스트 색상 설정
        backBufferGraphics.setColor(Color.WHITE);

        // 텍스트를 화면에 그리기
        drawCenteredRegularString(screen, levelText, 25);
    }

    /**
     * Draws the experience bar on the screen.
     *
     * @param screen              Screen to draw on.
     * @param currentExperience   Current experience points of the player.
     * @param experienceThreshold Experience threshold for the next level.
     */
    public void drawExperienceBar(final Screen screen, final int currentExperience,
        final int experienceThreshold, final int barHeight) {
        backBufferGraphics.setFont(fontRegular);

        // 경험치 바의 위치와 크기 설정
        int barX = 0; // 화면 왼쪽
        int barY = screen.getHeight() - barHeight; // 화면 하단에서 경험치 바 크기 위
        int barWidth = screen.getWidth(); // 화면 전체 너비

        // 경험치 비율 계산
        double experienceRatio = (double) currentExperience / experienceThreshold;
        int filledWidth = (int) (experienceRatio * barWidth);

        // 경험치 바 배경 (검은색으로 전체 채우기)
        backBufferGraphics.setColor(Color.BLACK);
        backBufferGraphics.fillRect(barX, barY + 1, barWidth, barHeight - 1);

        // 경험치 바 채워진 부분 (초록색)
        backBufferGraphics.setColor(Color.GREEN);
        backBufferGraphics.fillRect(barX, barY, filledWidth, barHeight);

        // 경험치 텍스트 (중앙에 표시)
        String expText = currentExperience + " / " + experienceThreshold + " EXP";
        int textX = barX + (barWidth - fontRegularMetrics.stringWidth(expText)) / 2;
        int textY = barY + ((barHeight - fontRegularMetrics.getHeight()) / 2)
            + fontRegularMetrics.getAscent();

        // 경험치 텍스트 색상
        backBufferGraphics.setColor(Color.WHITE);
        backBufferGraphics.drawString(expText, textX, textY);
    }

    /**
     * shipSelectScreen의 제목을 화면 상단에 위치시키는 메소드
     *
     * @param screen 화면 객체를 받는 매개변수
     */
    public void drawShipSelectTitle(final Screen screen) {
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
     * shipSelectScreen의 메뉴를 표시하는 메소드
     *
     * @param screen 화면 객체를 받는 매개변수
     * @param option 어떤 메뉴를 선택했는지 구분하는 매개변수
     */
    public void drawShipSelectMenu(final Screen screen, final int option, final int shipID) {
        String playString = "Play";
        String highScoresString = "Select ship";
        String[] shipColors = {"GREEN", "BLUE", "YELLOW", "RED"};

        if (option == 1) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, playString, screen.getHeight() / 3 * 2);

        if (option == 0) {
            backBufferGraphics.setColor(Color.GREEN);
        } else {
            backBufferGraphics.setColor(Color.WHITE);
        }
        drawCenteredRegularString(screen, highScoresString,
            screen.getHeight() / 3 * 2 + fontRegularMetrics.getHeight() * 2);
        switch (shipColors[shipID - 1]) {
            case "GREEN":
                backBufferGraphics.setColor(Color.GREEN);
                break;
            case "BLUE":
                backBufferGraphics.setColor(Color.BLUE);
                break;
            case "YELLOW":
                backBufferGraphics.setColor(Color.YELLOW);
                break;
            case "RED":
                backBufferGraphics.setColor(Color.RED);
                break;
        }
        drawCenteredRegularString(screen, shipColors[shipID - 1],
            screen.getHeight() / 3 * 2 + fontRegularMetrics.getHeight() * 4);
    }

    public void setSplashImage() {
        try {
            // 고정된 splash 이미지 로드
            backgroundImage = ImageIO.read(getClass().getResource("/splash_image.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load splash image.");
        }
    }

    public void drawBackgroundImage(final Screen screen, int offsetY) {
        if (backgroundImage != null) {
            // 이미지 크기
            int imageWidth = backgroundImage.getWidth();
            int imageHeight = backgroundImage.getHeight();

            // 화면 크기
            int screenWidth = screen.getWidth();
            int screenHeight = screen.getHeight();

            // 스케일 설정 (1.0 이하로 설정하면 축소됨)
            double scale = 0.7; // 이미지 크기를 50%로 축소

            // 스케일링된 이미지 크기
            int scaledWidth = (int) (imageWidth * scale);
            int scaledHeight = (int) (imageHeight * scale);

            // 이미지를 중앙에 배치하기 위한 좌표 계산
            int x = (screenWidth - scaledWidth) / 2; // 화면 중앙의 X 좌표
            int y = (screenHeight - scaledHeight) / 2 + offsetY; // 화면 중앙의 Y 좌표에 오프셋 추가

            // Graphics2D를 사용하여 스케일링된 이미지 그리기
            Graphics2D g2d = (Graphics2D) backBufferGraphics;
            g2d.drawImage(backgroundImage, x, y, scaledWidth, scaledHeight, null);
        } else {
            // 배경 이미지가 없을 경우 기본 색상으로 화면 채움
            backBufferGraphics.setColor(Color.BLACK);
            backBufferGraphics.fillRect(0, 0, screen.getWidth(), screen.getHeight());
        }
    }

    // 초기화 메서드
    public void resetGameTitleAnimation() {
        titleTypingIndex = 0;
        lastUpdateTime = System.currentTimeMillis();
    }

    public boolean drawGameTitle(final Screen screen, String titleString) {
        // 현재 시간
        long currentTime = System.currentTimeMillis();

        // 타이틀 글자 업데이트 조건 확인
        if (currentTime - lastUpdateTime >= TYPING_DELAY) {
            if (titleTypingIndex < titleString.length()) {
                titleTypingIndex++; // 다음 글자로 넘어감
            }
            lastUpdateTime = currentTime; // 마지막 업데이트 시간 갱신
        }

        // 현재까지의 글자만 출력
        String partialTitle = titleString.substring(0, titleTypingIndex);

        // 타이틀 출력
        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredBigString(screen, partialTitle, screen.getHeight() / 6);

        // 타이틀 글자가 모두 출력되었는지 반환
        return titleTypingIndex == titleString.length();
    }

    public void drawStartMessage(final Screen screen, String message) {
        // 현재 시간
        long currentTime = System.currentTimeMillis();

        // 메시지 깜빡임 로직
        if (currentTime - lastBlinkTime >= BLINK_DELAY) {
            showStartMessage = !showStartMessage; // 흰색 ↔ 회색 토글
            lastBlinkTime = currentTime;         // 마지막 깜빡임 시간 갱신
        }

        // 메시지 색상 설정
        backBufferGraphics.setColor(showStartMessage ? Color.WHITE : Color.GRAY);

        // 메시지 그리기
        drawCenteredRegularString(screen, message, screen.getHeight() / 2);
    }

    //보스 HP바 표시
    public void drawBossHp(Screen screen, final int lives, Boss boss) {
        backBufferGraphics.setFont(fontRegular);

        int barX = 10; // 체력 바의 X 좌표
        int barY = 30; // 체력 바의 Y 좌표
        int barWidth = 680; // 체력 바의 최대 너비
        int barHeight = 20; // 체력 바의 높이
        int hp = boss.getMaxHp(); // 최대 체력
        // 체력 바위 보스 이름과 보스 정보 출력
        backBufferGraphics.setColor(Color.RED);
        drawCenteredRegularString(screen, "Boss Name, Phase : " + boss.getPhase(),
            1 + fontRegularMetrics.getHeight());

        // 체력 바의 테두리 그리기
        backBufferGraphics.setColor(Color.GRAY);
        backBufferGraphics.drawRect(barX, barY, barWidth, barHeight);

        // 현재 체력에 따른 바의 너비 계산
        int healthWidth = (int) ((double) lives / hp * barWidth);

        // 체력 바 채우기
        backBufferGraphics.setColor(boss.getHpColor()); // 체력 바의 색상
        backBufferGraphics.fillRect(barX + 1, barY + 1, healthWidth - 1, barHeight - 1);
        // 다음 페이즈가 존재하는 경우
        if (boss.getNextHpColor() != null) {
            // 다음페이즈의 색으로 체력바 색깔 지정
            backBufferGraphics.setColor(boss.getNextHpColor());
            backBufferGraphics.fillRect(barX + healthWidth + 1, barY + 1,
                barWidth - healthWidth - 1, barHeight - 1);
        }

        // 체력 수치 표시
        backBufferGraphics.setColor(Color.WHITE);
        String hpText = +lives + "/" + hp;
        int textX = barX + (barWidth - fontRegularMetrics.stringWidth(hpText)) / 2;
        int textY = barY + ((barHeight - fontRegularMetrics.getHeight()) / 2)
            + fontRegularMetrics.getAscent();
        backBufferGraphics.drawString(hpText, textX, textY);
    }

    // 미사일 폭발 반경 표시
    public void drawExplosionRadius(Missile missile) {
        backBufferGraphics.setColor(new Color(255, 69, 0, 128)); // 반투명 주황색
        int radius = missile.getExplosionRadius();
        backBufferGraphics.fillOval(
            missile.getPositionX() + missile.getWidth() / 2 - radius,
            missile.getPositionY() + missile.getHeight() / 2 - radius,
            radius * 2,
            radius * 2
        );
    }
}
