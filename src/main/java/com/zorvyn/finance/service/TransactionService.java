package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.TransactionRequest;
import com.zorvyn.finance.dto.TransactionResponse;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.model.Transaction;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.TransactionRepository;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionResponse create(TransactionRequest request,
                                      String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        transaction.setUser(user);

        return toResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponse> getAll() {
        return transactionRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    public TransactionResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public TransactionResponse update(Long id,
                                      TransactionRequest request) {
        Transaction transaction = findById(id);
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        return toResponse(transactionRepository.save(transaction));
    }

    public void delete(Long id) {
        Transaction transaction = findById(id);
        transactionRepository.delete(transaction);
    }

    public List<TransactionResponse> filter(
            TransactionType type,
            String category,
            LocalDate start,
            LocalDate end) {

        List<Transaction> results;

        if (type != null && start != null && end != null) {
            results = transactionRepository
                    .findByTypeAndDateBetween(type, start, end);
        } else if (type != null) {
            results = transactionRepository.findByType(type);
        } else if (category != null) {
            results = transactionRepository.findByCategory(category);
        } else if (start != null && end != null) {
            results = transactionRepository.findByDateBetween(start, end);
        } else {
            results = transactionRepository.findAll();
        }

        return results.stream().map(this::toResponse).toList();
    }

    private Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found with id: " + id));
    }

    private TransactionResponse toResponse(Transaction t) {
        TransactionResponse response = new TransactionResponse();
        response.setId(t.getId());
        response.setAmount(t.getAmount());
        response.setType(t.getType());
        response.setCategory(t.getCategory());
        response.setDescription(t.getDescription());
        response.setDate(t.getDate());
        response.setCreatedAt(t.getCreatedAt());
        response.setUsername(t.getUser().getUsername());
        return response;
    }
}