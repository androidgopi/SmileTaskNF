package com.gopi.smiletasknf.mainActivity;

import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gopi.smiletasknf.R;
import com.gopi.smiletasknf.ui.MTRecyclerView;
import com.gopi.smiletasknf.models.GalleryItem;
import com.gopi.smiletasknf.models.ImageUtils;

import java.util.ArrayList;

public class SmilesRecyclerView {
    private RecyclerView recyclerView;

    public void setUpRecyclerView(final RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        int numberOfColumns = 2;
        //Check your orientation in your OnCreate
        if(recyclerView.getContext().getResources().getConfiguration().orientation ==
                recyclerView.getContext().getResources().getConfiguration()
                        .ORIENTATION_LANDSCAPE)
            numberOfColumns = 3;
        this.recyclerView.setLayoutManager(new GridLayoutManager
                (recyclerView.getContext(),
                        numberOfColumns,
                        GridLayoutManager.VERTICAL, false));
        SmilesAdapter adapter = new SmilesAdapter();
        this.recyclerView.setAdapter(adapter);
    }
    public void populate(ArrayList<GalleryItem> items){
        ((SmilesAdapter) this.recyclerView.getAdapter()).addItems(items);
    }

    class SmilesAdapter extends MTRecyclerView.Adapter<MyViewHolder, GalleryItem>{
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.smile_images_grid_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public ArrayList<GalleryItem> getDataSet() {
            return new ArrayList<>();
        }
    }

    class MyViewHolder extends MTRecyclerView.MTViewHolder<GalleryItem>{
        //        TextView movieName;
        SimpleDraweeView imageView;
        GalleryItem image;
        public MyViewHolder(View itemView) {
            super(itemView);
        }
        public void update(GalleryItem image){
            this.image = image;
            if(this.image.getImageUrl() != null) {
                Uri uri = Uri.parse(ImageUtils.FRESCO_FILE + image.getImageUrl());
                ImageUtils.requestImageResize(200,
                        400, uri, this.imageView);
            }
        }

        @Override
        public void bindViews() {
            this.imageView = (SimpleDraweeView)itemView.findViewById(R.id.image);
            this.imageView.setOnClickListener(this);
        }

        @Override
        public void clicked(View view) {
//            Intent intent = new Intent(view.getContext(), MovieDetailsActivity.class);
//            intent.putExtra("Movie", this.movie);
//            view.getContext().startActivity(intent);
        }

    }
}
