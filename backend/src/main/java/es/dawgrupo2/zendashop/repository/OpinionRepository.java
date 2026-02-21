package es.dawgrupo2.zendashop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.dawgrupo2.zendashop.model.Opinion;

public interface OpinionRepository extends JpaRepository<Opinion, Long> {

    List<Opinion> findByUserId(Long userId);

}