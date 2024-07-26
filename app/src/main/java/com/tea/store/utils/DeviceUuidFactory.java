package com.tea.store.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 产生uuid, 用以作为设备唯一标识
 * permission needed: android.permission.WRITE_EXTERNAL_STORAGE
 */
public class DeviceUuidFactory {
    private static String TAG = DeviceUuidFactory.class.getSimpleName();

    private static String deviceUUID = null;
    //TODO 不同的应用可能会发生变化
    private static String UUID_FILE_NAME = "kdid";
    private static String EXTERNAL_UUID_DIR = Environment
            .getExternalStorageDirectory()
            + File.separator
            + UUID_FILE_NAME;

    public synchronized static String getUUID(Context context) {
        if (deviceUUID == null) {
            File internalUUIDFile = new File(context.getFilesDir(), UUID_FILE_NAME);
            File externalUUIDFile = new File(EXTERNAL_UUID_DIR, UUID_FILE_NAME);
            try {
                // 1.优先从应用目录中读取
                String tempUUID = readUUIDFile(internalUUIDFile);

                // 2. 应用目录中无效，则尝试从SD卡中导入
                if (invalidUUID(tempUUID)) {
                    tempUUID = readUUIDFile(externalUUIDFile);
                    if (validUUID(tempUUID)) {
                        writeUUIDFile(internalUUIDFile, tempUUID);
                    }
                }

                // 3. 从SD卡中导入失败，则重新生成
                if (invalidUUID(tempUUID)) {
                    tempUUID = generateUUID();
                    writeUUIDFile(internalUUIDFile, tempUUID);
                    writeUUIDFile(externalUUIDFile, tempUUID);
                }

                deviceUUID = tempUUID;
            } catch (Exception e) {
                return null;
            }
        }

        return deviceUUID;
    }

    private static boolean validUUID(String uuid) {
        if (uuid == null || "".equals(uuid.trim())) {
            return false;
        }

        /*
		if (uuid.length() != 36) { // TODO
			return false;
		}
		*/

        return true;
    }

    private static boolean invalidUUID(String uuid) {
        return !validUUID(uuid);
    }

    private static String readUUIDFile(File uuidFile) {
        if (uuidFile == null || !uuidFile.exists()) {
            return null;
        }

        try {
            RandomAccessFile f = new RandomAccessFile(uuidFile, "r");
            byte[] bytes = new byte[(int) f.length()];
            f.readFully(bytes);
            f.close();

            return new String(bytes);
        } catch (IOException e) {
            ;
        }

        return null;
    }

    private static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private static void writeUUIDFile(File file, String u) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Files.write(Paths.get(file.toURI()), u.getBytes());
        } else {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(u.getBytes());
            outputStream.close();
        }
    }
}