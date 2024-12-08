package kr.ac.hanyang.engine;

import java.io.IOException;
import java.util.logging.Logger;

public class StatusManager {

    private static StatusManager instance;
    private static Logger logger;
    private ShipStatus status;
    private int shipID = 1;

    private StatusManager() {
        logger = Core.getLogger();

        resetDefaultStatus(shipID);
    }

    protected static StatusManager getInstance() {
        if (instance == null) {
            instance = new StatusManager();
        }
        return instance;
    }

    public int getShootingInterval() {
        return status.getShootingInterval();
    }

    public void setShootingInterval(int shootingInterval) {
        status.setShootingInterval(shootingInterval);
    }

    public int getBulletSpeed() {
        return status.getBulletSpeed();
    }

    public void setBulletSpeed(int bulletSpeed) {
        status.setBulletSpeed(bulletSpeed);
    }

    public double getSpeed() {
        return status.getSpeed();
    }

    public void setSpeed(double speed) {
        status.setSpeed(speed);
    }

    public int getBaseDamage() {
        return status.getBaseDamage();
    }

    public void setBaseDamage(int baseDamage) {
        status.setBaseDamage(baseDamage);
    }

    public int getRange() {
        return status.getRange();
    }

    public void setRange(int range) {
        status.setRange(range);
    }

    public int getMaxHp() {
        return status.getMaxHp();
    }

    public void setMaxHp(final int hp) {
        status.setMaxHp(hp);
    }

    public double getRegenHp() {
        return status.getRegen_hp();
    }

    public void setRegenHp(final double regenHp) {
        status.setRegen_hp(regenHp);
    }

    public double getRegenUltra() {
        return status.getRegen_ultra();
    }

    public void setRegenUltra(final double regenUltra) {
        status.setRegen_ultra(regenUltra);
    }

    public void setShipID(final int shipID) {
        this.shipID = shipID;
    }

    private void saveStatus() {
        try {
            FileManager.getInstance().saveShipStatus(status);
        } catch (IOException e) {
            logger.warning("Failed to save status.");
        }
    }

    public void resetDefaultStatus(final int shipID) {
        try {
            status = FileManager.getInstance().loadShipStatus(shipID);
        } catch (IOException e) {
            logger.warning("Failed to load status. Using default values.");
            switch (shipID) {
                case 1:
                    status = new ShipStatus(600, 7, 2.4, 15,
                        6, 120, 0.3, 0.2);
                    break;
                case 2:
                    status = new ShipStatus(750, 6, 2.2, 10,
                        6, 110, 0.2, 0.0);
                    break;
                case 3:
                    status = new ShipStatus(800, 5, 2.0, 10,
                        6, 100, 0.2, 0.0);
                    break;
                case 4:
                    status = new ShipStatus(700, 5, 2.0, 10,
                        6, 100, 0.2, 0.0);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid shipID: " + shipID);
            }
        }
    }
}