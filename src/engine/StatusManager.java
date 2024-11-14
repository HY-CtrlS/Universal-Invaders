package engine;

import java.io.IOException;
import java.util.logging.Logger;

public class StatusManager {

    private static StatusManager instance;
    private static Logger logger;
    private ShipStatus status;

    private StatusManager() {
        logger = Core.getLogger();

        
        resetDefaultStatus();

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

    public int getSpeed() {
        return status.getSpeed();
    }

    public void setSpeed(int speed) {
        status.setSpeed(speed);
    }

    public int getBaseDamage() {return status.getBaseDamage();}

    public void setBaseDamage(int baseDamage) {status.setBaseDamage(baseDamage);}


    public int getHp() {
        return status.getHp();
    }

    public void setMaxLives(final int hp) {
        status.setHp(hp);
    }


    private void saveStatus() {
        try {
            FileManager.getInstance().saveShipStatus(status);
        } catch (IOException e) {
            logger.warning("Failed to save status.");
        }
    }


    public void resetDefaultStatus() {
        try {
            status = FileManager.getInstance().loadShipStatus();
        } catch (IOException e) {
            logger.warning("Failed to load status. Using default values.");
            status = new ShipStatus(750, 6, 2, 1, 100);
        }
    }

}