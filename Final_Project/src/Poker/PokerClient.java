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
import java.util.Optional;

public class PokerClient extends JFrame{
	private User user;  // 声明存储用户信息
	private AuthService authService; //声明AuthService中的函数，下面要用updateUserScore
	
	Socket socket = null;
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;
	
	private JLabel usernameLabel; // Label to display Username
	private JPanel cardPanel, controlPanel;
    private JLabel scoreLabel, statusLabel;
    private JTextField betField;
    private JButton btnBet, btnHit, btnStop;
    private ImagePanel[] ClientImagePanels, ServerImagePanels;
    //private String chip_num;//先放个string在这里
    private boolean game_begin = false;
    private int currCard_client = 0;
    private int currCard_server = 1;
    private static Hand Player_hand = new Hand(); 
    private int bet;
    
    public PokerClient(User user) { 
        this.user = user;  // 接收并存储用户信息
        createMenu();
        prepareGUI();
    }
    
    public PokerClient(User user, AuthService authService) { 
        this.user = user;  // 接收并存储用户信息
        this.authService = authService;  // 接收并存储AuthService
        createMenu();
        prepareGUI();
    }

    /*
    private User user; // 存储用户信息
    private AuthService authService; // 用于处理密码更改和更新积分
    private JLabel userInfoLabel; // 用于实时显示Score更新*/
	
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
        ServerImagePanels = new ImagePanel[5];
        for (int i = 0; i < 5; i++) {
            ImagePanel cardImagePanel = new ImagePanel("./images/card_back.png");
            ServerImagePanels[i] = cardImagePanel;
            dealerCardPanel.add(cardImagePanel);
        }
        ClientImagePanels = new ImagePanel[5];
        for (int i = 0; i < 5; i++) {
            ImagePanel cardImagePanel = new ImagePanel("./images/card_back.png");
            ClientImagePanels[i] = cardImagePanel;
            playerCardPanel.add(cardImagePanel);
        }
        cardPanel = new JPanel(new GridLayout(2, 1, 10, 10)); // Two rows to contain dealer and player panels
        cardPanel.add(dealerPanel);
        cardPanel.add(playerPanel);
        add(cardPanel, BorderLayout.CENTER);
        
        statusLabel = new JLabel("Welcome" + user.getUsername() + "! Please join the game on up-left corner", SwingConstants.CENTER);

        // Control panel with buttons and bet field
        controlPanel = new JPanel();
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        //btnReady = new JButton("Ready");
        btnBet = new JButton("Bet");
        betField = new JTextField("0", 5);
        btnHit = new JButton("Hit");
        btnStop = new JButton("Stop");

        // Add listeners here (placeholders for actual actions)
        //btnReady.addActionListener(e -> System.out.println("Ready pressed"));
        //btnBet.addActionListener(e -> System.out.println("Bet: " + betField.getText()));
        //btnHit.addActionListener(e -> System.out.println("Hit pressed"));
        //btnStop.addActionListener(e -> System.out.println("Stop pressed"));
        
        JButton btnReturnToMain = new JButton("Return to Main Menu");
        
        //btnReady.addActionListener(this::onReady);
        btnBet.addActionListener(this::onBet);
        btnHit.addActionListener(this::onHit);
        btnStop.addActionListener(this::onStop);
        btnReturnToMain.addActionListener(e -> returnToMainMenu());
        

