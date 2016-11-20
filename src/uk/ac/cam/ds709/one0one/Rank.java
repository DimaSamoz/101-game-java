package uk.ac.cam.ds709.one0one;

/**
 * The list of the card ranks used in 101. Only 36 cards are used instead of the usual 52, the Deuces, Threes, Fours and
 * Fives are omitted. Typical implementation of displaying a readable string by overriding toString().
 */
public enum Rank {
	ACE("Ace", "A", 11),
	SIX("Six", "6", 6),
	SEVEN("Seven", "7", 7),
	EIGHT("Eight", "8", 8),
	NINE("Nine", "9", 0),
	TEN("Ten", "10", 10),
	JACK("Jack", "J", 2),
	QUEEN("Queen", "Q", 3),
	KING("King", "K", 4);

	private String mDisplayName;
	private String mShortName;
	private int mValue;

	Rank(String displayName, String shortName, int value) {
		mDisplayName = displayName;
		mShortName = shortName;
		mValue = value;
	}

	@Override
	public String toString() {
		return mDisplayName;
	}

	public String shortName() {
		return mShortName;
	}

	public int value() {
		return mValue;
	}

}
