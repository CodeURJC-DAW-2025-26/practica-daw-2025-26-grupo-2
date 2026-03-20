package es.dawgrupo2.zendashop.repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.dawgrupo2.zendashop.model.Garment;

public interface GarmentRepository extends JpaRepository<Garment, Long> {

    Optional<Garment> findByName(String name);
    Optional<Garment> findByCategory(String category);
    Optional<Garment> findByIdAndAvailableTrue(Long id);
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

    @Query(value = "WITH twin AS ( " +
        "  SELECT ot2.user_id " +
        "  FROM order_table ot1 " +
        "  JOIN order_item oi1 ON ot1.id = oi1.order_id " +
        "  JOIN order_item oi2 ON oi1.garment_id = oi2.garment_id " +
        "  JOIN order_table ot2 ON oi2.order_id = ot2.id " +
        "  WHERE ot1.user_id = :userId AND ot2.user_id <> :userId " +
        "  GROUP BY ot2.user_id " +
        "  ORDER BY COUNT(DISTINCT oi1.garment_id) DESC, ot2.user_id " +
        "  LIMIT 1 " +
        ") " +
        "SELECT g.* " +
        "FROM garment g " +
        "WHERE g.available = true " +
        "AND NOT EXISTS ( " +
        "  SELECT 1 FROM order_item oi_me " +
        "  JOIN order_table ot_me ON oi_me.order_id = ot_me.id " +
        "  WHERE ot_me.user_id = :userId AND oi_me.garment_id = g.id " +
        ") " +
        "ORDER BY " +
        "  CASE WHEN EXISTS ( " +
        "    SELECT 1 FROM order_item oi_t " +
        "    JOIN order_table ot_t ON oi_t.order_id = ot_t.id " +
        "    WHERE ot_t.user_id = (SELECT user_id FROM twin) AND oi_t.garment_id = g.id " +
        "  ) THEN 1 ELSE 2 END, " +
        "  RAND() " +
        "LIMIT 3", nativeQuery = true)
    List<Garment> findSmartRecommendations(@Param("userId") Long userId);
}