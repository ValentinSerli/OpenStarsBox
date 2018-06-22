package com.serli.telescope;

import com.serli.telescope.config.WebSocketConfiguration;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

@EnableScheduling
@SpringBootApplication
@Controller
@RestController
@CrossOrigin(origins = "http://192.168.86.105:8080")
public class TelescopeApplication {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(WebSocketConfiguration.class);

	public static void main(String[] args) throws ExecutionException, InterruptedException, UnknownHostException {


//		logger.info("Subscribing to greeting topic using session " + stompSession);

		ConfigurableApplicationContext context = SpringApplication.run(TelescopeApplication.class, args);
		WebSocketConfiguration webSocketConfiguration = context.getBean(WebSocketConfiguration.class);
		ListenableFuture<StompSession> f = webSocketConfiguration.connect();
		StompSession stompSession = f.get();
		webSocketConfiguration.subscribeGreetings(stompSession);

        String ip = InetAddress.getLocalHost().getHostAddress().toString();
        System.out.println("Votre IP est : " + ip);

	}

	@RequestMapping("/id")
	public String id() throws IOException {

		FileOutputStream fos = null;
		File idRasp = new File(".openstars");
		idRasp.createNewFile();
		System.out.println("/id Chemin du fichier : " + idRasp.getAbsolutePath());

		if (!idRasp.exists())
        {
            System.out.println("Erreur lors de la création du fichier");
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(idRasp));
                String readId = br.readLine();
                String osName = System.getProperty("os.name");
                String osVersion = System.getProperty("os.version");
                String osArch = System.getProperty("os.arch");
                String versionJava = System.getProperty("java.version");
                System.out.println("ID : " + readId);
                return "<title>ID</title>" +
                        "<style>.id{" +
                        "text-align: center" +
                        "}" +
                        "</style>" +
                        "<h2 class=\"id\">" + readId + "</h2>" +
                        "<h3 class=\"id\">Système d'exploitation : " + osName + "<h3>" +
                        "<h3 class=\"id\">Version du système : " + osVersion + "<h3>" +
                        "<h3 class=\"id\">Architecture du système : " + osArch + "<h3>" +
                        "<h3 class=\"id\">Version Java : " + versionJava + "<h3>";
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }

        return "Erreur lors de la création du fichier";
	}

	@RequestMapping("/")
    public RedirectView firstConnect(){
        File idRasp = new File(".openstars");
	    if (idRasp.exists()){
	        return new RedirectView("/id");
        } else {
            return new RedirectView("/register");
        }
    }

    @RequestMapping("/register")
    public RedirectView register(){
        File idRasp = new File(".openstars");
        if (idRasp.exists()){
            return new RedirectView("/id");
        } else {
            return new RedirectView("register.html");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/register/{id}")
    public String recupId(@PathVariable("id") String id) throws IOException {
        FileOutputStream fos = null;
        File idRasp = new File(".openstars");
//        idRasp.createNewFile();
        System.out.println("/register Chemin du fichier : " + idRasp.getAbsolutePath());
        System.out.println(id);

        if (!idRasp.exists())
        {
            try {
                System.out.println("ID reçu : " + id);
                BufferedWriter bw = new BufferedWriter(new FileWriter(idRasp));
                PrintWriter pWriter = new PrintWriter(bw);
                pWriter.print(id + "\n");
                pWriter.close();
//                return "<title>ID</title>" + "<h2>" + id.toString() + "</h2>";
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                System.out.println("Le fichier existe déjà, voici ce qu'il contient");
                BufferedReader br = new BufferedReader(new FileReader(idRasp));
                String readId = br.readLine();
                System.out.println("/register ID : " + readId);
//                return "<title>ID</title>" + "<h2>" + readId + "</h2>";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return id;
    }
}
