package com.serli.telescope.config;

import com.serli.telescope.data.Coordonnees;
import com.serli.telescope.data.Etat;
import com.serli.telescope.data.Image;
import com.serli.telescope.manager.CamManager;
import com.serli.telescope.manager.TelescopeManager;
import com.serli.telescope.repo.CoordonneeRepo;
import com.serli.telescope.repo.ImageRepo;
import com.serli.telescope.serie.comSerie;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Configuration
public class WebSocketConfiguration {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(WebSocketConfiguration.class);

    private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    @Autowired
    public CoordonneeRepo coordonneeRepo;

    @Autowired
    public ImageRepo imageRepo;

    @Autowired
    public TelescopeManager telescopeManager;

    @Autowired
    public CamManager camManager;

    public ListenableFuture<StompSession> connect() {

        Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        List<Transport> transports = Collections.singletonList(webSocketTransport);

        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setInboundMessageSizeLimit(Integer.MAX_VALUE);

        String url = "ws://{host}:{port}/socket";
        return stompClient.connect(url, headers, new MyHandler(), "192.168.86.105", 8080);

    }

    public void subscribeGreetings(StompSession stompSession) throws ExecutionException, InterruptedException {
        stompSession.subscribe("/telescope/move", new StompFrameHandler() {

            public Type getPayloadType(StompHeaders stompHeaders) {
                return Object.class;
//                return byte[].class;
            }

            public void handleFrame(StompHeaders stompHeaders, Object o) {
                logger.info("Received request " + new String((byte[]) o));
                logger.info("Taille du StompHeaders : " + stompHeaders.size());
                Coordonnees coord = coordonneeRepo.findByNomPlanete(new String((byte[]) o));
                Image image = new Image();
                if (coord != null)
                {
                    logger.info("Demande acceptee : " + coord.getNomPlanete());

                    try {
                        try {
                            logger.info("Traitement manager : " + coord.getNomPlanete());
//                            telescopeManager.move(coord.getNomPlanete(), coord.getCoord());
//                            byte[] picture = camManager.takePicture();
                            byte[] picture2 = new byte[10];
                            picture2[1] = 12;
                            picture2[2] = 56;
                            picture2[3] = 48;
                            picture2[4] = 63;
                            picture2[5] = 120;
                            byte[] encoded = Base64.getEncoder().encode(picture2);
                            logger.info("Envoie de l'image");

                            logger.info("Taille de l'image 2 : " + encoded.length);
                            logger.info("Connecter au WebSocket : " + stompSession.isConnected());

                            image.setNomPlanete(coord.getNomPlanete());
                            logger.info("Nom planete : " + image.getNomPlanete());
                            image.setImageBase64(new String(encoded));

                            logger.info("Image envoyée");
                            coord.setEtat(Etat.TRAITEE);
                            logger.info("Mise à jour de l'état");
                            Image save = imageRepo.save(image);
                            coordonneeRepo.save(coord);
                            Integer fini = 1;
                            stompSession.send("/app/receive", new byte[] {save.getIdImage().byteValue()});
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private class MyHandler extends StompSessionHandlerAdapter {
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            logger.info("Now connected");
        }
    }
}