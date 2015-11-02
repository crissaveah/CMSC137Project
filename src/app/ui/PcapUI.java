
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
import pcap.Sniffer;

public class PcapUI
{
    public static final AttributeSet ERROR_ATTRIB = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.RED);
    public static final AttributeSet PROMPT_ATTRIB = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
    
    private static Sniffer sniffer;
    private static JFrame pcapConsoleFrame;
    
    private static JButton pcapConsoleButton;
    private static JPanel pcapConsolePanel;
    private static JScrollPane pcapConsoleScrollPane;
    private static JTextField pcapConsoleTextField;
    private static JTextPane pcapConsoleTextPane;
    
    private PcapUI(){}
    
    public static void dispose()
    {
        pcapConsoleFrame.dispose();
    }
    
    public static void writeError(String message)
    {
        SwingUtilities.invokeLater
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    StyledDocument document = pcapConsoleTextPane.getStyledDocument();

                    try
                    {
                        document.insertString(document.getLength(), message+"\n", ERROR_ATTRIB);
                    } 
                    catch(BadLocationException ex){/*ignore exception*/}
                }
            }
        );
    }
    
    public static void writePrompt(String message)
    {
        SwingUtilities.invokeLater
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    StyledDocument document = pcapConsoleTextPane.getStyledDocument();

                    try
                    {
                        document.insertString(document.getLength(), message+"\n", PROMPT_ATTRIB);
                    } 
                    catch(BadLocationException ex){/*ignore exception*/}
                }
            }
        );
    }
    
    public static void initialize(Sniffer sniffer)
    {
        PcapUI.sniffer = sniffer;
        
        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            writeError("Failed to initialize platform look-and-feel.");
        }
        
        pcapConsoleFrame = new JFrame();
        
        pcapConsoleFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                pcapConsoleFrameWindowClosing(evt);
            }
        });
        
        pcapConsolePanel = new JPanel();
        pcapConsoleScrollPane = new JScrollPane();
        pcapConsoleTextPane = new JTextPane();
        pcapConsoleTextField = new JTextField();
        pcapConsoleButton = new JButton();

        pcapConsoleFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pcapConsoleFrame.setTitle("Pcap Console");
        pcapConsoleFrame.setAlwaysOnTop(true);
        pcapConsoleFrame.setMinimumSize(new Dimension(400, 150));

        pcapConsoleTextPane.setEditable(false);
        pcapConsoleScrollPane.setViewportView(pcapConsoleTextPane);

        pcapConsoleTextField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                pcapConsoleTextFieldKeyPressed(evt);
            }
        });

        pcapConsoleButton.setText("Enter");
        pcapConsoleButton.setFocusPainted(false);
        pcapConsoleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pcapConsoleButtonActionPerformed(evt);
            }
        });

        GroupLayout serverConsolePanelLayout = new GroupLayout(pcapConsolePanel);
        pcapConsolePanel.setLayout(serverConsolePanelLayout);
        serverConsolePanelLayout.setHorizontalGroup(serverConsolePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(pcapConsoleScrollPane)
            .addGroup(serverConsolePanelLayout.createSequentialGroup()
                .addComponent(pcapConsoleTextField, GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(pcapConsoleButton, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE))
        );
        serverConsolePanelLayout.setVerticalGroup(serverConsolePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(serverConsolePanelLayout.createSequentialGroup()
                .addComponent(pcapConsoleScrollPane, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(serverConsolePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(pcapConsoleTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pcapConsoleButton, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)))
        );

        GroupLayout layout = new GroupLayout(pcapConsoleFrame.getContentPane());
        pcapConsoleFrame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(pcapConsolePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(pcapConsolePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        pcapConsoleFrame.setVisible(true);
        pcapConsoleTextField.requestFocusInWindow();
    }
    
    private static void pcapConsoleFrameWindowClosing(WindowEvent evt) 
    {
        if(sniffer != null && sniffer.isAlive())
            sniffer.close();
    }
    
    private static void pcapConsoleButtonActionPerformed(ActionEvent evt) 
    {
    }

    private static void pcapConsoleTextFieldKeyPressed(KeyEvent evt) 
    {
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            String command = pcapConsoleTextField.getText();
            
            if(command.startsWith("/open"))
            {
                if(sniffer != null && !sniffer.isAlive())
                {
                    try
                    {
                        int device = Character.getNumericValue(command.charAt(6));

                        if(sniffer.open(device))
                        {
                            PcapUI.writePrompt("\nSniffing packets from device "+device+" "+sniffer.getDevices().get(device).getName());
                            sniffer.start();
                        }
                    }
                    catch(StringIndexOutOfBoundsException ex)
                    {
                        PcapUI.writeError("Invalid command. Format should be /open <device index>.");
                    }
                }
                else
                    PcapUI.writeError("A device is already open for packet capture.");
            }
            else if (command.startsWith("/close"))
            {
                pcapConsoleFrame.dispose();
            }
            
            pcapConsoleTextField.setText("");
        }
    }
}
