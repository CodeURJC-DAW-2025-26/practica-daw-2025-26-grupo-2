package es.dawgrupo2.zendashop.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.dawgrupo2.zendashop.model.Garment;

public interface GarmentRepository extends JpaRepository<Garment, Long> {

    Optional<Garment> findByName(String name);
    Optional<Garment> findByCategory(String category);
    Page<Garment> findByAvailableTrue(Pageable pageable);
    @Query("""
    SELECT g
    FROM Garment g
    WHERE (:searchName IS NULL OR :searchName = '' OR LOWER(g.name) LIKE LOWER(CONCAT('%', :searchName, '%')))
      AND (:searchCategory IS NULL OR :searchCategory = '' OR g.category = :searchCategory)
      AND (:minPrice IS NULL OR g.price >= :minPrice)
      AND (:maxPrice IS NULL OR g.price <= :maxPrice)
      AND g.available = true
""")
    Page<Garment> findAvailableGarmentsByOptionalFilters(
        @Param("searchName") String searchName,
        @Param("searchCategory") String searchCategory,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);
}