package es.dawgrupo2.zendashop.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Order;


public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserIdAndCompletedTrue(Long userId, Pageable pageable);
    Page<Order> findByCompletedTrue(Pageable pageable);
    List<Order> findByCompletedTrueAndCreationDateBetween(LocalDateTime start, LocalDateTime end);

    List<Order> findDistinctByCompletedFalseAndOrderItems_Garment_Id(Long garmentId);

    // Incomes of a specific day
    @Query("SELECT SUM(o.totalPrice) FROM OrderTable o WHERE o.creationDate BETWEEN :start AND :end")
    Double sumIncomeBetween(LocalDateTime start, LocalDateTime end);

    // Orders of a specific day
    @Query("SELECT COUNT(o) FROM OrderTable o WHERE o.creationDate BETWEEN :start AND :end")
    Long countOrdersBetween(LocalDateTime start, LocalDateTime end);

    // Incomes of a specific month
    @Query("SELECT SUM(o.totalPrice) FROM OrderTable o WHERE MONTH(o.creationDate) = :month AND YEAR(o.creationDate) = :year")
    Double sumIncomeByMonth(int month, int year);

    // Orders of a specific month
    @Query("SELECT COUNT(o) FROM OrderTable o WHERE MONTH(o.creationDate) = :month AND YEAR(o.creationDate) = :year")
    long countOrdersByMonth(int month, int year);

    // Incomes of a specific year
    @Query("SELECT SUM(o.totalPrice) FROM OrderTable o WHERE YEAR(o.creationDate) = :year")
    Double sumIncomeByYear(int year);

    @Transactional
    @Modifying
    @Query(value = "UPDATE order_table SET creation_date = :date WHERE id = :id", nativeQuery = true)
    void forceCreationDate(Long id, LocalDateTime date);
}