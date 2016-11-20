package uk.ac.cam.ds709.one0one;

import java.util.List;

/**
 * A class containing auxiliary static methods used in the game to control the command-line interface and user
 * experience
 */
public final class Interface {
	/**
	 * Make the class uninstantiable
	 */
	private Interface() {
	}


	/**
	 * Creates the supplied number of empty new lines in the console.
	 *
	 * @param num The number of inserted new lines.
	 */
	public static void lines(int num) {
		for (int i = 0; i < num; i++) {
			System.out.println();
		}
	}

	/**
	 * Clears the console screen.
	 */
	public static void clearScreen() {
		lines(25);
	}

	/**
	 * Waits the given number of milliseconds.
	 *
	 * @param ms The time of waiting in milliseconds.
	 */
	public static void wait(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays dots to indicate a short waiting period.
	 */
	public static void dots(int interval) {
		for (int i = 0; i < 8; i++) {
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print(".");
		}
	}

	/**
	 * Prints the graphical representation of the player's hand and the face card of the stack.
	 *
	 * @param game The current game
	 */
	public static void display(Game game) {

		Player userPlayer = game.getUserPlayer();
		boolean deckIsEmpty = game.getDeck().isEmpty();
		PlayedStack stack = game.getStack();
		Player[] players = game.getPlayers();
		Card tempCard = null;

		// Avoid displaying the dummy card
		if (stack.faceCard().isDummyCard()) {
			tempCard = stack.takeFaceCard();
		}

		String[] faceCardImage = stack.faceCard().getImage();

		// Display the UI row by row
		for (int i = 0; i < faceCardImage.length; i++) {
			String backLine = deckIsEmpty ? "           " : CardImages.BACK[i];
			String cardLine = faceCardImage[i];

			String opponentCards = "";
			boolean lineWithOpponentCards = 1 <= i && i <= players.length;

			if (lineWithOpponentCards) {
				for (int j = 0; j < players[i - 1].countCards(); j++) {
					opponentCards += "▯";
				}
			}
			System.out.printf("\t%s %s %12s %s%n",
					backLine, cardLine, lineWithOpponentCards ? players[i - 1].name() : "", opponentCards);
		}

		userPlayer.hand().displayHand();

		// Put the dummy card back
		if (tempCard != null) {
			stack.addCard(tempCard);
		}
	}

	public static void displayWithMessage(Game game, String message) {
		clearScreen();
		System.out.println("  " + message);
		display(game);
	}

	/**
	 * Shows the scores in a tabulated form.
	 *
	 * @param names  The names of the players.
	 * @param scores The scores of the players.
	 */
	public static void tabulate(Object[] names, Object[] scores) {
		String leftAlignFormat = "  | %-12s | %-7d |%n";

		System.out.format("  +--------------+---------+%n");
		System.out.printf("  | Name         | Score   |%n");
		System.out.format("  +--------------+---------+%n");
		for (int i = 0; i < names.length; i++) {
			System.out.format(leftAlignFormat, names[i], scores[i]);
		}
		System.out.format("  +--------------+---------+%n");
		lines(3);
	}

	/**
	 * Displays the graphical representation of the player's hand.
	 * Displays the cards in a spread, their indices showing.
	 *
	 * @param cards The list of cards to be displayed.
	 */
	public static void displayHand(List<Card> cards) {
		System.out.println("\n\t  Your cards");
		int numOfCards = cards.size();
		String[] lastCardImage = cards.get(numOfCards - 1).getImage();

		// Top edge
		System.out.printf("  ");
		for (Card card : cards) {
			System.out.printf(" __");
		}
		System.out.printf("_______ \n  ");

		// Ranks
		for (int i = 0; i < numOfCards - 1; i++) {
			Card card = cards.get(i);
			// Ternary operator useful when the rank is 10 and no trailing space is needed
			System.out.print("|" + card.shortRank() + (card.shortRank().length() == 2 ? "" : " "));
		}
		// Prints the second (rank) row of the last card image
		System.out.print(lastCardImage[1] + "\n  ");

		// Suits
		for (int i = 0; i < numOfCards - 1; i++) {
			Card card = cards.get(i);
			System.out.print("|" + card.shortSuit() + " ");
		}
		System.out.print(lastCardImage[2] + "\n  ");

		// Rest of the card
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < numOfCards - 1; j++) {
				System.out.print("|  ");
			}
			System.out.print(lastCardImage[3 + i] + "\n  ");
		}

		// Bottom edge
		for (Card card : cards) {
			System.out.printf(" ‾‾");
		}
		System.out.printf("‾‾‾‾‾‾‾ \n");
	}
}
