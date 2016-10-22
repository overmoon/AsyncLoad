package my.fun.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import my.fun.asyncload.imageloader.core.DisplayOption;
import my.fun.asyncload.imageloader.core.ImageLoader;
import my.fun.asyncload.imageloader.model.ImageSize;

/**
 * Created by admin on 2016/10/7.
 */
public class ListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<String> imagePaths;

    public ListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public ListAdapter(Context context, List<String> imagePaths) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem, parent, false);
        }

        TextView textView = ListViewHolder.get(convertView, R.id.textView);
        textView.setText(imagePaths.get(position));

        ImageView imageView = ListViewHolder.get(convertView, R.id.imageView);
        String filePath = imagePaths.get(position);
        DisplayOption displayOption = initDisplayOption(filePath, imageView, new ImageSize(36, 36), R.mipmap.ic_folder, context);
        ImageLoader.getInstance().loadBitmap(displayOption);
        return convertView;
    }

    private DisplayOption initDisplayOption(String data, ImageView imageView, ImageSize imageSize, int resId, Context context) {
        DisplayOption.Builder builder = new DisplayOption.Builder();
        builder.setImageView(imageView)
                .setResources(context.getResources())
                .setData(data)
                .setImageSize(imageSize)
                .setImageHolder(resId)
                .cacheInMem(true);

        return builder.build();
    }

}
