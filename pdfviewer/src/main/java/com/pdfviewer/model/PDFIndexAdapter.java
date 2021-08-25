package com.pdfviewer.model;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.helper.callback.Response;
import com.pdfviewer.R;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

public class PDFIndexAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Response.OnClickListener<Integer> clickListener;
    private List<PdfDocument.Bookmark> mList;
    private boolean isPageCountEnable = false;

    public PDFIndexAdapter(List<PdfDocument.Bookmark> mList, Response.OnClickListener<Integer> clickListener) {
        this.mList = mList;
        this.clickListener = clickListener;
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pdf_item_index, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int i) {
        ViewHolder myViewHolder = (ViewHolder) holder;
        PdfDocument.Bookmark item = mList.get(i);
        myViewHolder.tvTitle.setText(TextUtils.isEmpty(item.getTitle()) ? "Page" : item.getTitle());
        if(isPageCountEnable){
            myViewHolder.tvPages.setText(i + 1 + "");
        }else {
            myViewHolder.tvPages.setText(item.getPageIdx() + 1 + "");
            if (item.getChildren() != null && item.getChildren().size() > 0) {
                PDFIndexAdapter adapter = new PDFIndexAdapter(item.getChildren(), new Response.OnClickListener<Integer>() {
                    @Override
                    public void onItemClicked(View view, Integer index) {
                        clickListener.onItemClicked(view, index);
                    }
                });
                myViewHolder.rvList.setAdapter(adapter);
                myViewHolder.rvList.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.rvList.setVisibility(View.GONE);
            }
        }
    }

    public void setPageCount(boolean isPageCountEnable) {
        this.isPageCountEnable = isPageCountEnable;
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final RecyclerView rvList;
        private TextView tvTitle;
        private TextView tvPages;

        private ViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvPages = v.findViewById(R.id.tvPages);
            rvList = v.findViewById(R.id.rvList);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(mList.size() > position && position >= 0) {
                clickListener.onItemClicked(v, isPageCountEnable ? position : toInt(mList.get(position).getPageIdx()));
            }
        }
    }

    private Integer toInt(long pageIdx) {
        try {
            return (int) pageIdx;
        } catch (Exception e){
            return 0;
        }
    }
}