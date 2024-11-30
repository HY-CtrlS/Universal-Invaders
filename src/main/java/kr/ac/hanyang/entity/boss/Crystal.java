package kr.ac.hanyang.entity.boss;

import java.awt.Color;
import kr.ac.hanyang.engine.DrawManager.SpriteType;
import kr.ac.hanyang.entity.Entity;

public class Crystal extends Entity {

    private int hp;
    private boolean isBroken;

    public Crystal(final int positionX, final int positionY) {
        super(positionX, positionY, 20 * 2, 20 * 2, new Color[]{Color.CYAN, Color.ORANGE});

        this.positionX = positionX;
        this.positionY = positionY;
        this.spriteType = SpriteType.Crystal;

        this.hp = 50;
        this.isBroken = false;
    }

    public final boolean isBroken() {
        return isBroken;
    }

    public final int getHp() {
        return hp;
    }

    public final void getDamaged(int damage) {
        this.hp = Math.max(this.hp - damage, 0);
        if (this.hp == 0) {
            this.isBroken = true;
        }
    }
}
