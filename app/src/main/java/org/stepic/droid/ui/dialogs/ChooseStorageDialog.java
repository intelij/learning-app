package org.stepic.droid.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import org.jetbrains.annotations.NotNull;
import org.stepic.droid.R;
import org.stepic.droid.analytic.Analytic;
import org.stepic.droid.base.MainApplication;
import org.stepic.droid.model.StorageOption;
import org.stepic.droid.preferences.UserPreferences;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Inject;

public class ChooseStorageDialog extends DialogFragment {

    @Inject
    ThreadPoolExecutor threadPoolExecutor;

    @Inject
    UserPreferences userPreferences;

    @Inject
    Analytic analytic;

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MainApplication.component().inject(this);

        //fixme get From User Prefs
        final List<StorageOption> storageOptions = userPreferences.getStorageOptionList();
        String[] headers = new String[storageOptions.size()];
        int indexChosen = -1; //// FIXME: 08.06.16 change to 0
        for (int i = 0; i < headers.length; i++) {
            headers[i] = storageOptions.get(i).getPresentableInfo();
            if (storageOptions.get(i).isChosen()) {
                indexChosen = i;
            }
        }
        final int finalIndexChosen = indexChosen;
        final DialogFragment youWantMoveDataDialog = WantMoveDataDialog.newInstance(); //// FIXME: 08.06.16 make new Dialog with are you sure

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_storage_title)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        analytic.reportEvent(Analytic.Interaction.CANCEL_CHOOSE_STORE_CLICK);
                    }
                })
                .setSingleChoiceItems(headers,
                        finalIndexChosen,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                //show only if want change, check which
                                if (which == finalIndexChosen) {
                                    dismiss();
                                    return;
                                }

                                if (!youWantMoveDataDialog.isAdded()) {
                                    youWantMoveDataDialog.show(getFragmentManager(), null);
                                }
                                dismiss();
                            }
                        });

        return builder.create();
    }

}
