package uk.ac.cam.ds709.one0one;

/**
 * The list of the card suits used in 101. Typical implementation of
 * displaying a readable string by overriding toString().
 */
public enum Suit {
	SPADES("Spades", "♠"),
	DIAMONDS("Diamonds", "♦"),
	CLUBS("Clubs", "♣"),
	HEARTS("Hearts", "♥");

	private String mDisplayName;
	private String mSymbol;

	Suit(String name, String symbol) {
		mDisplayName = name;
		mSymbol = symbol;
	}

	@Override
	public String toString() {
		return mDisplayName;
	}

	public String symbol() {
		return mSymbol;
	}
}
