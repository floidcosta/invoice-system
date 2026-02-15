package com.invoice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.invoice.model.Invoice;
import com.invoice.model.InvoiceStatus;
import com.invoice.repository.InvoiceRepository;
import com.invoice.service.InvoiceService;

@SpringBootTest
class InvoiceServiceTest {

	@InjectMocks
	private InvoiceService invoiceService;

	@Mock
	private InvoiceRepository invoiceRepository;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCreateInvoice() {
		Invoice invoice = new Invoice();
		invoice.setAmount(100);

		when(invoiceRepository.getNextInvoiceId()).thenReturn(1);
		when(invoiceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

		Invoice result = invoiceService.createInvoice(invoice);

		assertEquals(1, result.getId());
		assertEquals(InvoiceStatus.PENDING, result.getStatus());
		verify(invoiceRepository).save(invoice);
	}

	@Test
	void testGetAllInvoices() {
		List<Invoice> mockInvoices = Arrays.asList(new Invoice(), new Invoice());
		when(invoiceRepository.findAll()).thenReturn(mockInvoices);

		List<Invoice> result = invoiceService.getAllInvoices();

		assertEquals(2, result.size());
	}

	@Test
	void testGetInvoiceById_Found() {
		Invoice invoice = new Invoice();
		invoice.setId(1);
		when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice));

		Optional<Invoice> result = invoiceService.getInvoiceById(1);

		assertTrue(result.isPresent());
		assertEquals(1, result.get().getId());
	}

	@Test
	void testGetInvoiceById_NotFound() {
		when(invoiceRepository.findById(99)).thenReturn(Optional.empty());

		Optional<Invoice> result = invoiceService.getInvoiceById(99);

		assertTrue(result.isEmpty());
	}

	@Test
	void testSaveInvoice() {
		Invoice invoice = new Invoice();
		when(invoiceRepository.save(invoice)).thenReturn(invoice);

		Invoice result = invoiceService.save(invoice);

		assertSame(invoice, result);
	}

	@Test
	void testGetOverdueInvoices() {
		LocalDate now = LocalDate.now();
		Invoice overdue = new Invoice();
		overdue.setDueDate(now.minusDays(1));
		overdue.setStatus(InvoiceStatus.PENDING);

		Invoice notOverdue = new Invoice();
		notOverdue.setDueDate(now.plusDays(5));
		notOverdue.setStatus(InvoiceStatus.PENDING);

		when(invoiceRepository.findAll()).thenReturn(List.of(overdue, notOverdue));

		List<Invoice> result = invoiceService.getOverdueInvoices(now);

		assertEquals(1, result.size());
		assertSame(overdue, result.get(0));
	}

	@Test
	void testPayInvoice_FullPayment() {
		Invoice invoice = new Invoice();
		invoice.setId(1);
		invoice.setAmount(100);
		invoice.setPaidAmount(50);
		invoice.setStatus(InvoiceStatus.PENDING);

		Invoice payment = new Invoice();
		payment.setAmount(50);

		when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice));
		when(invoiceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

		Invoice result = invoiceService.payInvoice(1, payment);

		assertEquals(100, result.getPaidAmount());
		assertEquals(InvoiceStatus.PAID, result.getStatus());
	}

	@Test
	void testPayInvoice_PartialPayment() {
		Invoice invoice = new Invoice();
		invoice.setId(1);
		invoice.setAmount(200);
		invoice.setPaidAmount(50);
		invoice.setStatus(InvoiceStatus.PENDING);

		Invoice payment = new Invoice();
		payment.setAmount(75);

		when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice));
		when(invoiceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

		Invoice result = invoiceService.payInvoice(1, payment);

		assertEquals(125, result.getPaidAmount());
		assertEquals(InvoiceStatus.PENDING, result.getStatus());
	}

	@Test
	void testPayInvoice_AlreadyPaid() {
		Invoice invoice = new Invoice();
		invoice.setId(1);
		invoice.setAmount(100);
		invoice.setPaidAmount(100);
		invoice.setStatus(InvoiceStatus.PAID);

		Invoice payment = new Invoice();
		payment.setAmount(50);

		when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice));

		Invoice result = invoiceService.payInvoice(1, payment);

		assertEquals(150, result.getPaidAmount());
		assertEquals(InvoiceStatus.PAID, result.getStatus());
		verify(invoiceRepository, never()).save(any());
	}

	@Test
	void testProcessOverdue() {
		LocalDate now = LocalDate.now();
		Invoice inv1 = new Invoice();
		inv1.setId(1);
		inv1.setDueDate(now.minusDays(10));
		inv1.setAmount(100);
		inv1.setPaidAmount(0);
		inv1.setStatus(InvoiceStatus.PENDING);

		Invoice inv2 = new Invoice();
		inv2.setId(2);
		inv2.setDueDate(now.minusDays(10));
		inv2.setAmount(100);
		inv2.setPaidAmount(50);
		inv2.setStatus(InvoiceStatus.PENDING);

		when(invoiceRepository.findByStatusAndDueDateBefore(eq(InvoiceStatus.PENDING), any()))
				.thenReturn(List.of(inv1, inv2));

		when(invoiceRepository.getNextInvoiceId()).thenReturn(10, 11);
		when(invoiceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

		invoiceService.processOverdue(25.0, 10);

		verify(invoiceRepository, times(4)).save(any()); // 2 originals updated + 2 new invoices
	}
}
