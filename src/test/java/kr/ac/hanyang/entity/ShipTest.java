package kr.ac.hanyang.entity;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import kr.ac.hanyang.engine.Core;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    private Ship ship;
    private int positionX;
    private int positionY;
    private Set<Bullet> bullets;

    @BeforeEach
    void setUp() {
        // Ship 객체를 생성합니다. 위치는 (0, 0)으로 초기화
        ship = new Ship(0, 0, Entity.Direction.UP, Color.WHITE, 1);
        this.positionX = ship.getPositionX();
        this.positionY = ship.getPositionY();
    }

    @Test
    void testMoveRight() {
        // Ship을 오른쪽으로 이동
        ship.moveRight();
        // x축으로 이동했으므로, x좌표는 증가했을 것
        assertTrue(ship.getPositionX() > positionX);
        // y축은 변하지 않았을 것
        assertEquals(ship.getPositionY(), positionY);
    }

    @Test
    void testMoveLeft() {
        // Ship을 왼쪽으로 이동
        ship.moveLeft();
        // x축으로 이동했으므로, x좌표는 감소했을 것
        assertTrue(ship.getPositionX() < positionX);
        // y축은 변하지 않았을 것
        assertEquals(ship.getPositionY(), positionY);
    }

    @Test
    void testMoveUp() {
        // Ship을 위로 이동
        ship.moveUp();
        // y축으로 이동했으므로, y좌표는 감소했을 것
        assertTrue(ship.getPositionY() < positionY);
        // x축은 변하지 않았을 것
        assertEquals(ship.getPositionX(), positionX);
    }

    @Test
    void testMoveDown() {
        // Ship을 아래로 이동
        ship.moveDown();
        // y축으로 이동했으므로, y좌표는 증가했을 것
        assertTrue(ship.getPositionY() > positionY);
        // x축은 변하지 않았을 것
        assertEquals(ship.getPositionX(), positionX);
    }

    @Test
    void testMoveUpRight() {
        // Ship을 오른쪽 위로 이동
        ship.moveUpRight();
        // x축과 y축으로 이동했으므로, x좌표와 y좌표는 증가했을 것
        assertTrue(ship.getPositionX() > positionX);
        assertTrue(ship.getPositionY() < positionY);
    }

    @Test
    void testMoveUpLeft() {
        // Ship을 왼쪽 위로 이동
        ship.moveUpLeft();
        // x축과 y축으로 이동했으므로, x좌표는 감소했을 것
        assertTrue(ship.getPositionX() < positionX);
        // y축은 증가했을 것
        assertTrue(ship.getPositionY() < positionY);
    }

    @Test
    void testMoveDownRight() {
        // Ship을 오른쪽 아래로 이동
        ship.moveDownRight();
        // x축과 y축으로 이동했으므로, x좌표와 y좌표는 증가했을 것
        assertTrue(ship.getPositionX() > positionX);
        assertTrue(ship.getPositionY() > positionY);
    }

    @Test
    void testMoveDownLeft() {
        // Ship을 왼쪽 아래로 이동
        ship.moveDownLeft();
        // x축과 y축으로 이동했으므로, x좌표와 y좌표는 감소했을 것
        assertTrue(ship.getPositionX() < positionX);
        assertTrue(ship.getPositionY() > positionY);
    }

    @Test
    void testDirection() {
        assertEquals(ship.getDirection(), Entity.Direction.UP);

        ship.setDirection(Entity.Direction.RIGHT);
        assertEquals(ship.getDirection(), Entity.Direction.RIGHT);

        ship.setDirection(Entity.Direction.DOWN);
        assertEquals(ship.getDirection(), Entity.Direction.DOWN);

        ship.setDirection(Entity.Direction.LEFT);
        assertEquals(ship.getDirection(), Entity.Direction.LEFT);

        ship.setDirection(Entity.Direction.UP_RIGHT);
        assertEquals(ship.getDirection(), Entity.Direction.UP_RIGHT);
        assertTrue(ship.isDiagonal());

        ship.setDirection(Entity.Direction.UP_LEFT);
        assertEquals(ship.getDirection(), Entity.Direction.UP_LEFT);
        assertTrue(ship.isDiagonal());

        ship.setDirection(Entity.Direction.DOWN_RIGHT);
        assertEquals(ship.getDirection(), Entity.Direction.DOWN_RIGHT);
        assertTrue(ship.isDiagonal());

        ship.setDirection(Entity.Direction.DOWN_LEFT);
        assertEquals(ship.getDirection(), Entity.Direction.DOWN_LEFT);
        assertTrue(ship.isDiagonal());
    }

    @Test
    void defaultStatusTest() {
        assertEquals(ship.getSpeed(), Core.getStatusManager().getSpeed());
        assertEquals(ship.getBaseDamage(), Core.getStatusManager().getBaseDamage());
        assertEquals(ship.bulletSpeed, Core.getStatusManager().getBulletSpeed());
        assertEquals(ship.shootingInterval, Core.getStatusManager().getShootingInterval());
    }

    @Test
    void updateStatusTest() {
        Core.getStatusManager().setSpeed(99);
        Core.getStatusManager().setBaseDamage(99);
        Core.getStatusManager().setBulletSpeed(99);
        Core.getStatusManager().setShootingInterval(99);

        ship.updateStatsFromStatusManager();

        assertEquals(ship.getSpeed(), Core.getStatusManager().getSpeed());
        assertEquals(ship.getBaseDamage(), Core.getStatusManager().getBaseDamage());
        assertEquals(ship.bulletSpeed, Core.getStatusManager().getBulletSpeed());
        assertEquals(ship.shootingInterval, Core.getStatusManager().getShootingInterval());
    }

    /*@Test
    void testShoot() {
        this.bullets = new HashSet<>();

        // shoot 메소드 호출
        boolean result = ship.shoot(bullets);

        // shoot이 성공했다면 bullets 집합에 총알이 추가되어야 함
        assertTrue(result);  // shoot 메소드가 true를 반환했는지 확인
        assertEquals(1, bullets.size());  // 총알이 하나 추가되었는지 확인

        // 추가된 총알이 올바르게 설정되었는지 확인
        Bullet bullet = bullets.iterator().next();  // 집합에서 첫 번째 총알을 가져옴
        assertEquals(ship.getPositionX() + ship.getWidth() / 2, bullet.getPositionX()); // 총알 위치 x
        assertEquals(ship.getPositionY() + ship.getHeight() / 2, bullet.getPositionY()); // 총알 위치 y
        assertEquals(ship.bulletSpeed, bullet.getSpeed());  // 총알 속도
        assertEquals(ship.baseDamage, bullet.getDamage());  // 총알 데미지
        assertEquals(ship.getDirection(), bullet.getDirection());  // 총알 방향
    }*/
}