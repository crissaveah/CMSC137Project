
package game;

public interface GameUIListener 
{
    public void pressed(GameUI ui);
    public void released(GameUI ui);
    public void entered(GameUI ui);
    public void exited(GameUI ui);
}
