package com.systronics.plugin;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import java.util.Locale;

/**
 * ★★★ TTSPlugin 클래스
 * - 안드로이드 기본 TextToSpeech 기능 Wrapping
 * - 싱글톤으로 Unity에서 접근
 */
public class TTSPlugin implements TextToSpeech.OnInitListener {
    private static TTSPlugin instance;
    private TextToSpeech tts;
    private static Activity unityActivity;

    /**
     * 플러그인 초기화 전에 반드시 호출
     */
    public static void setActivity(Activity activity) {
        unityActivity = activity;
    }

    /**
     * 싱글톤 인스턴스 반환
     */
    public static TTSPlugin getInstance() {
        if (instance == null) {
            instance = new TTSPlugin();
        }
        return instance;
    }

    /**
     * 초기화: TTS 객체 생성
     */
    public void init() {
        if (tts == null) {
            tts = new TextToSpeech(unityActivity, this);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.getDefault());
            // 발음 속도, 피치 등 추가 설정 가능
            tts.setPitch(1.0f);
            tts.setSpeechRate(1.0f);
        }
    }

    /**
     * 텍스트를 음성으로 출력
     * @param text 출력할 문자열
     */
    public void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UTTERANCE_ID");
        }
    }

    /**
     * TTS 리소스 해제
     */
    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}
