package com.mm87.android.lib.base.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import com.mm87.android.lib.base.R;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;


/**
 * Created by marco.mendoza on 01/02/2017.
 */

@TargetApi(Build.VERSION_CODES.M)
public class PermissionUtils {

    public static String[] location = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static String[] phone = {Manifest.permission.READ_PHONE_STATE};

    @PermissionChecker.PermissionResult
    public static boolean checkLocation(Context context) {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean checkPermission(Context context, String permission) {
        return (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Solicitar permisos por grupo, es decir los que pertenecen al mismo objetivo
     * como la ubicacion(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
     *
     * @param activity
     * @param requestCode      el cual le permitira reconocer su solicitud
     * @param rationaleMessage mensaje que se mostrara en caso sea necesario (dependera del tipo de permiso)
     * @param permissions
     */
    public static void request(final Activity activity, final int requestCode, String rationaleMessage, final String... permissions) {
        boolean flag = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                flag = true;
            }
        }

        if (flag) {
            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            if (rationaleMessage == null || rationaleMessage.isEmpty()) {
                rationaleMessage = activity.getString(R.string.permission_rationale_message);
            }
            new AlertDialog.Builder(activity)
                    .setTitle("Solicitud de permisos")
                    .setMessage(rationaleMessage)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(activity, permissions, requestCode);
                        }
                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

}