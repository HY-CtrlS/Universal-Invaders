package kr.ac.hanyang.engine;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CooldownTest {

    @Test
    @DisplayName("기본 Cooldown 생성 및 초기 상태 확인")
    void testCooldownInitialization() {
        Cooldown cooldown = new Cooldown(1000);

        assertNotNull(cooldown, "Cooldown 객체는 null이 아니어야 합니다.");
        assertTrue(cooldown.checkFinished(), "초기 상태에서 Cooldown은 완료 상태여야 합니다.");

        cooldown.reset();
        assertFalse(cooldown.checkFinished(), "reset 이후 Cooldown은 완료 상태가 아니어야 합니다.");
    }

    @Test
    @DisplayName("Variance가 있는 Cooldown의 reset 테스트")
    void testCooldownWithVariance() throws InterruptedException {
        Cooldown cooldown = new Cooldown(1000, 200);

        assertNotNull(cooldown, "Cooldown 객체는 null이 아니어야 합니다.");
        assertTrue(cooldown.checkFinished(), "초기 상태에서 Cooldown은 완료 상태여야 합니다.");

        cooldown.reset();
        assertFalse(cooldown.checkFinished(), "reset 이후 Cooldown은 완료 상태가 아니어야 합니다.");

        // Cooldown 기간이 끝날 때까지 대기 (최대 시간 계산)
        Thread.sleep(1200);
        assertTrue(cooldown.checkFinished(), "최대 Cooldown 기간 이후에는 완료 상태여야 합니다.");
    }
}
