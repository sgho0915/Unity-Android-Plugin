package com.systronics.plugin;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import com.unity3d.player.UnityPlayer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RTSPPlayer extends Activity {

    private static MediaPlayer mediaPlayer;
    private static SurfaceTexture surfaceTexture;
    private static Handler handler;
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void PlayRTSP(final String rtspUrl) {
        final Activity activity = UnityPlayer.currentActivity;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextureView textureView = new TextureView(activity);
                textureView.setSurfaceTextureListener(new SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                        RTSPPlayer.surfaceTexture = surfaceTexture;
                        Surface surface = new Surface(surfaceTexture);
                        mediaPlayer.setSurface(surface);

                        mediaPlayer.start();  // RTSP 스트리밍 시작

                        // Start a new thread to retrieve frame data
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                while (mediaPlayer.isPlaying()) {
                                    // Get the frame data from SurfaceTexture
                                    byte[] frameData = getFrameDataFromSurfaceTexture(surfaceTexture, width, height);
                                    if (frameData != null) {
                                        updateUnityTexture(frameData);
                                    }

                                    // Sleep for a short period to reduce CPU usage
                                    try {
                                        Thread.sleep(33); // ~30 FPS
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                        // Handle texture size changes if needed
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                        mediaPlayer.release();
                        return true;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                        // Handle texture updates if needed
                    }
                });
                activity.setContentView(textureView);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(rtspUrl);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static byte[] getFrameDataFromSurfaceTexture(SurfaceTexture surfaceTexture, int width, int height) {
        int[] pixels = new int[width * height];
        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4); // RGBA 4 bytes per pixel
        surfaceTexture.getTransformMatrix(new float[16]); // Optional: apply any transformation if needed
        surfaceTexture.updateTexImage(); // Update the image from the surface

        buffer.rewind();
        buffer.asIntBuffer().get(pixels);
        byte[] frameData = new byte[width * height * 4];
        for (int i = 0; i < pixels.length; i++) {
            frameData[i * 4] = (byte) ((pixels[i] >> 16) & 0xFF); // R
            frameData[i * 4 + 1] = (byte) ((pixels[i] >> 8) & 0xFF); // G
            frameData[i * 4 + 2] = (byte) (pixels[i] & 0xFF); // B
            frameData[i * 4 + 3] = (byte) ((pixels[i] >> 24) & 0xFF); // A
        }
        return frameData;
    }

    private static void updateUnityTexture(byte[] frameData) {
        handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                UnityPlayer.UnitySendMessage("RTSPStreamer", "UpdateFrame", new String(frameData));
            }
        });
    }
}
