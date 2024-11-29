package kr.ac.hanyang.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.entity.Entity.Direction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UltTest {

    private static Ship ship1, ship2, ship3, ship4;
    private static Cooldown ultCooldown;

    @BeforeAll
    static void createShip() {
        ship1 = new Ship1(100, 100, Direction.UP, Color.WHITE, 1);
        ship2 = new Ship2(100, 100, Direction.UP, Color.WHITE, 2);
        ship3 = new Ship3(100, 100, Direction.UP, Color.WHITE, 3);
        ship4 = new Ship4(100, 100, Direction.UP, Color.WHITE, 4);
        ultCooldown = Core.getCooldown(1000);
    }

    @Test
    void ship1Test() {
        assertEquals(200, ship1.getUltThreshold());
        ship1.useUlt();
        assertTrue(ship1.isUltActivated());
        assertEquals(0, ship1.getUltGauge());
    }

    @Test
    void ship2Test() {
        assertEquals(150, ship2.getUltThreshold());
        ship2.useUlt();
        assertTrue(ship2.isUltActivated());
        assertEquals(0, ship2.getUltGauge());
    }

    @Test
    void ship3Test() {
        assertEquals(150, ship3.getUltThreshold());
        ship3.useUlt();
        assertTrue(ship3.isUltActivated());
        assertEquals(0, ship3.getUltGauge());
    }

    @Test
    void ship4Test() {
        assertEquals(100, ship4.getUltThreshold());
        ship4.useUlt();
        assertTrue(ship4.isUltActivated());
        assertEquals(0, ship4.getUltGauge());
    }

    @Test
    void pauseTest() {
        ultCooldown.reset();
        ultCooldown.pause();
        assertTrue(ultCooldown.isPaused());
        assertFalse(ultCooldown.checkFinished());
        ultCooldown.resume();
        assertFalse(ultCooldown.isPaused());
    }
}
