package uk.ac.cam.ds709.one0one;

/**
 * Class representing a player in the game.
 */
public abstract class Player {

	// Fields

	private String mName;
	private int mScore;
	private Hand mHand;
	private Player mPreviousPlayer;
	private Player mNextPlayer;
	private boolean mJustPlacedCard = false;

	// Constructors

	public Player(String name) {
		this.mName = name;
		this.mScore = 0;
		this.mHand = new Hand();
	}

	public Player(String name, boolean justPlacedCard) {
		this(name);
		mJustPlacedCard = justPlacedCard;
	}

	// Accessors

	public String name() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public Hand hand() {
		return mHand;
	}

	public int score() {
		return mScore;
	}

	public void modifyScore(int delta) {
		mScore += delta;
	}

	public Player getNextPlayer() {
		return mNextPlayer;
	}

	public void setNextPlayer(Player nextPlayer) {
		mNextPlayer = nextPlayer;
	}

	public Player getPreviousPlayer() {
		return mPreviousPlayer;
	}

	public void setPreviousPlayer(Player previousPlayer) {
		mPreviousPlayer = previousPlayer;
	}

	/**
	 * Indicates if the player has just placed the card which the next player has to respond to.
	 * Querying the field automatically sets it to false, and it is set to true only after the player
	 * placed a card.
	 *
	 * @return True if the player has placed the last card on the stack, false otherwise.
	 */
	public boolean justPlacedCard() {
		boolean justPlacedCard = mJustPlacedCard;
		mJustPlacedCard = false;
		return justPlacedCard;
	}

	// Methods
	/**
	 * Takes the top card of the supplied deck and adds it to the player's hand.
	 *
	 * @param game The current game
	 * @return The card just taken
	 */
	public Card takeNewCard(Game game) {
		Deck deck = game.getDeck();
		PlayedStack stack = game.getStack();
		Card card = deck.topCard(stack);
		mHand.addCard(card);

		Interface.displayWithMessage(game, mName + " took a card.");
		if (!(this instanceof User)) System.out.println();
		return card;
	}

	public void takeTwoCards(Game game) {
		Deck deck = game.getDeck();
		PlayedStack stack = game.getStack();
		mHand.addCard(deck.topCard(stack));
		mHand.addCard(deck.topCard(stack));

		Interface.displayWithMessage(game, mName + " took two cards.");
		if (!(this.getNextPlayer() instanceof User)) System.out.println();
		//System.out.println();
	}

	/**
	 * The player is dealt a new card which is added to their hand.
	 *
	 * @param card The card dealt to the player.
	 */
	public void beDealtOneCard(Card card) {
		mHand.addCard(card);
	}

	/**
	 * Looks at the face card of the stack and responds according to the game rules.
	 * Six: take two cards and pass
	 * Seven: take one card and pass
	 * Eight: cannot be the face card of the deck
	 * Nine, Ten, Jack: no response
	 * Queen: cover with the suit the previous player requested
	 * King: no response
	 * Ace: pass
	 *
	 * @param game The current game
	 * @return true if the player has to pass this round, false otherwise
	 */
	public boolean respond(Game game) {
		PlayedStack stack = game.getStack();
		Rank faceCardRank = stack.faceCard().rank();
		boolean hasToPass = false;

		switch (faceCardRank) {

			case ACE: {
				hasToPass = true;
			}
				break;
			case SIX: {
				hasToPass = true;
				Interface.wait(1000);
				takeTwoCards(game);
				Interface.wait(1000);
			}
				break;
			case SEVEN: {
				hasToPass = true;
				Interface.wait(1000);
				takeNewCard(game);
				Interface.wait(1000);
			}
				break;
			case QUEEN: {
				Suit suit = mPreviousPlayer.requestSuit(game);
				stack.addCard(Card.getDummyCard(suit));
			}
				break;
			default: { }
			break;
		}
		return hasToPass;

	}

	public int countCards() {
		return mHand.getCards().size();
	}

	/**
	 * Makes a move in the game. Abstract method implemented by each Player subclass.
	 *
	 * @param game The current game
	 */
	public abstract Card makeMove(Game game);

	public abstract Suit requestSuit(Game game);

	/**
	 * Places the given card on the stack and removes it from the player's hand.
	 *
	 * @param game The current game.
	 * @param card  The card to be placed on the stack.
	 */
	public void placeCard(Game game, Card card) {
		PlayedStack stack = game.getStack();
		if (mHand.getCards().remove(card)) {
			// First, remove the dummy card if it is on the stack
			Card faceCard = stack.faceCard();
			// Need to remove card if it is exactly one of the dummy card constants
			if (faceCard.isDummyCard()) stack.takeFaceCard();

			stack.addCard(card);
		}

		mJustPlacedCard = true;

		Interface.displayWithMessage(game, mName + " played the " + card.toString() + ".");
		boolean needsNewline = !(this.getNextPlayer() instanceof User) ||
							   !(this instanceof User && card.rank() == Rank.QUEEN) ||
							   !(this instanceof User) && card.rank() == Rank.EIGHT;
		if (needsNewline) System.out.println();


		if (countCards() == 0) {
			game.win(this);
		}
	}

	/**
	 * When the player has taken a card but can't/doesn't want to play in this round.
	 */
	public void pass(Game game) {
		mJustPlacedCard = false;

		Interface.displayWithMessage(game, mName + " passed.");
		if (!(this.getNextPlayer() instanceof User)) System.out.println();


	}
}
