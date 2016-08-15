package com.ioyouyun.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import com.ioyouyun.YouyunApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by 卫彪 on 2016/6/15.
 */
public class FileUtil {

    public static String getAppRootPath() {
        String filePath = "/youyun_chat";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filePath = Environment.getExternalStorageDirectory() + filePath;
        } else {
            filePath = YouyunApplication.application.getApplicationContext().getCacheDir() + filePath;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = null;
        return filePath;
    }

    public static String getAudioPath() {
        String path = getAppRootPath() + "/audio/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        return path;
    }

    public static String getUserAudioPath(String uid) {
        String path = getAudioPath() + uid + "/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        return path;
    }

    public static void removeFile(String name) {
        File removeFile = new File(name);
        if (removeFile.exists()) {
            removeFile.delete();
            Logger.d("删除文件：" + name);
        }
    }

    public static String getImageRootPath() {
        String filePath = getAppRootPath() + "/image/";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = null;
        return filePath;
    }

    public static String getCameraPath() {
        String filePath = getImageRootPath() + "/camera/";
        File file = new File(filePath);
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        file = null;
        return filePath;
    }

    public static String getChatImageRootPath() {
        String path = getImageRootPath() + "chat_img/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getThumbnailImgRootPath(String uid) {
        String filePath = "/chat_thumbnail/" + uid + "/";
        filePath = getAppRootPath() + filePath;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = null;

        return filePath;
    }

    public static String getThumbnailPath(String uid, String filename) {
        String path = getThumbnailImgRootPath(uid) + filename + ".png";
        return path;
    }

    public static String getChatImagePath(String fileName) {
        String path = getChatImageRootPath() + fileName;
        return path;
    }

