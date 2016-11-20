package uk.ac.cam.ds709.one0one;


import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/**
 * The class representing the deck of cards.
 */
public class Deck {

	// Fields
	public static final int DECK_SIZE = 36;
	// TODO
	public final Stack<Card> mDeck = new Stack<>();


	// Constructors

	/**
	 * Creates a full deck of cards by iterating through the values of the Rank and Suit enum
	 */
	public Deck() {
		for (Suit suit : Suit.values()) {
			for (Rank rank : Rank.values()) {
				mDeck.push(new Card(rank, suit));
			}
		}
	}

	// Methods

	public boolean isEmpty() {
		return mDeck.size() == 0;
	}

	/**
	 * Shuffles the deck of cards, leaving it randomly permuted.
	 */
	public void shuffle() {
		Collections.shuffle(mDeck, new Random(1));
	}


	/**
	 * Deals the cards from the deck at the start of the game.
	 * Four cards are dealt to each player starting from the left of the winner
	 * of the last game. The last card dealt to the winner is placed face up on the
	 * table as the first card of the played stack.
	 *
	 * @param players The players in the game, the last player in the array was the previous winner.
	 * @return The starting card of the played stack.
	 */
	public Card deal(Player[] players) {
		// Each player gets at least three cards
		for (int i = 0; i < 3; i++) {
			for (Player player : players) {
				player.beDealtOneCard(topCard(new PlayedStack()));
			}
		}

		// The losers in the previous game get a fourth card
		for (int i = 0; i < players.length - 1; i++) {
			players[i].beDealtOneCard(topCard(new PlayedStack()));
		}

		// Return the next card, which is then added to the stack
		return topCard(new PlayedStack());
	}


	/**
	 * Removes and returns the top card of the deck.
	 *
	 * @return The first Card in the list of cards.
	 */
	public Card topCard(PlayedStack stack) {
		if (!mDeck.empty()) {
			return mDeck.pop();
		} else {
			refill(stack);
			return mDeck.pop();
		}
	}

	/**
	 * Refills the deck from the played stack of cards.
	 * When the deck has run out of cards, i.e. all cards are either in
	 * the played stack or in the hands of the players, by the rules
	 * of the game the played stack is turned over and
	 * used as the deck henceforth, while its face card is left
	 * face up on the table as the new bottom card of the played stack.
	 *
	 * @param stack The played stack which is then used as the deck.
	 */
	public void refill(PlayedStack stack) {
		Card faceCard = stack.takeFaceCard();
		while (!stack.isEmpty()) {
			mDeck.push(stack.takeFaceCard());
		}
		stack.addCard(faceCard);
	}
}
