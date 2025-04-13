package com.function.utils;

import java.io.*;
import java.nio.file.*;
import java.util.Objects;
public class ResourceUtils {

    public static String copyWalletToTemp() throws IOException {
        String walletDirName = "Wallet_CSMZSQ3ZR41HPBVN";
        String[] walletFiles = {
            "cwallet.sso", "ewallet.p12", "keystore.jks",
            "ojdbc.properties", "sqlnet.ora", "tnsnames.ora", "truststore.jks"
        };

        // Crear directorio temporal
        Path tempDir = Files.createTempDirectory("oracle_wallet_");
        File tempWalletDir = new File(tempDir.toFile(), walletDirName);
        tempWalletDir.mkdirs();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        for (String fileName : walletFiles) {
            InputStream is = classLoader.getResourceAsStream(walletDirName + "/" + fileName);
            if (is == null) {
                throw new IOException("❌ No se encontró el archivo del wallet: " + fileName);
            }

            File outFile = new File(tempWalletDir, fileName);
            try (OutputStream os = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        }

        System.out.println("✅ Wallet copiado a: " + tempWalletDir.getAbsolutePath());
        return tempWalletDir.getAbsolutePath();
    }
}
