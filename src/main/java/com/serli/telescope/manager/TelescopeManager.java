package com.serli.telescope.manager;

import com.serli.telescope.config.WebSocketConfiguration;
import com.serli.telescope.serie.ComSerie;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Classe Planete qui intéragie avec le télescope
 *
 * @param manager String nom de la manager
 * @param coord String contenant les coordonnées de la manager
 * @return La fin du mouvement du telescope
 */

/**
 * Implementation de l'interface manager
 *
 * Classe manager qui intéragie avec le téléscope et suis sont déplacement
 * @return la fin du déplacement du téléscope
 *
 */

@Component
public class TelescopeManager {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(WebSocketConfiguration.class);

	/**
	 * Méthode telescope qui intéragie avec le télescope
	 * Le paramètre manager qui contien le nom de la manager
	 * Le paramètre coord qui contient les coordonnées de la manager
	 * @throws InterruptedException
	 */

	public int move(String planete, String coord) throws Exception {
		boolean finDeplacement = false;
		byte[] buffer = new byte[1024];
		String retour = "L\n";
			try {
				logger.info("Port série détecter : " + ComSerie.getSerialPort());
				ComSerie.getSerialPort().getOutputStream().write(coord.getBytes());
				boolean retourCoordComplet=false;
				String reponseCoord="";
				// Vérifie si le Télescope à bien reçu les coordonnées
				while (!retourCoordComplet) {
					int nbLu= ComSerie.getSerialPort().getInputStream().read(buffer,0,1);
					if (nbLu>0)
					{
						reponseCoord += (char) buffer[0];
						retourCoordComplet=reponseCoord.endsWith("#");
						if (!reponseCoord.endsWith("#")){
//							return 2;
						}
					} else Thread.sleep(500);
				}
				logger.info("reponse coord : "+reponseCoord + "\n");
				// Vérifie si le télescope à bien fini sont déplacement
				while(!finDeplacement) {
					ComSerie.getSerialPort().getOutputStream().write(retour.getBytes());
					boolean retourComplet=false;
					String reponse="";
					while (!retourComplet) {
						int nbLu= ComSerie.getSerialPort().getInputStream().read(buffer,0,1);
						if (nbLu>0)
						{
							reponse += (char) buffer[0];
							retourComplet=reponse.endsWith("#");
							if (!reponse.endsWith("#")){
//								return 3;
							}
						} else Thread.sleep(500);
					}
					logger.info("reponse interrogation depl : " + reponse + "\n");
					if (reponse.startsWith("0#"))
					{
						logger.info("Stop");
						finDeplacement = true;

					} else Thread.sleep(500);
				}
			}
			catch (IOException e) {
				throw new Exception("Erreur lors du mouvement du telescope");
			}
		return 1;
	}
}