    public static String compressImage(String path, String destPath) {
        try {
            // 获取源图片的大小
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // 当opts不为null时，但decodeFile返回空，不为图片分配内存，只获取图片的大小，并保存在opts的outWidth和outHeight
            BitmapFactory.decodeFile(path, opts);
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;
            int destWidth = 0;
            int destHeight = 0;
            // 缩放的比例
            double ratio = 0.0;

            // 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
			/*if (srcWidth > srcHeight) {
				ratio = (double) srcWidth / (double) 1600;
				destWidth = 1600;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / (double) 1200;
				destHeight = 1200;
				destWidth = (int) (srcWidth / ratio);
			}*/

            if ((srcHeight > 4000 && srcWidth < 1000)
                    || (srcWidth > 4000 && srcHeight < 1000))
                return path;
            int min = srcWidth > srcHeight ? srcHeight : srcWidth;
            if (min <= 720) {
                destHeight = srcHeight;
                destWidth = srcWidth;
            } else {
                if (min == srcHeight) {
                    ratio = (double) srcHeight / (double) 720;
                    destHeight = 720;
                    destWidth = (int) (srcWidth / ratio);
                } else {
                    ratio = (double) srcWidth / (double) 720;
                    destWidth = 720;
                    destHeight = (int) (srcHeight / ratio);
                }
            }
            // 对图片进行压缩，是在读取的过程中进行压缩，而不是把图片读进了内存再进行压缩
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            newOpts.inSampleSize = (int) ratio + 1;
            // inJustDecodeBounds设为false表示把图片读进内存中
            newOpts.inJustDecodeBounds = false;
            // 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            // 添加尺寸信息，
            // 获取缩放后图片
            Bitmap destBm = BitmapFactory.decodeFile(path, newOpts);

			/*if (srcWidth < srcHeight) {
				Matrix matrix = new Matrix(); // 将图像顺时针旋转90度
				matrix.setRotate(270); // 生成旋转后的图像
				destBm = Bitmap.createBitmap(destBm, 0, 0, destBm.getWidth(),destBm.getHeight(), matrix, false);
			}*/

            if (destBm == null) {
                return path;
            } else {
                try {
                    ExifInterface exif = new ExifInterface(path);
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, 1);
                    Log.d("EXIF", "Exif: " + orientation);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                    } else if (orientation == 3) {
                        matrix.postRotate(180);
                    } else if (orientation == 8) {
                        matrix.postRotate(270);
                    }
                    destBm = Bitmap
                            .createBitmap(destBm, 0, 0, destBm.getWidth(),
                                    destBm.getHeight(), matrix, true); // rotating
                    // bitmap
                } catch (Exception e) {
                    Log.e("FileUtil", "" + e.getMessage());
                }
                File destFile = new File(destPath);
                // 创建文件输出流
                OutputStream os = new FileOutputStream(destFile);
                // 存储
                destBm.compress(Bitmap.CompressFormat.JPEG, 90, os);
                // 关闭流
                os.close();
                destBm.recycle();
                return destPath;
            }
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
        return null;
    }

    // 发送图片的缩略图大小
    public final static int THUMBNAIL_WIDTH = 150;
    public final static int THUMBNAIL_HEIGHT = 150;
    public final static long THUMBNAIL_MAX_LEN = 50 * 1024;

    public static byte[] genSendImgThumbnail(String filePath) {
        byte[] b = null;
        if (filePath != null && !filePath.equals("")) {
            File f = new File(filePath);
            if (f.exists()) {
                Bitmap bmp = compressImage(filePath,
                        THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, true);
                if (bmp != null) {
                    b = Bitmap2Bytes(bmp, THUMBNAIL_MAX_LEN);
                }
            }
        }
        return b;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm, long maxLength) {
        ByteArrayOutputStream baos;
        int quality = 100;
        int p = 5;
        do {
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= p;
        } while (baos.toByteArray().length >= maxLength && quality > 0);
        return baos.toByteArray();
    }

    /**
     * 取出来的缩放图片 宽最大不大于max_w 高最大不大于max_h (正比缩放) 原图宽高都小于最大值 则按原图 (注:缩放长宽结果不精确)
     *
     * @param path
     * @param max_w
     * @param max_h
     * @param cut
     *            是否裁剪 if true:缩放时长宽谁先达到要求就停止 其他还有大于max的地方截取中间 if
     *            false:缩放时长宽都要达到要求
     * @return
     */
    public static Bitmap compressImage(String path, int max_w, int max_h,
                                       boolean cut) {
        try {
            // 获取源图片的大小
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // 当opts不为null时，但decodeFile返回空，不为图片分配内存，只获取图片的大小，并保存在opts的outWidth和outHeight
            BitmapFactory.decodeFile(path, opts);
            // 原图的宽高
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;

            // 目标图片的宽高
            int destWidth = 0;
            int destHeight = 0;

            float scaleSize = 1;

            if (srcWidth <= max_w && srcHeight <= max_h) {// 原图宽高都小于最大值 则按原图
                destWidth = srcWidth;
                destHeight = srcHeight;
            } else {// 需要缩放
                float scaleWidth = ((float) max_w / srcWidth);
                float scaleHeight = ((float) max_h / srcHeight);
                // 缩放比例
                if (cut) {
                    scaleSize = Math.max(scaleWidth, scaleHeight);
                } else {
                    scaleSize = Math.min(scaleWidth, scaleHeight);
                }
                destWidth = (int) (srcWidth * scaleSize);
                destHeight = (int) (srcHeight * scaleSize);
            }

            // 对图片进行压缩，是在读取的过程中进行压缩，而不是把图片读进了内存再进行压缩
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            int scaleSizeInt = (int) (1.0 / scaleSize);
            newOpts.inSampleSize = scaleSizeInt;// 可能导致压缩长宽结果不精确
            // inJustDecodeBounds设为false表示把图片读进内存中
            newOpts.inJustDecodeBounds = false;
            // 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            // 添加尺寸信息，
            // 获取缩放后图片
            Bitmap destBm = BitmapFactory.decodeFile(path, newOpts);
            if (cut) {
                if (destBm.getWidth() > max_w || destBm.getHeight() > max_h) {
                    int x, y, w, h;
                    if (destBm.getWidth() > max_w) {
                        x = (destBm.getWidth() - max_w) / 2;
                        w = max_w;
                    } else {
                        x = 0;
                        w = destBm.getWidth();
                    }
                    if (destBm.getHeight() > max_h) {
                        y = (destBm.getHeight() - max_h) / 2;
                        h = max_h;
                    } else {
                        y = 0;
                        h = destBm.getHeight();
                    }
                    destBm = Bitmap.createBitmap(destBm, x, y, w, h);
                }
            }
            return destBm;
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
        return null;
    }

    // 保存byte[]图片
    public static void saveImg(byte[] byteImg, String filePath) {
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeByteArray(byteImg, 0, byteImg.length);
            saveImg(bmp, filePath);

        } catch (Exception e) {
            Log.e("FileUtil", e.getMessage());
        } finally {
            if (bmp != null) {
                bmp.recycle();
                bmp = null;
            }
        }

    }

    public static void saveImg(Bitmap bmp, String filePath) {
        saveImg(bmp, filePath, 100);
    }

    public static void saveImg(Bitmap bmp, String filePath, int quality) {
        File file = new File(filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            if (fos != null) {
                bmp.compress(Bitmap.CompressFormat.PNG, quality, fos);
                fos.flush();
            }

        } catch (Exception e) {
            Log.e("FileUtil", e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("FileUtil", e.getMessage());
                }
                fos = null;
            }
        }
    }


}
