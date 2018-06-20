package com.serli.telescope.task;

import java.text.SimpleDateFormat;
import java.util.List;

import com.serli.telescope.data.Coordonnees;
import com.serli.telescope.data.Etat;
import com.serli.telescope.manager.TelescopeManager;
import com.serli.telescope.repo.CoordonneeRepo;
import com.serli.telescope.serie.comSerie;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class ScheduledTasks {

    private static final Log log = LogFactory.getLog(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Value("${com.port}")
    private String portCom;

    @Autowired
    public CoordonneeRepo coordonneeRepo;

    @Autowired
    public TelescopeManager telescopeManager;

    @Scheduled(fixedRate = 500)
    public void reportCurrentTime() {
        final comSerie comSerie = new comSerie();
        try {
            comSerie.connect("/dev/ttyUSB0");
        } catch (Exception e) {
            //e.printStackTrace();
        }
        log.info("Watching");
        List<Coordonnees> coord = coordonneeRepo.findByEtat(Etat.ACCEPTEE);
//        if (coord != null && coord.size() > 0)
//        {
//            log.info("Demande acceptee : " + coord.size());
//            // Port pour le pc (pour les tests)
//            try {
//                //comSerie.connect("/dev/ttyUSB0");
//
//
//                coord.forEach((c) ->{
//                    try {
//                        log.info("Traitement manager : " + c.getNomPlanete());
//                        telescopeManager.move(c.getNomPlanete(), c.getCoord());
//                        c.setEtat(Etat.TRAITEE);
//                        coordonneeRepo.save(c);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
}