package kr.ac.hanyang.entity.boss;

import java.awt.Color;
import kr.ac.hanyang.engine.DrawManager.SpriteType;
import kr.ac.hanyang.entity.Entity;

public class Crystal extends Entity {

    private int hp;
    private int speed;
    private boolean isBroken;

    public Crystal(final int positionX, final int positionY) {
        super(positionX, positionY, 20 * 2, 20 * 2, new Color[]{Color.CYAN, Color.ORANGE});

        this.positionX = positionX;
        this.positionY = positionY;
        this.spriteType = SpriteType.Crystal;

        this.hp = 50;
        this.speed = 2;
        this.isBroken = false;
    }

    public void update() {
        final int minX = 49;
        final int maxX = 611;
        final int minY = 214;
        final int maxY = 672;

        boolean up = this.positionY == minY;
        boolean down = this.positionY == maxY;
        boolean right = this.positionX == maxX;
        boolean left = this.positionX == minX;

        if (up && left) {
            this.positionY += speed;
        } else if (down && left) {
            this.positionX += speed;
        } else if (down && right) {
            this.positionY -= speed;
        } else if (up && right) {
            this.positionX -= speed;
        } else if (up) {
            this.positionX -= speed;
        } else if (left) {
            this.positionY += speed;
        } else if (down) {
            this.positionX += speed;
        } else if (right) {
            this.positionY -= speed;
        }

        this.positionX = Math.max(minX, Math.min(this.positionX, maxX));
        this.positionY = Math.max(minY, Math.min(this.positionY, maxY));
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
