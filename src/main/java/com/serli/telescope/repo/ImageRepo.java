package com.serli.telescope.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepo extends CrudRepository<Image,Integer> {
    Image findFirstByNomPlanete(String nomPlanete);
}
