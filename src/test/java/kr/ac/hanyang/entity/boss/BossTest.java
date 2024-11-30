package kr.ac.hanyang.entity.boss;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import kr.ac.hanyang.engine.Cooldown;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.entity.Bullet;
import org.junit.jupiter.api.Test;


class BossTest {
    Boss boss = new Boss(1,1);

    @Test
    void spreadBulletTest() {
        Cooldown tempTimer = Core.getCooldown(1000);
        tempTimer.reset();

        boss = new Boss(1,1);
        Set<Bullet> bullets = new HashSet<>();

        // 보스가 생성되자 마자 쏘면 기본공격 쿨다운이 안돌아서 발사가 안됨.
        int resultNotShot = boss.spreadBullet(bullets, 90, 45, 4);
        int resultShot = 0; //초기에는 총을 안쏜 것으로 0으로 설정

        // 1초 기다리기
        do {

        } while (!tempTimer.checkFinished());

        //1초 기다린후에 쏘면 총알을 발사함, 발사하면 1로 설정됨.
        resultShot = boss.spreadBullet(bullets, 90, 45, 4);

        // 각각의 경우에 0과 1 리턴
        assertEquals(0, resultNotShot);
        assertEquals(1, resultShot);

    }

    @Test
    void getPhase() {
        boss.checkPhase();

        if (boss.getMaxHp() == 300) {
            assertEquals(1, boss.getPhase());
        } else if (boss.getMaxHp() == 500) {
            assertEquals(2, boss.getPhase());
        } else {
            assertEquals(3, boss.getPhase());
        }
    }

    @Test
    void getCurrentHp() {
        int previousHp = boss.getCurrentHp();

        boss.getDamaged(10);
        int newHp = boss.getCurrentHp();
        // 정상적으로 감소했다면 같으면 안됨.
        assertNotEquals(previousHp,newHp);
    }


    @Test
    void getDamaged() {
        int previousHp = boss.getCurrentHp();

        boss.getDamaged(10);
        int newHp = boss.getCurrentHp();
        // 정상적으로 감소했다면 10만큼 감소했을 것
        assertEquals(10, previousHp - newHp);

        // -데미지인 경우에는 적용안됨
        int previousHpTwo = boss.getCurrentHp();

        boss.getDamaged(-10);
        int newHpTwo = boss.getCurrentHp();
        assertEquals(previousHpTwo, newHpTwo);

    }

    @Test
    void getHpColor() {
        Color color = boss.getHpColor();
        assertNotNull(color);
    }

    @Test
    void getNextHpColor() {
        boss = new Boss(1,1);
        //페이즈 1인경우 테스트
        assertEquals(Boss.PHASE_2_HPCOLOR, boss.getNextHpColor());

        //페이즈 전환
        boss.getDamaged(400);
        boss.checkPhase();

        // 페이즈 2인 경우 3번째 페이즈 색을 출력함
        assertEquals(Boss.PHASE_3_HPCOLOR, boss.getNextHpColor());

        //페이즈 전환
        boss.getDamaged(600);
        boss.checkPhase();

        // null값을 반환
        assertNull(boss.getNextHpColor());
    }
}