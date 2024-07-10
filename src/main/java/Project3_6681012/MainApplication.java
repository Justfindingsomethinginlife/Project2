package Project3_6681012;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.Objects;

public class MainApplication extends JFrame 
{
	private int playerCount;
	private ArrayList<PlayerFrame> players;

	public static void main(String []args)
	{	
		//new Welcome();
		new MainApplication();
		new PlayerFrame("NOSCPE");
	}

	public MainApplication()
	{

	}
	public void setPlayerCount(int count)			{ playerCount = count; }

}

interface Myutils
{
	public static String path = "src/main/java/Project3_6681012";
	public static int card_holder_start_x = 250;
	public static int card_holder_space = 100;
	public static int card_holder_y = 600;
	public static int shield_holder_start_x = 1065;
}

class Welcome extends JFrame implements KeyListener 
{
	public Welcome()
	{
		super("hi");
		setSize(1366,768);	
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		addKeyListener(this);
		MyImageIcon bg = new MyImageIcon("src/main/java/Project3_6681012/resources/Welcome_Screen.png").resize(1366, 768);

		JPanel contentpane = (JPanel)getContentPane();
		JLabel background = new JLabel();

		background.setIcon(bg);
		background.setLayout(null);

		contentpane.add(background);
		contentpane.validate();
	}
	
	public void keyPressed(KeyEvent e)			{dispose();}
	public void keyReleased(KeyEvent e)			{}
	public void keyTyped(KeyEvent e)			{}
}

class PlayerFrame extends JFrame implements MouseListener
{	
	private String name;
	private int HP = 100;
	private int MP = 100;
	private int DP = 0;

	private Card activeCard;
	private ArrayList<CardPlace> templateList;
	private PlayerFrame target;

