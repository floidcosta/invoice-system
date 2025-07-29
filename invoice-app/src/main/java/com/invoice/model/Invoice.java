package com.invoice.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "invoice")
public class Invoice {
	@Id
	private int id;
	private double amount;
	private LocalDate dueDate;
	private double paidAmount = 0;

    private InvoiceStatus status = InvoiceStatus.PENDING;
    
	public Invoice() {

	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public InvoiceStatus getStatus() {
		return status;
	}

	public void setStatus(InvoiceStatus status) {
		this.status = status;
	}

	public Invoice(double amount, LocalDate dueDate) {
		super();
		this.id = id;
		this.amount = amount;
		this.dueDate = dueDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Invoice [id=" + id + ", amount=" + amount + ", dueDate=" + dueDate + ", paidAmount=" + paidAmount
				+ ", status=" + status + "]";
	}

}
