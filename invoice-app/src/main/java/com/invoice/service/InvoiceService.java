package com.invoice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoice.model.Invoice;
import com.invoice.model.InvoiceStatus;
import com.invoice.repository.InvoiceRepository;

@Service
public class InvoiceService {
	@Autowired
	private InvoiceRepository invoiceRepository;

	public Invoice createInvoice(Invoice invoice) {
		invoice.setId(invoiceRepository.getNextInvoiceId());
		invoice.setStatus(InvoiceStatus.PENDING);
		return invoiceRepository.save(invoice);
	}

	public List<Invoice> getAllInvoices() {
		return invoiceRepository.findAll();
	}

	public Optional<Invoice> getInvoiceById(int id) {
		return invoiceRepository.findById(id);
	}

	public Invoice save(Invoice invoice) {
		return invoiceRepository.save(invoice);
	}

	public List<Invoice> getOverdueInvoices(LocalDate now) {
		return invoiceRepository.findAll().stream()
				.filter(inv -> inv.getStatus().equals("pending") && inv.getDueDate().isBefore(now)).toList();
	}

	public Invoice payInvoice(int id, Invoice invoiceAmount) {
		Optional<Invoice> optionalInvoice = invoiceRepository.findById(id);
		if (optionalInvoice.isEmpty())
			return null;

		Invoice invoice = optionalInvoice.get();

		if ((InvoiceStatus.PAID).equals(invoice.getStatus())) {
			invoice.setPaidAmount(invoice.getPaidAmount() + invoiceAmount.getAmount());
			return invoice;
		}

		invoice.setPaidAmount(invoice.getPaidAmount() + invoiceAmount.getAmount());

		if (invoice.getPaidAmount() >= invoice.getAmount()) {
			invoice.setStatus(InvoiceStatus.PAID);
		}

		return invoiceRepository.save(invoice);
	}

	public void processOverdue(double lateFee, int overdueDays) {
		LocalDate thresholdDate = LocalDate.now().minusDays(overdueDays);
		List<Invoice> overdueInvoices = invoiceRepository.findByStatusAndDueDateBefore(InvoiceStatus.PENDING,
				thresholdDate);

		for (Invoice invoice : overdueInvoices) {
			double paid = invoice.getPaidAmount();
			double due = invoice.getAmount();

			invoice.setStatus(paid > 0 ? InvoiceStatus.PAID : InvoiceStatus.VOID);
			invoiceRepository.save(invoice);

			double remaining = due - paid;
			if (remaining > 0) {
				Invoice newInvoice = new Invoice();
				newInvoice.setId(invoiceRepository.getNextInvoiceId());
				newInvoice.setAmount(remaining + lateFee);
				newInvoice.setPaidAmount(0);
				newInvoice.setDueDate(LocalDate.now().plusDays(overdueDays));
				newInvoice.setStatus(InvoiceStatus.PENDING);
				invoiceRepository.save(newInvoice);
			}
		}
	}

}
