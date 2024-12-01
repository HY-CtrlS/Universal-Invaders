package kr.ac.hanyang.entity.boss;

import java.awt.Color;
import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager.SpriteType;
import kr.ac.hanyang.entity.Entity;

public class Laser extends Entity {

    private static final int DAMAGE = 20;
    private boolean isReady;
    private boolean isAct;
    private boolean isDone;
    private Cooldown ready;
    private Cooldown act;

    public Laser(final int positionX, final int positionY, final Direction direction) {
        super(positionX, positionY, 720 * 2, 15 * 2,
            new Color[]{new Color(255, 255, 255, 128), new Color(189, 102, 255, 128),
            new Color(94, 255, 250, 128)}, direction);

        this.positionX = positionX;
        this.positionY = positionY;
        this.spriteType = SpriteType.WarningLaser;

        this.isReady = false;
        this.isAct = false;
        this.isDone = false;

        this.ready = Core.getCooldown(800);
        this.ready.reset();
        this.act = Core.getCooldown(800);
    }

    public final void update() {
        if (!this.isReady && this.ready.checkFinished()) {
            this.isReady = true;
        }

        if (this.isAct && this.act.checkFinished()) {
            this.isDone = true;
        }

        if (!this.isAct && this.isReady) {
            this.isAct = true;
            this.act.reset();
            this.spriteType = SpriteType.Laser;
        }


    }

    public final boolean isDone() {
        return isDone;
    }

    public final boolean isReady() {
        return isReady;
    }

    public final int getDamage() {
        return DAMAGE;
    }
}
