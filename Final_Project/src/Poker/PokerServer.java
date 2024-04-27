package Poker;

import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import java.security.*;

public class PokerServer extends JFrame implements Runnable{
	private JTextArea server_window = new JTextArea();
	private int clientID = 0;
	private int clientCount = 0;
	private static ArrayList<DataOutputStream> clientStreams = new ArrayList<>();
	private static Map<Integer, Boolean> clientReadyStatus = new HashMap<>();
	private static Map<Integer, Boolean> clientStopStatus = new HashMap<>();
	private static Map<Integer, Integer> clientbet = new HashMap<>();
	//private static Map<Integer, Integer> clientvalue = new HashMap<>();
	//private static Map<Integer, String> clientresult = new HashMap<>();
	private static Deck deck;
	private static Hand Dealer_hand = new Hand(); 
	private static Card hidden_card;
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> System.exit(0));
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}
	
	public PokerServer() {
		super("Poker Server");
		this.setSize(500, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createMenu();
		this.setVisible(true);
		setLayout(new BorderLayout());
	    add(new JScrollPane(server_window), BorderLayout.CENTER);
	    server_window.setEditable(false);
	    SwingUtilities.invokeLater(() -> {
	        Thread t = new Thread(this);
	        t.start();
	        // 确保在所有组件都添加到窗体后再调用setVisible
	        this.setVisible(true);
	    });
	}
	
	public static void main(String[] args) {
		PokerServer PokerServer = new PokerServer();
	}
	
	class HandleClient implements Runnable{
		private Socket socket; // A connected socket
		private int clientID;
		private DataInputStream inputFromClient;
        private DataOutputStream outputToClient;
        
        public HandleClient(Socket socket, int clientID) {
			this.socket = socket;
			this.clientID = clientID;
			try {
                inputFromClient = new DataInputStream(socket.getInputStream());
                outputToClient = new DataOutputStream(socket.getOutputStream());
                synchronized(clientStreams) {
                    clientStreams.add(outputToClient);
                    clientReadyStatus.put(clientID, false); //初始化准备状态
                    clientStopStatus.put(clientID, false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
	    }
        
        private void waitForReady() throws IOException {
            String message = inputFromClient.readUTF();
            if ("ready".equals(message)) {
            	int bet = inputFromClient.readInt();
                synchronized (clientReadyStatus) {
                	clientReadyStatus.put(clientID, true);
                    clientbet.put(clientID, bet);
                    server_window.append("Client " + clientID + " is ready with bet: " + bet + ".\n");
                }
                if (allClientsReady()) {
                    startNewGame();
                }
            }
        }
        
        private void startNewGame() {
            deck = new Deck();
            deck.Shuffle();
            Dealer_hand.removeAllCards();
            Card c1 = deck.Deal();
            Card c2 = deck.Deal();
            Dealer_hand.addCard(c1);
            Dealer_hand.addCard(c2);
            server_window.append("Dealer begins with: " + c1.toString() + " " + c2.toString() + "\n");
            hidden_card = c1;
            broadcast(c2.toString());
            for (DataOutputStream out : clientStreams) {
                try {
                    out.writeUTF(deck.Deal().toString());
                    out.writeUTF(deck.Deal().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            clientReadyStatus.clear();
            clientStopStatus.clear();
        }
        
        @Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				try {
					waitForReady();
		            // Start the game process for this client
		            handleGameActions();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
		}
        private boolean allClientsReady() {
            return clientReadyStatus.values().stream().allMatch(status -> status);
        }
        
        private boolean allClientsStopped() {
            return clientStopStatus.values().stream().allMatch(Boolean::booleanValue);
        }
        
        private void broadcast(String message) {
            synchronized (clientStreams) {
                for (DataOutputStream out : clientStreams) {
                    try {
                        out.writeUTF(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        private void processEndOfGame() {
        	server_window.append("All clients have stopped.\n");
        	synchronized (deck) {
            	while (Dealer_hand.getBlackjackValue() < 17 && Dealer_hand.size() < 5) {
            		Card card = deck.Deal();
            		Dealer_hand.addCard(card);
            		server_window.append("Dealer draw a card: " + card.toString() + "\n");
            		broadcast(card.toString());
            	}
            }
        	broadcast("hidden");
            broadcast(hidden_card.toString());
            server_window.append("Dealer's hand value: " + Dealer_hand.getBlackjackValue() + "\n");
            broadcast("done");
          //开始处理结果
            int dealerValue = Dealer_hand.getBlackjackValue();
            synchronized (clientStreams) {
                for (DataOutputStream out : clientStreams) {
                    try {
                        out.writeInt(dealerValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            clientReadyStatus.clear();
            clientStopStatus.clear();
        }
        
        private void handleGameActions() throws IOException {
            while (true) {
                String action = inputFromClient.readUTF();
                if ("hit".equals(action)) {
                    synchronized (deck) {
                        Card newCard = deck.Deal();
                        outputToClient.writeUTF(newCard.toString());
                        server_window.append("Client " + clientID + " hitted and received: " + newCard.toString() + "\n");
                    }
                } else if ("stop".equals(action)) {
                    clientStopStatus.put(clientID, true);
                    server_window.append("Client " + clientID + " stopped.\n");
                    if (allClientsStopped()) {
                    	processEndOfGame();
                        return;
                    }
                }
            }
        }
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ServerSocket serverSocket = new ServerSocket(9898);
			server_window.append("Poker server started at "+new java.util.Date() + '\n');
			 while (true) {
				 Socket socket = serverSocket.accept();
				 clientID ++;
				 clientCount ++;
				 server_window.append("Starting thread for client " + clientID + " at " + new Date() + '\n');
				 InetAddress inetAddress = socket.getInetAddress();
				 server_window.append("Client " + clientID + "'s host name is " + inetAddress.getHostName() + "\n");
				 server_window.append("Client " + clientID + "'s IP Address is " + inetAddress.getHostAddress() + "\n");
				 
				 new Thread(new HandleClient(socket, clientID)).start();
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}