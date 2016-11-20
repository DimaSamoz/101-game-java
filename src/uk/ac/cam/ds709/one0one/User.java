package uk.ac.cam.ds709.one0one;

public class User extends Player {

	private int index = 0;
	public User(String name) {
		super(name);
	}

	public User(String name, boolean justPlacedCard) {
		super(name, justPlacedCard);
	}

	@Override
	public Card makeMove(Game game) {

		boolean hasToPass = false;
		if (super.getPreviousPlayer().justPlacedCard()) {
			hasToPass = respond(game);
		}
		if (hasToPass) {
			System.out.println();
			Interface.wait(1000);
			return null;
		}

		Card chosenCard = enterChoice(game, false, false);

		while (chosenCard != null && chosenCard.rank() == Rank.EIGHT) {
			// chosenCard will never be null but the compiler doesn't know that
			placeCard(game, chosenCard);
			Interface.wait(800);
			Interface.displayWithMessage(game, "You need to cover up the Eight.");
			chosenCard = enterChoice(game, false, true);
		}

		return chosenCard;
	}

	private Card enterChoice(Game game, boolean alreadyTookCard, boolean eightFollowUp) {
		PlayedStack stack = game.getStack();

		for (Card card : hand().getCards()) {
			System.out.print("  " + "(" + card.shortTextName() + ")");
		}
		System.out.println(alreadyTookCard ? " | Pass (P)" : " | Take card (T)");

		System.out.print("  Enter your choice:  ");
		String code = System.console().readLine().toUpperCase();
//		String[] responses = {"10C", "T", "P", "T", "P", "9S", "8H", "6H"};
//		String code = responses[index++];
		Card chosenCard = null; // Null represents passing

		if (!alreadyTookCard && code.equals("T")) {
			takeNewCard(game);
			if (eightFollowUp) {
				chosenCard = enterChoice(game, false, true);
			} else {
				chosenCard = enterChoice(game, true, false);
			}
		} else if (alreadyTookCard && code.equals("P")) {
			return null;
		} else {
			boolean found = false;
			for (Card card : hand().getCards()) {
				if (card.shortTextName().equals(code)) {
					chosenCard = card;
					found = true;
					break;
				}
			}
			if (!found) {
				System.out.println("  Error: \"" + code + "\" is not a valid card input. Please try again.");
				Interface.wait(1000);
				chosenCard = enterChoice(game, alreadyTookCard, eightFollowUp);
			}
		}
		if (chosenCard != null && !choiceIsValid(stack, chosenCard)) {
			System.out.println("  The card choice is invalid: you must match suit or rank, or choose a Queen.\n  Please try again.");
			Interface.wait(1000);
			chosenCard = enterChoice(game, alreadyTookCard, eightFollowUp);
		}
		return chosenCard;
	}

	@Override
	public Suit requestSuit(Game game) {
		System.out.println("  Spades (S) | Diamonds (D) | Clubs (C) | Hearts (H)");
		System.out.print("  Request a suit: ");

		String input = System.console().readLine().toUpperCase();
//		String input = "H";
		boolean valid = input.length() == 1 && "SDCH".contains(input);
		Suit suit = null; // Will be necessarily replaced by a valid suit, so this is safe.
		if (valid) {
			switch (input) {
				case "S" : suit = Suit.SPADES;
					break;
				case "D" : suit = Suit.DIAMONDS;
					break;
				case "C" : suit = Suit.CLUBS;
					break;
				case "H" : suit = Suit.HEARTS;
					break;
			}
		} else {
			System.out.println("  Error: \"" + input + "\" is not a valid suit input. Please try again.\n");
			suit = requestSuit(game);
		}

		Interface.clearScreen();
		assert suit != null;
		Interface.displayWithMessage(game, name() + " requested " + suit.toString());
		System.out.println();

		return suit;
	}

	private boolean choiceIsValid(PlayedStack stack, Card chosenCard) {
		return chosenCard.rank() == Rank.QUEEN ||
				stack.faceCard().rank() == chosenCard.rank() ||
				stack.faceCard().suit() == chosenCard.suit();

	}


}
