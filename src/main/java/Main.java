import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CyclicBarrier;
import java.util.ArrayList;

public class Main
{
	public static void main(String[] args)
	{	
		File fp = new File("src/main/java/config_1.txt");
		
		try
		{
			Scanner fr = new Scanner(fp);
			System.out.println(fr.nextLine());
			fr.close();
		}catch (Exception e){}

		ArrayList<DeliveryShop> shopList = new ArrayList<>();

		SellerThread s1 = new SellerThread();
		SellerThread s2 = new SellerThread();

		DeliveryThread d1 = new DeliveryThread();

		shopList.add(new DeliveryShop());
		shopList.add(new DeliveryShop());

		AtomicBoolean instate = new AtomicBoolean(true);
		AtomicBoolean outstate = new AtomicBoolean(true);

		CyclicBarrier barrier = new CyclicBarrier(2);
		
		d1.setShop(shopList.get(1));
		d1.setMyState(instate);
		d1.setOtherState(outstate);
		d1.setMaxDay(2);

		s1.setShop(shopList);
		s1.setMyState(instate);
		s1.setOtherState(outstate);
		s1.setMaxDay(2);
	
		
		s2.setShop(shopList);
		s2.setMyState(instate);
		s2.setOtherState(outstate);
		s2.setMaxDay(2);
		

		s1.start();	
		s2.start();
		try{s1.join();s2.join();}catch(Exception e){}
		d1.start();

					
	}
}

class Fleet
{
	private int number;
	private int max_load;
	private String type;

	public void setNumber(int number)			{ this.number = number; }
	public void setMaxLoad(int max_load)			{ this.max_load = max_load; }
	public void setType(String type)			{ this.type = type; }
	
	public int getNumber()					{ return number; }
	public int getMaxLoad()					{ return max_load; }
	public String getType()					{ return type; }
}

class SellerThread extends Thread
{
	private AtomicBoolean Seller;
	private AtomicBoolean Deliver;

	private int round = 1;
	private int sim_day;
	private int max_parcel;

	private ArrayList<DeliveryShop> sharedShop;
	private CyclicBarrier barrier;

	public void setShop(ArrayList<DeliveryShop> shop)	{ sharedShop = shop; }
	public void setMyState(AtomicBoolean state)		{ Seller  = state; }
	public void setOtherState(AtomicBoolean state)		{ Deliver = state; }
	public void setMaxDay(int day)				{ sim_day = day; }
	public void setBarrier(CyclicBarrier br)		{ barrier = br; }
	public void setParcel(int max_parcel)			{ this.max_parcel = max_parcel; }

	public int getCurrentDay()				{ return round; }	

	@Override
	public void run()
	{
		while(round < sim_day)
		{
			while(!Seller.get())
			{	synchronized(this)
				{
					try { wait(); }catch (InterruptedException | IllegalMonitorStateException e){Seller.compareAndSet(false,true);}
				}
			}

			sharedShop.get(new Random().nextInt(0,sharedShop.size())).addParcel(10);
			round++;
			//Seller.compareAndSet(true,false);
			Deliver.compareAndSet(false,true);
			
		}
	}
}

class DeliveryThread extends Thread
{
	private AtomicBoolean Deliver;
	private AtomicBoolean Seller;

	private int round = 1;
	private int sim_day;
	
	private CyclicBarrier barrier;
	private DeliveryShop managedShop;

	public void setShop(DeliveryShop shop)			{ managedShop = shop; }
	public void setMyState(AtomicBoolean state)		{ Deliver = state; }
	public void setOtherState(AtomicBoolean state)		{ Seller = state; }
	public void setMaxDay(int day)				{ sim_day = day; }

	public int getCurrentDay()				{ return round; }	

	
	@Override
	public void run()
	{
		while(round < sim_day)
		{
			while(!Deliver.get())
			{	synchronized(this)
				{
					try { wait(); }catch (InterruptedException e){Deliver.compareAndSet(false,true);}
				}
			}
			managedShop.report();
			System.out.println(getName() + "  >>  " + round);
			round++;
			Deliver.compareAndSet(true,false);
			Seller.compareAndSet(false,true);
		}
	}

}

class DeliveryShop
{
	private int parcel = 0;
	private AtomicInteger sharedCourier;
	private int max_load;
	private String type;
	
	public synchronized void addParcel(int max_parcel)
	{	
		SellerThread me = (SellerThread)Thread.currentThread();
		int d_parcel = new Random().nextInt(1,max_parcel);
		parcel += d_parcel;
		System.out.println(me.getName() + " " + d_parcel + " " + parcel + " " +me.getCurrentDay());
	}

	public synchronized void subParcel()
	{
		DeliveryThread me = (DeliveryThread)Thread.currentThread();

	}

	public synchronized void report()
	{
		DeliveryThread me = (DeliveryThread)Thread.currentThread();
		System.out.println(me.getName() + "  >>  parcel to deliver = " + parcel); 
	}
}
