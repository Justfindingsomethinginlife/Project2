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

		mainController mainapp = new mainController();

		ArrayList<DeliveryShop> shopList = new ArrayList<>();

		SellerThread s1 = new SellerThread();
		SellerThread s2 = new SellerThread();

		DeliveryThread d1 = new DeliveryThread();
		DeliveryThread d2 = new DeliveryThread();		
		
		shopList.add(new DeliveryShop());
		shopList.add(new DeliveryShop());

		AtomicBoolean instate = new AtomicBoolean(false);
		AtomicBoolean outstate = new AtomicBoolean(false);

		CyclicBarrier dropFinish = new CyclicBarrier(2);
		CyclicBarrier deliveryFinish = new CyclicBarrier(2);
		
		
		d1.setShop(shopList.get(0));
		d1.setOutState(outstate);
		d1.setInState(instate);
		d1.setBarrier(deliveryFinish);
		d1.setController(mainapp);
		d1.setMaxDay(3);

		d2.setShop(shopList.get(1));
		d2.setOutState(outstate);
		d2.setInState(instate);
		d2.setBarrier(deliveryFinish);
		d2.setMaxDay(3);
		

		s1.setShop(shopList);
		s1.setInState(instate);
		s1.setOutState(outstate);
		s1.setBarrier(dropFinish);
		d2.setController(mainapp);
		s1.setMaxDay(3);
	
		
		s2.setShop(shopList);
		s2.setInState(instate);
		s2.setOutState(outstate);
		s2.setBarrier(dropFinish);
		s2.setMaxDay(3);
		

		s1.start();	
		s2.start();

		d1.start();
		d2.start();

		int round = 1;

		while(round < 3)
		{
			mainapp.day(instate,outstate,round);
			round++;
			instate.compareAndSet(false,true);
			for(int i=0;i<shopList.size();i++)shopList.get(i).wake();

		}
	
		try{ s1.join(); s2.join(); }catch(Exception e){}
		System.out.println(s1.isRunning());
		mainapp.wake();
					
	}

}

class mainController
{
	public void day(AtomicBoolean inRunning, AtomicBoolean outRunning, int day)
	{
		while(inRunning.get() || outRunning.get())
		{
			synchronized(this)
			{
				try{ wait(); }catch (Exception e) {}
			}
		}
		System.out.println("Day " + day);
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

	public void setNumber(int number)			{ this.number = number; }
	public void setMaxLoad(int max_load)			{ this.max_load = max_load; }
	public void setType(String type)			{ this.type = type; }
	
	public int getNumber()					{ return number; }
	public int getMaxLoad()					{ return max_load; }
	public String getType()					{ return type; }
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

	public void setShop(ArrayList<DeliveryShop> shop)	{ sharedShop = shop; }
	public void setInState(AtomicBoolean state)		{ inRunning  = state; }
	public void setOutState(AtomicBoolean state)		{ outRunning = state; }
	public void setMaxDay(int day)				{ sim_day = day; }
	public void setBarrier(CyclicBarrier br)		{ barrier = br; }
	public void setParcel(int max_parcel)			{ this.max_parcel = max_parcel; }

	public int getCurrentDay()				{ return round; }
	public Boolean isRunning()			{ return inRunning.get();}	

	@Override
	public void run()
	{
		while(round < sim_day)
		{	
			sharedShop.get(new Random().nextInt(0,sharedShop.size())).addParcel(10);
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
	
	private CyclicBarrier barrier;
	private DeliveryShop managedShop;
	private mainController main;

	public void setShop(DeliveryShop shop)			{ managedShop = shop; }
	public void setBarrier(CyclicBarrier br)		{ barrier = br; }
	public void setInState(AtomicBoolean state)		{ inRunning = state; }
	public void setOutState(AtomicBoolean state)		{ outRunning = state; }
	public void setMaxDay(int day)				{ sim_day = day; }
	public void setController(mainController m)		{ main = m; }

	public int getCurrentDay()				{ return round; }
	public Boolean isRunning()				{ return outRunning.get(); }

	
	@Override
	public void run()
	{
		while(round < sim_day)
		{	
			managedShop.report();

			try{ barrier.await(); }catch(Exception e){}
			//managedShop.subParcel();
			
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
	private Fleet sharedFleet;
	private String type;
	
	synchronized public void addParcel(int max_parcel)
	{	
		SellerThread me = (SellerThread)Thread.currentThread();
		
		while (!me.isRunning())
		{
           	 	//System.out.printf("%s >> waits \n\n", me.getName());
            		try { wait(); } catch(Exception e) { }
		}
		
		//System.out.printf("%s >> is released \n\n", me.getName());	
		
		int d_parcel = new Random().nextInt(1,max_parcel);
		parcel += d_parcel;
		System.out.println(me.getName() + "  >>  dropped " + d_parcel + " total parcel: " + parcel + " day: " + me.getCurrentDay() + " " + me.isRunning() );
	}

	synchronized public void wake()
	{
		notifyAll();
	}

	synchronized public void subParcel()
	{
		DeliveryThread me = (DeliveryThread)Thread.currentThread();

	}

	synchronized public void report()
	{
		DeliveryThread me = (DeliveryThread)Thread.currentThread();
		
		while (!me.isRunning())
		{
           	 	//System.out.printf("%s >> waits \n\n", me.getName());
            		try { wait(); } catch(Exception e) { }
		}
		
		//System.out.printf("%s >> is released \n\n", me.getName());	
		
		System.out.println(me.getName() + "  >>  parcel to deliver: " + parcel);

		if(!me.isRunning())notifyAll();
	}
}
