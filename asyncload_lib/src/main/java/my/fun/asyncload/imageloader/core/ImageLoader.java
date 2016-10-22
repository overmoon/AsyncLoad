package my.fun.asyncload.imageloader.core;

import android.util.Log;

/**
 * Created by admin on 2016/10/8.
 */
public class ImageLoader {
    private final String TAG = this.getClass().toString();
    private static ImageLoader instance;
    private ImageLoaderConfiguration imageLoaderConfiguration;

    protected ImageLoader() {

    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    // init configuration
    public synchronized void init(ImageLoaderConfiguration configuration) {
        if (imageLoaderConfiguration != null){
            Log.d(TAG, "imageLoaderConfiguration Overlapped");
        }

        if (configuration == null){
            throw new IllegalArgumentException("Init failed. imageLoaderConfiguration is null");
        }
        imageLoaderConfiguration = configuration;
    }

    public boolean isInit(){
        if(imageLoaderConfiguration==null){
            Log.d(TAG, "Image Loader hasn't init. The imageLoaderConfiguration is null");
            return false;
        }
        return true;
    }

    // 加载图片
    public void loadBitmap( DisplayOption displayOption) {
        if (isInit()) {
            ImageLoaderTask loaderTask = new ImageLoaderTask(displayOption, imageLoaderConfiguration);
            loaderTask.execute();
        }
    }

}
