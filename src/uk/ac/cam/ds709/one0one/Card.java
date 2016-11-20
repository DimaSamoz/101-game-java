package uk.ac.cam.ds709.one0one;

import java.util.Comparator;

/**
 * A class representing a playing card in the deck. The card has a suit and a rank, given in the private Suit and Rank
 * fields. An overridden toString() method is included.
 */

public class Card {

	// Fields
	public static final CardValueComparator VALUE_COMPARATOR = new CardValueComparator();

	private final Rank mRank;
	private final Suit mSuit;
	private String[] mImage;

	/* When a queen is played and a suit is requested, we can look at the queen as if it was
	* of the requested suit, and which needs to be covered normally by the next player.
	* For example, if the Queen of Hearts is played and Clubs are requested, we could
	* see the QH as the QC which the next player can cover with either a Queen or a card
	* of matching suit. This is the inspiration behind this approach.*/
	// List of dummy cards: used to "cover up" queens so that the next player can respond accordingly
	public static final Card DUMMY_SPADE = new Card(Rank.QUEEN, Suit.SPADES);
	public static final Card DUMMY_DIAMOND = new Card(Rank.QUEEN, Suit.DIAMONDS);
	public static final Card DUMMY_CLUB = new Card(Rank.QUEEN, Suit.CLUBS);
	public static final Card DUMMY_HEART = new Card(Rank.QUEEN, Suit.HEARTS);


	private static class CardValueComparator implements Comparator<Card> {
		@Override
		public int compare(Card o1, Card o2) {
			return o1.rank().value() - o2.rank().value();
		}
	}


	// Constructors
	public Card(Rank rank, Suit suit) {
		this.mRank = rank;
		this.mSuit = suit;

		mImage = retrieveImage();
	}

	// Accessors
	public Rank rank() {
		return mRank;
	}

	public Suit suit() {
		return mSuit;
	}

	public String[] getImage() {
		return mImage;
	}

	// Methods
	@Override
	public String toString() {
		return mRank + " of " + mSuit;
	}

	public String shortRank() {
		return mRank.shortName();
	}

	public String shortSuit() {
		return mSuit.symbol();
	}

	public String shortSymbolName() {
		return shortRank() + shortSuit();
	}

	public String shortTextName() {
		String suit = "";
		switch (mSuit) {
			case SPADES: suit = "S";
				break;
			case DIAMONDS: suit = "D";
				break;
			case CLUBS: suit = "C";
				break;
			case HEARTS: suit = "H";
				break;
		}
		return mRank.shortName() + suit;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Card card = (Card) o;

		if (mRank != card.mRank) return false;
		return mSuit == card.mSuit;

	}

	@Override
	public int hashCode() {
		int result = mRank.hashCode();
		result = 31 * result + mSuit.hashCode();
		return result;
	}

	public static Card getDummyCard(Suit suit) {
		Card card = null; // Will necessarily be changed in the switch statement, so this is safe
		switch (suit) {
			case SPADES: card = DUMMY_SPADE;
				break;
			case DIAMONDS: card = DUMMY_DIAMOND;
				break;
			case CLUBS: card = DUMMY_CLUB;
				break;
			case HEARTS: card = DUMMY_HEART;
				break;
		}
		return card;
	}

	public boolean isDummyCard() {
		return this == DUMMY_CLUB || this == DUMMY_DIAMOND || this == DUMMY_SPADE || this == DUMMY_HEART;
	}

	/**
	 * Gets the String array representing the ASCII image of the card.
	 * UGLY
	 */
	private String[] retrieveImage() {
		String[] image = new String[9];
		switch (mRank) {
			case ACE: {
				switch (mSuit) {
					case SPADES: image = CardImages.Ace.SPADES;
						break;
					case DIAMONDS: image = CardImages.Ace.DIAMONDS;
						break;
					case CLUBS: image = CardImages.Ace.CLUBS;
						break;
					case HEARTS: image = CardImages.Ace.HEARTS;
						break;
				}
			}
			break;
			case SIX: {
				switch (mSuit) {
					case SPADES: image = CardImages.Six.SPADES;
						break;
					case DIAMONDS: image = CardImages.Six.DIAMONDS;
						break;
					case CLUBS: image = CardImages.Six.CLUBS;
						break;
					case HEARTS: image = CardImages.Six.HEARTS;
						break;
				}
			}
			break;
			case SEVEN: {
				switch (mSuit) {
					case SPADES: image = CardImages.Seven.SPADES;
						break;
					case DIAMONDS: image = CardImages.Seven.DIAMONDS;
						break;
					case CLUBS: image = CardImages.Seven.CLUBS;
						break;
					case HEARTS: image = CardImages.Seven.HEARTS;
						break;
				}
			}
			break;
			case EIGHT: {
				switch (mSuit) {
					case SPADES: image = CardImages.Eight.SPADES;
						break;
					case DIAMONDS: image = CardImages.Eight.DIAMONDS;
						break;
					case CLUBS: image = CardImages.Eight.CLUBS;
						break;
					case HEARTS: image = CardImages.Eight.HEARTS;
						break;
				}
			}
			break;
			case NINE: {
				switch (mSuit) {
					case SPADES: image = CardImages.Nine.SPADES;
						break;
					case DIAMONDS: image = CardImages.Nine.DIAMONDS;
						break;
					case CLUBS: image = CardImages.Nine.CLUBS;
						break;
					case HEARTS: image = CardImages.Nine.HEARTS;
						break;
				}
			}
			break;
			case TEN: {
				switch (mSuit) {
					case SPADES: image = CardImages.Ten.SPADES;
						break;
					case DIAMONDS: image = CardImages.Ten.DIAMONDS;
						break;
					case CLUBS: image = CardImages.Ten.CLUBS;
						break;
					case HEARTS: image = CardImages.Ten.HEARTS;
						break;
				}
			}
			break;
			case JACK: {
				switch (mSuit) {
					case SPADES: image = CardImages.Jack.SPADES;
						break;
					case DIAMONDS: image = CardImages.Jack.DIAMONDS;
						break;
					case CLUBS: image = CardImages.Jack.CLUBS;
						break;
					case HEARTS: image = CardImages.Jack.HEARTS;
						break;
				}
			}
			break;
			case QUEEN: {
				switch (mSuit) {
					case SPADES: image = CardImages.Queen.SPADES;
						break;
					case DIAMONDS: image = CardImages.Queen.DIAMONDS;
						break;
					case CLUBS: image = CardImages.Queen.CLUBS;
						break;
					case HEARTS: image = CardImages.Queen.HEARTS;
						break;
				}
			}
			break;
			case KING: {
				switch (mSuit) {
					case SPADES: image = CardImages.King.SPADES;
						break;
					case DIAMONDS: image = CardImages.King.DIAMONDS;
						break;
					case CLUBS: image = CardImages.King.CLUBS;
						break;
					case HEARTS: image = CardImages.King.HEARTS;
						break;
				}
			}
			break;
		}
		return image;
	}



}
