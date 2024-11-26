package kr.ac.hanyang.entity;
import static org.junit.jupiter.api.Assertions.*;

import kr.ac.hanyang.engine.DrawManager.SpriteType;
import org.junit.jupiter.api.Test;

class EnemyShipTest {

    @Test
    void testEnemyShip() {
        // 각 스프라이트 타입별로 스탯이 의도한 대로 초기화 되는지 확인
        // 추가적으로 스프라이트 크기대로 히트스캔이 설정 되었는지 확인
        EnemyShip enemy = new EnemyShip(50, 50, SpriteType.EnemyShipA1);
        assertEquals(10, enemy.getHp());
        assertEquals(1, enemy.getXSpeed());
        assertEquals(1, enemy.getYSpeed());
        assertEquals(24, enemy.getWidth());
        assertEquals(16, enemy.getHeight());

        enemy = new EnemyShip(50, 50, SpriteType.EnemyShipB1);
        assertEquals(100, enemy.getHp());
        assertEquals(0.5, enemy.getXSpeed());
        assertEquals(0.5, enemy.getYSpeed());
        assertEquals(48, enemy.getWidth());
        assertEquals(32, enemy.getHeight());

        enemy = new EnemyShip(50, 50, SpriteType.EnemyShipC1);
        assertEquals(1, enemy.getHp());
        assertEquals(4, enemy.getXSpeed());
        assertEquals(4, enemy.getYSpeed());
        assertEquals(16, enemy.getWidth());
        assertEquals(16, enemy.getHeight());

        enemy = new EnemyShip(50, 50, SpriteType.Obstacle);
        assertEquals(200, enemy.getHp());
        assertEquals(0, enemy.getXSpeed());
        assertEquals(0, enemy.getYSpeed());
        assertEquals(20, enemy.getWidth());
        assertEquals(20, enemy.getHeight());
    }
}