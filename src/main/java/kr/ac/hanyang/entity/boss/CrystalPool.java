package kr.ac.hanyang.entity.boss;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager;
import kr.ac.hanyang.entity.ship.Ship;

public final class CrystalPool {

    private Set<Crystal> crystals;
    private DrawManager drawManager;
    private Ship ship;
    private Logger logger;

    public CrystalPool(Ship ship) {
        this.crystals = new HashSet<>();
        this.drawManager = Core.getDrawManager();
        this.ship = ship;
        this.logger = Core.getLogger();
    }

    public void update() {
        Set<Crystal> toRemove = new HashSet<>();

        for (Crystal crystal : crystals) {
            if (crystal.isBroken()) {
                toRemove.add(crystal);
            }
        }
        crystals.removeAll(toRemove);
    }

    public void move() {
        for (Crystal crystal : crystals) {
            if (!crystal.isBroken()) {
                crystal.move();
            }
        }
    }

    public void draw() {
        for (Crystal crystal : crystals) {
            if (!crystal.isBroken()) {
                drawManager.drawEntity(crystal, crystal.getPositionX(), crystal.getPositionY());
            }
        }
    }

    public void createCrystal() {
        int positionX;
        int positionY;
        boolean up = this.ship.getPositionY() <= 451;
        boolean right = this.ship.getPositionX() >= 337;

        if (up && right) {
            positionX = 48;
            positionY = 670;
        } else if (up && !right) {
            positionX = 620;
            positionY = 670;
        } else if (!up && right) {
            positionX = 48;
            positionY = 214;
        } else {
            positionX = 620;
            positionY = 214;
        }

        Crystal crystal = new Crystal(positionX, positionY);
        crystals.add(crystal);
        this.logger.info("Crystal Created! You need to break this down!");
    }

    public void createFinalCrystal() {
        crystals.clear();

        crystals.add(new Crystal(48, 214));
        crystals.add(new Crystal(620, 214));
        crystals.add(new Crystal(48, 670));
        crystals.add(new Crystal(620, 670));
    }

    public boolean isAllBroken() {
        return this.crystals.isEmpty();
    }

    public Set<Crystal> getCrystals() {
        return this.crystals;
    }
}
