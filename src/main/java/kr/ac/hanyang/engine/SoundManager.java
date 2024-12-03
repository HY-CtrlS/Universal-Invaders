package kr.ac.hanyang.engine;

import java.io.InputStream;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class SoundManager {

    private static SoundManager instance;
    private static Logger logger;
    private Clip bgmClip; // 배경음악 클립
    private FloatControl bgmVolumeControl; // 배경음악 볼륨 조절
    private float backgroundMusicVolume; // 현재 배경음악 볼륨
    private float soundEffectsVolume; // 현재 효과음 볼륨

    // 생성자 - 로그 설정 및 초기 볼륨 값을 0.5으로 설정
    private SoundManager() {
        logger = Core.getLogger();
        backgroundMusicVolume = 0.5f;
        soundEffectsVolume = 0.5f;
    }

    // SoundManager 인스턴스를 싱글톤 패턴으로 가져오기
    protected static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /**
     * 배경음악 파일을 재생합니다.
     *
     * @param filepath 재생할 배경음악 파일 경로
     */
    private void playBackgroundMusic(String filepath) {
        try {
            if (bgmClip != null && bgmClip.isOpen()) {
                // 이미 음악이 재생 중이라면 종료하지 않고 그대로 유지
                if (bgmClip.isRunning()) {
                    return;
                }
                bgmClip.stop();
                bgmClip.close();
            }
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filepath);
            if (inputStream == null) {
                logger.warning("Resource not found: " + filepath);
                return;
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioInputStream);

            // 배경음악의 볼륨 컨트롤 설정
            bgmVolumeControl = getSupportedVolumeControl(bgmClip);
            if (bgmVolumeControl != null) {
                setVolume(bgmVolumeControl, backgroundMusicVolume);
            } else {
                logger.warning("지원되는 볼륨 컨트롤을 찾을 수 없습니다.");
            }

            bgmClip.loop(Clip.LOOP_CONTINUOUSLY); // 배경음악 반복 재생
            bgmClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logger.warning("배경음악 재생 실패: " + e.getMessage());
        }
    }

    /**
     * 배경음악을 중지합니다.
     */
    public void stopBackgroundMusic() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
        }
    }

    /**
     * 배경음악을 다시 재생합니다.
     */
    public void resumeBackgroundMusic() {
        if (bgmClip != null && !bgmClip.isRunning()) {
            bgmClip.start();
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * 배경음악 볼륨을 설정합니다.
     *
     * @param gain 설정할 볼륨 (0.0 - 1.0)
     */
    private void setBackgroundMusicVolume(float gain) {
        backgroundMusicVolume = Math.max(0.0f, Math.min(1.0f, gain)); // 볼륨 범위 제한
        if (bgmVolumeControl != null) {
            setVolume(bgmVolumeControl, backgroundMusicVolume);
        }
    }

    /**
     * 효과음을 재생합니다.
     *
     * @param filepath 재생할 효과음 파일 경로
     */
    private void playSoundEffect(String filepath) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filepath);
            if (inputStream == null) {
                logger.warning("Resource not found: " + filepath);
                return;
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            Clip sfxClip = AudioSystem.getClip();
            sfxClip.open(audioInputStream);

            // 효과음의 볼륨 컨트롤 설정
            FloatControl sfxVolumeControl = getSupportedVolumeControl(sfxClip);
            if (sfxVolumeControl != null) {
                setVolume(sfxVolumeControl, soundEffectsVolume);
            } else {
                logger.warning("지원되는 볼륨 컨트롤을 찾을 수 없습니다.");
            }

            sfxClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    sfxClip.close();
                }
            });
            sfxClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logger.warning("효과음 재생 실패: " + e.getMessage());
        }
    }

    /**
     * 효과음 볼륨을 설정합니다.
     *
     * @param gain 설정할 볼륨 (0.0 - 1.0)
     */
    private void setSoundEffectsVolume(float gain) {
        soundEffectsVolume = Math.max(0.0f, Math.min(1.0f, gain)); // 볼륨 범위 제한
    }

    /**
     * 현재 볼륨 값을 조정합니다.
     *
     * @param volumeControl 볼륨 컨트롤 객체
     * @param gain          설정할 볼륨 (0.0 - 1.0)
     */
    private void setVolume(FloatControl volumeControl, float gain) {
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();
        float dB;

        if (gain <= 0.0f) {
            dB = min;
        } else {
            dB = (float) (Math.log10(gain) * 20.0);
            dB = Math.max(min, Math.min(dB, max)); // dB 값 범위 제한
        }
        volumeControl.setValue(dB);
    }

    /**
     * 오디오 클립이 지원하는 볼륨 컨트롤을 반환합니다.
     *
     * @param clip 오디오 클립
     * @return 지원되는 FloatControl 객체 또는 null
     */
    private FloatControl getSupportedVolumeControl(Clip clip) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } else if (clip.isControlSupported(FloatControl.Type.VOLUME)) {
            return (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
        } else {
            return null;
        }
    }

    /**
     * 배경음악 볼륨을 0.1 증가시킵니다.
     */
    public void BGMUp() {
        float newBgmVolume = Math.min(1.0f, backgroundMusicVolume + 0.1f);
        setBackgroundMusicVolume(Math.round(newBgmVolume * 10) / 10.0f); // 소수점 첫째 자리까지 반올림
    }

    /**
     * 배경음악 볼륨을 0.1 감소시킵니다.
     */
    public void BGMDown() {
        float newBgmVolume = Math.max(0.0f, backgroundMusicVolume - 0.1f);
        setBackgroundMusicVolume(Math.round(newBgmVolume * 10) / 10.0f); // 소수점 첫째 자리까지 반올림
    }

    /**
     * 효과음 볼륨을 0.1 증가시킵니다.
     */
    public void SFXUp() {
        float newSfxVolume = Math.min(1.0f, soundEffectsVolume + 0.1f);
        setSoundEffectsVolume(Math.round(newSfxVolume * 10) / 10.0f); // 소수점 첫째 자리까지 반올림
    }

    /**
     * 효과음 볼륨을 0.1 감소시킵니다.
     */
    public void SFXDown() {
        float newSfxVolume = Math.max(0.0f, soundEffectsVolume - 0.1f);
        setSoundEffectsVolume(Math.round(newSfxVolume * 10) / 10.0f); // 소수점 첫째 자리까지 반올림
    }

    // 현재 배경음악과 효과음 볼륨을 가져오는 게터
    public float getBackgroundMusicVolume() {
        return backgroundMusicVolume;
    }

    public float getSoundEffectsVolume() {
        return soundEffectsVolume;
    }

    /**
     * 버튼 클릭 사운드를 재생합니다.
     */
    public void playButtonSound() {
        playSoundEffect("sounds/button.wav");
    }

    /**
     * 총알 발사 사운드를 재생합니다.
     */
    public void playBulletShotSound() {
        playSoundEffect("sounds/bullet_shot.wav");
    }

    /**
     * 총알 적중 사운드를 재생합니다.
     */
    public void playBulletHitSound() {
        playSoundEffect("sounds/bullet_hit.wav");
    }

    /**
     * 아군 함선 피해 사운드를 재생합니다.
     */
    public void playDamageSound() {
        playSoundEffect("sounds/damage.wav");
    }

    /**
     * 아군 함선 파괴 사운드를 재생합니다.
     */
    public void playExplosionSound() {
        playSoundEffect("sounds/explosion.wav");
    }

    /**
     * 게임 시작 사운드를 재생합니다.
     */
    public void playPlaySound() {
        playSoundEffect("sounds/play.wav");
    }

    /**
     * 경험치 획득 사운드를 재생합니다.
     */
    public void playExpCollectSound() {
        playSoundEffect("sounds/exp_collect.wav");
    }

    /**
     * 레벨 업 사운드를 재생합니다.
     */
    public void playLevelUpSound() {
        playSoundEffect("sounds/level_up.wav");
    }

    /**
     * 기본 공격 소리를 재생합니다.
     */
    public void playBasicAttack() {
        playBackgroundMusic("sounds/Lasergun.wav");
    }

    /**
     * 크리스탈 때리는 소리를 재생합니다.
     */
    public void playHitCrystal() {
        playBackgroundMusic("sounds/CrystalBreak.wav");
    }

    /**
     * 무적인 보스를 때리는 소리를 연출합니다.
     */
    public void playHitInvicibleBoss() {
        playBackgroundMusic("sounds/InvicibleHit.wav");
    }

    /**
     * 배경음악이 재생 중인지 확인합니다.
     *
     * @return 배경음악이 재생 중이면 true, 아니면 false
     */
    public boolean isBackgroundMusicPlaying() {
        return bgmClip != null && bgmClip.isRunning();
    }

    /**
     * 타이틀 스크린 배경음악을 재생합니다.
     */
    public void playTitleScreenBGM() {
        playBackgroundMusic("sounds/title_screen.wav");
    }

    /**
     * 인 게임 배경음악을 재생합니다.
     */
    public void playInGameBGM() {
        playBackgroundMusic("sounds/in_game.wav");
    }

    /**
     * 로비 배경음악을 재생합니다.
     */
    public void playLobbyBGM() {
        playBackgroundMusic("sounds/lobby.wav");
    }

    /**
     * 엔딩 배경음악을 재생합니다.
     */
    public void playEndingBGM() {
        playBackgroundMusic("sounds/ending.wav");
    }

    /**
     * 보스 배경음악을 재생합니다.
     */
    public void playBossBGM() {
        playBackgroundMusic("sounds/boss.wav");
    }
}