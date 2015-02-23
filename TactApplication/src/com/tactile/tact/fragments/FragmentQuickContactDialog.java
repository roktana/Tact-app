package com.tactile.tact.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.tactile.tact.R;

import java.io.Serializable;
import java.util.ArrayList;

public class FragmentQuickContactDialog extends DialogFragment {

    private static final String ARG_ITEMS = "arg_items";

    public enum Type {
        PHONE,
        EMAIL
    }

    public static final class QuickContactItem implements Serializable {
        private Type type;
        private String label;
        private String value;

        public QuickContactItem(Type type, String label, String value) {
            this.type = type;
            this.label = label;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }

        public Type getType() {
            return type;
        }
    }

    public static FragmentQuickContactDialog newInstance(ArrayList<QuickContactItem> quickContactItems) {
        FragmentQuickContactDialog fragment = new FragmentQuickContactDialog();

        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEMS, quickContactItems);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            //noinspection unchecked
            final ArrayList<QuickContactItem> quickContactItems = (ArrayList<QuickContactItem>) args.getSerializable(ARG_ITEMS);

            if (quickContactItems != null) {
                String[] items = new String[quickContactItems.size()];
                for (int i = 0; i < quickContactItems.size(); i++) {
                    String formattedLabel = quickContactItems.get(i).getLabel();
                    formattedLabel = String.valueOf(formattedLabel.charAt(0)).toUpperCase() + formattedLabel.substring(1, formattedLabel.length()).toLowerCase();
                    items[i] = formattedLabel + ": " + quickContactItems.get(i).getValue();
                }

                final Activity activity = getActivity();

                if (activity != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            final Type type = quickContactItems.get(which).getType();
                            Intent intent;
                            switch (type) {
                                case PHONE:
                                    intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + quickContactItems.get(which).getValue()));
                                    activity.startActivity(intent);
                                    break;
                                case EMAIL:
                                    intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("message/rfc822");
                                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{quickContactItems.get(which).getValue()});
                                    activity.startActivity(Intent.createChooser(intent, activity.getResources().getString(R.string.send_email)));
                                    break;
                            }
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    return builder.create();
                }

            }
        }
        return null;
    }
}
