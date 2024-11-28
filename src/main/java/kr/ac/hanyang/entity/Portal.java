package kr.ac.hanyang.entity;

import java.awt.Color;
import kr.ac.hanyang.engine.DrawManager.SpriteType;

/**
 * 보스 스테이지로의 이동을 담당하는 포탈 클래스
 */
public class Portal extends Entity {

    /** 포탈 표시 여부 */
    private boolean isVisible;

    /**
     * 포탈 생성자
     *
     * @param positionX Initial X position.
     * @param positionY Initial Y position.
     */
    public Portal(int positionX, int positionY) {
        super(positionX, positionY, 13 * 2, 11 * 2,
            new Color[]{Color.CYAN, Color.PINK, Color.WHITE});
        this.spriteType = SpriteType.Portal; // 포탈 스프라이트 타입 설정
        this.isVisible = false; // 초기에는 비활성화 상태
    }

    /**
     * 포탈 활성화 메소드
     */
    public void activate() {
        this.isVisible = true;
    }

    /**
     * 포탈 비활성화 메소드
     */
    public void deactivate() {
        this.isVisible = false;
    }

    /**
     * 현재 포탈이 표시되는지 확인하는 메소드
     *
     * @return 현재 포탈의 표시 여부
     */
    public boolean isVisible() {
        return isVisible;
    }
}