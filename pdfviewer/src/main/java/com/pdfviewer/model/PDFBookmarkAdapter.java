package com.pdfviewer.model;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pdfviewer.PDFViewer;
import com.pdfviewer.R;
import com.pdfviewer.util.PDFCallback;

import java.util.List;

public class PDFBookmarkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final PDFCallback.OnListClickListener<PDFModel> clickListener;
    private List<PDFModel> mList;

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pdf_item_bookmark, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public PDFBookmarkAdapter(List<PDFModel> mList, PDFCallback.OnListClickListener<PDFModel> clickListener) {
        this.mList = mList;
        this.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int i) {
        ViewHolder myViewHolder = (ViewHolder) holder;
        myViewHolder.tvTitle.setText(mList.get(i).getTitle());
        if(!TextUtils.isEmpty(mList.get(i).getBookmarkPages())) {
            myViewHolder.tvPages.setText("Pages : " + mList.get(i).getBookmarkPages());
            myViewHolder.tvPages.setVisibility(View.VISIBLE);
        }else {
            myViewHolder.tvPages.setVisibility(View.INVISIBLE);
        }

        if(!TextUtils.isEmpty(mList.get(i).getSubTitle())) {
            myViewHolder.tvSubTitle.setText(mList.get(i).getSubTitle());
            myViewHolder.tvSubTitle.setVisibility(View.VISIBLE);
        }else {
            myViewHolder.tvSubTitle.setVisibility(View.GONE);
        }

        if(PDFViewer.getInstance().isEnableViewCount() && !TextUtils.isEmpty(mList.get(i).getViewCountFormatted())) {
            myViewHolder.tvViewsCount.setText(holder.itemView.getContext().getString(R.string.pdf_views, mList.get(i).getViewCountFormatted()));
            myViewHolder.tvViewsCount.setVisibility(View.VISIBLE);
        }else {
            myViewHolder.tvViewsCount.setVisibility(View.GONE);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tvViewsCount;
        private final TextView tvTitle;
        private final TextView tvSubTitle;
        private final TextView tvPages;

        private ViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvSubTitle = v.findViewById(R.id.tvSubTitle);
            tvPages = v.findViewById(R.id.tvPages);
            tvViewsCount = v.findViewById(R.id.tvViewsCount);

            itemView.setOnClickListener(this);
            v.findViewById(R.id.iv_delete).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(mList.size() > position && position >= 0) {
                if (v.getId() == R.id.iv_delete) {
                    clickListener.onDeleteClicked(v, position, mList.get(position));
                } else {
                    clickListener.onItemClicked(v, mList.get(position));
                }
            }
        }
    }

}