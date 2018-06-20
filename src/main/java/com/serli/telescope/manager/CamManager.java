package com.serli.telescope.manager;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.fswebcam.FsWebcamDriver;
import com.serli.telescope.config.WebSocketConfiguration;
import javassist.bytecode.ByteArray;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class CamManager {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(WebSocketConfiguration.class);

    static {
        Webcam.setDriver(new FsWebcamDriver()); // this is important
    }

    public byte[] takePicture() throws IOException {
        List<Webcam> webcamList = Webcam.getWebcams();
        Webcam webcam2 = Webcam.getDefault();
        System.out.println("Liste de webcam : " + webcamList);
        System.out.println("Webcam par default : " + webcam2);
        Webcam webcam = null;
        if (webcamList != null && webcamList.size() > 0)
        {
            webcam = webcamList.get(0);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            if (!webcam.isOpen()){
                webcam.open();
            }

            System.out.println(webcam.getName());

            BufferedImage image = webcam.getImage();

            System.out.println("Image capturé");


            ImageIO.write(image, "PNG", baos);
            baos.flush();
            System.out.println("Image enregistrer");
//            String tabImage = "";
//            byte[] imageByte = baos.toByteArray();
//            for (int i = 0; i < baos.size(); i++)
//            {
//                tabImage += imageByte[i];
//            }
//            logger.info("Tableau contenant : " + tabImage);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (webcam != null) {
                webcam.close();
            }
        }

        System.out.println("Image prise");

        return null;

    }
}
