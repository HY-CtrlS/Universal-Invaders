package engine;

public class ShipStatus {

    private int shootingInterval;
    private int bulletSpeed;
    private int speed;
    private int baseDamage;
    private int hp;


    public ShipStatus(int shootingInterval, int bulletSpeed, int speed, int baseDamage, int hp) {
        this.shootingInterval = shootingInterval;
        this.bulletSpeed = bulletSpeed;
        this.speed = speed;
        this.baseDamage = baseDamage;
        this.hp = hp;
    }

    public int getShootingInterval() {
        return shootingInterval;
    }

    public void setShootingInterval(int shootingInterval) {
        this.shootingInterval = shootingInterval;
    }

    public int getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(int bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMaxHp() {return hp;}

    public void setMaxHp(int hp) {
        this.hp = hp;
    }

    public int getBaseDamage() {return baseDamage;}

    public void setBaseDamage(int baseDamage) {this.baseDamage = baseDamage;}
}