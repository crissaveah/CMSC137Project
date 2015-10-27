
package game;

public class Button extends GameUI
{
    private int defaultIcon;
    private int hoverIcon;
    private int pressIcon;
    private boolean toggled;
    
    public void setToggle(boolean toggle)
    {
        toggled = toggle;
        
        if(toggle)
            activeIcon = pressIcon;
        else
        {
            if(inside)
                activeIcon = hoverIcon;
            else
                activeIcon = defaultIcon;
        }
    }
    
    public boolean isToggleOn()
    {
        return toggled;
    }
    
    public void setDefaultIcon(int index)
    {
        defaultIcon = index;
    }
    
    public int getDefaultIcon()
    {
        return defaultIcon;
    }
    
    public void setHoverIcon(int index)
    {
        hoverIcon = index;
    }
    
    public int getHoverIcon()
    {
        return hoverIcon;
    }
    
    public void setPressIcon(int index)
    {
        pressIcon = index;
    }
    
    public int getPressIcon()
    {
        return pressIcon;
    }
    
    @Override
    public void pressed() 
    {
        toggled = !toggled;
        activeIcon = pressIcon;
    }

    @Override
    public void released()
    {
        
    }

    @Override
    public void entered() 
    {
        if(!toggled)
            activeIcon = hoverIcon;
    }

    @Override
    public void exited()
    {
        if(!toggled)
            activeIcon = defaultIcon;
    }
}
