package bussimulator;

import tijdtools.TijdFuncties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class Runner implements Runnable {

	private static HashMap<Integer,ArrayList<Bus>> busStart = new HashMap<Integer,ArrayList<Bus>>();
	private static ArrayList<Bus> actieveBussen = new ArrayList<Bus>();
	private static int interval=1000;
	private static int syncInterval=5;

	private static void addBus(int starttijd, Bus bus){
		ArrayList<Bus> bussen = new ArrayList<Bus>();
		if (busStart.containsKey(starttijd)) {
			bussen = busStart.get(starttijd);
		}
		bussen.add(bus);
		busStart.put(starttijd,bussen);
		bus.setbusID(starttijd);
	}

	private static int startBussen(int tijd){
		for (Bus bus : busStart.get(tijd)){
			actieveBussen.add(bus);
		}
		busStart.remove(tijd);
		return (!busStart.isEmpty()) ? Collections.min(busStart.keySet()) : -1;
	}

	public static void moveBussen(int nu){
		Iterator<Bus> itr = actieveBussen.iterator();
		while (itr.hasNext()) {
			Bus bus = itr.next();
			boolean eindpuntBereikt = bus.move();
			if (eindpuntBereikt) {
				bus.sendLastETA(nu);
				itr.remove();
			}
		}		
	}

	public static void sendETAs(int nu){
		Iterator<Bus> itr = actieveBussen.iterator();
		while (itr.hasNext()) {
			Bus bus = itr.next();
			bus.sendETAs(nu);
		}				
	}

	public static int initBussen(){
		addBusesInBothDirection(Lijnen.LIJN1, Bedrijven.ARRIVA, 3);
		addBusesInBothDirection(Lijnen.LIJN2, Bedrijven.ARRIVA, 5);
		addBusesInBothDirection(Lijnen.LIJN3, Bedrijven.ARRIVA, 4);
		addBusesInBothDirection(Lijnen.LIJN4, Bedrijven.ARRIVA, 6);
		addBusesInBothDirection(Lijnen.LIJN5, Bedrijven.FLIXBUS, 3);
		addBusesInBothDirection(Lijnen.LIJN6, Bedrijven.QBUZZ, 5);
		addBusesInBothDirection(Lijnen.LIJN7, Bedrijven.QBUZZ, 4);
		addBusesInBothDirection(Lijnen.LIJN1, Bedrijven.ARRIVA, 6);
		addBusesInBothDirection(Lijnen.LIJN4, Bedrijven.ARRIVA, 12);
		addBusesInBothDirection(Lijnen.LIJN5, Bedrijven.FLIXBUS, 10);
		addBusesInBothDirection(Lijnen.LIJN8, Bedrijven.QBUZZ, 3);
		addBusesInBothDirection(Lijnen.LIJN8, Bedrijven.QBUZZ, 5);
		addBusesInBothDirection(Lijnen.LIJN3, Bedrijven.ARRIVA, 14);
		addBusesInBothDirection(Lijnen.LIJN4, Bedrijven.ARRIVA, 16);
		addBusesInBothDirection(Lijnen.LIJN5, Bedrijven.FLIXBUS, 13);
		return Collections.min(busStart.keySet());
	}

	private static void addBusesInBothDirection(Lijnen lijn, Bedrijven bedrijf, int starttijd) {
		addBus(starttijd, new Bus(lijn, bedrijf, 1));
		addBus(starttijd, new Bus(lijn, bedrijf, -1));
	}

	@Override
	public void run() {
		int tijd=0;
		int counter=0;
		TijdFuncties tijdFuncties = new TijdFuncties();
		tijdFuncties.initSimulatorTijden(interval,syncInterval);
		int volgende = initBussen();
		while ((volgende>=0) || !actieveBussen.isEmpty()) {
			counter=tijdFuncties.getCounter();
			tijd=tijdFuncties.getTijdCounter();
			System.out.println("De tijd is:" + tijdFuncties.getSimulatorWeergaveTijd());
			volgende = (counter==volgende) ? startBussen(counter) : volgende;
			moveBussen(tijd);
			sendETAs(tijd);
			try {
				tijdFuncties.simulatorStep();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
