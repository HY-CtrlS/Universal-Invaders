package kr.ac.hanyang.engine;

import java.util.logging.Logger;

public class AnimationManager {

    /* AnimationManager 클래스의 싱글톤 인스턴스 */
    private static AnimationManager instance;
    /* FileManager 인스턴스 */
    private static FileManager fileManager;
    /* 애플리케이션 로거 */
    private static Logger logger;

    /* 애니메이션 타입과 프레임 배열(스프라이트)의 맵 */


    /**
     * 프라이빗 생성자
     */
    private AnimationManager() {
        fileManager = Core.getFileManager();
        logger = Core.getLogger();
        logger.info("Started Loading Animations.");
    }

}
