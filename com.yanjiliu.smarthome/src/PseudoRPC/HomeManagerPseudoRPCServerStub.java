package PseudoRPC;

import org.avis.client.*;

public class HomeManagerPseudoRPCServerStub extends Thread{

	private Elvin elvin;
	
	public HomeManagerPseudoRPCServerStub(String elvinURL){
		try {
			elvin = new Elvin(elvinURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
	}
	
	public void exit() {
		elvin.close();
		Thread.currentThread().interrupt();
	}
}