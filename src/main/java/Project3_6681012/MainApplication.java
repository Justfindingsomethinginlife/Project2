package Project3_6681012;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainApplication extends JFrame //implement 
{

	public static void main(String []args)
	{
		new MainApplication();
	}

	public MainApplication()
	{
		super("hi");
		setSize(1366,768);	
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		
		MyImageIcon bg = new MyImageIcon("src/main/java/Project3_6681012/resources/BG.png").resize(1366, 768);

	        JPanel contentpane = (JPanel)getContentPane();
		JLabel background = new JLabel();
		JLabel game = new JLabel();
		
		background.setIcon(bg);
		background.setLayout(null);

		background.repaint();


		
		ItemLabel l = new ItemLabel("src/main/java/Project3_6681012/resources/Apple_dummy.png",64,96,this);
		l.setMoveConditions(100, 100, true, true);
		game.add(l);

		game.repaint();

		contentpane.add(game);
		contentpane.add(background, null);
		
		contentpane.validate();

	}
}

abstract class BaseLabel extends JLabel
{
    protected MyImageIcon      iconMain, iconAlt;
    protected int              curX, curY, width, height;
    protected boolean          horizontalMove, verticalMove;
    protected MainApplication  parentFrame;

    public BaseLabel() { }
    public BaseLabel(String file1, int w, int h, MainApplication pf)
    {
        width = w; height = h;
        iconMain = new MyImageIcon(file1).resize(width, height);
	setHorizontalAlignment(JLabel.CENTER);
	setIcon(iconMain);
        parentFrame = pf;
        iconAlt = null;
    }
    
    public BaseLabel(String file1, String file2, int w, int h, MainApplication pf)
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

class ItemLabel extends BaseLabel implements MouseMotionListener
{
    public ItemLabel(String file, int w, int h, MainApplication pf)
    {
        // Alternative icon = null
        super(file, w, h, pf);
        addMouseMotionListener(this);
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

    public void setMainIcon()       		{ setIcon(iconMain); }
    public void setAltIcon()        		{ setIcon(iconAlt); }

    public void mouseMoved(MouseEvent e) { }

    public void mouseDragged(MouseEvent e)
    {
	   curX=curX - width/2 + e.getX();
	   curY=curY - height/2 + e.getY();
	   updateLocation();
    }
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
