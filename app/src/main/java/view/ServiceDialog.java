package view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import controller.communication.wifi.UDPService;
import orleans.info.fr.remotecontrol.R;

/**
 * Created by Valentin on 02/12/2015.
 */
public class ServiceDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] tab = {getString(R.string.wifi)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.servicedialog_title)
                .setItems(tab, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //Wifi
                                HomeActivity act = (HomeActivity) getActivity();
                                act.unbindTcpService();
                                act.unbindUdpService();
                                Intent i = new Intent(act.getApplicationContext(), UDPService.class);
                                act.bindService(i, act.getUdpServiceConnection(), Context.BIND_AUTO_CREATE);
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