	public PlayerFrame(String name)
	{	
		super(name);
		this.name = name;

		setSize(1366,768);	
		setVisible(true);
		setResizable(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		
		MyImageIcon bg = new MyImageIcon("src/main/java/Project3_6681012/resources/BG.png").resize(getWidth(), getHeight());

		JLabel background = new JLabel();
		templateList = new ArrayList<>();
		
		JPanel contentpane = (JPanel)getContentPane();
		background.setIcon(bg);
		background.setLayout(null);

		JButton btn1 = new JButton("G");
		btn1.setBounds(300,100,50,100);

		background.repaint();

		background.add(btn1);

		Card card1 = new Card("src/main/java/Project3_6681012/resources/Card_TEMP.png",64,96,this);
		card1.setMoveConditions(100, 100, true, true);
		
		Card card2 = new Card("src/main/java/Project3_6681012/resources/Card_template_real.png",64,96,this);
		card2.setMoveConditions(100, 100, true, true);

		Card card3 = new Card("src/main/java/Project3_6681012/resources/Card_template_real.png",64,96,this);
		card3.setMoveConditions(100, 100, true, true);	

		CardPlace holder1 = new CardPlace("src/main/java/Project3_6681012/resources/Place_holder.png",64,96,this);
		holder1.setMoveConditions(250, 600, false, false);	
		
		CardPlace holder2 = new CardPlace("src/main/java/Project3_6681012/resources/Place_holder.png",64,96,this);
		holder2.setMoveConditions(350, 600, false, false);	
		
		CardPlace holder3 = new CardPlace("src/main/java/Project3_6681012/resources/Place_holder.png",64,96,this);
		holder3.setMoveConditions(450, 600, false, false);

		CardPlace holder4 = new CardPlace("src/main/java/Project3_6681012/resources/Place_holder.png",64,96,this);
		holder4.setMoveConditions(550, 600, false, false);

		CardPlace holder5 = new CardPlace("src/main/java/Project3_6681012/resources/Place_holder.png",64,96,this);
		holder5.setMoveConditions(650, 600, false, false);

		CardPlace holder_act_1 = new CardPlace("src/main/java/Project3_6681012/resources/Place_holder.png",64,96,this);
		holder_act_1.setMoveConditions(250, 300, false, false);

		CardPlace holder_act_2 = new CardPlace("src/main/java/Project3_6681012/resources/Place_holder.png",64,96,this);
		holder_act_2.setMoveConditions(350, 300, false, false);

		CardPlace holder_act_3 = new CardPlace("src/main/java/Project3_6681012/resources/Place_holder.png",64,96,this);
		holder_act_3.setMoveConditions(450, 300, false, false);

		CardPlace holder_act_4 = new CardPlace("src/main/java/Project3_6681012/resources/Place_holder.png",64,96,this);
		holder_act_4.setMoveConditions(550, 300, false, false);	

		CardPlace holder_def1 = new CardPlace("src/main/java/Project3_6681012/resources/Place_holder.png",64,96,this);
		holder_def1.setMoveConditions(1065, 600, false, false);

		CardPlace holder_def2 = new CardPlace("src/main/java/Project3_6681012/resources/Place_holder.png",64,96,this);
		holder_def2.setMoveConditions(1245, 600, false, false);

		background.add(card1);
		background.add(card2);
		background.add(card3);

		background.add(holder1);
		background.add(holder2);
		background.add(holder3);
		background.add(holder4);
		background.add(holder5);
		
		background.add(holder_act_1);
		background.add(holder_act_2);
		background.add(holder_act_3);
		background.add(holder_act_4);
		
		background.add(holder_def1);
		background.add(holder_def2);

		templateList.add(holder1);
		templateList.add(holder2);
		templateList.add(holder3);
		templateList.add(holder4);
		templateList.add(holder5);
		templateList.add(holder_def1);
		templateList.add(holder_def2);
		templateList.add(holder_act_1);
		templateList.add(holder_act_2);
		templateList.add(holder_act_3);
		templateList.add(holder_act_4);

		contentpane.add(background, null);
		
		contentpane.validate();
		addMouseListener(this);	
	}

	public void setActiveCard(Card card)			{ activeCard = card; }
	public Card getActiveCard()				{ return activeCard; }

	public void snapCheck()						
	{ 	
		boolean intersectAny = false;
		for(int i=0;i<templateList.size();i++)
		{
			if(templateList.get(i).getBounds().intersects(activeCard.getBounds()))
			{
				if(!templateList.get(i).isOccupied())
				{	
					intersectAny = true;
					templateList.get(i).Occupied(activeCard);
					activeCard.setMoveConditions(templateList.get(i).getX(),templateList.get(i).getY(),true,true);
					
					if(activeCard.getCurSlot() != null)activeCard.getCurSlot().freeSlot();

					activeCard.occupiedSlot(templateList.get(i));
					templateList.get(i).Occupied(activeCard);
					break;
				}
				else
				{	
					intersectAny = true;
					if(activeCard.getCurSlot() != null)
					{
						CardPlace tempC = templateList.get(i);

						if(activeCard.getCurSlot().equals(templateList.get(i))){ activeCard.setMoveConditions(tempC.getX(),tempC.getY(),true,true); break; }

						int tempX = tempC.getX();
						int tempY = tempC.getY();
						Card tempCard = tempC.getCard();

						tempCard.setMoveConditions(activeCard.getCurSlot().getX(),activeCard.getCurSlot().getY(),true,true);
						activeCard.setMoveConditions(tempX,tempY,true,true);

						activeCard.getCurSlot().freeSlot();
					
						tempC.getCard().occupiedSlot(activeCard.getCurSlot());
						activeCard.getCurSlot().Occupied(tempC.getCard());
						activeCard.occupiedSlot(tempC);
						tempC.Occupied(activeCard);

					}
					break;
				}
			}
			else continue;

		}
		if(!intersectAny)
		{
			if(activeCard.getCurSlot() != null)activeCard.setMoveConditions(activeCard.getCurSlot().getX(),activeCard.getCurSlot().getY(),false,false);
		}
	}

	public void mousePressed( MouseEvent e )			{ } 
    	public void mouseEntered( MouseEvent e )			{ }
    	public void mouseExited( MouseEvent e )				{ }
    	public void mouseMoved( MouseEvent e )				{ }
    	public void mouseClicked( MouseEvent e )			{ }
	


    	public void mouseReleased( MouseEvent e )			{ }

}

abstract class BaseLabel extends JLabel
{
    	protected MyImageIcon      iconMain, iconAlt;
   	protected int              curX, curY, width, height;
    	protected boolean          horizontalMove, verticalMove;
    	protected PlayerFrame      parentFrame;

