// KASIDECH THONGKING 6681012
// Don't forget to rename the package
package Ex8_6681012;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class MainApplication extends JFrame implements KeyListener
{
    private JLabel          contentpane;
    private CharacterLabel  []petLabels;
    private CharacterLabel  activeLabel;
    private ItemLabel       wingLabel;

    private int framewidth   = MyConstants.FRAMEWIDTH;
    private int frameheight  = MyConstants.FRAMEHEIGHT;
    private int groundY      = MyConstants.GROUND_Y;
    private int skyY         = MyConstants.SKY_Y;
    private int bridgeLeftX  = MyConstants.BRIDGE_LEFT;
    private int bridgeRightX = MyConstants.BRIDGE_RIGHT;

    public static void main(String[] args)
    {
	new MainApplication();
    }

    public MainApplication()
    {
	setSize(framewidth, frameheight);
        setLocationRelativeTo(null);
	setVisible(true);
	setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );

	// set background image by using JLabel as contentpane
	setContentPane(contentpane = new JLabel());
	MyImageIcon background = new MyImageIcon(MyConstants.FILE_BG).resize(framewidth, frameheight);
	contentpane.setIcon( background );
	contentpane.setLayout( null );

        petLabels = new CharacterLabel[2];
	petLabels[0] = new CharacterLabel(MyConstants.FILE_DOG_1, MyConstants.FILE_DOG_2,
                                          120, 100, this);
        petLabels[0].setMoveConditions(bridgeLeftX-120, groundY, true, false);

        petLabels[1] = new CharacterLabel(MyConstants.FILE_CAT_1, MyConstants.FILE_CAT_2,
                                          120, 100, this);
        petLabels[1].setMoveConditions(bridgeRightX, groundY, true, false);

        wingLabel = new ItemLabel(MyConstants.FILE_WING, 100, 80, this);
        wingLabel.setMoveConditions(bridgeRightX+300, skyY, true, true);


        // first added label is at the front, last added label is at the back
        contentpane.add( wingLabel );
        contentpane.add( petLabels[0] );
        contentpane.add( petLabels[1] );
	addKeyListener(this);

        setDog();
	repaint();

	}

    	@Override
    	public void keyTyped(KeyEvent e)
	{
		//System.out.println(e.getKeyChar());
		switch(e.getKeyChar())
		{
			case 'D':
				setDog();
				break;
			case 'C':
				setCat();
				break;
			case 'd':
				setDog();
				break;
			case 'c':
				setCat();
				break;

		}
		e.consume();

    	}

	@Override
    	public void keyPressed(KeyEvent e)
	{
		 switch(e.getKeyCode())
		 {
			case KeyEvent.VK_RIGHT:
				activeLabel.moveRight();
				break;
			case KeyEvent.VK_LEFT:
				activeLabel.moveLeft();
				break;
			case KeyEvent.VK_UP:
				activeLabel.moveUp();
				break;
			case KeyEvent.VK_DOWN:
				activeLabel.moveDown();
				break;
			case KeyEvent.VK_J:
				activeLabel.jump();
				break;
			case KeyEvent.VK_ESCAPE:
				if(!activeLabel.getVertical())break;
				activeLabel.setMoveConditions(activeLabel.getCurX(), groundY, true, false);
				activeLabel.setMainIcon();
        			wingLabel.setMoveConditions(activeLabel.getCurX(), skyY, true, true);
				wingLabel.setMainIcon();
				break;
		 }
    	}

	@Override
    	public void keyReleased(KeyEvent e)
	{
    	}

    	public CharacterLabel getActiveLabel()  { return activeLabel; }
    	public void setDog()                    { activeLabel = petLabels[0]; setTitle("Dog is active"); }
	public void setCat()			{ activeLabel = petLabels[1]; setTitle("Cat is active"); }
	public JLabel getActivelPet()		{ return activeLabel; }

}

////////////////////////////////////////////////////////////////////////////////
abstract class BaseLabel extends JLabel
{
    protected MyImageIcon      iconMain, iconAlt;
    protected int              curX, curY, width, height;
    protected boolean          horizontalMove, verticalMove;
    protected MainApplication  parentFrame;

    // Constructors
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

    // Common methods
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

////////////////////////////////////////////////////////////////////////////////
class CharacterLabel extends BaseLabel
{
    public CharacterLabel(String file1, String file2, int w, int h, MainApplication pf)
    {
        // Main icon without wings, alternative icon with wings
        super(file1, file2, w, h, pf);
    }

    public void updateLocation()
    {
	int f_width = parentFrame.getContentPane().getWidth();
	int f_height = parentFrame.getContentPane().getHeight();

	if(curX+width >= f_width)curX=0;
	else if(curX <= 0)curX=f_width-width;
	if(curY + height >= f_height)curY=f_height-height;
	else if(curY <= 0)curY=0;

	setBounds(curX, curY, width, height);
	setLocation(curX,curY);
    }

    public void setMainIcon()       		{ setIcon(iconMain); }
    public void setAltIcon()        		{ setIcon(iconAlt); }

    public void moveUp()            		{ if(verticalMove)curY -= 0.1*height; updateLocation(); }
    public void moveDown()          		{ if(verticalMove)curY += 0.1*height; updateLocation(); }
    public void moveLeft()          		{ if(horizontalMove)curX -= 0.1*width; updateLocation(); }
    public void moveRight()         		{ if(horizontalMove)curX += 0.1*width; updateLocation(); }
    public void jump()
    {
    	if((curX < MyConstants.BRIDGE_LEFT && curX > MyConstants.BRIDGE_RIGHT) || verticalMove);
	else if(curX+width < MyConstants.BRIDGE_LEFT) curX = MyConstants.BRIDGE_RIGHT;
	else if(curX > MyConstants.BRIDGE_RIGHT) curX = MyConstants.BRIDGE_LEFT-width;
	updateLocation();
    }

    public int getCurX()	    		{ return curX; }
    public int getCurY()	    		{ return curY; }
    public boolean getHorizontal()  		{ return horizontalMove; }
    public boolean getVertical()	   	{ return verticalMove; }
}

////////////////////////////////////////////////////////////////////////////////
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

	if ( this.getBounds().intersects(parentFrame.getActiveLabel().getBounds()) )
	{
		CharacterLabel tmp = parentFrame.getActiveLabel();
		tmp.setMoveConditions(tmp.getHorizontal(),true);
		tmp.setAltIcon();
		setAltIcon();
	}

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
