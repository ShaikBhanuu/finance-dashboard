package com.zorvyn.finance.repository;

import com.zorvyn.finance.model.Transaction;
import com.zorvyn.finance.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByCategory(String category);

    List<Transaction> findByDateBetween(LocalDate start, LocalDate end);

    List<Transaction> findByTypeAndDateBetween(
            TransactionType type, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = :type")
    BigDecimal sumByType(TransactionType type);

    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t " +
            "WHERE t.type = :type GROUP BY t.category")
    List<Object[]> sumGroupedByCategory(TransactionType type);

    @Query("SELECT MONTH(t.date), SUM(t.amount) FROM Transaction t " +
            "WHERE t.type = :type AND YEAR(t.date) = :year GROUP BY MONTH(t.date)")
    List<Object[]> monthlyTotals(TransactionType type, int year);
}