        //buttonPanel.add(btnReady);
        buttonPanel.add(btnBet);
        buttonPanel.add(betField);
        buttonPanel.add(btnHit);
        buttonPanel.add(btnStop);
        buttonPanel.add(statusLabel);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);
        controlPanel.add(btnReturnToMain, BorderLayout.NORTH);  // Add the return button

        add(controlPanel, BorderLayout.SOUTH);
        // Top panel to hold username and score labels
        JPanel topPanel = new JPanel(new BorderLayout());

        // Score label
        scoreLabel = new JLabel("Your chips: " + user.getScore());
        topPanel.add(scoreLabel, BorderLayout.WEST);  // Add score label to the left side of the top panel

        // Username label setup
        usernameLabel = new JLabel("Username: " + user.getUsername());
        usernameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(usernameLabel, BorderLayout.EAST);  // Add username label to the right side of the top panel

        add(topPanel, BorderLayout.NORTH);
        
        setVisible(true);
    }
	
	private void returnToMainMenu() {
	    this.dispose(); // Close current window
	    new WelcomeUI(user, authService).setVisible(true); // Open WelcomeUI window
	}

	private void onBet(ActionEvent e) {
	    // 处理Bet按钮事件
		if (!game_begin) {
			statusLabel.setText("Game not begin yet, check upper-left corner");
		}
		else {
		    for (int i = 0; i < 5; i++) {
		        ServerImagePanels[i].updateImage(new ImageIcon("./images/card_back.png").getImage());
		    }
		    for (int i = 0; i < 5; i++) {
		        ClientImagePanels[i].updateImage(new ImageIcon("./images/card_back.png").getImage());
		    }
		    Player_hand.removeAllCards();
		    currCard_client = 0;
		    currCard_server = 1;
		    bet = 0;
		    try {
		        bet = Integer.parseInt(betField.getText());
		    } catch (NumberFormatException ex) {
		        statusLabel.setText("Please enter a valid number for betting.");
		        return;
		    }
		    
		    if (bet <= 0) {
		        statusLabel.setText("Please type a value greater than 0.");
		    } else if (bet > user.getScore()) {
		        statusLabel.setText("Not enough chips. Your chips: " + user.getScore());
		    } else {
		        user.setScore(user.getScore() - bet); // 在user层面修改Score
		        authService.updateUserScore(user.getUsername(), user.getScore()); // 利用authService将user层面的修改更新至数据库
		        scoreLabel.setText("Your chips: " + user.getScore()); // 实时更新左上角
		        statusLabel.setText("Waiting for other players...");
		        try {
		            toServer.writeUTF("ready");
		            toServer.writeInt(bet);
	
		            String card = fromServer.readUTF();
		            System.out.println(card);
	
		            ServerImagePanels[currCard_server].updateImage(new ImageIcon("./images/card_" + card + ".png").getImage());
		            currCard_server++;
		            // 玩家的牌
		            card = fromServer.readUTF();
		            Card card1 = new Card(card);
		            Player_hand.addCard(card1);
		            ClientImagePanels[currCard_client].updateImage(new ImageIcon("./images/card_" + card + ".png").getImage());
		            currCard_client++;
		            card = fromServer.readUTF();
		            Card card2 = new Card(card);
		            Player_hand.addCard(card2);
		            ClientImagePanels[currCard_client].updateImage(new ImageIcon("./images/card_" + card + ".png").getImage());
		            currCard_client++;
		            statusLabel.setText("Current value of your hand: " + Player_hand.getBlackjackValue());
		        } catch (IOException e1) {
		            e1.printStackTrace();
		        }
		    }
	    }
	}


    private void onHit(ActionEvent e) {
        // 处理Hit按钮事件
    	try {
    		if (currCard_client == 5) {
    			statusLabel.setText("You can't hit more, the maximun amount of card has reached, value: " + Player_hand.getBlackjackValue());
    		}
    		else if(Player_hand.getBlackjackValue() > 21) {
    			statusLabel.setText("Your hand has busted (" + "value: " + Player_hand.getBlackjackValue() + "). Please click stop.");
    		}
    		else {
    			toServer.writeUTF("hit");
    			String card = fromServer.readUTF();
    			Card card1 = new Card(card);
    			Player_hand.addCard(card1);
    			ClientImagePanels[currCard_client].updateImage(new ImageIcon("./images/card_" + card + ".png").getImage());
    			currCard_client ++;
    			statusLabel.setText("Current value of your hand: " + Player_hand.getBlackjackValue());
    			if (Player_hand.getBlackjackValue() > 21){
    				statusLabel.setText("Current value of your hand: " + Player_hand.getBlackjackValue() + ", Busted! Please click stop.");
    			}
    		}
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }

    private void onStop(ActionEvent e) {
        // 处理Stop按钮事件
    	try {
    		toServer.writeUTF("stop");
			while(true) {
				String card = fromServer.readUTF();
				if ("hidden".equals(card)) {
					String hidden_card = fromServer.readUTF();
					ServerImagePanels[0].updateImage(new ImageIcon("./images/card_" + hidden_card + ".png").getImage());
				}
				else if ("done".equals(card)) {
	                break;
                }
				else {
					Card cc = new Card(card);
					ServerImagePanels[currCard_server].updateImage(new ImageIcon("./images/card_" + cc + ".png").getImage());
	    			currCard_server ++;
				}
			}
			int hand_value = Player_hand.getBlackjackValue();
			int dealer_value = fromServer.readInt();
			if (hand_value > 21) {
				statusLabel.setText("Busted! Wish you good luck next time. Your value: " + hand_value);
	        } else if (dealer_value > 21 || hand_value > dealer_value) {
	        	statusLabel.setText("Winner Winner, Chicken Dinner! Your value: " + hand_value + " Dealer value: " + dealer_value);
	        	user.setScore(user.getScore() + bet * 2);//赢钱
	        	authService.updateUserScore(user.getUsername(), user.getScore()); // 利用authService将user层面的修改更新至数据库
		        scoreLabel.setText("Your chips: " + user.getScore());
	        } else if (hand_value < dealer_value) {
	        	statusLabel.setText("Bad luck! Sorry for your lost. Your value: " + hand_value + " Dealer value: " + dealer_value);
	        } else {
	        	statusLabel.setText("Tie game, want to try again?Your value: " + hand_value + " Dealer value: " + dealer_value);
	        	user.setScore(user.getScore() + bet);//还钱
	        	authService.updateUserScore(user.getUsername(), user.getScore()); // 利用authService将user层面的修改更新至数据库
		        scoreLabel.setText("Your chips: " + user.getScore());
	        }
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
	
    class OpenConnectionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                socket = new Socket("localhost", 9898);
                fromServer = new DataInputStream(socket.getInputStream());
                toServer = new DataOutputStream(socket.getOutputStream());
                
                game_begin = true;

                // 从AuthService获取分数
                Optional<User> maybeUser = authService.getUserByUsername(user.getUsername());
                if (maybeUser.isPresent()) {
                    User currentUser = maybeUser.get();
                    // 更新UI显示分数
                    scoreLabel.setText("Your chips: " + currentUser.getScore());
                }

                // 现在，客户端已经更新了UI，可以发送准备就绪的信号给服务器
                statusLabel.setText("Game joined, please type in the bet and press bet");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

	
	public static void main(String[] args) {
        new PokerClient();
    }
}