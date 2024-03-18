package com.systronics.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.unity3d.player.UnityPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileChooserActivity extends Activity {
    private static final int REQUEST_CODE_FILE_CHOOSER = 1;
    static String strFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 파일 선택 Intent 호출
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FILE_CHOOSER && resultCode == RESULT_OK) {
            // 선택한 파일의 Uri 가져오기
            Uri selectedFileUri = data.getData();

            // 내부 저장소에 폴더 생성 및 이미지 저장
            String filePath = saveImageToInternalStorage(selectedFileUri);
            strFilePath = filePath;

            // Unity로 파일 경로를 전달
            UnityPlayer.UnitySendMessage("SYSTEM_MANAGER", "ReceiveFilePath", strFilePath);
        }

        // 액티비티 종료
        finish();
    }


    // 내부 저장소에 이미지 저장하고 파일 경로 반환
    // 내부 저장소에 이미지 저장하고 파일 경로 반환
    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            // 이미지를 비트맵으로 디코딩
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // 최상위 외부 저장소 경로에 SYStronics/FPImages 폴더 경로 생성
            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "SYStronics" + File.separator + "FPImages");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 선택한 이미지의 파일명 가져오기
            String[] filePathColumn = {MediaStore.Images.Media.DISPLAY_NAME};
            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            String fileName = null;
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            if (columnIndex >= 0) {
                fileName = cursor.getString(columnIndex);
            } else {
                // 예외 처리 또는 오류 처리
                Log.e("FileChooserActivity", "Invalid column index");
            }
            cursor.close();

            // 최상위 경로의 SYStronics/FPImages 폴더에 이미지 저장
            File file = new File(directory, fileName);
            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

            // 스트림 닫기
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            // 파일 경로 반환
            return file.getPath();
        } catch (Exception e) {
            Log.e("FileChooserActivity", "Error saving image to external storage: " + e.getMessage());
            return null;
        }
    }

}