    	public BaseLabel() { }
    	public BaseLabel(String file1, int w, int h, PlayerFrame pf)
    	{
        	width = w; height = h;
        	iconMain = new MyImageIcon(file1).resize(width, height);
		setHorizontalAlignment(JLabel.CENTER);
		setIcon(iconMain);
        	parentFrame = pf;
        	iconAlt = null;
    	}
    
    	public BaseLabel(String file1, String file2, int w, int h, PlayerFrame pf)
    	{
        	this(file1, w, h, pf);
        	iconAlt = new MyImageIcon(file2).resize(width, height);
    	}

    	public void setMoveConditions(boolean hm, boolean vm)
    	{
       		horizontalMove = hm;
        	verticalMove   = vm;
    	}


    	public void setMoveConditions(int x, int y, boolean hm, boolean vm)
    	{
        	curX = x; curY = y;
        	setBounds(curX, curY, width, height);
        	setMoveConditions(hm, vm);
    	}

    	abstract public void updateLocation();
}

class CardPlace extends BaseLabel
{	
	private boolean isOccupied = false;
	private Card occupiedCard;

	private int type;

    	public CardPlace(String file, int w, int h, PlayerFrame pf)
    	{
        	// Alternative icon = null
        	super(file, w, h, pf);
    	}

    	public void updateLocation() { }

    	public void setMainIcon()       		{ setIcon(iconMain); }
    	public void setAltIcon()        		{ setIcon(iconAlt); }

	public void Occupied(Card card)			{ isOccupied = true; occupiedCard = card; }
	public void freeSlot()				{ isOccupied = false; occupiedCard = null; }
	public boolean isOccupied()			{ return isOccupied; }
	public Card getCard()				{ return occupiedCard; }
}

class Card extends BaseLabel implements MouseMotionListener, MouseListener
{
    	private CardPlace curSlot = null;
	
	public Card(String file, int w, int h, PlayerFrame pf)
    	{
        	// Alternative icon = null
        	super(file, w, h, pf);
        	addMouseMotionListener(this);
		addMouseListener(this);
    	}	

    	public void updateLocation()
    	{
    		int f_width = parentFrame.getContentPane().getWidth();
		int f_height = parentFrame.getContentPane().getHeight();

		if(curX+width >= f_width)curX=f_width-width;
		else if(curX <= 0)curX=0;
		if(curY + height >= f_height)curY=f_height-height;
		else if(curY <= 0)curY=0;

		setBounds(curX, curY, width, height);
		setLocation(curX,curY);
    	}

    	public void setMainIcon()       				{ setIcon(iconMain); }
    	public void setAltIcon()        				{ setIcon(iconAlt); }

    	public void mousePressed( MouseEvent e )			{ parentFrame.setActiveCard(this); } 
    	public void mouseEntered( MouseEvent e )			{ }
    	public void mouseExited( MouseEvent e )				{ }
    	public void mouseMoved( MouseEvent e )				{ }
    	public void mouseClicked( MouseEvent e )			{ }
	


    	public void mouseReleased( MouseEvent e )			{ parentFrame.snapCheck(); }

    	public void mouseDragged(MouseEvent e)
    	{
	   	curX=curX - width/2 + e.getX();
	   	curY=curY - height/2 + e.getY();
	   	updateLocation();
    	}

	public void occupiedSlot(CardPlace slot)			{ curSlot = slot; }
	public CardPlace getCurSlot()					{ return curSlot; }
}

class MyImageIcon extends ImageIcon
{
    	public MyImageIcon(String fname)  { super(fname); }
    	public MyImageIcon(Image image)   { super(image); }

    	public MyImageIcon resize(int width, int height)
    	{
		Image oldimg = this.getImage();
		Image newimg = oldimg.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        	return new MyImageIcon(newimg);
    	}
}
