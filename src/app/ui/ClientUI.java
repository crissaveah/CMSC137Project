
package app.ui;

import client.ChatClient;
import client.Client;
import game.GameSurface;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public final class ClientUI 
{
    public static final AttributeSet ERROR_ATTRIB = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.RED);
    public static final AttributeSet PROMPT_ATTRIB = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
    public static final AttributeSet CHAT_ATTRIB = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.GREEN);
    
    private static JFrame clientFrame;
    
    private static JButton consoleButton;
    private static JDialog consoleDialog;
    private static JPanel consolePanel;
    private static JScrollPane consoleScrollPane;
    private static JTextField consoleTextField;
    private static JTextPane consoleTextPane;
    private static GameSurface gamePanel;
    
    private ClientUI(){}
    
    public static void writeError(final String message)
    {
        SwingUtilities.invokeLater
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    StyledDocument document = consoleTextPane.getStyledDocument();

                    try 
                    {
                        document.insertString(document.getLength(), message+"\n", ERROR_ATTRIB);
                    } 
                    catch(BadLocationException ex){/*ignore exception*/}
                }
            }
        );
    }
    
    public static void writePrompt(final String message)
    {
        SwingUtilities.invokeLater
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    StyledDocument document = consoleTextPane.getStyledDocument();

                    try 
                    {
                        document.insertString(document.getLength(), message+"\n", PROMPT_ATTRIB);
                    } 
                    catch(BadLocationException ex){/*ignore exception*/}
                }
            }
        );
    }
    
    public static void writeChat(final String message)
    {
        SwingUtilities.invokeLater
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    StyledDocument document = consoleTextPane.getStyledDocument();

                    try 
                    {
                        document.insertString(document.getLength(), message+"\n", CHAT_ATTRIB);
                    } 
                    catch(BadLocationException ex){/*ignore exception*/}
                }
            }
        );
    }
    
    public static int getWidth()
    {
        return clientFrame.getWidth();
    }
    
    public static int getHeight()
    {
        return clientFrame.getHeight();
    }
    
    public static void dispose()
    {
        consoleDialog.dispose();
        clientFrame.dispose();
    }
    
    public static GameSurface getDrawingSurface()
    {
        return gamePanel;
    }
    
    public static void initialize()
    {
        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            writeError("Failed to initialize platform look-and-feel.");
        }
        
        clientFrame = new JFrame();
        
        consoleDialog = new JDialog();
        consolePanel = new JPanel();
        consoleScrollPane = new JScrollPane();
        consoleTextPane = new JTextPane();
        consoleTextField = new JTextField();
        consoleButton = new JButton();
        gamePanel = new GameSurface();

        consoleDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        consoleDialog.setTitle("Console");
        consoleDialog.setAlwaysOnTop(true);
        consoleDialog.setName("");

        consoleTextPane.setEditable(false);
        consoleScrollPane.setViewportView(consoleTextPane);

        consoleTextField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                consoleTextFieldKeyPressed(evt);
            }
        });

        consoleButton.setText("Enter");
        consoleButton.setFocusPainted(false);
        consoleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                consoleButtonActionPerformed(evt);
            }
        });

        GroupLayout consolePanelLayout = new GroupLayout(consolePanel);
        consolePanel.setLayout(consolePanelLayout);
        consolePanelLayout.setHorizontalGroup(consolePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(consoleScrollPane)
            .addGroup(consolePanelLayout.createSequentialGroup()
                .addComponent(consoleTextField, GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(consoleButton, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE))
        );
        consolePanelLayout.setVerticalGroup(consolePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(consolePanelLayout.createSequentialGroup()
                .addComponent(consoleScrollPane, GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(consolePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(consoleTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(consoleButton, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)))
        );

        GroupLayout consoleDialogLayout = new GroupLayout(consoleDialog.getContentPane());
        consoleDialog.getContentPane().setLayout(consoleDialogLayout);
        consoleDialogLayout.setHorizontalGroup(consoleDialogLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(consolePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        consoleDialogLayout.setVerticalGroup(consoleDialogLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(consolePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        clientFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        clientFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        clientFrame.setName(""); // NOI18N
        clientFrame.setUndecorated(true);
        clientFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        GroupLayout gamePanelLayout = new GroupLayout(gamePanel);
        gamePanel.setLayout(gamePanelLayout);
        gamePanelLayout.setHorizontalGroup(gamePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 597, Short.MAX_VALUE)
        );
        gamePanelLayout.setVerticalGroup(gamePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );

        GroupLayout layout = new GroupLayout(clientFrame.getContentPane());
        clientFrame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(gamePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(gamePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        if(gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT))
        {
            consoleDialog.setUndecorated(true);
            consoleDialog.setOpacity(0.7f);
        }
        
        clientFrame.setVisible(true);
        
        consoleDialog.setSize(400, 150);
        consoleDialog.setLocation(4, clientFrame.getHeight()-154);
        consoleDialog.setVisible(true);
        
        consoleTextField.requestFocusInWindow();
    }
    
    private static void consoleTextFieldKeyPressed(java.awt.event.KeyEvent evt) 
    {
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            String text = consoleTextField.getText();
            
            if(!text.equals(""))
            {
                if(text.charAt(0) == '/')
                    Client.processCommand(text.substring(1));
                else
                    ChatClient.getInstance().sendMessage(Client.CHAT+" "+ChatClient.getInstance().getName()+": "+text);
            }

            consoleTextField.setText("");
        }
    }                                           

    private static void consoleButtonActionPerformed(ActionEvent evt) 
    {
        String text = consoleTextField.getText();
        
        if(!text.equals(""))
        {
            if(text.charAt(0) == '/')
                Client.processCommand(text.substring(1));
            else
                ChatClient.getInstance().sendMessage(Client.CHAT+" "+ChatClient.getInstance().getName()+": "+text);
        }
        
        consoleTextField.setText("");
    }
    
    private static void formWindowClosing(WindowEvent evt) 
    {
        Client.stop();
        consoleDialog.dispose();
    }    
}
