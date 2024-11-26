package kr.ac.hanyang.entity;

import static org.junit.jupiter.api.Assertions.*;

import kr.ac.hanyang.engine.DrawManager.SpriteType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExperienceTest {

    private Experience experience;

    @BeforeEach
    void setUp() {
        // 실제 Cooldown 객체를 사용하여 Experience 객체 생성
        experience = new Experience(50, 100, 10);
    }

    @Test
    void testInitialAttributes() {
        // 초기 값 확인
        assertEquals(50, experience.getPositionX(), "Initial positionX should be 50.");
        assertEquals(100, experience.getPositionY(), "Initial positionY should be 100.");
        assertEquals(10, experience.getValue(), "Initial value should be 10.");
        assertEquals(SpriteType.ExperienceA, experience.getSpriteType(), "Initial spriteType should be ExperienceA.");
    }

    @Test
    void testSpriteAnimationSwitch() throws InterruptedException {
        // 애니메이션 변경 전 초기 상태 확인
        assertEquals(SpriteType.ExperienceA, experience.getSpriteType(), "Initial spriteType should be ExperienceA.");

        // Cooldown 시간이 지나도록 대기
        Thread.sleep(500); // Cooldown 시간 (500ms)

        // update 호출 후 상태 확인
        experience.update();
        assertEquals(SpriteType.ExperienceB, experience.getSpriteType(), "SpriteType should switch to ExperienceB.");

        // Cooldown 시간이 다시 지나도록 대기
        Thread.sleep(500);

        // update 호출 후 상태 확인
        experience.update();
        assertEquals(SpriteType.ExperienceA, experience.getSpriteType(), "SpriteType should switch back to ExperienceA.");
    }

    @Test
    void testValueSetterAndGetter() {
        // 값 설정 및 확인
        experience.setValue(20);
        assertEquals(20, experience.getValue(), "Value should be updated to 20.");
    }
}