package com.invoice.model;

public class OverdueProcessRequest {
    private double lateFee;
    private int overdueDays;

    // Getters and setters    
    public double getLateFee() {
		return lateFee;
	}
	public void setLateFee(double lateFee) {
		this.lateFee = lateFee;
	}
	public int getOverdueDays() {
		return overdueDays;
	}
	public void setOverdueDays(int overdueDays) {
		this.overdueDays = overdueDays;
	}
}
