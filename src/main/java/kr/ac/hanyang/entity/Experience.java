package kr.ac.hanyang.entity;

import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import java.awt.Color;
import kr.ac.hanyang.engine.DrawManager.SpriteType;

public class Experience extends Entity {

    private int value;
    // 플레이어를 지속적으로 추적하는데 필요한 변수
    private double remainingMovementX = 0;
    private double remainingMovementY = 0;
    protected Cooldown animationCooldown;

    public Experience(int positionX, int positionY, int value) {
        super(positionX, positionY, 7, 7, Color.GREEN);
        this.value = value;

        this.spriteType = SpriteType.ExperienceA;

        this.animationCooldown = Core.getCooldown(500);
        this.animationCooldown.reset();
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
        }
    }

    /**
     * Moves the Exp the specified distance.
     *
     * @param distanceX Distance to move in the X axis.
     * @param distanceY Distance to move in the Y axis.
     */
    // 이동 잔량을 남기어 최소 단위인 1 이상만큼이 누적되면 누적된 정수만큼 이동후 이동 잔량에서 뺄셈.
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
}