package com.invoice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.model.Invoice;
import com.invoice.model.OverdueProcessRequest;
import com.invoice.repository.InvoiceRepository;
import com.invoice.service.InvoiceService;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/v1")
public class InvoiceController {

	@Autowired
	InvoiceRepository invoiceRepository;

	@Autowired
	InvoiceService invoiceService;

	@GetMapping("/invoices")
	public ResponseEntity<List<Invoice>> getAllInvoices(@RequestParam(required = false) String id) {
		try {
			List<Invoice> invoices = new ArrayList<Invoice>();
			invoiceRepository.findAll().forEach(invoices::add);

			if (invoices.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(invoices, HttpStatus.OK);
		} catch (Exception e) {
			System.out.print(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/invoices/{id}")
	public ResponseEntity<Invoice> getInvoicesById(@PathVariable("id") int id) {
		Optional<Invoice> invoiceData = invoiceRepository.findById(id);

		if (invoiceData.isPresent()) {
			return new ResponseEntity<>(invoiceData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/invoices")
	public ResponseEntity<Invoice> createTutorial(@RequestBody Invoice invoice) {
		try {
			Invoice _invoice = invoiceService.createInvoice(invoice);
			return new ResponseEntity<>(_invoice, HttpStatus.CREATED);
		} catch (Exception e) {
			System.out.print(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/invoices/{id}/payments")
	public ResponseEntity<Invoice> updateInvoices(@PathVariable("id") int id, @RequestBody Invoice invoice) {

		Invoice _invoice = invoiceService.payInvoice(id, invoice);

		if (_invoice != null) {
			return new ResponseEntity<>(invoiceRepository.save(_invoice), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/process-overdue")
	public ResponseEntity<String> processOverdue(@RequestBody OverdueProcessRequest request) {
		invoiceService.processOverdue(request.getLateFee(), request.getOverdueDays());
		return ResponseEntity.ok("Overdue invoices processed.");
	}

}
