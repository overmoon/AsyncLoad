package my.fun.asyncload.imageloader.core;

import android.content.res.Resources;
import android.util.Log;
import android.widget.ImageView;

import my.fun.asyncload.imageloader.model.ImageSize;
import my.fun.asyncload.imageloader.model.Scheme;

/**
 * Created by admin on 2016/10/10.
 */
public class DisplayOption {
    private final String TAG = this.getClass().toString();
    private int resId;
    private ImageSize imageSize;
    private boolean cacheInMem = true;
    private boolean cacheOnDisk = true;
    private Resources resources;
    private ImageView imageView;
    private String data;

    private DisplayOption(DisplayOption.Builder builder) {
        this.imageView = builder.imageView;
        imageView.setTag(this);
        this.resId = builder.resId;
        this.imageSize = builder.imageSize;
        this.cacheInMem = builder.cacheInMem;
        this.cacheOnDisk = builder.cacheOnDisk;
        this.data = builder.data;

        if (Scheme.ofScheme(data) == Scheme.UNKNOWN) {
            throw new IllegalArgumentException("illegal data: " + data + " for imageLoaderTask executor");
        }

        if (builder.resources == null)
            throw new IllegalArgumentException("Resources can't be null");
        this.resources = builder.resources;

        // if size not set, then will cache the original image
        if (imageSize == null && resId == 0) {
            imageSize = new ImageSize();
            Log.d(TAG, "ImageSize and img not set, will cache the original img");
        } else if (imageSize == null) {
            imageSize = new ImageSize(resId, resources);
        }
    }

    public String getData() {
        return data;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public boolean isCacheInMem() {
        return cacheInMem;
    }

    public ImageSize getImageSize() {
        return imageSize;
    }

    public int getResId() {
        return resId;
    }

    public boolean isCacheOnDisk() {
        return cacheOnDisk;
    }

    public Resources getResources() {
        return resources;
    }

    @Override
    public boolean equals(Object o){
        if (o!=null && o instanceof DisplayOption) {
            DisplayOption option = (DisplayOption) o;
            if (option.getResId() == resId && option.getImageSize().equals(imageSize)
                     && option.data.equals(data))
                return true;
        }
        return  false;
    }

    /**
     * DisplayOption.Builder class
     */
    public static class Builder {
        private int resId;
        private ImageSize imageSize;
        private boolean cacheInMem = true;
        private boolean cacheOnDisk = true;
        private Resources resources;
        private ImageView imageView;
        private String data;

        // set image holder
        public Builder setImageView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        // set image holder
        public Builder setImageHolder(int drawable) {
            this.resId = drawable;
            return this;
        }

        // set image size to be loaded
        public Builder setImageSize(ImageSize imageSize) {
            this.imageSize = imageSize;
            return this;
        }

        // set whether to use mem cache
        public Builder cacheInMem(boolean isMemCache) {
            this.cacheInMem = isMemCache;
            return this;
        }

        // set whether to use disk cache
        public Builder cacheInDisk(boolean isDiskCache) {
            this.cacheOnDisk = isDiskCache;
            return this;
        }

        public Builder setResources(Resources resources) {
            this.resources = resources;
            return this;
        }

        public Builder setData(String data) {
            this.data = data;
            return this;
        }

        // Create the DisplayOption
        public DisplayOption build() {
            return new DisplayOption(this);
        }


    }

}
