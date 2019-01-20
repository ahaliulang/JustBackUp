package com.ahaliulang.work;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahaliulang.work.bean.CibaBean;
import com.ahaliulang.work.network.Network;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ShareCardActivity extends AppCompatActivity {

    public static final int CHOOSE_PHOTO = 1010;
    public static final int PHOTO_REQUEST_GALLERY = 1011;
    public static final int PHOTO_REQUEST_CUT = 1012;
    private Button avatarBtn;
    private Button bgBtn;
    private Button buildBtn;
    private Button changeAvatarBtn;
    private Button changeBgBtn;
    private Button changeContentBtn;
    private Button addBtn;
    private Button subBtn;
    private EditText qrEditText;
    private EditText contentEditText;
    private TextView shareTextView;
    private CardStyleInfo mCardStyleInfo;
    private ImageView mPreImageView;
    private Bitmap mShareBitmap;
    private Disposable subscribe;
    private CibaBean mCibaBean;
    private List<Bitmap> mBgList = new ArrayList<>();
    private List<String> mContentList = new ArrayList<>();
    private List<Bitmap> mAvatarList = new ArrayList<>();
    private int mAvatarIndex;
    private int mBgIndex;
    private int mContentIndex;
    private int mCountDays;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_card);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mCardStyleInfo = new CardStyleInfo();
        List<Bitmap> bitmapInfos = new ArrayList<>();
        Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.share_card_noworks_bg);
        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
        bitmapInfos.add(bg);
        bitmapInfos.add(avatar);
        mBgList.add(bg);
        mAvatarList.add(avatar);
        mCardStyleInfo.bitmapInfos = bitmapInfos;
        mCountDays = TimeUtil.countShareDays();
        mCardStyleInfo.day = String.valueOf(TimeUtil.countShareDays());
        mCardStyleInfo.introduction = "Why are you trying so hard to fit in when you are born to stand out?\n" +
                "你本就生而不凡，为何还要拼命去融入大众呢？";
        subscribe = Network.getCibaApi().ciba(TimeUtil.getDate()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<CibaBean>() {
                    @Override
                    public void accept(CibaBean cibaBean) throws Exception {
                        if (cibaBean != null) {
                            mCibaBean = cibaBean;
                            mCardStyleInfo.introduction = cibaBean.getContent();
                            mContentList.add(cibaBean.getContent());
                            mContentList.add(cibaBean.getNote());
                            mContentList.add(cibaBean.getContent() + "\n" + cibaBean.getNote());
                            Glide.with(ShareCardActivity.this).load(mCibaBean.getPicture2()).asBitmap().into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    mBgList.add(resource);
                                    mAvatarList.add(resource);
                                }
                            });
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void initView() {
        avatarBtn = findViewById(R.id.avatar_btn);
        bgBtn = findViewById(R.id.bg_btn);
        buildBtn = findViewById(R.id.build_btn);
        qrEditText = findViewById(R.id.qr_edit);
        contentEditText = findViewById(R.id.content_edit);
        shareTextView = findViewById(R.id.share_text_view);
        mPreImageView = findViewById(R.id.preview_image_view);
        changeAvatarBtn = findViewById(R.id.change_avatar_btn);
        changeBgBtn = findViewById(R.id.change_bg_btn);
        changeContentBtn = findViewById(R.id.change_content);
        addBtn = findViewById(R.id.add_btn);
        subBtn = findViewById(R.id.sub_btn);
    }

    private void initListener() {
        shareTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mShareBitmap != null) {
                        String path = BitmapUtil.saveTempImageDefault(ShareCardActivity.this, mShareBitmap);
                        String url = BitmapUtil.insertImageToSystem(ShareCardActivity.this, path);
                        if (!TextUtils.isEmpty(url)) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
                            sendIntent.setType("image/png");
                            Intent chooser = Intent.createChooser(sendIntent, "tantiago");
                            if (sendIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(chooser);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        buildBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                buildShareCard();
                Toast.makeText(ShareCardActivity.this, "生成卡片成功", Toast.LENGTH_SHORT).show();
            }
        });
        changeContentBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(contentEditText.getText().toString())) {
                    mContentList.add(3, contentEditText.getText().toString());
                }
                mContentIndex = (++mContentIndex) % mContentList.size();
                mCardStyleInfo.introduction = mContentList.get(mContentIndex);
                buildShareCard();
            }
        });

        changeBgBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                mBgIndex = (++mBgIndex) % mBgList.size();
                mCardStyleInfo.bitmapInfos.set(0, mBgList.get(mBgIndex));
                buildShareCard();
            }
        });

        changeAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                mAvatarIndex = (++mAvatarIndex) % mAvatarList.size();
                mCardStyleInfo.bitmapInfos.set(1, mAvatarList.get(mAvatarIndex));
                buildShareCard();
            }
        });

        avatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openAlbum();
                openGallery();
            }
        });
        bgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlbum();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                mCardStyleInfo.day = String.valueOf(++mCountDays);
                buildShareCard();
            }
        });
        subBtn.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                mCardStyleInfo.day = String.valueOf(--mCountDays);
                buildShareCard();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void buildShareCard() {
        if (!TextUtils.isEmpty(qrEditText.getText().toString())) {
            mCardStyleInfo.qrUrl = qrEditText.getText().toString();
        }
        mShareBitmap = BitmapUtil.generateCardNoWorksStyle(ShareCardActivity.this, mCardStyleInfo);
        if (mShareBitmap != null) {
            mPreImageView.setImageBitmap(mShareBitmap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);//打开相册
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        //开启一个带返回值的Activity,请求码为PHOTO_REQUEST_GALLERY
        // context.startActivity(intent);
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    }
                }
                break;
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    //得到图片的全路径
                    Uri uri = data.getData();
                    crop(uri);
                }
                break;
            case PHOTO_REQUEST_CUT:
                if (data != null && resultCode == RESULT_OK) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    mBgList.add(bitmap);
                    mAvatarList.add(bitmap);
                    mCardStyleInfo.bitmapInfos.set(1, bitmap);
                    buildShareCard();
                }

                break;
            default:
                break;
        }
    }


    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        Glide.with(this).load(imagePath).asBitmap().into(new SimpleTarget<Bitmap>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                //mPreImageView.setImageBitmap(resource);
                mAvatarList.add(resource);
                mBgList.add(resource);
                mCardStyleInfo.bitmapInfos.set(0,resource);
                buildShareCard();
            }
        });
    }


    /**
     * 剪切图片
     */
    private void crop(Uri uri) {

        Log.e("1223", "crop: 1");
        //裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("corp", "true");
        //裁剪框比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");//图片格式
        //intent.putExtra("noFaceDetection", true);//取消人脸识别
        intent.putExtra("return-data", true);
        //开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


}
