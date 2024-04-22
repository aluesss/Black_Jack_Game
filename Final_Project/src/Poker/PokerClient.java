package Poker;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
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

import chat.ChatClient.OpenConnectionListener;

import java.security.*;

public class PokerClient extends JFrame{
	Socket socket = null;
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;
	
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
	
	class OpenConnectionListener implements ActionListener{
		
	}
	
	
}