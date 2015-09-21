
package game;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface GameListener 
{
    public void update();
    public void processKeyEvent(KeyEvent e);
    public void processMouseEvent(MouseEvent e);
}
