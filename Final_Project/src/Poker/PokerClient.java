package Poker;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import java.security.*;

public class PokerClient extends JFrame{
	Socket socket = null;
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;
	
	private JPanel cardPanel, controlPanel;
    private JLabel scoreLabel, statusLabel;
    private JTextField betField;
    private JButton btnReady, btnBet, btnHit, btnStop;
    private ImagePanel[] ClientImagePanels, ServerImagePanels;
    private String chip_num;//先放个string在这里
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem connectItem = new JMenuItem("Join the Game");
		connectItem.addActionListener(new OpenConnectionListener());
		menu.add(connectItem);
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> System.exit(0));
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}
	
	public PokerClient() {
		createMenu();
        prepareGUI();
    }
	
	private void prepareGUI() {
		setTitle("Poker Client");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel dealerPanel = new JPanel(new BorderLayout());
        JLabel dealerLabel = new JLabel("Dealer's hand", SwingConstants.CENTER);
        dealerPanel.add(dealerLabel, BorderLayout.NORTH);
        JPanel dealerCardPanel = new JPanel(new GridLayout(1, 5, 10, 0)); // Single row for cards
        dealerPanel.add(dealerCardPanel, BorderLayout.CENTER);
        JPanel playerPanel = new JPanel(new BorderLayout());
        JLabel playerLabel = new JLabel("Player's hand", SwingConstants.CENTER);
        playerPanel.add(playerLabel, BorderLayout.NORTH);
        JPanel playerCardPanel = new JPanel(new GridLayout(1, 5, 10, 0)); // Single row for cards
        playerPanel.add(playerCardPanel, BorderLayout.CENTER);
        
        // Card display panel
        //String imagePath = "./image/card_back.png";图片放文件夹就不好使，直接放目录就好使，什么鬼东西？？？
        ServerImagePanels = new ImagePanel[5];
        for (int i = 0; i < 5; i++) {
            ImagePanel cardImagePanel = new ImagePanel("card_back.png"); // Assuming images folder is in classpath
            ServerImagePanels[i] = cardImagePanel;
            dealerCardPanel.add(cardImagePanel);
        }
        ClientImagePanels = new ImagePanel[5];
        for (int i = 0; i < 5; i++) {
            ImagePanel cardImagePanel = new ImagePanel("card_back.png"); // Same assumption as above
            ClientImagePanels[i] = cardImagePanel;
            playerCardPanel.add(cardImagePanel);
        }
        cardPanel = new JPanel(new GridLayout(2, 1, 10, 10)); // Two rows to contain dealer and player panels
        cardPanel.add(dealerPanel);
        cardPanel.add(playerPanel);
        add(cardPanel, BorderLayout.CENTER);
        
        statusLabel = new JLabel("Welcome, please join the game on up-left corner", SwingConstants.CENTER);

        // Control panel with buttons and bet field
        controlPanel = new JPanel();
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnReady = new JButton("Ready");
        btnBet = new JButton("Bet");
        betField = new JTextField("0", 5);
        btnHit = new JButton("Hit");
        btnStop = new JButton("Stop");

        // Add listeners here (placeholders for actual actions)
        //btnReady.addActionListener(e -> System.out.println("Ready pressed"));
        btnBet.addActionListener(e -> System.out.println("Bet: " + betField.getText()));
        btnHit.addActionListener(e -> System.out.println("Hit pressed"));
        btnStop.addActionListener(e -> System.out.println("Stop pressed"));
        
        btnReady.addActionListener(this::onReady);
        //btnBet.addActionListener(this::onBet);
        //btnHit.addActionListener(this::onHit);
        //btnStop.addActionListener(this::onStop);
        

        buttonPanel.add(btnReady);
        buttonPanel.add(btnBet);
        buttonPanel.add(betField);
        buttonPanel.add(btnHit);
        buttonPanel.add(btnStop);
        buttonPanel.add(statusLabel, BorderLayout.NORTH);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(controlPanel, BorderLayout.SOUTH);
        // Score label
        scoreLabel = new JLabel("Your chips: "); // Placeholder score
        add(scoreLabel, BorderLayout.NORTH);

        setVisible(true);
    }
	
	private void onReady(ActionEvent e) {
        // 处理Ready按钮事件
		try {
			toServer.writeUTF("ready");
			statusLabel.setText("Waiting for other players...");
			String response = fromServer.readUTF();
			statusLabel.setText(response);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }

    private void onBet(ActionEvent e) {
        // 处理Bet按钮事件
    }

    private void onHit(ActionEvent e) {
        // 处理Hit按钮事件
    }

    private void onStop(ActionEvent e) {
        // 处理Stop按钮事件
    }
	
	class OpenConnectionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try {
				socket = new Socket("localhost", 9898);
				fromServer = new DataInputStream(socket.getInputStream());
	            toServer = new DataOutputStream(socket.getOutputStream());
	            statusLabel.setText("Game joined, please press Ready");
	            chip_num = fromServer.readUTF();
	            scoreLabel.setText("Your chips: " + chip_num);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
	            

		}
		//Hold there
	}
	
	public static void main(String[] args) {
        new PokerClient();
    }
}