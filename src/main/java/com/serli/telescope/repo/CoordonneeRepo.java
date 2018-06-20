package com.serli.telescope.repo;

import com.serli.telescope.data.Coordonnees;
import com.serli.telescope.data.Etat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoordonneeRepo extends CrudRepository<Coordonnees,Integer> {
    List<Coordonnees> findByEtat(Etat etat);
    Integer countByEtat(Etat etat);

    Coordonnees findByNomPlanete(String nom);
}
