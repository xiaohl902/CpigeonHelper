package com.cpigeon.cpigeonhelper.modular.xiehui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cpigeon.cpigeonhelper.R;

import org.w3c.dom.Text;

/**
 * Created by Administrator on 2017/5/31.
 */

public class EditDialogFragment extends DialogFragment {
    public static final int DIALOG_TYPE_SHORTNAME = 1;
    public static final int DIALOG_TYPE_ADDRESS = 2;
    public static final int DIALOG_TYPE_EMAIL = 3;
    public static final int DIALOG_TYPE_GYP_NAME = 4;
    public static final int DIALOG_TYPE_GYP_PLACE = 5;
    public static final int DIALOG_TYPE_GYP_LATLNG = 6;


    public static EditDialogFragment getInstance(int type,String title) {
        EditDialogFragment dialogFragment = new EditDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Dialog_Type", type);
        bundle.putString("Dialog_Title",title);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_edittext, null);

        EditText editText = (EditText) view.findViewById(R.id.et_info);

        int dialog_type = getArguments().getInt("Dialog_Type");
        String dialog_title = getArguments().getString("Dialog_Title");
        builder.setView(view)
                .setTitle(dialog_title)
                .setPositiveButton("确认", (dialog, which) -> {
                    switch (dialog_type) {
                        case DIALOG_TYPE_SHORTNAME:

                            TextView mName = (TextView) getActivity().findViewById(R.id.tv_xiehui_shotname);
                            mName.setText(editText.getText().toString().trim());
                            break;
                        case DIALOG_TYPE_ADDRESS:

                            TextView mAddress = (TextView) getActivity().findViewById(R.id.tv_xiehui_address);
                            mAddress.setText(editText.getText().toString().trim());
                            break;
                        case DIALOG_TYPE_EMAIL:

                            TextView mShotName = (TextView) getActivity().findViewById(R.id.tv_xiehui_shotname);
                            mShotName.setText(editText.getText().toString().trim());
                            break;
                        case DIALOG_TYPE_GYP_NAME:

                            TextView mGeYunTongName = (TextView) getActivity().findViewById(R.id.tv_geyuntong_name);
                            mGeYunTongName.setText(editText.getText().toString().trim());
                            break;
                        case DIALOG_TYPE_GYP_PLACE:

                            TextView mGeYunTongPlace = (TextView) getActivity().findViewById(R.id.tv_geyuntong_place);
                            mGeYunTongPlace.setText(editText.getText().toString().trim());
                            break;
                        case DIALOG_TYPE_GYP_LATLNG:

                            break;
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                });
        builder.setCancelable(true);
        return builder.create();
    }
}
