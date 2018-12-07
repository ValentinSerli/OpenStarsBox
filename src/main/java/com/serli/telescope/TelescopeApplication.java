package com.serli.telescope;

import com.serli.telescope.config.WebSocketConfiguration;
import com.serli.telescope.manager.TokenManager;
import com.serli.telescope.serie.comSerie;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.*;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

@EnableScheduling
@SpringBootApplication
@Controller
@RestController
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@CrossOrigin(origins = "http://192.168.86.119:8080")
public class TelescopeApplication {

    @Autowired
    public TokenManager tokenManager;

    public static WebSocketConfiguration webSocketConfiguration;

    private StompSession stompSession;

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(WebSocketConfiguration.class);

	public static void main(String[] args) throws Exception {

		ConfigurableApplicationContext context = SpringApplication.run(TelescopeApplication.class, args);
        webSocketConfiguration = context.getBean(WebSocketConfiguration.class);
        /**
         * Connexion au port série
         */
        comSerie comSerie = new comSerie();
        comSerie.connect("/dev/ttyUSB0");

	}

	@RequestMapping("/id")
	public String id() throws IOException {

		FileOutputStream fos = null;
		File idRasp = new File(".openstars");
		idRasp.createNewFile();
		logger.info("/id Chemin du fichier : " + idRasp.getAbsolutePath());

		if (!idRasp.exists())
        {
            logger.info("Erreur lors de la création du fichier");
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(idRasp));
                String readId = br.readLine();
                String osName = System.getProperty("os.name");
                String osVersion = System.getProperty("os.version");
                String osArch = System.getProperty("os.arch");
                String versionJava = System.getProperty("java.version");
                logger.info("ID : " + readId);
                return "<title>ID</title>" +
                        "<style>.id{" +
                        "text-align: center" +
                        "}" +
                        ".button{" +
                        "background-color: #cd0004;\n" +
                        "    color: white;\n" +
                        "    font-size: 18px;" +
                        "    padding: 14px 20px;\n" +
                        "    margin: 8px 0;\n" +
                        "    border: none;\n" +
                        "    border-radius: 4px;\n" +
                        "    cursor: pointer;" +
                        "}" +
                        "</style>" +
                        "<script> function deconnexion(){" +
                        "fetch(\"/deconnexion\")" +
                        ".then((response) => {\n" +
                        "            console.log(response.text());" +
                        "            window.location.href = \"/register.html\";" +
                        "})" +
                        "}" +
                        "</script>" +
                        "<h2 class=\"id\">Connexion réussi</h2>" +
                        "<h2 class=\"id\">ID : " + readId + "</h2>" +
                        "<h3 class=\"id\">Système d'exploitation : " + osName + "</h3>" +
                        "<h3 class=\"id\">Version du système : " + osVersion + "</h3>" +
                        "<h3 class=\"id\">Architecture du système : " + osArch + "</h3>" +
                        "<h3 class=\"id\">Version Java : " + versionJava + "</h3>" +
                        "<h3 class=\"id\"><button class=\"button\" onClick=\"deconnexion()\">Deconnexion</button></h3>";
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }

        return "Erreur lors de la création du fichier";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/deconnexion")
    public String deconnexion(){
	    tokenManager.setToken("");
	    stompSession.disconnect();
	    webSocketConfiguration.disconnect();
	    stompSession = null;
	    if (stompSession != null){
            if (stompSession.isConnected()){
                logger.info("Erreur : Encore connecter");
            }
        }
	    return "deconnexion";
    }

	@RequestMapping(method = RequestMethod.GET, value = "/token/{id}")
    public String token(@PathVariable("id") String token) throws ExecutionException, InterruptedException {
        tokenManager.setToken(token);
        logger.info("token reçu : " + tokenManager.getToken());
        ListenableFuture<StompSession> f = webSocketConfiguration.connect();
        stompSession = f.get();
		webSocketConfiguration.subscribeGreetings(stompSession);
        return token;
    }

	@RequestMapping("/")
    public RedirectView firstConnect(){
        File idRasp = new File(".openstars");
        if (stompSession != null){
            if (!stompSession.isConnected()){
                return new RedirectView("/id");
            }
            return new RedirectView("/id");
        } else {
            return new RedirectView("/register");
        }
    }

    @RequestMapping("/register")
    public RedirectView register(){
        File idRasp = new File(".openstars");
        if (stompSession != null){
            if (!stompSession.isConnected()){
                return new RedirectView("/id");
            }
            return new RedirectView("/id");
        } else {
            return new RedirectView("register.html");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/register/{id}")
    public String recupId(@PathVariable("id") String id) throws IOException {
        FileOutputStream fos = null;
        File idRasp = new File(".openstars");
        logger.info("/register Chemin du fichier : " + idRasp.getAbsolutePath());
        logger.info(id);

        if (!idRasp.exists())
        {
            try {
                logger.info("ID reçu : " + id);
                BufferedWriter bw = new BufferedWriter(new FileWriter(idRasp));
                PrintWriter pWriter = new PrintWriter(bw);
                pWriter.print(id + "\n");
                pWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                logger.info("Le fichier existe déjà, voici ce qu'il contient :");
                BufferedReader br = new BufferedReader(new FileReader(idRasp));
                String readId = br.readLine();
                if (!readId.equals(id))
                {
                    logger.info("Erreur : Les ID ne sont pas identique");
                    idRasp.delete();
                    File newIdRasp = new File(".openstars");
                    BufferedWriter bw = new BufferedWriter(new FileWriter(newIdRasp));
                    PrintWriter pWriter = new PrintWriter(bw);
                    pWriter.print(id + "\n");
                    pWriter.close();
                }
                logger.info("/register ID : " + readId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return id;
    }
}
