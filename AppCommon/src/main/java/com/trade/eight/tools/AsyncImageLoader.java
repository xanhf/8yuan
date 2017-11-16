package com.trade.eight.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.trade.eight.net.HttpClientHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;


public class AsyncImageLoader {

    private static final int IO_BUFFER_SIZE = 4 * 1024;
    static private AsyncImageLoader _instance = null;
    private HashMap<String, SoftReference<Drawable>> imageCache;
    private HashMap<String, SoftReference<Bitmap>> imageCache1;

    protected AsyncImageLoader() {
        imageCache = new HashMap<String, SoftReference<Drawable>>();
        imageCache1 = new HashMap<String, SoftReference<Bitmap>>();
    }

    static public AsyncImageLoader instance() {
        if (null == _instance) {
            _instance = new AsyncImageLoader();
        }
        return _instance;
    }

    public static Bitmap threadGetBitmap(String imageUrl) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        opts.inJustDecodeBounds = true;
        InputStream in = null;
        BufferedOutputStream out = null;
        Bitmap bitmap = null;
        if (imageUrl == null)
            return null;
        try {
            InputStream is = HttpClientHelper.getStreamFromGet(null, imageUrl);
            if (is == null) {
                return null;
            }
            in = new BufferedInputStream(is, IO_BUFFER_SIZE);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copy(in, out);
            out.flush();

            byte[] data = dataStream.toByteArray();

            BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            if (opts.outHeight > 480) {
                opts.inSampleSize = 2;
            } else {
                opts.inSampleSize = 1;
            }

            opts.inJustDecodeBounds = false;
            opts.inInputShareable = true;
            opts.inPurgeable = true;

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            System.gc();
            return null;
        }
        return bitmap;
    }

    public static Bitmap threadGetPostBitmap(String imageUrl) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        opts.inJustDecodeBounds = true;
        InputStream in = null;
        BufferedOutputStream out = null;
        Bitmap bitmap = null;
        if (imageUrl == null)
            return null;
        try {
            InputStream is = HttpClientHelper.getStreamFromGet(null, imageUrl);
            if (is == null) {
                return null;
            }
            in = new BufferedInputStream(is, IO_BUFFER_SIZE);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copy(in, out);
            out.flush();

            byte[] data = dataStream.toByteArray();

            BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            opts.inSampleSize = 1;

            opts.inJustDecodeBounds = false;
            opts.inInputShareable = true;
            opts.inPurgeable = true;

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            System.gc();
            return null;
        }
        return bitmap;
    }

    /**
     * 计算图片宽高样本值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.v("AsyncImageLoader", "inSampleSize===========" + inSampleSize);
        return inSampleSize;
    }

    public static Drawable loadImageFromUrl(String url) {
//        URL m;
        InputStream i = null;
        try {
//            m = new URL(url);
//            i = (InputStream) m.getContent();
            i = HttpClientHelper.getStreamFromGet(null, url);
//        } catch (MalformedURLException e1) {
//            e1.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        if (i != null) {
            try {
                Drawable d = Drawable.createFromStream(i, "src");
                return d;
            } catch (OutOfMemoryError e) {
                return null;
            }
        } else {
            return null;
        }

    }

    public static Bitmap GetNetBitmap(String url, boolean flag) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 2;
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
//            in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);
            InputStream is = HttpClientHelper.getStreamFromGet(null, url);
            if (is == null) {
                return null;
            }
            in = new BufferedInputStream(is, IO_BUFFER_SIZE);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copy(in, out);
            out.flush();

            byte[] data = dataStream.toByteArray();
            if (flag)
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            else
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        } catch (IOException e) {
            System.out.println(url + "===============");
//			 Log.v("GetNetBitmap", url);
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bitmap;
        } catch (Exception e2) {
            e2.printStackTrace();
            return bitmap;
        }
    }

    public static Bitmap GetNetBitmap(String url, boolean flag, int pixels) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 2;
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
//            in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);
            InputStream is = HttpClientHelper.getStreamFromGet(null, url);
            if (is == null) {
                return null;
            }
            in = new BufferedInputStream(is, IO_BUFFER_SIZE);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copy(in, out);
            out.flush();

            byte[] data = dataStream.toByteArray();
            if (flag)
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            else
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            if (pixels != 0) {
                bitmap = toRoundCorner(bitmap, pixels);
            }
            data = null;
            return bitmap;
        } catch (IOException e) {
            System.out.println(url + "===============");
//			 Log.v("GetNetBitmap", url);
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bitmap;
        } catch (Exception e2) {
            e2.printStackTrace();
            return bitmap;
        }
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    public static int[] imageScale(Bitmap bitmap, int w, int h) {
        double nheight = 0, nwidth = 0, dheight = 0, dwidth = 0;
        try {
            double width = bitmap.getWidth();  // 得到源图宽
            double height = bitmap.getHeight();  // 得到源图长
            dwidth = 0;
            double hw = height / width;          //得到高比宽
            double wh = width / height;         //得到宽比高
            if (width <= w) {                 //图片的宽度＜要设置的宽度则宽度需要缩放
                if (height <= h) {
                    nheight = height;
                    nwidth = width;
                } else {
                    nheight = h;
                    nwidth = h * wh;
                }
            } else {              //图片的宽度＞预设宽度则高度需要缩放
                if (height <= h) {
                    nheight = w * hw;
                    nwidth = w;
                } else {
                    dheight = height - h;
                    dwidth = width - w;
                    if (dheight > dwidth) {
                        nheight = h;
                        nwidth = h * wh;
                    } else {
                        nheight = w * hw;
                        nwidth = w;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        int nw = new Long(Math.round(nwidth)).intValue();
        int nh = new Long(Math.round(nheight)).intValue();
        int[] i = {nw, nh};
        return i;
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] bmpToByteArrayCompressedJpeg(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 10, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public Drawable loadDrawable(final String imageUrl,
                                 final ImageCallback imageCallback) {
        if (imageCache.containsKey(imageUrl)) {
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            Drawable drawable = softReference.get();
            if (drawable != null) {
                return drawable;
            }
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
            }
        };
        new Thread() {
            @Override
            public void run() {
                Drawable drawable = loadImageFromUrl(imageUrl);
                imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
                Message message = handler.obtainMessage(0, drawable);
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }

    public Bitmap loadBitmap(final String imageUrl, final ImageCallback1 imageCallback) {
        return loadBitmap(imageUrl, null, imageCallback, false);
    }

    public Bitmap loadBitmap(final String imageUrl, final Bitmap defaultBmp,
                             final ImageCallback1 imageCallback) {
        return loadBitmap(imageUrl, defaultBmp, imageCallback, false);
    }

    public boolean containsBitmap(final String imageUrl) {
        if (imageCache1.containsKey(imageUrl)) {
            // SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            SoftReference<Bitmap> sonReference = imageCache1.get(imageUrl);
            Bitmap bitmap = sonReference.get();
            if (bitmap != null) {
                return true;
            }
        }
        return false;
    }

    public Bitmap loadBitmap(final String imageUrl,
                             final ImageCallback1 imageCallback, final boolean flag, final int pixels) {
        if (imageUrl == null)
            return null;
        if (imageCache1.containsKey(imageUrl)) {
            // SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            SoftReference<Bitmap> sonReference = imageCache1.get(imageUrl);
            Bitmap bitmap = sonReference.get();
            if (bitmap != null) {
                return bitmap;
            }
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
            }
        };
        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = GetNetBitmap(imageUrl, flag, pixels);
                imageCache1.put(imageUrl, new SoftReference<Bitmap>(bitmap));
                Message message = handler.obtainMessage(0, bitmap);
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }

    public Bitmap loadBitmap(final String imageUrl, final Bitmap defaultBmp,
                             final ImageCallback1 imageCallback, final boolean flag) {
        if (imageUrl == null)
            return null;

        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
            }
        };

        if (imageCache1.containsKey(imageUrl)) {
            // SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            SoftReference<Bitmap> sonReference = imageCache1.get(imageUrl);
            Bitmap bitmap = sonReference.get();
            if (bitmap != null) {
                Message message = handler.obtainMessage(0, bitmap);
                handler.sendMessage(message);
                return bitmap;
            }
        }

        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = threadGetBitmap(imageUrl);
                if (bitmap != null) {
                    imageCache1.put(imageUrl, new SoftReference<Bitmap>(bitmap));
                } else {
                    bitmap = defaultBmp;
                }
                Message message = handler.obtainMessage(0, bitmap);
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }

    public Bitmap loadPostBitmap(final String imageUrl, final Bitmap defaultBmp,
                                 final ImageCallback1 imageCallback, final boolean flag) {
        if (imageUrl == null)
            return null;
        if (imageCache1.containsKey(imageUrl)) {
            // SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            SoftReference<Bitmap> sonReference = imageCache1.get(imageUrl);
            Bitmap bitmap = sonReference.get();
            if (bitmap != null) {
                return bitmap;
            }
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
            }
        };
        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = threadGetPostBitmap(imageUrl);
                if (bitmap != null) {
                    imageCache1.put(imageUrl, new SoftReference<Bitmap>(bitmap));
                } else {
                    bitmap = defaultBmp;
                }
                Message message = handler.obtainMessage(0, bitmap);
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }

    public void recycleBitMap() {
        if (imageCache1 != null && imageCache1.size() > 0) {

            try {
                for (SoftReference<Bitmap> sonReference : imageCache1.values()) {
                    Bitmap bitmap = sonReference.get();
                    if (bitmap != null)
                        bitmap.recycle();
                }
            } catch (Exception e) {
            }
        }
    }


    public interface ImageCallback {
        public void imageLoaded(Drawable imageDrawable, String imageUrl);
    }

    public interface ImageCallback1 {
        public void imageLoaded(Bitmap bitmap, String imageUrl);
    }


}
