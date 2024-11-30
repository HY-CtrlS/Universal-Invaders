package kr.ac.hanyang.engine;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import kr.ac.hanyang.engine.AnimationManager.AnimationType;
import kr.ac.hanyang.engine.DrawManager.SpriteType;

/**
 * Manages files used in the application.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public final class FileManager {

    /** Singleton instance of the class. */
    private static FileManager instance;
    /** Application logger. */
    private static Logger logger;
    /** Max number of high scores. */
    private static final int MAX_SCORES = 7;

    /**
     * private constructor.
     */
    private FileManager() {
        logger = Core.getLogger();
    }

    /**
     * Returns shared instance of FileManager.
     *
     * @return Shared instance of FileManager.
     */
    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    /**
     * Loads sprites from disk.
     *
     * @param spriteMap Mapping of sprite type and empty boolean matrix that will contain the
     *                  image.
     * @throws IOException In case of loading problems.
     */
    public void loadSprite(final Map<SpriteType, boolean[][][]> spriteMap)
        throws IOException {
        InputStream inputStream = null;

        try {
            inputStream = DrawManager.class.getClassLoader()
                .getResourceAsStream("graphics");
            char c;

            // Sprite loading.
            for (Map.Entry<SpriteType, boolean[][][]> sprite : spriteMap
                .entrySet()) {
                for (int i = 0; i < sprite.getValue().length; i++) {
                    for (int j = 0; j < sprite.getValue()[i].length; j++) {
                        for (int k = 0; k < sprite.getValue()[i][j].length; k++) {
                            do {
                                c = (char) inputStream.read();
                            }
                            while (c != '0' && c != '1');

                            sprite.getValue()[i][j][k] = c == '1';
                        }
                    }
                }
                logger.fine("Sprite " + sprite.getKey() + " loaded.");
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public void loadAnimationFrame(final Map<AnimationType, Animation> animationMap) throws IOException {
        ClassLoader cl = AnimationManager.class.getClassLoader();
        InputStream inputStream = null;

        for (AnimationType animName : AnimationType.values()) {
            try {
                inputStream = cl.getResourceAsStream(
                    "animation/".concat(animName.toString()));

                byte[] bytes = inputStream.readAllBytes();
                Animation anim = AnimationManager.animationMap.get(animName);
                anim.loadAnimation(bytes);
            } catch (NullPointerException e) {
                System.out.println("Couldn't load animations!");
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
    }

    /**
     * Loads a font of a given size.
     *
     * @param size Point size of the font.
     * @return New font.
     * @throws IOException         In case of loading problems.
     * @throws FontFormatException In case of incorrect font format.
     */
    public Font loadFont(final float size) throws IOException,
        FontFormatException {
        InputStream inputStream = null;
        Font font;

        try {
            // Font loading.
            inputStream = FileManager.class.getClassLoader()
                .getResourceAsStream("font.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(
                size);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return font;
    }

    /**
     * Returns the application default scores if there is no user high scores file.
     *
     * @return Default high scores.
     * @throws IOException In case of loading problems.
     */
    private List<Score> loadDefaultHighScores() throws IOException {
        List<Score> highScores = new ArrayList<Score>();
        InputStream inputStream = null;
        BufferedReader reader = null;

        try {
            inputStream = FileManager.class.getClassLoader()
                .getResourceAsStream("scores");
            reader = new BufferedReader(new InputStreamReader(inputStream));

            Score highScore = null;
            String name = reader.readLine();
            String survivalTime = reader.readLine();

            while ((name != null) && (survivalTime != null)) {
                highScore = new Score(name, Integer.parseInt(survivalTime));
                highScores.add(highScore);
                name = reader.readLine();
                survivalTime = reader.readLine();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return highScores;
    }

    /**
     * Loads high scores from file, and returns a sorted list of pairs score - value.
     *
     * @return Sorted list of scores - players.
     * @throws IOException In case of loading problems.
     */
    public List<Score> loadHighScores() throws IOException {

        List<Score> highScores = new ArrayList<Score>();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            String jarPath = FileManager.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");

            String scoresPath = new File(jarPath).getParent();
            scoresPath += File.separator;
            scoresPath += "scores";

            File scoresFile = new File(scoresPath);
            inputStream = new FileInputStream(scoresFile);
            bufferedReader = new BufferedReader(new InputStreamReader(
                inputStream, Charset.forName("UTF-8")));

            logger.info("Loading user high scores.");

            Score highScore = null;
            String name = bufferedReader.readLine();
            String survivalTime = bufferedReader.readLine();

            while ((name != null) && (survivalTime != null)) {
                highScore = new Score(name, Integer.parseInt(survivalTime));
                highScores.add(highScore);
                name = bufferedReader.readLine();
                survivalTime = bufferedReader.readLine();
            }

        } catch (FileNotFoundException e) {
            // loads default if there's no user scores.
            logger.info("Loading default high scores.");
            highScores = loadDefaultHighScores();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        Collections.sort(highScores);
        return highScores;
    }

    /**
     * Saves user high scores to disk.
     *
     * @param highScores High scores to save.
     * @throws IOException In case of loading problems.
     */
    public void saveHighScores(final List<Score> highScores)
        throws IOException {
        OutputStream outputStream = null;
        BufferedWriter bufferedWriter = null;

        try {
            String jarPath = FileManager.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");

            String scoresPath = new File(jarPath).getParent();
            scoresPath += File.separator;
            scoresPath += "scores";

            File scoresFile = new File(scoresPath);

            if (!scoresFile.exists()) {
                scoresFile.createNewFile();
            }

            outputStream = new FileOutputStream(scoresFile);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                outputStream, Charset.forName("UTF-8")));

            logger.info("Saving user high scores.");

            // Saves 7 or less scores.
            int savedCount = 0;
            for (Score score : highScores) {
                if (savedCount >= MAX_SCORES) {
                    break;
                }
                bufferedWriter.write(score.getName());
                bufferedWriter.newLine();
                bufferedWriter.write(Integer.toString(score.getSurvivalTime()));
                bufferedWriter.newLine();
                savedCount++;
            }

        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
    }

    private ShipStatus loadDefaultShipStatus() throws IOException {

        ShipStatus shipStatus;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            inputStream = FileManager.class.getClassLoader()
                .getResourceAsStream("status");
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            int shootingInterval = Integer.parseInt(bufferedReader.readLine());
            int bulletSpeed = Integer.parseInt(bufferedReader.readLine());
            int speed = Integer.parseInt(bufferedReader.readLine());
            int baseDamage = Integer.parseInt(bufferedReader.readLine());
            int maxHp = Integer.parseInt(bufferedReader.readLine());
            double regenHp = Double.parseDouble(bufferedReader.readLine());

            shipStatus = new ShipStatus(shootingInterval, bulletSpeed, speed, baseDamage, maxHp,
                regenHp);

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return shipStatus;
    }

    public ShipStatus loadShipStatus() throws IOException {

        ShipStatus shipStatus;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            String jarPath = FileManager.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");

            String scoresPath = new File(jarPath).getParent();
            scoresPath += File.separator;
            scoresPath += "status";

            File scoresFile = new File(scoresPath);
            inputStream = new FileInputStream(scoresFile);
            bufferedReader = new BufferedReader(new InputStreamReader(
                inputStream, Charset.forName("UTF-8")));

            logger.info("Loading user ship status.");

            int shootingInterval = Integer.parseInt(bufferedReader.readLine());
            int bulletSpeed = Integer.parseInt(bufferedReader.readLine());
            int speed = Integer.parseInt(bufferedReader.readLine());
            int baseDamage = Integer.parseInt(bufferedReader.readLine());
            int maxHp = Integer.parseInt(bufferedReader.readLine());
            double regenHP = Double.parseDouble((bufferedReader.readLine()));

            shipStatus = new ShipStatus(shootingInterval, bulletSpeed, speed, baseDamage, maxHp,
                regenHP);

        } catch (FileNotFoundException e) {
            // loads default if there's no user scores.
            logger.info("Loading default ship status.");
            shipStatus = loadDefaultShipStatus();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return shipStatus;
    }

    public void saveShipStatus(final ShipStatus shipStatus)
        throws IOException {
        OutputStream outputStream = null;
        BufferedWriter bufferedWriter = null;

        try {
            String jarPath = FileManager.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");

            String scoresPath = new File(jarPath).getParent();
            scoresPath += File.separator;
            scoresPath += "status";

            File scoresFile = new File(scoresPath);

            if (!scoresFile.exists()) {
                scoresFile.createNewFile();
            }

            outputStream = new FileOutputStream(scoresFile);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                outputStream, Charset.forName("UTF-8")));

            logger.info("Saving user ship status.");
            bufferedWriter.write(String.valueOf(shipStatus.getShootingInterval()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(shipStatus.getBulletSpeed()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(shipStatus.getSpeed()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(shipStatus.getBaseDamage()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(shipStatus.getMaxHp()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(shipStatus.getRegen_hp()));

        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
    }
}
