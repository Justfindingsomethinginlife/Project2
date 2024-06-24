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
		
		shopList.add(new DeliveryShop());
		shopList.add(new DeliveryShop());

		AtomicBoolean state = new AtomicBoolean(false);

		s1.setShop(shopList);
		s1.setSusState(state);
		s1.setMaxDay(4);
	
		
		s2.setShop(shopList);
		s2.setSusState(state);
		s2.setMaxDay(4);
		

		s1.start();	
		s2.start();

		synchronized(System.out)
		{	
			System.out.println("Day 1");
		}

		for(int i=0;i<1000000;i++)
		{	
			s1.wake();
			s2.wake();
		}


		synchronized(System.out)
		{	
			System.out.println("Day 2");
		}
			
		for(int i=0;i<1000000;i++)
		{	
			s1.wake();
			s2.wake();
		}
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
	private AtomicBoolean isSuspended;
	private int round = 1;
	private int sim_day;
	private int max_parcel;

	private ArrayList<DeliveryShop> sharedShop;
	private CyclicBarrier barrier;

	public void setShop(ArrayList<DeliveryShop> shop)	{ sharedShop = shop; }
	public void setSusState(AtomicBoolean state)		{ isSuspended = state; }
	public void setMaxDay(int day)				{ sim_day = day; }
	public void setParcel(int max_parcel)			{ this.max_parcel = max_parcel; }	

	@Override
	public void run()
	{
		while(round < sim_day)
		{
			while(!isSuspended.get())
			{	synchronized(this)
				{
					try { wait(); }catch (InterruptedException e){isSuspended.compareAndSet(false,true);}
				}
			}

			sharedShop.get(new Random().nextInt(1,sharedShop.size())).addParcel(10);
			round++;
			isSuspended.compareAndSet(true,false);
		}
	}

	public synchronized void wake()
	{
		isSuspended.compareAndSet(false,true);
		notify();
	}
}

class DeliveryShop
{
	private int parcel = 0;
	
	public synchronized void addParcel(int max_parcel)
	{	
		SellerThread me = (SellerThread)Thread.currentThread();
		int d_parcel = new Random().nextInt(1,max_parcel);
		parcel += d_parcel;
		System.out.println(me.getName() + " " + d_parcel + " " + parcel);
	}
	public void subParcel()
	{
	
	}
}
