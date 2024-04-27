/*
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				String message = inputFromClient.readUTF();
				if ("ready".equals(message)) {
					clientReadyStatus.put(clientID, true);
                    server_window.append("Client " + clientID + " is ready.\n");
                    if (allClientsReady()) {
                    	broadcast("All clients ready, proceeding with game.\n");
                    }
				}
				
				while (true) {
                    //在这里开始游戏进程
                    deck.Shuffle();
                    Hand Dealer_hand = new Hand();
                    Card c1 = deck.Deal();
                    Card c2 = deck.Deal();
                    Dealer_hand.addCard(c1);
                    Dealer_hand.addCard(c2);
                    broadcast(c2.toString());
                    synchronized(clientStreams) {
                        for (DataOutputStream stream : clientStreams) {
                        	Card card1 = deck.Deal();
                            Card card2 = deck.Deal();
                            stream.writeUTF(card1.toString());
                            stream.writeUTF(card2.toString());
                            String action = inputFromClient.readUTF();
                            while(true) {
                                if ("call".equals(action)) {
                                    Card newCard = deck.Deal();
                                    outputToClient.writeUTF(newCard.toString());
                                    server_window.append("Client " + clientID + " called and received: " + newCard.toString() + "\n");
                                }
                                else if ("stop".equals(action)) {
                                	clientStopStatus.put(clientID, true);
                                	server_window.append("Client " + clientID + " stopped.\n");
                                	break;
                                }
                            }
                        }
                    }
                }
			} catch (IOException e) {
                e.printStackTrace();
            }
		}*/