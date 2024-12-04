package kr.ac.hanyang.entity;

import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import java.awt.Color;
import kr.ac.hanyang.engine.DrawManager.SpriteType;

public class Experience extends Entity {

    // 지속시간
    private static final long DURATION = 10000; // 10초
    private static final long WARNING_DURATION = 3000; // 소멸 경고 시간 3초
    private static final Color[] WARNING_COLOR = new Color[]{Color.BLACK};

    private int value;
    private double remainingMovementX = 0;
    private double remainingMovementY = 0;
    protected Cooldown animationCooldown;

    // 생성된 시간을 저장
    private long creationTime;

    // 원래 색상과 경고 색상
    private Color[] originalColor;
    private boolean isWarningColor = false;

    public Experience(int positionX, int positionY, int value) {
        super(positionX, positionY, 7, 7, Color.GREEN);
        this.value = value;
        this.originalColor = this.getColor();

        this.spriteType = SpriteType.ExperienceA;

        this.animationCooldown = Core.getCooldown(500);
        this.animationCooldown.reset();

        // 생성된 시간 기록
        this.creationTime = System.currentTimeMillis();
    }

    /**
     * Updates attributes, mainly used for animation purposes.
     */
    public final void update() {
        if (this.animationCooldown.checkFinished()) {
            this.animationCooldown.reset();

            // 스프라이트를 번갈아가며 변경
            if (this.spriteType == SpriteType.ExperienceA) {
                this.spriteType = SpriteType.ExperienceB;
            } else {
                this.spriteType = SpriteType.ExperienceA;
            }

            // 소멸 경고 시간인지 확인
            if (System.currentTimeMillis() - creationTime > DURATION - WARNING_DURATION) {
                // 색상 교체
                if (isWarningColor) {
                    this.setColor(originalColor, false);
                } else {
                    this.setColor(WARNING_COLOR, false);
                }
                isWarningColor = !isWarningColor;
            }
        }
    }

    /**
     * Moves the Exp the specified distance.
     *
     * @param distanceX Distance to move in the X axis.
     * @param distanceY Distance to move in the Y axis.
     */
    public final void move(final double distanceX, final double distanceY) {
        this.remainingMovementX += distanceX;
        this.remainingMovementY += distanceY;

        int intMoveX = (int) remainingMovementX;
        int intMoveY = (int) remainingMovementY;

        remainingMovementX -= intMoveX;
        remainingMovementY -= intMoveY;

        this.positionX += intMoveX;
        this.positionY += intMoveY;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public void setColor(Color[] color, boolean updateOriginal) {
        super.setColor(color);
        if (updateOriginal) {
            this.originalColor = color;
        }
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - creationTime > DURATION; // 10초
    }
}