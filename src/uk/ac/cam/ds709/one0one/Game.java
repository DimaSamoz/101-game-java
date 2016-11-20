package uk.ac.cam.ds709.one0one;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the current game.
 */
public class Game {

	// Fields
	private final int mNumOfPlayers;
	private final Player[] mPlayers;
	private final int mGameNumber;
	private final Player mUserPlayer;
	private final Deck mDeck;
	private final PlayedStack mStack;
	private Player mWinner = null;

	// Constructors
	public Game(Player[] players, int gameNumber, Player userPlayer) {
		mPlayers = players;
		mNumOfPlayers = mPlayers.length;
		mGameNumber = gameNumber;
		mUserPlayer = userPlayer;
		mDeck = new Deck();
		mStack = new PlayedStack();

		startNewGame();
	}

	// For creating a game with a temporary stack
	public Game(Deck deck, PlayedStack playedStack){
		mPlayers = new Player[1];
		mNumOfPlayers = mPlayers.length;
		mGameNumber = 1;
		mUserPlayer = new User("");
		mDeck = deck;
		mStack = playedStack;


	}

	// Accessors
	public PlayedStack getStack() {
		return mStack;
	}

	public Player getUserPlayer() {
		return mUserPlayer;
	}

	public Deck getDeck() {
		return mDeck;
	}

	public Player[] getPlayers() {
		return mPlayers;
	}

	// Methods
	public void startNewGame() {
		System.out.print("  Starting Game #" + mGameNumber);
		Interface.dots(200);
		Interface.clearScreen();

		// Setting up the game
		mDeck.shuffle();
		Card startingCard = mDeck.deal(mPlayers);
		mStack.addCard(startingCard);

		Interface.display(this);

		while (mWinner == null) {
			for (Player player : mPlayers) {
				Card chosenCard = player.makeMove(this);
				if (chosenCard != null) {
					player.placeCard(this, chosenCard);
				} else {
					player.pass(this);
				}



				if (mWinner != null) {
					if ((mStack.faceCard().rank() == Rank.SEVEN) || (mStack.faceCard().rank() == Rank.SIX)) {
						respondAfterWin(player);
					}
					break;
				}
			}
		}

		Interface.clearScreen();
		System.out.println("  " + mWinner.name() + " won this game!");
		Interface.lines(11);
		Interface.wait(2000);

		calculateScores();


	}

	/**
	 * If the last card of the winner was a seven or a six, the next player still has to take the necessary number of cards.
	 * @param player The winning player.
	 */
	private void respondAfterWin(Player player) {
		System.out.println();
		Interface.wait(1000);
		if (mStack.faceCard().rank() == Rank.SEVEN) {
			player.getNextPlayer().takeNewCard(this);
		} else if (mStack.faceCard().rank() == Rank.SIX) {
			player.getNextPlayer().takeTwoCards(this);
		}
		System.out.println();
		Interface.wait(1000);
	}

	public void win(Player player) {
		mWinner = player;
	}

	private void calculateScores() {
		// List the losing players
		List<Player> losingPlayers = new ArrayList<>();
		for (Player player : mPlayers) {
			if (player != mWinner) losingPlayers.add(player);
		}

		// Print message if winner finished with a Queen
		Card winningCard = mStack.faceCard();
		if (winningCard.rank() == Rank.QUEEN) {
			if (winningCard.suit() == Suit.SPADES) {
				System.out.println("  " + mWinner.name() + " finished with the Queen of Spades -> -40");
				mWinner.modifyScore(-40);
			} else {
				System.out.println("  " + mWinner.name() + " finished with the Queen of " + winningCard.suit().toString() + " -> -20");

				mWinner.modifyScore(-20);
			}
			Interface.wait(1000);
		}


		for (int i = 0; i < losingPlayers.size(); i++) {
			for (int j = 0; j <= i; j++) {
				Player player = losingPlayers.get(j);
				if (player != mWinner) {
					String remainingCardsNames = "";
					int score = 0;
					List<Card> remainingCards = player.hand().getCards();
					if (remainingCards.size() == 1 && remainingCards.get(0).rank() == Rank.QUEEN) {
						Card lastCard = remainingCards.get(0);
						if (lastCard.suit() == Suit.SPADES) {
							remainingCardsNames = lastCard.shortSymbolName();
							score = 40;
						}
						else {
							remainingCardsNames = lastCard.shortSymbolName();
							score = 20;
						}
					} else {
						for (Card card : remainingCards) {
							remainingCardsNames += card.shortSymbolName() + " ";
							score += card.rank().value();
						}
					}
					player.modifyScore(score);
					System.out.printf("  %-12s: %-25s -> %d%n", player.name(), remainingCardsNames, score);

				}
			}
			Interface.lines(15 - i);
			Interface.wait(1000);
		}
	}

}
