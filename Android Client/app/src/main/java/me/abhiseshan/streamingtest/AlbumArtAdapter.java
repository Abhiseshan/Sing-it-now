package me.abhiseshan.streamingtest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Abhinav on 9/19/2015.
 */

public class AlbumArtAdapter extends ArrayAdapter<AlbumArt> {
    Context context;
    int layoutResourceId;
    AlbumArt data[] = null;
    AlbumArt items;

    public AlbumArtAdapter(Context context, int layoutResourceId, AlbumArt[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AlbumArtHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new AlbumArtHolder();
            holder.name = (TextView) row.findViewById(R.id.songName);
            holder.album_art = (ImageView) row.findViewById(R.id.albumArt);
            holder.artist = (TextView) row.findViewById(R.id.artistName);

            row.setTag(holder);
        } else
            holder = (AlbumArtHolder) row.getTag();

        items = data[position];
        holder.name.setText(items.name);
        //holder.album_art.setImageResource(items.icon);
        Glide.with(context).load(items.icon).into(holder.album_art);
        holder.artist.setText(items.artist);
        return row;
    }

    static class AlbumArtHolder
    {
        ImageView album_art;
        TextView name;
        TextView artist;
    }
}
