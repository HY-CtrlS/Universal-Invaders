package kr.ac.hanyang.engine;

public class ShipStatus {

    private int shootingInterval;
    private int bulletSpeed;
    private int speed;
    private int baseDamage;
    private int range;
    private int hp;
    private double regen_hp;
    private double regen_ultra;

    public ShipStatus(int shootingInterval, int bulletSpeed, int speed, int baseDamage, int range,
        int hp, double regen_hp, double regen_ultra) {
        this.shootingInterval = shootingInterval;
        this.bulletSpeed = bulletSpeed;
        this.speed = speed;
        this.baseDamage = baseDamage;
        this.range = range;
        this.hp = hp;
        this.regen_hp = regen_hp;
        this.regen_ultra = regen_ultra;
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

    public int getMaxHp() {
        return hp;
    }

    public void setMaxHp(int hp) {
        this.hp = hp;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(int baseDamage) {
        this.baseDamage = baseDamage;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public double getRegen_hp() {
        return regen_hp;
    }

    public void setRegen_hp(double regenHp) {
        this.regen_hp = regenHp;
    }

    public double getRegen_ultra() {
        return regen_ultra;
    }

    public void setRegen_ultra(double regenUltra) {
        this.regen_ultra = regenUltra;
    }
}