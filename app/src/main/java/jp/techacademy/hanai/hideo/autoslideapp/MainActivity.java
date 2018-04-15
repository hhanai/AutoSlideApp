package jp.techacademy.hanai.hideo.autoslideapp;

import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.Timer;
import java.util.TimerTask;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;
    double mTimerSec = 0.00;
    Handler mHandler = new Handler();
    Cursor cursor;

    Button FowB,BackB,PlayB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FowB = (Button) findViewById(R.id.fowB);
        FowB.setOnClickListener(this);

        BackB= (Button) findViewById(R.id.backB);
        BackB.setOnClickListener(this);

        PlayB= (Button) findViewById(R.id.playB);
        PlayB.setOnClickListener(this);


        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fowB:
                if ((mTimer)==null) {
                    DispPic("F");
                }
                break;

            case R.id.backB:
                if (mTimer==null) {
                    DispPic("B");
                }
                break;

            case R.id.playB:

                if (mTimer == null) {

                    //　ボタン切り替え
                    PlayB.setText("■");
                    BackB.setTextColor(0x66000000);
                    FowB.setTextColor(0x66000000);

                    //　タイマー設定
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mTimerSec += 2;

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
//                                    mTimerText.setText(String.format("%.1f", mTimerSec));
                                    DispPic("F");
                                }
                            });
                        }
                    }, 1000, 2000);
                }   else  {

                    //　タイマー初期化
                    mTimer.cancel();
                    mTimer = null;
                    PlayB.setText("▶");
                    BackB.setTextColor(0xff000000);
                    FowB.setTextColor(0xff000000);
                }
                break;

            default:
                break;


        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        Log.d("Android", "onDestroy");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {
        int i;
        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            DispPic("S");
        }
    }

    private void DispPic(String mode) {

        switch (mode) {
            case "F":
                if (!(cursor.moveToNext())) {
                    cursor.moveToFirst();
                }
                break;

            case "B":
                if (!(cursor.moveToPrevious())) {
                    cursor.moveToLast();
                }

            case "S":
                break;
        }

        // indexからIDを取得し、そのIDから画像のURIを取得する
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        Log.d("ANDROID", "URI : " + imageUri.toString());
        ImageView imageView = (ImageView) findViewById(R.id.picture);
        imageView.setImageURI(imageUri);

    }
}