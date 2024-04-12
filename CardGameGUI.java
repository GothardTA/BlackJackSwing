import java.awt.Point;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.util.*;

/**
 * This class provides a GUI for solitaire games related to Elevens.
 */
public class CardGameGUI extends JFrame implements ActionListener {

	/** Height of the game frame. */
	private static int DEFAULT_HEIGHT = 800;
	/** Width of the game frame. */
	private static int DEFAULT_WIDTH = 800;
	/** Width of a card. */
	private static final int CARD_WIDTH = 150;
	/** Height of a card. */
	private static final int CARD_HEIGHT = 200;

	private static final int PADDING = 40;
	
	private final int minSpacing = 20;

	private static final int BUTTON_LEFT = 0;
	private static final int BUTTON_HEIGHT = 60;
	private static final int BUTTON_WIDTH = 200;
	
	private static final String[] RANKS = {"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};
	private static final String[] SUITS = {"spades", "hearts", "diamonds", "clubs"};
	private static final int[] POINT_VALUES = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};
	private Deck deck = new Deck(RANKS, SUITS, POINT_VALUES);

	/** The main panel containing the game components. */
	private JPanel panel;
	/** The stay button. */
	private JButton stayButton;
	/** The hit button. */
	private JButton hitButton;
	/** The deal button. */
	private JButton dealButton;
	/** The Bet button. */
	private JButton betButton;
	/** The card displays. */
	private ArrayList<JLabel> dealerDisplayCards = new ArrayList<JLabel>();
	/** The card displays. */
	private ArrayList<JLabel> playerDisplayCards = new ArrayList<JLabel>();
	/** The message displayed when the game is over */
	private JLabel endGameMsg;
	/** The message that displays the players money */
	private JLabel playerMoneyLabel;
	/** The message that displays how many points the player has */
	private JLabel playerTotalLabel;
	/** The message that displays how much was bet */
	private JLabel playerBetLabel;
	/** The input box for the bet amount */
	private JTextField betAmount;
	/** The coordinates of the card displays. */
	private ArrayList<Point> dealerCardCoords = new ArrayList<Point>();
	private ArrayList<Point> playerCardCoords = new ArrayList<Point>();
	/** arrays that hold the player and dealers cards */
	private ArrayList<Card> dealerCards = new ArrayList<Card>();
	private ArrayList<Card> playerCards = new ArrayList<Card>();
	/** Game variables */
	private boolean dealerBust = false;
	private boolean playerBust = false;
	private boolean dealerBlackjack = false;
	private boolean playerBlackjack = false;
	private boolean tieGame = false;
	private boolean playerWon = false;
	private boolean dealerWon = false;

	private boolean playerStayed = false;
	private boolean gameOver = false;
	private boolean hideCards = true;
	private boolean playerBankrupt = false;

	private int playerMoney = 100;
	private int playerBetAmount = 0;

	private int cheatCounter = 0;
	private boolean cheaterDetected = false;


	public CardGameGUI() {
		initDisplay();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		repaint();
	}

