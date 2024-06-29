package Project2_6681012;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.CyclicBarrier;
import java.util.ArrayList;
import java.util.Collections;

public class Main
{
	public static void main(String[] args)
	{	
           new Main().demo_1(); 
	}
        
        public void demo_1() 
        {
            Scanner keyboard = new Scanner(System.in);
            String path = "src/main/java/Project2_6681012/";
            String filename = "config_1.txt";

            int sim_days = 0;
            int bike_max_load = 0, bike_num = 0;
            int truck_max_load = 0, truck_num = 0;
            int seller_max_drop = 0, seller_num = 0;
            int bikes_delivery = 0, truck_delivery = 0;

            Boolean opensuccess = false;
            while (!opensuccess) {
                try (
                        Scanner fscan = new Scanner(new File(path + filename));) {
                    opensuccess = true;
                    while (fscan.hasNextLine()) {
                        String ln = fscan.nextLine();
                        String[] col = ln.split(",");
                        switch (col[0].trim()) {
                            case "days":
                                sim_days = Integer.parseInt(col[1].trim());
                                break;
                            case "bike_num_maxload":
                                bike_num = Integer.parseInt(col[1].trim());
                                bike_max_load = Integer.parseInt(col[2].trim());
                                break;
                            case "truck_num_maxload":
                                truck_num = Integer.parseInt(col[1].trim());
                                truck_max_load = Integer.parseInt(col[2].trim());
                                break;
                            case "seller_num_maxdrop":
                                seller_num = Integer.parseInt(col[1].trim());
                                seller_max_drop = Integer.parseInt(col[2].trim());
                                break;
                            case "delivery_bybike_bytruck":
                                bikes_delivery = Integer.parseInt(col[1].trim());
                                truck_delivery = Integer.parseInt(col[2].trim());
                                break;
                        }
                    }
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                    System.out.println("New file name = ");
                    filename = keyboard.next();
                }
            }

            //System.out.println(sim_days + " " + bike_max_load + " " + bike_num + " " + truck_max_load + " " + truck_num + " " + seller_max_drop + " " + seller_num);
            Fleet BikeFleet = new Fleet(bike_num, bike_max_load, "bike");
            Fleet TruckFleet = new Fleet(truck_num, truck_max_load, "truck");

            mainController mainapp = new mainController();

            AtomicBoolean instate = new AtomicBoolean(false);
            AtomicBoolean outstate = new AtomicBoolean(false);

            CyclicBarrier dropFinish = new CyclicBarrier(seller_num);
            CyclicBarrier deliveryFinish = new CyclicBarrier(bikes_delivery + truck_delivery);

            ArrayList<DeliveryShop> shopList = new ArrayList<>();
            ArrayList<SellerThread> sellerList = new ArrayList<>();
            ArrayList<DeliveryThread> deliveryList = new ArrayList<>();

            for (int i = 0; i < bikes_delivery; i++) {
                shopList.add(new DeliveryShop(BikeFleet, BikeFleet.getType()));
            }
            for (int i = 0; i < truck_delivery; i++) {
                shopList.add(new DeliveryShop(TruckFleet, TruckFleet.getType()));
            }
                        
            for (int i = 0; i < seller_num; i++) {
                SellerThread s_tmp = new SellerThread("Seller_" + i);
                s_tmp.setShop(shopList);
                s_tmp.setInState(instate);
                s_tmp.setOutState(outstate);
                s_tmp.setBarrier(dropFinish);
                s_tmp.setMaxDay(sim_days);
                s_tmp.setMaxParcel(seller_max_drop);
                sellerList.add(s_tmp);
            }

            int bike_count = 0;
            int truck_count = 0;

            for (int i = 0; i < shopList.size(); i++) {
                int index = 0;
                String tmp = "";
                if (shopList.get(i).getType().equals("bike")) {
                    index = bike_count;
                    tmp = "Bike";
                    bike_count++;
                } else if (shopList.get(i).getType().equals("truck")) {
                    index = truck_count;
                    tmp = "Truck";
                    truck_count++;
                }
                DeliveryThread d_tmp = new DeliveryThread(tmp + "Delivery_" + index);
                d_tmp.setShop(shopList.get(i));
                d_tmp.setOutState(outstate);
                d_tmp.setInState(instate);
                d_tmp.setBarrier(deliveryFinish);
                d_tmp.setController(mainapp);
                d_tmp.setMaxDay(sim_days);
                shopList.get(i).setName(d_tmp.getName());

                deliveryList.add(d_tmp);
            }

            System.out.printf("%20s  >>  %10s Parameters %10s\n", Thread.currentThread().getName(), "=".repeat(20), "=".repeat(20));
            System.out.printf("%20s  >>  days of simulation = %d\n", Thread.currentThread().getName(), sim_days);
            System.out.printf("%20s  >>  %-5s Fleet total bikes  = %3d, max load = %3d parcels, min load = %3d parcels\n", Thread.currentThread().getName(), "Bike", BikeFleet.getNumber(), BikeFleet.getMaxLoad(), BikeFleet.getMaxLoad() / 2);
            System.out.printf("%20s  >>  %-5s Fleet total bikes  = %3d, max load = %3d parcels, min load = %3d parcels\n", Thread.currentThread().getName(), "Truck", TruckFleet.getNumber(), TruckFleet.getMaxLoad(), TruckFleet.getMaxLoad() / 2);

            {
                String ln = "[";
                for (int i = 0; i < seller_num; i++) {
                    ln += sellerList.get(i).getName();
                    ln += ", ";
                }
                ln = ln.substring(0, ln.length() - 2);
                ln += "]";
                System.out.printf("%20s  >>  %-15s = %s\n", Thread.currentThread().getName(), "SellerThreads", ln);
            }
            System.out.printf("%20s  >>  %-15s = %3d\n", Thread.currentThread().getName(), "max_parcel_drop", seller_max_drop);

            {
                String ln = "[";
                for (int i = 0; i < deliveryList.size(); i++) {
                    ln += deliveryList.get(i).getName();
                    ln += ", ";
                }
                ln = ln.substring(0, ln.length() - 2);
                ln += "]";
                System.out.printf("%20s  >>  %-15s = %s\n", Thread.currentThread().getName(), "DeliveryThreads", ln);
            }
                
            Collections.shuffle(shopList);
            
            for (int i = 0; i < deliveryList.size(); i++) {
                deliveryList.get(i).start();
            }
            for (int i = 0; i < sellerList.size(); i++) {
                sellerList.get(i).start();
            }

            int round = 1;

            while (round <= sim_days) {
                mainapp.day(instate, outstate, round);
                round++;
                instate.compareAndSet(false, true);
                BikeFleet.setNumber(bike_num);
                TruckFleet.setNumber(truck_num);
                for (int i = 0; i < shopList.size(); i++) {
                    shopList.get(i).wake();
                }
            }

            try {
                for (DeliveryThread dThread : deliveryList) {
                    dThread.join();
                }
                for (SellerThread sThread : sellerList) {
                    sThread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("%20s  >>  \n", Thread.currentThread().getName());
            System.out.printf("%20s  >>  %20s\n", Thread.currentThread().getName(), "=".repeat(52));
            System.out.printf("%20s  >>  Summary \n", Thread.currentThread().getName());
            for (DeliveryThread dThread : deliveryList) {
                System.out.printf("%20s  >>  %-20s  %10s %5d, %s %5d, %s  %.2f\n", Thread.currentThread().getName(), dThread.getName(), "Received:", dThread.getParcelsReceived(), "Delivered:", dThread.getParcelsDelivered(), "Success rate:", dThread.getParcelsReceived() == 0 ? 0.0 : ((double) dThread.getParcelsDelivered() / dThread.getParcelsReceived()));
            }

    }

}

class mainController
{
	public void day(AtomicBoolean inRunning, AtomicBoolean outRunning, int day)
	{
		Thread me = Thread.currentThread();
		while(inRunning.get() || outRunning.get())
		{
			synchronized(this)
			{
				try{ wait(); }catch (Exception e) {}
			}
		}

		System.out.printf("%20s  >>  \n", me.getName());
		System.out.printf("%20s  >>  Day %d\n", me.getName(), day);
		System.out.printf("%20s  >>  %20s\n", me.getName(),"=".repeat(52));
	
	}

	synchronized public void wake()
	{
		notifyAll();
	}

}

class Fleet
{
	private int number;
	private int max_load;
	private String type;

	public Fleet(int num, int load, String type)		{ number = num; max_load = load; this.type = type; }

	public void setNumber(int number)			{ this.number = number; }
	public void setMaxLoad(int max_load)			{ this.max_load = max_load; }
	public void setType(String type)			{ this.type = type; }
	
	public int getNumber()					{ return number; }
	public int getMaxLoad()					{ return max_load; }
	public String getType()					{ return type; }

	public int allocateDelivery(int parcel)
	{	
		int delivered = (parcel/max_load);

		if(parcel%max_load >= max_load/2)delivered++;
		
		//System.out.println(delivered);
		if(delivered <= number)number -= delivered;
                else 
                {
                    int tmp = number;
                    number = 0;
                    return tmp*max_load;
                }
		if(delivered*max_load > parcel)return parcel;
		
		return delivered*max_load;
	}
}

class SellerThread extends Thread
{
	private AtomicBoolean inRunning;
	private AtomicBoolean outRunning;

	private int round = 1;
	private int sim_day;
	private int max_parcel;

	private ArrayList<DeliveryShop> sharedShop;
	private CyclicBarrier barrier;

	public SellerThread(String name)			{ super(name); }

	public void setShop(ArrayList<DeliveryShop> shop)	{ sharedShop = shop; }
	public void setInState(AtomicBoolean state)		{ inRunning  = state; }
	public void setOutState(AtomicBoolean state)		{ outRunning = state; }
	public void setMaxDay(int day)				{ sim_day = day; }
	public void setBarrier(CyclicBarrier br)		{ barrier = br; }
	public void setMaxParcel(int max_parcel)		{ this.max_parcel = max_parcel; }

	public int getCurrentDay()				{ return round; }
	public Boolean isRunning()				{ return inRunning.get(); }	

	@Override
	public void run()
	{
		while(round <= sim_day)
		{	
			sharedShop.get(new Random().nextInt(0,sharedShop.size())).addParcel(max_parcel);
			round++;

			try { barrier.await(); }catch(Exception e){};
			inRunning.compareAndSet(true,false);
			outRunning.compareAndSet(false,true);
			for(int i=0;i<sharedShop.size();i++)sharedShop.get(i).wake();
		}
	}
}

class DeliveryThread extends Thread
{
	private AtomicBoolean inRunning;
	private AtomicBoolean outRunning;

	private int round = 1;
	private int sim_day;
        
        private int parcelsReceived = 0;
        private int parcelsDelivered = 0;
	
	private CyclicBarrier barrier;
	private DeliveryShop managedShop;
	private mainController main;

	public DeliveryThread(String name)			{ super(name); }

	public void setShop(DeliveryShop shop)			{ managedShop = shop; }
	public void setBarrier(CyclicBarrier br)		{ barrier = br; }
	public void setInState(AtomicBoolean state)		{ inRunning = state; }
	public void setOutState(AtomicBoolean state)		{ outRunning = state; }
	public void setMaxDay(int day)				{ sim_day = day; }
	public void setController(mainController m)		{ main = m; }

	public int getCurrentDay()				{ return round; }
	public Boolean isRunning()				{ return outRunning.get(); }
        
        public int getParcelsReceived() {
            return parcelsReceived;
        }

        public int getParcelsDelivered() {
            return parcelsDelivered;
        }

	@Override
	public void run()
	{
		while(round <= sim_day)
		{	
			managedShop.report();

			try{ barrier.await(); }catch(Exception e){}
			parcelsReceived += managedShop.getParcelCount();
                        managedShop.subParcel();
                        parcelsDelivered += managedShop.getDeliveredCount();
			
			round++;
			try{ barrier.await(); }catch(Exception e){}
			

			//inRunning.compareAndSet(false,true);
			outRunning.compareAndSet(true,false);
			managedShop.wake();
			main.wake();
		}
	}
}

class DeliveryShop
{
	private int parcel = 0;
        private int deliveredCount;
	private Fleet sharedFleet;
	private String type;
	private String name;

	public String getType()					{ return sharedFleet.getType(); }
	
	public void setName(String name)			{ this.name = name; }

	public DeliveryShop(Fleet ft, String type)
	{
		sharedFleet = ft;
		this.type   = type;
	}
	
	synchronized public void addParcel(int max_parcel)
	{	
		SellerThread me = (SellerThread)Thread.currentThread();
		
		while (!me.isRunning())
		{
            		try { wait(); } catch(Exception e) { }
		}
			
		int d_parcel = new Random().nextInt(1,max_parcel);
		parcel += d_parcel;
		System.out.printf("%20s  >>  drop %3d parcels at %-20s shop\n", me.getName(), d_parcel, name);
	}

	synchronized public void wake()
	{
		notifyAll();
	}

	synchronized public void subParcel()
	{
		DeliveryThread me = (DeliveryThread)Thread.currentThread();
		int d_parcel = sharedFleet.allocateDelivery(parcel);
		parcel -= d_parcel;
                deliveredCount = d_parcel;       
		System.out.printf("%20s  >>  deliver %3d parcels by %3d %-5s     remaining parcels = %3d, remaining %-5s = %3d\n",me.getName(), d_parcel, (d_parcel + sharedFleet.getMaxLoad() -1)/sharedFleet.getMaxLoad(), sharedFleet.getType(), parcel, sharedFleet.getType(), sharedFleet.getNumber());
	}

	synchronized public void report()
	{
		DeliveryThread me = (DeliveryThread)Thread.currentThread();
		
		while (!me.isRunning())
		{
            		try { wait(); } catch(Exception e) { }
		}
		System.out.printf("%20s  >>      parcels to deliver = %3d\n",me.getName(), parcel);
	}
        
        synchronized public int getParcelCount() {
            return parcel;
        }

        synchronized public int getDeliveredCount() {
            return deliveredCount;
        }
}
