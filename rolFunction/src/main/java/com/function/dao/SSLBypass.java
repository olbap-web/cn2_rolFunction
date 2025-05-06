package com.function.dao;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

public class SSLBypass {
    private static final Logger logger = Logger.getLogger(SSLBypass.class.getName());
    
    private static boolean isDisabled = false;
    
    public static synchronized void disableSSLVerification() {
        if (isDisabled) {
            logger.info("La verificación SSL ya ha sido deshabilitada");
            return;
        }
        
        try {
            logger.info("Deshabilitando verificación SSL...");
            
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            SSLContext.setDefault(sc);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            
            // Configuración específica para Azure SDK
            System.setProperty("com.azure.core.http.netty.netty.http.client.log.wire", "false");
            System.setProperty("com.azure.core.http.netty.disable.ssl.verification", "true");
            
            isDisabled = true;
            logger.info("Verificación SSL deshabilitada correctamente");
            
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.severe("Error al deshabilitar SSL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}