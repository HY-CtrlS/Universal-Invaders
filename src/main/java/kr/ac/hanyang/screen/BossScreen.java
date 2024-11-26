package kr.ac.hanyang.screen;


/**
 * 보스 스테이지의 화면을 정의하는 클래스
 */
public class BossScreen extends Screen {
    // For fields


    /**
     * 생성자, 화면의 속성을 설정
     */
    public BossScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        this.returnCode = 1;
    }

    /**
     * 화면의 기본 속성을 초기화하고 필요한 요소를 추가
     */
    public final void initialize() {
        super.initialize();
    }

    /**
     * 화면을 실행
     *
     * @return 화면의 현재 리턴코드
     */
    public final int run() {
        super.run();

        return this.returnCode;
    }

    /**
     * 화면의 요소를 업데이트하고 이벤트를 확인
     */
    protected final void update() {
        super.update();
    }

    private void draw() {
        drawManager.initDrawing(this);

        drawManager.completeDrawing(this);
    }
}
