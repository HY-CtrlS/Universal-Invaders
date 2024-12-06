package kr.ac.hanyang.entity;

import java.awt.Color;
import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager.SpriteType;

public class Spacebar extends Entity {
    private final Cooldown animationCooldown = Core.getCooldown(500);;

    public Spacebar() {
        super(0, 0, 30, 11, Color.WHITE);
        this.spriteType = SpriteType.Spacebar;
    }

    public final void update() {
        if (this.animationCooldown.checkFinished()) {
            this.animationCooldown.reset();

            switch (this.spriteType) {
                case Spacebar:
                    this.spriteType = SpriteType.SpacebarPressed;
                    break;
                case SpacebarPressed:
                    this.spriteType = SpriteType.Spacebar;
                    break;
            }
        }
    }
}
