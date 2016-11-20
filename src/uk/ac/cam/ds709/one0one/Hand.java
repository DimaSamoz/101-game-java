package uk.ac.cam.ds709.one0one;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class representing the hand of cards each Player is dealt.
 */
public class Hand {

	// Fields
	private List<Card> mCards = new ArrayList<>();

	// Constructor
	public Hand() {}

	public Hand(List<Card> cards) {
		mCards = cards;
	}

	// Accessors
	public List<Card> getCards() {
		Collections.sort(mCards, Card.VALUE_COMPARATOR);
		return mCards;
	}

	// Methods
	/**
	 * Add the supplied card to the player's hand.
	 *
	 * @param card The card to be added to the player's hand.
	 */
	public void addCard(Card card) {
		mCards.add(card);
	}

	/**
	 * Filters the player's hand based on the given suit.
	 *
	 * @param suit The suit used to filter the cards.
	 * @return A sorted list of all cards whose suit matches the input.
	 */
	public List<Card> filterBySuit(Suit suit) {
		List<Card> sameSuit = new ArrayList<>();

		for (Card card : mCards) {
			if (card.suit() == suit && card.rank() != Rank.QUEEN && card.rank() != Rank.EIGHT)
				sameSuit.add(card);
		}
		Collections.sort(sameSuit, Card.VALUE_COMPARATOR);
		return sameSuit;
	}

	/**
	 * Filters the player's hand based on the given rank.
	 *
	 * @param rank The rank used to filter the cards.
	 * @return A list of all cards whose rank matches the input.
	 */
	public List<Card> filterByRank(Rank rank) {
		List<Card> sameRank = new ArrayList<>();

		for (Card card : mCards) {
			if (card.rank() == rank && card.rank() != Rank.QUEEN) sameRank.add(card);
		}

		return sameRank;
	}

	/**
	 * Searches for queens in the player's hand.
	 *
	 * @return A list containing the Queens in the player's hand.
	 */
	public List<Card> filterQueens() {
		List<Card> queens = new ArrayList<>();

		for (Card card : mCards) {
			if (card.rank() == Rank.QUEEN) queens.add(card);
		}
		return queens;
	}

	/**
	 * Searches for eights in the player's hand.
	 *
	 * @return A list containing the Queens in the player's hand.
	 */
	public List<Card> filterEights() {
		return filterByRank(Rank.EIGHT);
	}

	/**
	 * Displays the graphical representation of the player's hand.
	 * Delegates the display to the Interface class to keep the UI methods together.
	 * The cards are in ascending order by their value.
	 */
	public void displayHand() {
		Interface.displayHand(getCards());
	}
}
