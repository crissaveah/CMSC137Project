
package game;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public final class GameSurface extends JPanel
{   
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        if(GameEngine.getInstance() != null && GameEngine.getInstance().isRunning())
        {
            Graphics2D graphics = (Graphics2D) g;
            graphics.drawImage(GameEngine.getInstance().getFramebuffer(), 0, 0, this);
        }
    }
}
