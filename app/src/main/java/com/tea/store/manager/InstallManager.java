package com.tea.store.manager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.tea.store.bean.AppItem;
import com.tea.store.enums.Atts;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

public class InstallManager {

    private Callback callback;

    public InstallManager(Callback callback){
        this.callback = callback;
    }

    public void installPackage(Context context, final AppItem item, final File file, Handler handler) throws Exception {
        String name = "base.apk";
        FileInputStream in = new FileInputStream(file);
        PackageManager packageManger = context.getPackageManager();
        PackageInstaller packageInstaller = packageManger.getPackageInstaller();
        packageInstaller.registerSessionCallback(new PackageInstaller.SessionCallback() {
            @Override
            public void onCreated(int sessionId) {

            }

            @Override
            public void onBadgingChanged(int sessionId) {
            }

            @Override
            public void onActiveChanged(int sessionId, boolean active) {
                if (!active) {
                    packageInstaller.unregisterSessionCallback(this);
                    item.setStatus(AppItem.STATU_INSTALL_FAIL);
                    if (callback != null) callback.onFinal();
                }
            }

            @Override
            public void onProgressChanged(int sessionId, float progress) {
            }

            @Override
            public void onFinished(int sessionId, boolean success) {
                packageInstaller.unregisterSessionCallback(this);
                item.setStatus(success ? AppItem.STATU_INSTALL_SUCCESS : AppItem.STATU_INSTALL_FAIL);
                if (callback != null) callback.onFinal();
            }
        }, handler);
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        PackageInstaller.Session session = null;
        int sessionId = packageInstaller.createSession(params);
        session = packageInstaller.openSession(sessionId);
        OutputStream out = session.openWrite(name, 0, -1);
        byte buffer[] = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }
        session.fsync(out);
        out.close();
        in.close();

        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.putExtra(Atts.DOWNLOAD_URL, item.getAppDownLink());
        session.commit(PendingIntent.getBroadcast(context, sessionId, intent, PendingIntent.FLAG_MUTABLE).getIntentSender());
        session.close();
    }

    public interface Callback{
        void onFinal();
    }
}