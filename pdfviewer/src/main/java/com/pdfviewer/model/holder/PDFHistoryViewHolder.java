package com.pdfviewer.model.holder;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.helper.callback.Response;
import com.helper.model.HistoryModelResponse;
import com.helper.util.BaseUtil;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.R;
import com.pdfviewer.model.PDFModel;
import com.pdfviewer.task.TaskDeleteHistory;
import com.pdfviewer.util.PDFCallback;


/**
 * Use Layout Resource R.layout.pdf_item_history
 */
public class PDFHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvTitle;
    public TextView tvSubTitle;
    public TextView tvTime;
    private Response.OnDeleteListener deleteListener;
    private Response.OnListClickListener<HistoryModelResponse> onClick;
    public View ivDelete;
    private HistoryModelResponse mItem;
    public TextView tvStatus;
    public TextView tvType;
    public TextView tvViewCount;

    public PDFHistoryViewHolder(View itemView, Response.OnDeleteListener deleteListener) {
        super(itemView);
        this.deleteListener = deleteListener;
        initUI(itemView);
        ivDelete.setVisibility(deleteListener == null ? View.GONE : View.VISIBLE);
    }

    public PDFHistoryViewHolder(View itemView, Response.OnListClickListener<HistoryModelResponse> onClick) {
        super(itemView);
        this.onClick = onClick;
        initUI(itemView);
    }

    private void initUI(View itemView) {
        tvTitle =  itemView.findViewById(R.id.tvTitle);
        tvSubTitle =  itemView.findViewById(R.id.tvSubTitle);
        tvTime =  itemView.findViewById(R.id.tvTime);
        tvStatus =  itemView.findViewById(R.id.tv_status);
        tvType =  itemView.findViewById(R.id.tv_type);
        tvViewCount =  itemView.findViewById(R.id.tv_view_count);
        ivDelete =  itemView.findViewById(R.id.iv_delete);
        itemView.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(mItem != null) {
            if(view.getId() == R.id.iv_delete){
                if(onClick != null) {
                    onClick.onDeleteClicked(view, getAdapterPosition(), mItem);
                }else {
                    new TaskDeleteHistory(view.getContext(), mItem, new PDFCallback.Status<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            if(deleteListener != null){
                                if(getAdapterPosition() >= 0) {
                                    deleteListener.onRemoveItemFromList(getAdapterPosition());
                                }
                            }
                        }
                    }).execute();
                }
            }else {
                if(onClick != null) {
                    onClick.onItemClicked(view, mItem);
                }else {
                    PDFModel pdfProperty = mItem.getJsonModel(new TypeToken<PDFModel>() {
                    });
                    PDFViewer.openPdfViewerFromHistory(view.getContext(), pdfProperty);
                }
            }
        }
    }

    public void setStatus(String status) {
        tvStatus.setText(status);
    }

    public void setType(String type) {
        tvType.setText(type);
    }

    public void setData(HistoryModelResponse mItem) {
        setData(mItem, true);
    }
    public void setData(HistoryModelResponse mItem, boolean isVisibleDelete) {
        this.mItem = mItem;
        tvTitle.setText(mItem.getTitle());
        if(!TextUtils.isEmpty(mItem.getSubTitle())){
            tvSubTitle.setText(mItem.getSubTitle());
            tvSubTitle.setVisibility(View.VISIBLE);
        }else {
            tvSubTitle.setVisibility(View.INVISIBLE);
        }
        tvTime.setText(BaseUtil.getTimeSpanString(mItem.getCreatedAt()));
        tvType.setText("PDF");
        if(PDFViewer.getInstance().isEnableViewCount() && !TextUtils.isEmpty(mItem.getViewCountFormatted())) {
            tvViewCount.setText(itemView.getContext().getString(R.string.pdf_views, mItem.getViewCountFormatted()));
            tvViewCount.setVisibility(View.VISIBLE);
        }else {
            tvViewCount.setVisibility(View.GONE);
        }
        ivDelete.setVisibility(isVisibleDelete ? View.VISIBLE : View.GONE);
    }
}