package entity;

import engine.Cooldown;
import engine.Core;
import java.awt.Color;
import engine.DrawManager.SpriteType;

public class Experience extends Entity {

    private int value;

    private Cooldown animationCooldown;

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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}