package com.function.dao;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient.ResponseReceiver;

import javax.net.ssl.*;
import java.util.logging.Logger;

public class EventGridHelper {
    private static final Logger logger = Logger.getLogger(EventGridHelper.class.getName());
    private static final String eventGridTopicEndpoint = "https://usuarioroleventgrid.eastus-1.eventgrid.azure.net/api/events";
    private static final String eventGridTopicKey = "2iHMJNambxhzRRcfxSgCTjek8W2DVb4hrzotRW4e2axJEKggT1s9JQQJ99BEACYeBjFXJ3w3AAABAZEGKkij";

    private static EventGridPublisherClient<EventGridEvent> client;

    static {
        try {
            // Crear un contexto SSL inseguro para Netty
            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            // Crear un cliente HTTP personalizado que use este contexto SSL
            HttpClient httpClient = new NettyAsyncHttpClientBuilder()
            .wiretap(false)
            // .port(8080)
            // .sslContext(sslContext)
            .build();

            // Crear el cliente Event Grid con este cliente HTTP personalizado
            client = new EventGridPublisherClientBuilder()
                    .endpoint(eventGridTopicEndpoint)
                    .credential(new AzureKeyCredential(eventGridTopicKey))
                    .httpClient(httpClient)
                    .buildEventGridEventPublisherClient();

            logger.info("Cliente Event Grid inicializado correctamente");
        } catch (Exception e) {
            logger.severe("Error al inicializar Event Grid Helper: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean sendEvent(String eventType, String message, Logger callerLogger) {
        try {
            logger.info("Enviando evento a Event Grid: " + eventType);
            
            EventGridEvent event = new EventGridEvent(
                    "../consumerEG-1.0-SNAPSHOT.jar",
                    eventType,
                    BinaryData.fromObject(message),
                    "1.0"
            );
            
            // Enviar el evento
            client.sendEvent(event);
            
            String successMessage = "Evento enviado correctamente: " + eventType;
            logger.info(successMessage);
            callerLogger.info(successMessage);
            return true;
        } catch (Exception e) {
            String errorMessage = "Error al enviar evento: " + e.getMessage();
            logger.severe(errorMessage);
            callerLogger.severe(errorMessage);
            e.printStackTrace();
            return false;
        }
    }
}