package kr.ac.hanyang.engine;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import java.util.Map;

public final class AnimationManager {

    /* AnimationManager 클래스의 싱글톤 인스턴스 */
    private static AnimationManager instance;
    /* FileManager 인스턴스 */
    private static FileManager fileManager;
    /* 애플리케이션 로거 */
    private static Logger logger;

    /* 애니메이션 타입 열거형과 프레임 배열(스프라이트)의 맵 */
    public static Map<AnimationType, Animation> animationMap;

    /* 애니메이션의 종류 */
    public static enum AnimationType {
        /* 테스트 */
        TestAnimation
    }

    /** 프라이빗 생성자 */
    private AnimationManager() {
        fileManager = Core.getFileManager();
        logger = Core.getLogger();
        logger.info("Started Loading Animations.");

        try {
            animationMap = new LinkedHashMap<>();

            animationMap.put(AnimationType.TestAnimation, new Animation(9, 1, 1));

            fileManager.loadAnimationFrame(animationMap);
        } catch (IOException e) {
            logger.warning("Loading failed.");
        }
    }


//    private Animation AnimationFrame() {
//
//    }

}
