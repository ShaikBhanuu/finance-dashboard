package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.DashboardSummary;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    public DashboardSummary getSummary() {
        BigDecimal totalIncome = transactionRepository
                .sumByType(TransactionType.INCOME);
        BigDecimal totalExpenses = transactionRepository
                .sumByType(TransactionType.EXPENSE);
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        DashboardSummary summary = new DashboardSummary();
        summary.setTotalIncome(totalIncome);
        summary.setTotalExpenses(totalExpenses);
        summary.setNetBalance(netBalance);
        summary.setIncomeByCategory(
                buildCategoryMap(TransactionType.INCOME));
        summary.setExpenseByCategory(
                buildCategoryMap(TransactionType.EXPENSE));
        return summary;
    }

    public Map<String, BigDecimal> getMonthlyTrends(
            TransactionType type, int year) {
        List<Object[]> results =
                transactionRepository.monthlyTotals(type, year);
        Map<String, BigDecimal> trends = new LinkedHashMap<>();
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                "Jul","Aug","Sep","Oct","Nov","Dec"};
        for (Object[] row : results) {
            int month = ((Number) row[0]).intValue();
            BigDecimal total = (BigDecimal) row[1];
            trends.put(months[month - 1], total);
        }
        return trends;
    }

    private Map<String, BigDecimal> buildCategoryMap(
            TransactionType type) {
        List<Object[]> results =
                transactionRepository.sumGroupedByCategory(type);
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] row : results) {
            map.put((String) row[0], (BigDecimal) row[1]);
        }
        return map;
    }
}