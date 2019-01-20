package com.ahaliulang.work;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.ahaliulang.work.bean.CardStyleInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class BitmapUtil {

    //生成名片样式三，没有作品
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap generateCardNoWorksStyle(Context context, CardStyleInfo cardStyleInfo) {
        if (cardStyleInfo == null || cardStyleInfo.bitmapInfos == null) {
            //throw new IllegalArgumentException("illegalArgumentException");
            return null;
        }


        final int resultWidth = 750; //根据设计图写死的宽高 744
        final int resultHeight = 1333;

        Bitmap result = Bitmap.createBitmap(resultWidth, resultHeight, Bitmap.Config.ARGB_8888);
        result.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(result);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        TextPaint paint = new TextPaint();
        paint.setColor(Color.parseColor("#ccf5f5f5"));
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);


        //画背景
        //分割线
        Bitmap noworkBg = BitmapFactory.decodeResource(context.getResources(), R.drawable.share_card_noworks_bg);
        if (cardStyleInfo.bitmapInfos.size() > 0) {
            noworkBg = cardStyleInfo.bitmapInfos.get(0);
            noworkBg = CreateFixBitmap(noworkBg, resultWidth, resultHeight, POS_CENTER, 0, Bitmap.Config.ARGB_8888);
        }
        Rect srcplay = new Rect(0, 0, noworkBg.getWidth(), noworkBg.getHeight());
        Rect dstplay = new Rect(0, 0, resultWidth, resultHeight);
        canvas.drawBitmap(noworkBg, srcplay, dstplay, null);

        //白色小背景
        int left = (resultWidth - 606) / 2;
        int top = 138;
        int right = (resultWidth - 606) / 2 + 606;
        int bottom = 140 + 1116;
        canvas.drawRoundRect(left, top, right, bottom, 15, 15, paint);


        //获取头像
        Bitmap avatar = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar);
        if (cardStyleInfo.bitmapInfos.size() > 1) {
            avatar = cardStyleInfo.bitmapInfos.get(1);
        }
        Bitmap avatarBitmap = CreateBitmap(avatar, -1, -1, 1f, 0, Bitmap.Config.ARGB_8888);
        //裁剪为圆形

        //头像描边
        paint.setColor(Color.BLACK);
        canvas.drawCircle((resultWidth - 222) / 2 + 111, 367, 113, paint);

        Bitmap output = Bitmap.createBitmap(resultWidth, resultHeight, Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(output);
        bitmapCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        bitmapCanvas.drawCircle((resultWidth - 222) / 2 + 111, 367, 111, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect src = new Rect(0, 0, avatarBitmap.getWidth(), avatarBitmap.getHeight());
        Rect des = new Rect((resultWidth - 222) / 2, 256, (resultWidth - 222) / 2 + 222, 478);
        bitmapCanvas.drawBitmap(avatarBitmap, src, des, paint);
        paint.setXfermode(null);
        Rect rect = new Rect(0, 0, output.getWidth(), output.getHeight());
        canvas.drawBitmap(output, rect, rect, paint);


        //昵称
        paint.setColor(Color.BLACK);
        paint.setTextSize(88);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.LEFT);

        int textMaxWidth = resultWidth - 280;
        String ellipsizeNickname = TextUtils.ellipsize(cardStyleInfo.day, paint, textMaxWidth, TextUtils.TruncateAt.END).toString();
        float v = paint.measureText(ellipsizeNickname);
        int nickLeft = (int) ((resultWidth - v) / 2);
        Rect bound = new Rect();
        paint.getTextBounds(cardStyleInfo.day, 0, cardStyleInfo.day.length(), bound);
        //获取 baseline
        int nickNameBaseLine = bound.height() - bound.bottom;
        canvas.drawText(ellipsizeNickname, nickLeft, 525 + nickNameBaseLine, paint);

        //简介
        paint.setColor(Color.parseColor("#333333"));
        paint.setTextSize(32);
        paint.setFakeBoldText(false);


        if (!TextUtils.isEmpty(cardStyleInfo.introduction)) {
            StaticLayout staticLayout = new StaticLayout(cardStyleInfo.introduction, paint, textMaxWidth, Layout.Alignment.ALIGN_NORMAL, 1, 6, false);
            canvas.save();
            canvas.translate(160, 555 + nickNameBaseLine);
            staticLayout.draw(canvas);
            canvas.restore();


            //作者
            if (!TextUtils.isEmpty(cardStyleInfo.author)) {
                if(!cardStyleInfo.author.contains("——")){
                    cardStyleInfo.author = "——" + cardStyleInfo.author;
                }
                //简介
                paint.setColor(Color.parseColor("#7c7c7c"));
                paint.setTextSize(30);
                paint.setFakeBoldText(false);
                paint.setTextAlign(Paint.Align.RIGHT);
                //获取 baseline
                int authorBaseLine = getTextBaseLine(paint, cardStyleInfo.author);
                float authorLeft = resultWidth - 150;
                canvas.drawText(cardStyleInfo.author, authorLeft, 570 + nickNameBaseLine + authorBaseLine + staticLayout.getHeight(), paint);
            }
        }


        //画二维码
        int qrRight = resultWidth - 112;
        int qrLeft = qrRight - 150;
        int qrBottom = resultHeight - 120;
        int qrTop = qrBottom - 150;
        String qrcodeUrl = TextUtils.isEmpty(cardStyleInfo.qrUrl) ? "---今天没有分享的彩蛋---" : cardStyleInfo.qrUrl;
        Bitmap qrCodeBitmap = getQrCode(qrcodeUrl, 150, 150, BarcodeFormat.QR_CODE);
        Rect qrCodeSrcRect = new Rect(0, 0, qrCodeBitmap.getWidth(), qrCodeBitmap.getHeight());
        Rect qrCodeDesRect = new Rect(qrLeft, qrTop, qrRight, qrBottom);
        canvas.drawBitmap(qrCodeBitmap, qrCodeSrcRect, qrCodeDesRect, paint);


        //签名
        String sign = "玩一下\n@tantiago";
        paint.setColor(Color.parseColor("#7c7c7c"));
        paint.setTextSize(18);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setFakeBoldText(false);
        StaticLayout staticLayout = new StaticLayout(sign, paint, textMaxWidth, Layout.Alignment.ALIGN_NORMAL, 1, 7, true);
        canvas.save();
        canvas.translate(resultWidth - 360, resultHeight - 177);
        staticLayout.draw(canvas);
        canvas.restore();

        return result;
    }


    /**
     * 获取二维码
     *
     * @param url
     * @param width
     * @param height
     * @param format {@link BarcodeFormat#QR_CODE}
     * @return
     * @throws WriterException
     */
    public static Bitmap getQrCode(String url, int width, int height, BarcodeFormat format) {
        return getQrCode(url, width, height, BarcodeFormat.QR_CODE, null);
    }

    /**
     * 获取二维码
     *
     * @param url
     * @param width
     * @param height
     * @param format
     * @param errorCorrectionLevel 容错率
     * @return
     */
    public static Bitmap getQrCode(String url, int width, int height, BarcodeFormat format, ErrorCorrectionLevel errorCorrectionLevel) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            Hashtable<EncodeHintType, Object> hst = new Hashtable<EncodeHintType, Object>();
            hst.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hst.put(EncodeHintType.MARGIN, 0);//边距
            if (errorCorrectionLevel != null) {
                hst.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
            }
            BitMatrix matrix = null;

            matrix = writer.encode(url, format, width, height, hst);

            matrix = deleteWhite(matrix);//删除白边
            width = matrix.getWidth();
            height = matrix.getHeight();

            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        //pixels[y * width + x] = 0xffffffff;
                        pixels[y * width + x] = 0x00ffffff;
                    }

                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 通过像素数组生成bitmap,具体参考api
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1])) resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }


    /**
     * 根据限制参数输出图片，图片不会拉伸
     *
     * @param bmp       原始bitmap
     * @param maxW      输出容器的最大宽度，-1为不限制
     * @param maxH      输出容器的最大高度，-1为不限制
     * @param scale_w_h 输出图片的宽高比例，-1为按图比例
     * @param rotate    原始bitmap需要旋转的角度 0,90,180,270...
     * @param config    像素类型
     */
    public static Bitmap CreateBitmap(Bitmap bmp, int maxW, int maxH, float scale_w_h, int rotate, Bitmap.Config config) {
        Bitmap outBmp = null;
        if (bmp != null) {
            int inW = bmp.getWidth();
            int inH = bmp.getHeight();

            if (rotate % 180 != 0) {
                inW += inH;
                inH = inW - inH;
                inW -= inH;
            }

            MyRect clipRect;
            if (scale_w_h > 0) {
                clipRect = MakeRect(inW, inH, scale_w_h);
            } else {
                clipRect = new MyRect();
                clipRect.m_x = 0;
                clipRect.m_y = 0;
                clipRect.m_w = inW;
                clipRect.m_h = inH;
            }

            float scale = 1;
            if (maxW > 0 && maxH > 0 && (maxW < clipRect.m_w || maxH < clipRect.m_h)) {
                float scale1 = (float) maxW / clipRect.m_w;
                float scale2 = (float) maxH / clipRect.m_h;
                if (scale1 <= scale2) {
                    scale = scale1;
                } else {
                    scale = scale2;
                }
            } else if (maxW > 0) {
                if (maxW < clipRect.m_w) {
                    scale = (float) maxW / clipRect.m_w;
                }
            } else if (maxH > 0) {
                if (maxH < clipRect.m_h) {
                    scale = (float) maxH / clipRect.m_h;
                }
            }

            int bmpW = Math.round(clipRect.m_w * scale);
            if (bmpW < 1) {
                bmpW = 1;
            }
            int bmpH = Math.round(clipRect.m_h * scale);
            if (bmpH < 1) {
                bmpH = 1;
            }
            float centerX = bmpW / 2f;
            float centerY = bmpH / 2f;
            Matrix matrix = new Matrix();
            matrix.postTranslate((bmpW - bmp.getWidth()) / 2f, (bmpH - bmp.getHeight()) / 2f);
            matrix.postRotate(rotate, centerX, centerY);
            matrix.postScale(scale, scale, centerX, centerY);

            outBmp = Bitmap.createBitmap(bmpW, bmpH, config);
            Canvas canvas = new Canvas(outBmp);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Paint pt = new Paint();
            pt.setAntiAlias(true);
            pt.setFilterBitmap(true);
            canvas.drawBitmap(bmp, matrix, pt);
        }

        return outBmp;
    }

    /**
     * 返回符合比例的图片裁剪矩形
     *
     * @param w         图片的宽
     * @param h         图片的高
     * @param scale_w_h 宽高比例
     */
    public static MyRect MakeRect(float w, float h, float scale_w_h) {
        MyRect outRect = new MyRect();
        outRect.m_w = w;
        outRect.m_h = w / scale_w_h;
        if (outRect.m_h > h) {
            outRect.m_h = h;
            outRect.m_w = h * scale_w_h;
        }
        outRect.m_x = (w - outRect.m_w) / 2f;
        outRect.m_y = (h - outRect.m_h) / 2f;

        return outRect;
    }

    public static class MyRect {
        public float m_x;
        public float m_y;
        public float m_w;
        public float m_h;
    }


    public static final int POS_START = 0x0100;
    public static final int POS_CENTER = 0x0200;
    public static final int POS_END = 0x0400;

    /**
     * 按比例缩放，输出大小与要求的一致（自动裁剪超出部分）
     *
     * @param bmp     原始bitmap
     * @param outW    输出的图片宽
     * @param outH    输出的图片高
     * @param posType POS_START(开始截取) POS_CENTER(中间截取) POS_END(尾部截取)
     * @param rotate  原始bitmap需要旋转的角度0,90,180,270...
     * @param config  像素类型
     */
    public static Bitmap CreateFixBitmap(Bitmap bmp, int outW, int outH, int posType, int rotate, Bitmap.Config config) {
        Bitmap outBmp = null;
        if (bmp != null) {
            int inW = bmp.getWidth();
            int inH = bmp.getHeight();

            if (rotate % 180 != 0) {
                inW += inH;
                inH = inW - inH;
                inW -= inH;
            }

            float scale = (float) outW / inW;
            if (scale * inH < outH) {
                scale = (float) outH / inH;
            }

            Matrix matrix = new Matrix();
            switch (posType) {
                case POS_START:
                    switch (rotate % 360) {
                        case 0:
                            matrix.postScale(scale, scale, 0, 0);
                            break;
                        case 90:
                            matrix.postTranslate(inW, 0);
                            matrix.postRotate(rotate, inW, 0);
                            matrix.postScale(scale, scale, 0, 0);
                            break;
                        case 180:
                            matrix.postTranslate(inW, inH);
                            matrix.postRotate(rotate, inW, inH);
                            matrix.postScale(scale, scale, 0, 0);
                            break;
                        case 270:
                            matrix.postTranslate(0, inH);
                            matrix.postRotate(rotate, 0, inH);
                            matrix.postScale(scale, scale, 0, 0);
                            break;
                        default:
                            break;
                    }
                    break;
                case POS_CENTER:
                    matrix.postTranslate((outW - bmp.getWidth()) / 2f, (outH - bmp.getHeight()) / 2f);
                    matrix.postRotate(rotate, outW / 2f, outH / 2f);
                    matrix.postScale(scale, scale, outW / 2f, outH / 2f);
                    break;
                case POS_END:
                    switch (rotate % 360) {
                        case 0:
                            matrix.postTranslate(outW - inW, outH - inH);
                            matrix.postScale(scale, scale, outW, outH);
                            break;
                        case 90:
                            matrix.postRotate(rotate, 0, 0);
                            matrix.postTranslate(outW, outH - inH);
                            matrix.postScale(scale, scale, outW, outH);
                            break;
                        case 180:
                            matrix.postRotate(rotate, 0, 0);
                            matrix.postTranslate(outW, outH);
                            matrix.postScale(scale, scale, outW, outH);
                            break;
                        case 270:
                            matrix.postRotate(rotate, 0, 0);
                            matrix.postTranslate(outW - inW, outH);
                            matrix.postScale(scale, scale, outW, outH);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }

            outBmp = Bitmap.createBitmap(outW, outH, config);
            Canvas canvas = new Canvas(outBmp);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Paint pt = new Paint();
            pt.setAntiAlias(true);
            pt.setFilterBitmap(true);
            canvas.drawBitmap(bmp, matrix, pt);
        }

        return outBmp;
    }

    /**
     * 拉伸图片
     *
     * @param bmp    原始bitmap
     * @param outW   输出的图片宽
     * @param outH   输出的图片高
     * @param rotate 原始bitmap需要旋转的角度0,90,180,270...
     * @param config 像素类型
     */
    public static Bitmap CreateTensileBitmap(Bitmap bmp, int outW, int outH, int rotate, Bitmap.Config config) {
        Bitmap outBmp = null;
        if (bmp != null) {
            int inW = bmp.getWidth();
            int inH = bmp.getHeight();

            if (rotate % 180 != 0) {
                inW += inH;
                inH = inW - inH;
                inW -= inH;
            }

            Matrix matrix = new Matrix();
            matrix.postTranslate((outW - bmp.getWidth()) / 2f, (outH - bmp.getHeight()) / 2f);
            matrix.postRotate(rotate, outW / 2f, outH / 2f);
            matrix.postScale((float) outW / (float) inW, (float) outH / (float) inH, outW / 2f, outH / 2f);

            outBmp = Bitmap.createBitmap(outW, outH, config);
            Canvas canvas = new Canvas(outBmp);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Paint pt = new Paint();
            pt.setAntiAlias(true);
            pt.setFilterBitmap(true);
            canvas.drawBitmap(bmp, matrix, pt);
        }

        return outBmp;
    }


    public static String saveTempImageDefault(Context context, Bitmap bmp) {
        String tempImg = context.getCacheDir().getPath();
        File file = new File(tempImg + File.separator + "temp.png");
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bmp.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static String insertImageToSystem(Context context, String imagePath) {
        String url = "";
        try {
            url = MediaStore.Images.Media.insertImage(context.getContentResolver(), imagePath, "tantiago", "你对图片的描述");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static int getTextBaseLine(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height() - rect.bottom;
    }


}
