package engine;

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

    // 생성자 - 로그 설정 및 초기 볼륨 값을 1.0으로 설정
    private SoundManager() {
        logger = Core.getLogger();
        backgroundMusicVolume = 1.0f;
        soundEffectsVolume = 1.0f;
    }

    // SoundManager 인스턴스를 싱글톤 패턴으로 가져오기
    public static SoundManager getInstance() {
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
    public void playBackgroundMusic(String filepath) {
        try {
            if (bgmClip != null && bgmClip.isOpen()) {
                bgmClip.stop();
                bgmClip.close();
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filepath));
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
    public void setBackgroundMusicVolume(float gain) {
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
    public void playSoundEffect(String filepath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filepath));
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
    public void setSoundEffectsVolume(float gain) {
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

    // 현재 배경음악과 효과음 볼륨을 가져오는 게터
    public float getBackgroundMusicVolume() {
        return backgroundMusicVolume;
    }

    public float getSoundEffectsVolume() {
        return soundEffectsVolume;
    }
}