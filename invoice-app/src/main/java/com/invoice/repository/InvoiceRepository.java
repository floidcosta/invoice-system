package com.invoice.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.invoice.model.Invoice;
import com.invoice.model.InvoiceStatus;

public interface InvoiceRepository extends MongoRepository<Invoice, Integer>,InvoiceRepositoryCustom  {
   // List<Invoice> findByStatus(InvoiceStatus status);
    
   List<Invoice> findByStatusAndDueDateBefore(InvoiceStatus status, LocalDate dueDate);
}
