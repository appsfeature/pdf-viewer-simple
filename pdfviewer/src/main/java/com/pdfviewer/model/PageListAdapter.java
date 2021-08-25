package com.pdfviewer.model;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.helper.task.TaskRunner;
import com.pdfviewer.PDFViewer;
import com.pdfviewer.R;
import com.pdfviewer.util.ArraysUtil;
import com.pdfviewer.util.PDFCallback;
import com.pdfviewer.util.PdfUtil;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class PageListAdapter extends RecyclerView.Adapter<PageListAdapter.ViewHolder> {
    private ArrayList<String> items;
    private PDFCallback.OnClickListenerWithDelete<String> callback;
    private AlertDialog dialog;

    private PDFModel pdfModel;
    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }

    public PageListAdapter(PDFModel pdfModel, ArrayList<String> mList, PDFCallback.OnClickListenerWithDelete<String> callback) {
        this.pdfModel = pdfModel;
        this.items = mList;
        this.callback = callback;
    }


    @NonNull
    @Override
    public PageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.slot_page_item, viewGroup, false);
        return new PageListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.tvTitle.setText(items.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog!=null)
                    dialog.dismiss();
                callback.onItemClicked(v, items.get(position));
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBookmarkPage(v , position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void deleteBookmarkPage(View view, int position) {
        if(items.size() > position) {
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            if (items.size() == 0) {
                if (dialog != null)
                    dialog.dismiss();
                deleteBookFromBookmarkList(view.getContext(), new TaskRunner.Callback<Boolean>() {
                    @Override
                    public void onComplete(Boolean result) {
                        callback.onUpdateUI();
                        PDFViewer.getInstance().updateBookmarkUpdateListener(pdfModel.getId(), "");
                    }
                });
            } else {
                callback.onDeleteClicked(view, ArraysUtil.join(items, null));
            }
        }
    }

    private void deleteBookFromBookmarkList(Context context, TaskRunner.Callback<Boolean> callback) {
        TaskRunner.getInstance().executeAsync(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
//                PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().delete(pdfModel.getId(), pdfModel.getTitle());
                PDFViewer.getInstance().getDatabase(context).pdfViewerDAO().updateBookmarkPages(pdfModel.getId(), pdfModel.getTitle(),"", PdfUtil.getDatabaseDateTime());
                return true;
            }
        }, callback);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private ImageView ivDelete;

        ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tv_title);
            ivDelete = view.findViewById(R.id.iv_delete);
        }
    }
}
