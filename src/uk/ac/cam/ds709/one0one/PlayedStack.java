package uk.ac.cam.ds709.one0one;

import java.util.Stack;

/**
 * The class representing the stack of cards already played
 */
public class PlayedStack {

	// Fields
	private Stack<Card> mStack = new Stack<>();

	// Constructor
	public PlayedStack() {
		mStack = new Stack<>();
	}


	// Methods

	/**
	 * Adds the supplied card to the stack
	 *
	 * @param card The card to be added to the top of the stack
	 */
	public void addCard(Card card) {
		mStack.push(card);
	}

	/**
	 * Returns the face card of the played stack without removing it from the stack.
	 * @return The face card of the played stack.
	 */
	public Card faceCard() {
		return mStack.peek();
	}

	public Card takeFaceCard() {
		return mStack.pop();
	}

	public boolean isEmpty() {
		return mStack.empty();
	}

}
