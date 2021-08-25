package com.pdfviewer.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pdfviewer.R;
import com.pdfviewer.model.PDFModel;
import com.pdfviewer.model.PageListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AlertUtil {

    public static void openPageSelection(final Context context, PDFModel pdfModel, final List<String> list, final PDFCallback.OnClickListenerWithDelete<String> callback) {
        // setup the alert builder
        if (list != null && list.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
            builder.setTitle(PDFConstant.DIALOG_OPEN_PAGE_SELECTION_TITLE);
            View viewHolder = LayoutInflater.from(context).inflate(R.layout.pdf_dialog_page_selection, null);
            RecyclerView recyclerView = viewHolder.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            PageListAdapter adapter = new PageListAdapter(pdfModel, new ArrayList<>(list), callback);
            recyclerView.setAdapter(adapter);

            builder.setView(viewHolder);
            AlertDialog dialog = builder.create();
            adapter.setDialog(dialog);
            dialog.show();
        } else {
            PdfUtil.showToast(context,"Not found.");
        }
    }

    public static void openAlertDialog(final Context context, String title, String message, String positiveButton, final PDFCallback.OnClickListener<Void> callback) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        //Setting message manually and performing action on button click
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback.onItemClicked(new View(context), null);
                        dialog.dismiss();
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();
    }


}
