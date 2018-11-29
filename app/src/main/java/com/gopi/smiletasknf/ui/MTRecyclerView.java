package com.gopi.smiletasknf.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.ArrayList;

public abstract class MTRecyclerView {

    public static abstract class Adapter<VH extends MTViewHolder, T>
            extends  RecyclerView.Adapter<VH>{
        ArrayList<T> dataSource;
        ArrayList<T> originalContent;

        public Adapter() {
            this.dataSource = getDataSet();
            this.originalContent = getDataSet();
        }

        //TODO: figure a way to do that
//        private int viewResource;

//        public void setup(int viewResource, ArrayList<T> dataSource){
//            this.viewResource = viewResource;
//            this.dataSource = dataSource;
//        }

        abstract public ArrayList<T>  getDataSet();

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.update(this.dataSource.get(position));
        }

        @Override
        public int getItemCount() {
            return this.dataSource.size();
        }

        public void addItem(T item){
            this.dataSource.add(item);
            this.originalContent.add(item);
            this.notifyItemInserted(this.dataSource.size()-1);
        }

        public void addItems(ArrayList<T> dataSource){
            for (T item : dataSource) {
                addItem(item);
            }
        }

        public void removeItem(T item){
            int position = this.dataSource.lastIndexOf(item);
            this.dataSource.remove(item);
            this.originalContent.remove(item);
            this.notifyItemChanged(position);
        }

        public void removeItems(ArrayList<T> dataSource){
            for (T item : dataSource) {
                removeItem(item);
            }
        }

        public void replaceAll(ArrayList<T> itemsToReplace){
            this.removeItems(this.dataSource);
            this.addItems(itemsToReplace);
        }

        /**
         * Filters all sections of the sectioned list on the filter string
         * @param filterPredicate the predicate on which all sections will be filterd upon
         */
        public void filterBy(Predicate<T> filterPredicate){
            this.dataSource = new ArrayList<>(Collections2
                            .filter(this.dataSource, filterPredicate));
            notifyDataSetChanged();
        }

        public void revertDataToOriginal(){
            if(this.originalContent != null
                    || !originalContent.isEmpty()){
                this.dataSource.clear();
                this.dataSource.addAll(this.originalContent);
                notifyDataSetChanged();
            }
        }
    }

    public static abstract class MTViewHolder <T> extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        public MTViewHolder(View itemView) {
            super(itemView);
            bindViews();
        }

        abstract public void bindViews();
        abstract public void update(T object);
        abstract public void clicked(View view);
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            clicked(v);
        }
    }
}
