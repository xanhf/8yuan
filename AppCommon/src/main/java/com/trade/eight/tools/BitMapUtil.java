package com.trade.eight.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class BitMapUtil {
    private static final Size ZERO_SIZE = new Size(0, 0);
    private static final Options OPTIONS_GET_SIZE = new Options();
    private static final Options OPTIONS_DECODE = new Options();
    private static final byte[] LOCKED = new byte[0];
    // 此对象用来保持Bitmap的回收顺序,保证最后使用的图片被回收
    private static final LinkedList CACHE_ENTRIES = new LinkedList();
    // 线程请求创建图片的队列
    private static final Queue TASK_QUEUE = new LinkedList();
    // 保存队列中正在处理的图片的key,有效防止重复添加到请求创建队列
    private static final Set TASK_QUEUE_INDEX = new HashSet();
    // 缓存Bitmap
    private static final Map IMG_CACHE_INDEX = new HashMap(); // 通过图片路径,图片大小
    private static int CACHE_SIZE = 10; // 缓存图片数量

    private static int MAX_CACHE_MEMORY_SIZE = 10 * 1024 * 1024; // 缓存图片数量

    static {
        OPTIONS_GET_SIZE.inJustDecodeBounds = true;
        // 初始化创建图片线程,并等待处理
        new Thread() {
            {
                setDaemon(true);
            }

            public void run() {
                while (true) {
                    synchronized (TASK_QUEUE) {
                        if (TASK_QUEUE.isEmpty()) {
                            try {
                                TASK_QUEUE.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    QueueEntry entry = (QueueEntry) TASK_QUEUE.poll();
                    String key = createKey(entry.path, entry.width,
                            entry.height);
                    TASK_QUEUE_INDEX.remove(key);
                    createBitmap(entry.path, entry.width, entry.height);
                }
            }
        }.start();
    }

    public static Bitmap getBitmap(String path, int width, int height) {
        if (path == null) {
            return null;
        }
        Bitmap bitMap = null;
        try {
            if (CACHE_ENTRIES.size() >= CACHE_SIZE) {
                destoryLast();
            }

            bitMap = useBitmap(path, width, height);
            if (bitMap != null && !bitMap.isRecycled()) {
                return bitMap;
            }
            bitMap = createBitmap(path, width, height);
            String key = createKey(path, width, height);
            synchronized (LOCKED) {
                IMG_CACHE_INDEX.put(key, bitMap);
                CACHE_ENTRIES.addFirst(key);
            }
        } catch (OutOfMemoryError err) {
            destoryLast();
            System.out.println(CACHE_SIZE);
            return createBitmap(path, width, height);
        }
        return bitMap;
    }

    public static Size getBitMapSize(String path) {
        File file = new File(path);
        if (file.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                BitmapFactory.decodeStream(in, null, OPTIONS_GET_SIZE);
                return new Size(OPTIONS_GET_SIZE.outWidth,
                        OPTIONS_GET_SIZE.outHeight);
            } catch (FileNotFoundException e) {
                return ZERO_SIZE;
            } finally {
                closeInputStream(in);
            }
        }
        return ZERO_SIZE;
    }

    // ------------------------------------------------------------------
    // private Methods
    // 将图片加入队列头
    private static Bitmap useBitmap(String path, int width, int height) {
        Bitmap bitMap = null;
        String key = createKey(path, width, height);
        synchronized (LOCKED) {
            bitMap = (Bitmap) IMG_CACHE_INDEX.get(key);
            if (null != bitMap) {
                if (CACHE_ENTRIES.remove(key)) {
                    CACHE_ENTRIES.addFirst(key);
                }
            }
        }
        return bitMap;
    }

    // 回收最后一张图片
    private static void destoryLast() {
        synchronized (LOCKED) {
            if (CACHE_ENTRIES.size() > 0) {
                String key = (String) CACHE_ENTRIES.removeLast();
                if (key.length() > 0) {
                    Bitmap bitMap = (Bitmap) IMG_CACHE_INDEX.remove(key);
                    if (bitMap != null && !bitMap.isRecycled()) {
                        bitMap.recycle();
                        bitMap = null;
                    }
                }

            }

        }
    }

    // 创建键
    private static String createKey(String path, int width, int height) {
        if (null == path || path.length() == 0) {
            return "";
        }
        return path + "_" + width + "_" + height;
    }

    // 通过图片路径,宽度高度创建一个Bitmap对象
    private static Bitmap createBitmap(String path, int width, int height) {
        File file = new File(path);
        if (file.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                Size size = getBitMapSize(path);
                if (size.equals(ZERO_SIZE)) {
                    return null;
                }
                int scale = 1;
                int a = size.getWidth() / width;
                int b = size.getHeight() / height;
                scale = Math.max(a, b);
                synchronized (OPTIONS_DECODE) {
                    OPTIONS_DECODE.inSampleSize = scale;
                    Bitmap bitMap = BitmapFactory.decodeStream(in, null,
                            OPTIONS_DECODE);
                    int degree = readPictureDegree(path);

                    if (degree == 90) {
                        return rotaingImageView(degree, bitMap);
                    }
                    return bitMap;
                }
            } catch (FileNotFoundException e) {
                Log.v("BitMapUtil", "createBitmap==" + e.toString());
            } finally {
                closeInputStream(in);
            }
        }
        return null;
    }


    /**
     * 等比例缩放图片
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {

        if (bitmap == null)
            return null;

        // 旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        try {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return resizedBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    // 关闭输入流
    private static void closeInputStream(InputStream in) {
        if (null != in) {
            try {
                in.close();
            } catch (IOException e) {
                Log.v("BitMapUtil", "closeInputStream==" + e.toString());
            }
        }
    }

    /**
     * 计算缩放比
     *
     * @param oldWidth
     * @param oldHeight
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static int reckonThumbnail(int oldWidth, int oldHeight,
                                      int newWidth, int newHeight) {
        if ((oldHeight > newHeight && oldWidth > newWidth)
                || (oldHeight <= newHeight && oldWidth > newWidth)) {
            int be = (int) (oldWidth / (float) newWidth);
            if (be <= 1)
                be = 1;
            return be;
        } else if (oldHeight > newHeight && oldWidth <= newWidth) {
            int be = (int) (oldHeight / (float) newHeight);
            if (be <= 1)
                be = 1;
            return be;
        }

        return 1;
    }

    public static void showBitmap(ImageView img, String localPaht, int width,
                                  int height) {
        Size size = getBitMapSize(localPaht);
        if (size != null) {
            int imgHeight = size.getHeight();
            int imgWidth = size.getWidth();
            int scaleHeight = imgHeight / height;
            int scaleWidth = imgWidth / width;
            int scale = Math.max(scaleWidth, scaleHeight);

            if (scale < 1)
                scale = 1;

            Bitmap bitMap = getBitmap(localPaht, size.getWidth() / scale,
                    size.getHeight() / scale);
            img.setImageBitmap(bitMap);

        }
    }

    // 回收最后一张图片
    public static void destoryFirst() {
        synchronized (LOCKED) {
            if (CACHE_ENTRIES.size() > 0) {
                String key = (String) CACHE_ENTRIES.removeFirst();
                if (key.length() > 0) {
                    Bitmap bitMap = (Bitmap) IMG_CACHE_INDEX.remove(key);
                    if (bitMap != null && !bitMap.isRecycled()) {
                        bitMap.recycle();
                        bitMap = null;
                    }
                }

            }

        }
    }

    // 通过图片路径,宽度高度创建一个Bitmap对象
    public static Bitmap createBitmap(InputStream is, int width, int height) {
        try {
            Size size = getBitMapSize(is);
            if (size.equals(ZERO_SIZE)) {
                return null;
            }
            int scale = 1;
            int a = size.getWidth() / width;
            int b = size.getHeight() / height;
            scale = Math.max(a, b);
            synchronized (OPTIONS_DECODE) {
                OPTIONS_DECODE.inSampleSize = scale;
                Bitmap bitMap = BitmapFactory.decodeStream(is, null,
                        OPTIONS_DECODE);
                return bitMap;
            }
        } catch (Exception e) {
            Log.v("BitMapUtil", "createBitmap==" + e.toString());
        } finally {
            closeInputStream(is);
        }
        return null;
    }

    public static Size getBitMapSize(InputStream is) {
        try {
            BitmapFactory.decodeStream(is, null, OPTIONS_GET_SIZE);
            return new Size(OPTIONS_GET_SIZE.outWidth,
                    OPTIONS_GET_SIZE.outHeight);
        } catch (Exception e) {
            return ZERO_SIZE;
        } finally {
            closeInputStream(is);
        }
    }

    // 图片大小
    public static class Size {
        private int width, height;

        Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    // 队列缓存参数对象
    static class QueueEntry {
        public String path;
        public int width;
        public int height;
    }


    //提取图像Alpha位图
    public static Bitmap getAlphaBitmap(Bitmap mBitmap, int mColor) {

        Bitmap mAlphaBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas mCanvas = new Canvas(mAlphaBitmap);
        Paint mPaint = new Paint();

        mPaint.setColor(mColor);
        //从原位图中提取只包含alpha的位图
        Bitmap alphaBitmap = mBitmap.extractAlpha();
        //在画布上（mAlphaBitmap）绘制alpha位图
        mCanvas.drawBitmap(alphaBitmap, 0, 0, mPaint);

        return mAlphaBitmap;
    }

    /**
     * 描述：裁剪图片.
     *
     * @param bitmap    the bitmap
     * @param newWidth  新图片的宽
     * @param newHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap cutImg(Bitmap bitmap, int newWidth, int newHeight) {
        if (bitmap == null) {
            return null;
        }
        if (newWidth <= 0 || newHeight <= 0) {
            throw new IllegalArgumentException("裁剪图片的宽高设置不能小于0");
        }
        Bitmap newBitmap = null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= 0 || height <= 0) {
            return null;
        }
        int offsetX = 0;
        int offsetY = 0;
        ///此段代码，意味着截图截最中心的那块
        if (width > newWidth) {
            offsetX = (width - newWidth) / 2;
        } else {
            newWidth = width;
        }
        if (height > newHeight) {
            offsetY = (height - newHeight) / 2;
        } else {
            newHeight = height;
        }


        newBitmap = Bitmap.createBitmap(bitmap, offsetX, offsetY, newWidth, newHeight);
        return newBitmap;
    }
    /**
     * Drawable对象转换Bitmap对象.
     * @param bitmap 要转化的Bitmap对象
     * @return Drawable 转化完成的Drawable对象
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        BitmapDrawable mBitmapDrawable = null;
        try {
            if(bitmap==null){
                return null;
            }
            mBitmapDrawable = new BitmapDrawable(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmapDrawable;
    }
}
