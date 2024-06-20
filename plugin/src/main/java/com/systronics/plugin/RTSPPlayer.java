package com.systronics.plugin;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.unity3d.player.UnityPlayer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RTSPPlayer extends Activity {

    private static MediaPlayer mediaPlayer;
    private static SurfaceTexture surfaceTexture;
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    // RTSP 스트림을 재생하는 함수
    public static void PlayRTSP(final String rtspUrl) {
        final Activity activity = UnityPlayer.currentActivity;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 프레임 레이아웃과 텍스처 뷰를 설정
                FrameLayout frameLayout = new FrameLayout(activity);
                TextureView textureView = new TextureView(activity);
                LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                frameLayout.addView(textureView, layoutParams);
                activity.addContentView(frameLayout, layoutParams);

                textureView.setSurfaceTextureListener(new SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture st, int width, int height) {
                        surfaceTexture = st;
                        Surface surface = new Surface(surfaceTexture);

                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(rtspUrl);
                            mediaPlayer.setSurface(surface);
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();

                                    // 새로운 스레드에서 프레임 데이터를 가져옴
                                    executorService.submit(new Runnable() {
                                        @Override
                                        public void run() {
                                            while (mediaPlayer.isPlaying()) {
                                                byte[] frameData = getFrameDataFromSurfaceTexture(surfaceTexture, width, height);
                                                if (frameData != null) {
                                                    updateUnityTexture(frameData);
                                                }

                                                // CPU 사용량을 줄이기 위해 짧은 시간 동안 대기
                                                try {
                                                    Thread.sleep(1000 / 30); // ~30 FPS
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                        // 텍스처 크기 변경 처리
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                        mediaPlayer.release();
                        return true;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                        // 텍스처 업데이트 처리
                    }
                });
            }
        });
    }

    // SurfaceTexture에서 프레임 데이터를 가져오는 함수
    private static byte[] getFrameDataFromSurfaceTexture(SurfaceTexture surfaceTexture, int width, int height) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4); // RGBA 4바이트 픽셀 당
        surfaceTexture.updateTexImage(); // 서피스에서 이미지 업데이트

        buffer.rewind();
        byte[] frameData = new byte[buffer.capacity()];
        buffer.get(frameData);
        return frameData;
    }

    // Unity 텍스처를 업데이트하는 함수
    private static void updateUnityTexture(byte[] frameData) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                String frameDataBase64 = Base64.encodeToString(frameData, Base64.NO_WRAP);
                UnityPlayer.UnitySendMessage("RTSPStreamer", "UpdateFrame", frameDataBase64);
            }
        });
    }
}
