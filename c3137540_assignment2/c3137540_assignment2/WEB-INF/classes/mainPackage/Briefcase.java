package mainPackage;
/*
 * Amanda Crowley - c3137540
 * Assignment 2 SENG2050
 * Simple class (Java Bean) used to represent briefcase objects that are used in this application
 */

public class Briefcase implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private int caseID = 0;
	private double amount = 0;
	private boolean briefcaseOpen = false;

	public Briefcase() {}
	
	public int getCaseID(){
	      return this.caseID;
	}
	public double getAmount(){
	      return this.amount;
	}
	public boolean isBriefcaseOpen() {
		return this.briefcaseOpen;
	}
	public void setCaseID(int caseNo) {
		this.caseID = caseNo;
	}
	public void setAmount(double amt) {
		this.amount = amt;
	}
	public void setBriefcaseOpen(boolean briefcase) {
		this.briefcaseOpen = briefcase;
	}
}