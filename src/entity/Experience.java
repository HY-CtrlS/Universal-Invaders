package entity;

import java.awt.Color;
import engine.DrawManager.SpriteType;

public class Experience extends Entity {
    private int value;

    public Experience(int positionX, int positionY, int value) {
        super(positionX, positionY, 10, 10, Color.GREEN);
        this.value = value;
        this.spriteType = SpriteType.AttackSpeedUpItem;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}