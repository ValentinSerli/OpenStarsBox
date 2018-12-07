package com.serli.telescope.manager;

import com.serli.telescope.serie.comSerie;

import java.io.IOException;

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
				System.out.println("Port série détecter : " + comSerie.getSerialPort());
				comSerie.getSerialPort().getOutputStream().write(coord.getBytes());
				boolean retourCoordComplet=false;
				String reponseCoord="";
				// Vérifie si le Télescope à bien reçu les coordonnées
				while (!retourCoordComplet) {
					int nbLu= comSerie.getSerialPort().getInputStream().read(buffer,0,1);
					if (nbLu>0)
					{
						reponseCoord += (char) buffer[0];
						retourCoordComplet=reponseCoord.endsWith("#");
						if (!reponseCoord.endsWith("#")){
//							return 2;
						}
					} else Thread.sleep(500);
				}
				System.out.println("reponse coord : "+reponseCoord);
				System.out.println();
				// Vérifie si le télescope à bien fini sont déplacement
				while(!finDeplacement) {
					comSerie.getSerialPort().getOutputStream().write(retour.getBytes());
					boolean retourComplet=false;
					String reponse="";
					while (!retourComplet) {
						int nbLu= comSerie.getSerialPort().getInputStream().read(buffer,0,1);
						if (nbLu>0)
						{
							reponse += (char) buffer[0];
							retourComplet=reponse.endsWith("#");
							if (!reponse.endsWith("#")){
//								return 3;
							}
						} else Thread.sleep(500);
					}
					System.out.println("reponse interrogation depl : "+reponse);
					System.out.println();
					if (reponse.startsWith("0#"))
					{
						System.out.println("Stop");
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
