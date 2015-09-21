
package app.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import javax.swing.JButton;
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
import server.Server;

public final class ServerConsole
{
    public static final AttributeSet ERROR_ATTRIB = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.RED);
    public static final AttributeSet PROMPT_ATTRIB = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
    
    private static JFrame serverConsoleFrame;
    
    private static JButton serverConsoleButton;
    private static JPanel serverConsolePanel;
    private static JScrollPane serverConsoleScrollPane;
    private static JTextField serverConsoleTextField;
    private static JTextPane severConsoleTextPane;
    
    private ServerConsole(){}
    
    public static void dispose()
    {
        serverConsoleFrame.dispose();
    }
    
    public static void writeError(final String message)
    {
        SwingUtilities.invokeLater
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    StyledDocument document = severConsoleTextPane.getStyledDocument();

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
                    StyledDocument document = severConsoleTextPane.getStyledDocument();

                    try
                    {
                        document.insertString(document.getLength(), message+"\n", PROMPT_ATTRIB);
                    } 
                    catch(BadLocationException ex){/*ignore exception*/}
                }
            }
        );
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
        
        serverConsoleFrame = new JFrame();
        
        serverConsoleFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                serverConsoleFrameWindowClosing(evt);
            }
        });
        
        serverConsolePanel = new JPanel();
        serverConsoleScrollPane = new JScrollPane();
        severConsoleTextPane = new JTextPane();
        serverConsoleTextField = new JTextField();
        serverConsoleButton = new JButton();

        serverConsoleFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        serverConsoleFrame.setTitle("Server Console");
        serverConsoleFrame.setAlwaysOnTop(true);
        serverConsoleFrame.setMinimumSize(new Dimension(400, 150));

        severConsoleTextPane.setEditable(false);
        serverConsoleScrollPane.setViewportView(severConsoleTextPane);

        serverConsoleTextField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                serverConsoleTextFieldKeyPressed(evt);
            }
        });

        serverConsoleButton.setText("Enter");
        serverConsoleButton.setFocusPainted(false);
        serverConsoleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                serverConsoleButtonActionPerformed(evt);
            }
        });

        GroupLayout serverConsolePanelLayout = new GroupLayout(serverConsolePanel);
        serverConsolePanel.setLayout(serverConsolePanelLayout);
        serverConsolePanelLayout.setHorizontalGroup(serverConsolePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(serverConsoleScrollPane)
            .addGroup(serverConsolePanelLayout.createSequentialGroup()
                .addComponent(serverConsoleTextField, GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(serverConsoleButton, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE))
        );
        serverConsolePanelLayout.setVerticalGroup(serverConsolePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(serverConsolePanelLayout.createSequentialGroup()
                .addComponent(serverConsoleScrollPane, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(serverConsolePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(serverConsoleTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(serverConsoleButton, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)))
        );

        GroupLayout layout = new GroupLayout(serverConsoleFrame.getContentPane());
        serverConsoleFrame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(serverConsolePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(serverConsolePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        serverConsoleFrame.setVisible(true);
        serverConsoleTextField.requestFocusInWindow();
    }
    
    private static void serverConsoleFrameWindowClosing(WindowEvent evt) 
    {
        Server.getInstance().stop();
        Server.getInstance().exit();
    }
    
    private static void serverConsoleButtonActionPerformed(ActionEvent evt) 
    {
        Server.getInstance().runCommand(serverConsoleTextField.getText());
        serverConsoleTextField.setText("");
    }

    private static void serverConsoleTextFieldKeyPressed(KeyEvent evt) 
    {
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            Server.getInstance().runCommand(serverConsoleTextField.getText());
            serverConsoleTextField.setText("");
        }
    }
}
