package es.dawgrupo2.zendashop.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import es.dawgrupo2.zendashop.model.Garment;

public interface GarmentRepository extends JpaRepository<Garment, Long> {

    Optional<Garment> findByName(String name);
    Optional<Garment> findByCategory(String category);
    Page<Garment> findByAvailableTrue(Pageable pageable);
}