package kr.ac.hanyang.entity.boss;

import java.awt.Color;
import kr.ac.hanyang.engine.DrawManager.SpriteType;
import kr.ac.hanyang.entity.Entity;

public class Asteroid extends Entity {

    public Asteroid(final int positionX, final int positionY, final Direction direction) {
        super(positionX, positionY, 10 * 2, 10 * 2,
            new Color[]{new Color(0x4F372A), new Color(0x2F2823)}, direction);

        this.positionX = positionX;
        this.positionY = positionY;
        this.spriteType = SpriteType.Asteroid;
    }
}
