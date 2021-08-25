package com.pdfviewer.fragment;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.helper.callback.Response;
import com.pdfviewer.R;
import com.pdfviewer.model.PDFIndexAdapter;
import com.shockwave.pdfium.PdfDocument;

import java.util.ArrayList;
import java.util.List;


public class PDFIndexFragment extends Fragment {

    private PDFIndexAdapter adapter;
    private List<PdfDocument.Bookmark> mList;
    private Activity activity;
    private Response.OnClickListener<Integer> mListener;
    private int totalPageCount;
    private BottomSheetDialog mBottomSheetDialog;


    public static PDFIndexFragment newInstance(List<PdfDocument.Bookmark> mList, int totalPageCount, Response.OnClickListener<Integer> mListener) {
        PDFIndexFragment fragment = new PDFIndexFragment();
        fragment.mList = mList;
        fragment.totalPageCount = totalPageCount;
        fragment.mListener = mListener;
        return fragment;
    }

    public BottomSheetDialog show(Context context) {
        if (context != null) {
            mBottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
            LayoutInflater inflater = LayoutInflater.from(context);
            mBottomSheetDialog.setContentView(viewHolder(inflater.inflate(R.layout.pdf_index_activity, null)));
            mBottomSheetDialog.show();
        }
        return mBottomSheetDialog;
    }

    private View viewHolder(View view) {
        initView(view);
        loadList();
        return view;
    }

    private void initView(View view) {
        RecyclerView rvList = view.findViewById(R.id.rvList);

        if (mList == null) {
            mList = new ArrayList<>();
        }
        adapter = new PDFIndexAdapter(mList, new Response.OnClickListener<Integer>() {
            @Override
            public void onItemClicked(View view, Integer index) {
                mListener.onItemClicked(view, index);
                if (mBottomSheetDialog != null) {
                    mBottomSheetDialog.dismiss();
                    mBottomSheetDialog = null;
                }
            }
        });
        rvList.setAdapter(adapter);
    }

    private void loadList() {
        if (mList != null && mList.size() < 1) {
            for (int i = 1; i <= totalPageCount; i++) {
                mList.add(new PdfDocument.Bookmark());
            }
            adapter.setPageCount(true);
            adapter.notifyDataSetChanged();
        }
    }

    public void finish() {
        if (mBottomSheetDialog != null) {
            mBottomSheetDialog.dismiss();
            mBottomSheetDialog = null;
        }
    }
}