	/**
	 * Run the game.
	 */
	public void displayGame() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				setVisible(true);
			}
		});
		dealCards();
		initCardPositions();
		repaint();
	}

	/**
	 * Draw the display (cards and messages).
	 */
	public void repaint() {
		// update button visibility
		if (gameOver) {
			dealButton.setVisible(true);
			stayButton.setVisible(false);
			hitButton.setVisible(false);
			betButton.setVisible(false);
			betAmount.setVisible(false);
			playerTotalLabel.setVisible(false);
		} else if (!gameOver && hideCards) {
			dealButton.setVisible(false);
			stayButton.setVisible(false);
			hitButton.setVisible(false);
			betButton.setVisible(true);
			betAmount.setVisible(true);
			playerTotalLabel.setVisible(false);
		} else if (!gameOver && !hideCards) {
			dealButton.setVisible(false);
			stayButton.setVisible(true);
			hitButton.setVisible(true);
			betButton.setVisible(false);
			betAmount.setVisible(false);
			playerTotalLabel.setVisible(true);
		}

		// update button positions
		if (DEFAULT_WIDTH < (BUTTON_WIDTH * 2 + PADDING * 3)) {
			int buttonSpacing = (DEFAULT_WIDTH - BUTTON_WIDTH) / 2;

			hitButton.setBounds(buttonSpacing, DEFAULT_HEIGHT-BUTTON_HEIGHT-PADDING, BUTTON_WIDTH, BUTTON_HEIGHT);
			stayButton.setBounds(buttonSpacing, DEFAULT_HEIGHT-(BUTTON_HEIGHT*2)-(PADDING*2), BUTTON_WIDTH, BUTTON_HEIGHT);
			dealButton.setBounds(buttonSpacing, DEFAULT_HEIGHT-BUTTON_HEIGHT-PADDING, BUTTON_WIDTH, BUTTON_HEIGHT);
			betButton.setBounds(buttonSpacing, DEFAULT_HEIGHT-(BUTTON_HEIGHT * 2)-(PADDING * 2), BUTTON_WIDTH, BUTTON_HEIGHT);
			betAmount.setBounds(buttonSpacing, DEFAULT_HEIGHT-BUTTON_HEIGHT-PADDING, BUTTON_WIDTH, BUTTON_HEIGHT);
		} else {
			int buttonSpacing1 = (DEFAULT_WIDTH - BUTTON_WIDTH) / 2;
			int buttonSpacing2 = (DEFAULT_WIDTH - (BUTTON_WIDTH * 2)) / 3;

			hitButton.setBounds(DEFAULT_WIDTH-BUTTON_WIDTH-PADDING, DEFAULT_HEIGHT-BUTTON_HEIGHT-PADDING, BUTTON_WIDTH, BUTTON_HEIGHT);
			stayButton.setBounds(PADDING, DEFAULT_HEIGHT-BUTTON_HEIGHT-PADDING, BUTTON_WIDTH, BUTTON_HEIGHT);
			dealButton.setBounds(buttonSpacing1, DEFAULT_HEIGHT-BUTTON_HEIGHT-PADDING, BUTTON_WIDTH, BUTTON_HEIGHT);
			betButton.setBounds(buttonSpacing2, DEFAULT_HEIGHT-BUTTON_HEIGHT-PADDING, BUTTON_WIDTH, BUTTON_HEIGHT);
			betAmount.setBounds(buttonSpacing2, DEFAULT_HEIGHT-BUTTON_HEIGHT-PADDING, BUTTON_WIDTH, BUTTON_HEIGHT);
		}

		if (hideCards) {
			// dealers cards
			if (dealerDisplayCards.size() != 0) {
				if (playerStayed) {
					String cardImageFileName = imageFileName(null);
					ImageIcon icon = new ImageIcon(cardImageFileName);
					dealerDisplayCards.get(0).setIcon(icon);
					dealerDisplayCards.get(0).setVisible(true);
				} else {
					String cardImageFileName = imageFileName(null);
					ImageIcon icon = new ImageIcon(cardImageFileName);
					dealerDisplayCards.get(0).setIcon(icon);
					dealerDisplayCards.get(0).setVisible(true);
				}

				for (int i = 1; i < dealerCards.size(); i++) {
					String cardImageFileName = imageFileName(null);
					ImageIcon icon = new ImageIcon(cardImageFileName);
					dealerDisplayCards.get(i).setIcon(icon);
					dealerDisplayCards.get(i).setVisible(true);
				}
			}

			// player cards
			if (playerDisplayCards.size() != 0) {
				for (int i = 0; i < playerCards.size(); i++) {
					String cardImageFileName = imageFileName(null);
					ImageIcon icon = new ImageIcon(cardImageFileName);
					playerDisplayCards.get(i).setIcon(icon);
					playerDisplayCards.get(i).setVisible(true);
				}
			}
		} else {
			// dealers cards
			if (dealerDisplayCards.size() != 0) {
				if (playerStayed) {
					String cardImageFileName = imageFileName(dealerCards.get(0));
					ImageIcon icon = new ImageIcon(cardImageFileName);
					dealerDisplayCards.get(0).setIcon(icon);
					dealerDisplayCards.get(0).setVisible(true);
				} else {
					String cardImageFileName = imageFileName(null);
					ImageIcon icon = new ImageIcon(cardImageFileName);
					dealerDisplayCards.get(0).setIcon(icon);
					dealerDisplayCards.get(0).setVisible(true);
				}

				for (int i = 1; i < dealerCards.size(); i++) {
					String cardImageFileName = imageFileName(dealerCards.get(i));
					ImageIcon icon = new ImageIcon(cardImageFileName);
					dealerDisplayCards.get(i).setIcon(icon);
					dealerDisplayCards.get(i).setVisible(true);
				}
			}

			// player cards
			if (playerDisplayCards.size() != 0) {
				for (int i = 0; i < playerCards.size(); i++) {
					String cardImageFileName = imageFileName(playerCards.get(i));
					ImageIcon icon = new ImageIcon(cardImageFileName);
					playerDisplayCards.get(i).setIcon(icon);
					playerDisplayCards.get(i).setVisible(true);
				}
			}
		}

		if (playerMoney == 0) {
			endGameMsg.setText("You lost all your money!");
			endGameMsg.setForeground(Color.RED);
			endGameMsg.setVisible(true);
		}
		
		if (playerBlackjack && !tieGame) {
			endGameMsg.setText("Blackjack!");
			endGameMsg.setForeground(Color.GREEN);
		} else if (dealerBlackjack && !tieGame) {
			endGameMsg.setText("Dealer Blackjack");
			endGameMsg.setForeground(Color.RED);
		} else if (tieGame) {
			endGameMsg.setText("Tie Game");
			endGameMsg.setForeground(Color.BLACK);
		} else if (playerBust && !tieGame) {
			endGameMsg.setText("You busted");
			endGameMsg.setForeground(Color.RED);
		} else if (dealerBust && !tieGame) {
			endGameMsg.setText("Dealer busted!");
			endGameMsg.setForeground(Color.GREEN);
		} else if (playerWon) {
			endGameMsg.setText("You won!");
			endGameMsg.setForeground(Color.GREEN);
		} else if (dealerWon) {
			endGameMsg.setText("Dealer won");
			endGameMsg.setForeground(Color.RED);
		} else {
			endGameMsg.setText("");
			endGameMsg.setForeground(Color.BLACK);
		}

		if (playerBankrupt) {
			endGameMsg.setText("You went bankrupt!");
			endGameMsg.setFont( new Font("SansSerif", Font.BOLD, 36) );
			int textSize = panel.getGraphics().getFontMetrics( new Font("SansSerif", Font.BOLD, 36) ).stringWidth("You went bankrupt!");
			endGameMsg.setBounds( (DEFAULT_WIDTH / 2) - (textSize / 2), DEFAULT_HEIGHT / 2 + 80, textSize, 30);
			endGameMsg.setForeground(Color.RED);
			endGameMsg.setVisible(true);

			betButton.setIcon( new ImageIcon(".\\media\\icons\\restart.png") );
		}

		endGameMsg.setVisible(true);

		// update point counter test
		playerTotalLabel.setText("Points: " + countHand(playerCards));
		int textSize = panel.getGraphics().getFontMetrics( new Font("SansSerif", Font.BOLD, 24) ).stringWidth("Points:   " + countHand(playerCards));
		playerTotalLabel.setBounds((DEFAULT_WIDTH/2) - (textSize / 2), DEFAULT_HEIGHT-320, textSize, 30);

		// update player's money total text
		playerMoneyLabel.setText("$" + playerMoney);
		textSize = panel.getGraphics().getFontMetrics( new Font("SansSerif", Font.BOLD, 24) ).stringWidth("$" + playerMoney);
		playerMoneyLabel.setBounds((DEFAULT_WIDTH/2) - (textSize / 2), DEFAULT_HEIGHT-290, textSize, 30);

		// update player's bet amount text
		playerBetLabel.setText("Bet: $" + playerBetAmount);
		textSize = panel.getGraphics().getFontMetrics( new Font("SansSerif", Font.BOLD, 24) ).stringWidth(("Bet: $" + playerBetAmount));
		playerBetLabel.setBounds((DEFAULT_WIDTH/2) - (textSize / 2), DEFAULT_HEIGHT-260, textSize, 30);

		pack();
		panel.repaint();
	}

	/**
	 * Initialize the display.
	 */
	private void initDisplay()	{
		panel = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
		};
		
		panel.addMouseListener(new MyMouseListener());
		setTitle("Blackjack");

		this.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		panel.setLayout(null);
		panel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

		// stay button
		stayButton = new JButton(new ImageIcon(".\\media\\icons\\stay.png"));
		// stayButton.setText("");
		panel.add(stayButton);
		stayButton.setBounds(BUTTON_LEFT+20, DEFAULT_HEIGHT-BUTTON_HEIGHT-PADDING, BUTTON_WIDTH, BUTTON_HEIGHT);
		stayButton.setFont(new Font("SansSerif", Font.BOLD, 24));
		stayButton.addActionListener(this);
		stayButton.setVisible(false);

		// hit button
		hitButton = new JButton(new ImageIcon(".\\media\\icons\\hit.png"));
		// hitButton.setText("");
		panel.add(hitButton);
		hitButton.setBounds(DEFAULT_WIDTH-BUTTON_WIDTH-PADDING, DEFAULT_HEIGHT-BUTTON_HEIGHT-PADDING, BUTTON_WIDTH, BUTTON_HEIGHT);
		hitButton.setFont(new Font("SansSerif", Font.BOLD, 24));
		hitButton.addActionListener(this);
		hitButton.setVisible(false);

		// deal button
		dealButton = new JButton(new ImageIcon(".\\media\\icons\\deal.png"));
		// dealButton.setText("");
		panel.add(dealButton);
		dealButton.setBounds((DEFAULT_WIDTH/2)-(BUTTON_WIDTH/2), DEFAULT_HEIGHT-(BUTTON_HEIGHT * 2)-(PADDING * 2), BUTTON_WIDTH, BUTTON_HEIGHT);
		dealButton.setFont(new Font("SansSerif", Font.BOLD, 24));
		dealButton.addActionListener(this);
		dealButton.setVisible(false);
		
		// bet button
		betButton = new JButton(new ImageIcon(".\\media\\icons\\bet.png"));
		// betButton.setText("");
		panel.add(betButton);
		betButton.setBounds((DEFAULT_WIDTH/2)-(BUTTON_WIDTH/2), DEFAULT_HEIGHT-(BUTTON_HEIGHT * 2)-(PADDING * 2), BUTTON_WIDTH, BUTTON_HEIGHT);
		betButton.setFont(new Font("SansSerif", Font.BOLD, 24));
		betButton.addActionListener(this);
		betButton.setVisible(true);

		// end game message
		endGameMsg = new JLabel();
		endGameMsg.setBounds(BUTTON_LEFT+PADDING, DEFAULT_HEIGHT-BUTTON_HEIGHT-(PADDING * 2), 200, 30);
		endGameMsg.setFont(new Font("SansSerif", Font.BOLD, 24));
		endGameMsg.setForeground(Color.BLACK);
		endGameMsg.setText("");
		panel.add(endGameMsg);
		endGameMsg.setVisible(false);

		// player card count total
		playerTotalLabel = new JLabel();
		playerTotalLabel.setBounds(DEFAULT_WIDTH/2, DEFAULT_HEIGHT-150, PADDING, 30);
		playerTotalLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
		playerTotalLabel.setForeground(Color.BLACK);
		playerTotalLabel.setText("Points: ");
		panel.add(playerTotalLabel);
		playerTotalLabel.setVisible(true);

		// player money 
		playerMoneyLabel = new JLabel();
		playerMoneyLabel.setBounds(DEFAULT_WIDTH/2, DEFAULT_HEIGHT-150, PADDING, 30);
		playerMoneyLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
		playerMoneyLabel.setForeground(Color.BLACK);
		playerMoneyLabel.setText("$");
		panel.add(playerMoneyLabel);
		playerMoneyLabel.setVisible(true);
		
		// player bet amount 
		playerBetLabel = new JLabel();
		playerBetLabel.setBounds(DEFAULT_WIDTH/2, DEFAULT_HEIGHT-150, PADDING, 30);
		playerBetLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
		playerBetLabel.setForeground(Color.BLACK);
		playerBetLabel.setText("Bet: $");
		panel.add(playerBetLabel);
		playerBetLabel.setVisible(true);

		// bet input amount
		betAmount = new JTextField("Bet Amount", 10);
		betAmount.setBounds((DEFAULT_WIDTH/2)-(BUTTON_WIDTH/2), DEFAULT_HEIGHT-BUTTON_HEIGHT-PADDING, BUTTON_WIDTH, BUTTON_HEIGHT);
		betAmount.setFont(new Font("SansSerif", Font.BOLD, 24));
		panel.add(betAmount);
		betAmount.setVisible(true);

		pack();
		getContentPane().add(panel);
		getRootPane().setDefaultButton(stayButton);
		panel.setVisible(true);
	}

	private void initCardPositions() {
		// remove old card positions from screen
		if (dealerCardCoords.size() != 0) {
			for (JLabel c : dealerDisplayCards) {
				panel.remove(c);
			}
			for (JLabel c : playerDisplayCards) {
				panel.remove(c);
			}
		}

		// reset arrays that hold card positions and labels
		dealerDisplayCards = new ArrayList<JLabel>();
		playerDisplayCards = new ArrayList<JLabel>();
		
		dealerCardCoords = new ArrayList<Point>();
		playerCardCoords = new ArrayList<Point>();
		
		// change window size to fit cards if needed
		int dealerCardSpacing = (dealerCards.size() * CARD_WIDTH) + ((dealerCards.size() + 1) * minSpacing);
		int playerCardSpacing = (playerCards.size() * CARD_WIDTH) + ((playerCards.size() + 1) * minSpacing);

		if (dealerCardSpacing > playerCardSpacing) {
			DEFAULT_WIDTH = dealerCardSpacing;
			panel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		} else {
			DEFAULT_WIDTH = playerCardSpacing;
			panel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		}


		for (int i = 0; i < dealerCards.size(); i++) {
			dealerCardCoords.add( new Point( (minSpacing * (i+1)) + (CARD_WIDTH * i), PADDING ) );
		}

		for (int i = 0; i < playerCards.size(); i++) {
			playerCardCoords.add( new Point( (minSpacing * (i+1)) + (CARD_WIDTH * i), 2*PADDING + CARD_HEIGHT ) );
		}

		// add cards to screen
		for (int i = 0; i < dealerCardCoords.size(); i++) {
			dealerDisplayCards.add(new JLabel());
			panel.add(dealerDisplayCards.get(i));
			dealerDisplayCards.get(i).setBounds(dealerCardCoords.get(i).x, dealerCardCoords.get(i).y,
										CARD_WIDTH, CARD_HEIGHT);
		}

		for (int i = 0; i < playerCardCoords.size(); i++) {
			playerDisplayCards.add(new JLabel());
			panel.add(playerDisplayCards.get(i));
			playerDisplayCards.get(i).setBounds(playerCardCoords.get(i).x, playerCardCoords.get(i).y,
										CARD_WIDTH, CARD_HEIGHT);
		}
	}

	private void dealCards() {
		for (int i = 0; i < 4; i++) {
			if (deck.size() < 8) {
				deck.shuffle();
			}

			Card c = deck.deal();

			if (i % 2 == 0) {
				playerCards.add(c);
			} else {
				dealerCards.add(c);
			}
		}
		
		if (cheaterDetected) {
			dealerCards.set(0, deck.deal(1));
			dealerCards.set(1, deck.deal(10));
		}

		if (countHand(playerCards) == 21 && countHand(dealerCards) != 21) {
			playerBlackjack = true;
			playerStayed = true;
			gameOver = true;
			// player receives their bet back with an additional 1.5 times your bet
			playerMoney += playerBetAmount;
			playerMoney += (int) (playerBetAmount * 1.5);
			playerBetAmount = 0;
		} else if (countHand(playerCards) != 21 && countHand(dealerCards) == 21) {
			dealerBlackjack = true;
			playerStayed = true;
			gameOver = true;
			playerBetAmount = 0;
		} else if (countHand(playerCards) == 21 && countHand(dealerCards) == 21) {
			dealerBlackjack = true;
			playerStayed = true;
			gameOver = true;
			playerMoney += playerBetAmount;
			playerBetAmount = 0;
		}

		repaint();
	}

	private String imageFileName(Card c) {
		String str = "C:\\users\\gothardta\\Documents\\BlackJack\\media\\cards\\";
		if (c == null) {
			return "C:\\users\\gothardta\\Documents\\BlackJack\\media\\cards\\gray_back.png";
		}

		if (c.rank().equals("king")) {
			str += "K";
		} else if (c.rank().equals("queen")) {
			str += "Q";
		} else if (c.rank().equals("jack")) {
			str += "J";
		} else if (c.rank().equals("ace")) {
			str += "A";
		} else {
			str += c.rank();
		}

		if (c.suit().equals("spades")) {
			str += "S";
		} else if (c.suit().equals("hearts")) {
			str += "H";
		} else if (c.suit().equals("clubs")) {
			str += "C";
		} else if (c.suit().equals("diamonds")) {
			str += "D";
		}
		str += ".png";
		return str;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(stayButton)) {
			getRootPane().setDefaultButton(stayButton);

			stayActions();

			repaint();
		}
		else if (e.getSource().equals(hitButton)) {
			getRootPane().setDefaultButton(hitButton);

			hitActions();

			repaint();
		}
		else if (e.getSource().equals(dealButton)) {
			getRootPane().setDefaultButton(dealButton);

			dealActions();

			repaint();
		} else if (e.getSource().equals(betButton)) {
			getRootPane().setDefaultButton(betButton);

			betActions();

			repaint();
		}
		else {
			return;
		}
	}

	private void betActions() {
		if (playerBankrupt) {
			playerMoney = 100;
			playerBankrupt = false;
			endGameMsg.setVisible(false);
			betButton.setIcon( new ImageIcon(".\\media\\icons\\bet.png") );

			endGameMsg.setBounds(BUTTON_LEFT+PADDING, DEFAULT_HEIGHT-BUTTON_HEIGHT-(PADDING * 2), 200, 30);
			endGameMsg.setFont(new Font("SansSerif", Font.BOLD, 24));
			endGameMsg.setForeground(Color.BLACK);
			endGameMsg.setText("");
			endGameMsg.setVisible(false);

			repaint();
		} else {
			try {
				playerBetAmount += Integer.parseInt(betAmount.getText());
				
				if (playerMoney - playerBetAmount < 0) {
					betAmount.setText("Too much!");
					playerBetAmount = 0;
				} else if (playerBetAmount < 0) {
					betAmount.setText("No Cheating!");
					playerBetAmount = 0;
				} else {
					playerMoney -= playerBetAmount;
					betButton.setVisible(false);
					hideCards = false;
					betAmount.setText("" + playerBetAmount);
				}

				repaint();
			} catch (Exception e) {
				try {
					if (betAmount.getText().startsWith("IWANT")) {
						cheatCounter++;

						if (cheatCounter >= 2) {
							dealCards();
							cheaterDetected = true;
							playerMoney += Integer.parseInt(betAmount.getText().substring(5));
						} else {
							playerMoney += Integer.parseInt(betAmount.getText().substring(5));
						}
					}
				} catch (Exception f) {}

				betAmount.setText("Not A number!");
			}
		}
		
		repaint();
	}

	private void dealActions() {
		repaint();
		if (playerMoney <= 0) {
			playerBankrupt = true;
			repaint();
		}
		playerCards = new ArrayList<Card>();
		dealerCards = new ArrayList<Card>();

		dealCards();

		initCardPositions();

		dealerBust = false;
		playerBust = false;
		dealerBlackjack = false;
		playerBlackjack = false;
		tieGame = false;
		playerWon = false;
		dealerWon = false;
		playerStayed = false;

		gameOver = false;
		hideCards = true;
		hideCards = true;
		betButton.setVisible(true);
	}

	private void hitActions() {
		playerCards.add(deck.deal());

		if (countHand(playerCards) > 21) {
			playerBust = true;
			playerStayed = true;
			gameOver = true;
			playerBetAmount = 0;
		} else if (countHand(playerCards) == 21) {
			playerStayed = true;
			playerBlackjack = true;
			gameOver = true;
			playerMoney += playerBetAmount * 2;
			playerBetAmount = 0;
		}

		initCardPositions();
	}

	private void stayActions() {
		playerStayed = true;

		// makes ace be counted last, makes it easier to count points
		if (dealerCards.get(0).rank().equals("ace")) {
			Card c = dealerCards.remove(0);
			dealerCards.add(c);
		}
		if (playerCards.get(0).rank().equals("ace")) {
			Card c = playerCards.remove(0);
			playerCards.add(c);
		}

		int totalDealerValue = countHand(dealerCards);
		if (totalDealerValue == 21) {
			dealerBlackjack = true;
			gameOver = true;
		}
		while (totalDealerValue <= 16) {
			totalDealerValue = countHand(dealerCards);

			if (totalDealerValue <= 16) {
				dealerCards.add(deck.deal());
				// doesn't work :(
				// long start = System.currentTimeMillis();
				// while (start + 1000 < System.currentTimeMillis()) {
				// 	// give a break between drawing cards
				// }
				initCardPositions();
				repaint();
			}
		}

		int totalPlayerValue = countHand(playerCards);

		if (totalPlayerValue == 21) {
			playerBlackjack = true;
			playerMoney += playerBetAmount * 2;
			playerBetAmount = 0;
		} else if (totalPlayerValue > 21) {
			playerBust = true;
			// bet handled in hit method
		}

		if (totalDealerValue == 21) {
			dealerBlackjack = true;
			playerBetAmount = 0;
		} else if (totalDealerValue > 21) {
			dealerBust = true;
			playerMoney += (playerBetAmount * 2);
			playerBetAmount = 0;
		}

		if ((playerBlackjack && dealerBlackjack) || (playerBust && dealerBust)) {
			tieGame = true;
		}

		if (!playerBlackjack && !playerBust && !dealerBlackjack && !dealerBust) {
			if (totalDealerValue > totalPlayerValue) {
				dealerWon = true;
				playerBetAmount = 0;
			}
			if (totalDealerValue < totalPlayerValue) {
				playerWon = true;
				playerMoney += (playerBetAmount * 2);
				playerBetAmount = 0;
			}

			if (totalDealerValue == totalPlayerValue) {
				tieGame = true;
			}
		}

		if (tieGame) {
			playerMoney += playerBetAmount;
			playerBetAmount = 0;
		}

		gameOver = true;
	}

	private int countHand(ArrayList<Card> tmpHand) {
		ArrayList<Card> hand = new ArrayList<Card>();
		for (Card c : tmpHand) {
			hand.add(c);
		}

		// counts aces last
		int aceSwaps = 0;
		for (int i = 0; i < hand.size()-1; i++) {
			Card c = hand.get(i);

			if (c.rank().equals("ace")) {
				Card cr = hand.remove(i);
				hand.add(cr);
				i--;
				aceSwaps++;
			}

			if (aceSwaps > 4) {
				break;
			}
		}

		int totalValue = 0;

		for (Card c : hand) {
			if (c.rank().equals("ace") && totalValue <= 10) {
				totalValue += 11;
			} else if (c.rank().equals("ace") && totalValue > 10) {
				totalValue += 1;
			} else {
				totalValue += c.pointValue();
			}
		}

		return totalValue;
	}

	private class MyMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			System.out.println("(" + e.getX() + ", " + e.getY() + ")");
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}
	}
}