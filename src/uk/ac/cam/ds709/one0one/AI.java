package uk.ac.cam.ds709.one0one;

import java.util.*;

public class AI extends Player {

	// Fields

	private Random mProbGen = new Random(1);

	public AI(String name) {
		super(name);
	}

	public AI(String name, boolean justPlacedCard) {
		super(name, justPlacedCard);
	}

	/**
	 * AI player chooses a card and makes a valid move, or takes a new card from the deck. The general strategy is:
	 * <p/>
	 * - get rid of high value cards as soon as possible <p/> - try to build combinations using Eights <p/> - hold back
	 * Queens except when there are more than one (keep the Queen of Spades till the end) <p/> - use a 6, 7 or Ace if
	 * the next player has few cards in his/her hand
	 *
	 * @param game The current game
	 * @return Returns the chosen card or null, if the player has to pass.
	 */
	@Override
	public Card makeMove(Game game) {

		Hand hand = super.hand();
		boolean hasToPass = false;
		if (super.getPreviousPlayer().justPlacedCard()) {
			hasToPass = respond(game);
		}
		if (hasToPass) {
			Interface.wait(1000);
			return null;
		}

		Interface.dots(350);
		return chooseCard(game, hand, false);
	}

	private Card chooseCard(Game game, Hand hand, boolean alreadyTookCard) {
		Card chosenCard = null;
		Card faceCard = game.getStack().faceCard();


		// All four lists are disjoint
		List<Card> matchingSuit = hand.filterBySuit(faceCard.suit());
		List<Card> matchingRank = hand.filterByRank(faceCard.rank());
		List<Card> queens = hand.filterQueens();
		List<Card> eights = hand.filterEights();

		/* Adds a small amount of randomness to the game. Numbers can be tweaked and
		later replaced with better tactic evaluation, but this makes the game
		less deterministic and "simulates" different playing strategies (or stupidity). */

		// A proper move is a valid card which is not a Queen or an Eight
		boolean hasProperMove = !matchingRank.isEmpty() || !matchingSuit.isEmpty();

		// TODO: if (hasProperMove && eights.size() > 1) {}

		if (hasProperMove) { // Player has at least one suitable card
			chosenCard = findProperCard(game, matchingSuit, matchingRank, eights);

		} else if (!queens.isEmpty()) { // Player has no proper cards, but has queens
			chosenCard = findQueen(game, alreadyTookCard, queens);
			if (chosenCard == null) chosenCard = null; // Pass

		} else if (!eights.isEmpty()) {
			findEight(game, alreadyTookCard, eights, faceCard.suit());

		} else { // No suitable cards in the hand
			if (alreadyTookCard) chosenCard = null; // Pass
			else {
				// No valid moves, must take new card
				takeNewCard(game);
				Interface.dots(200);
				chosenCard = chooseCard(game, hand, true); // Recursive call, but can only repeat once
			}
		}

		return chosenCard;
	}

	private Card findProperCard(Game game, List<Card> matchingSuit, List<Card> matchingRank,
								List<Card> eights) {
		// A list of the high value cards
		List<Card> highValueCards = new ArrayList<>();
		if (!matchingRank.isEmpty())
			highValueCards.add(matchingRank.get(matchingRank.size() - 1));
		if (!matchingSuit.isEmpty())
			highValueCards.add(matchingSuit.get(matchingSuit.size() - 1));

		boolean nextPlayerHasFewCards =
				getNextPlayer().countCards() == 1 || getNextPlayer().countCards() == 2;
		List<Card> actionCards = null;

		if (nextPlayerHasFewCards) {
			// A list of action cards (6, 7, Ace)
			actionCards = new ArrayList<>();
			for (Card card : matchingSuit) {
				if (card.rank() == Rank.SIX || card.rank() == Rank.SEVEN || card.rank() == Rank.ACE)
					actionCards.add(card);
			}
			for (Card card : matchingRank) {
				if (card.rank() == Rank.SIX || card.rank() == Rank.SEVEN || card.rank() == Rank.ACE)
					actionCards.add(card);
			}
		}

		// Evaluate choices
		Card chosenCard = Collections.max(highValueCards, Card.VALUE_COMPARATOR);

		/* Accounts for the scenario  of the next player having few cards:
		 most of the time we would use an action card to make them skip
		 the next move (and maybe give them new cards). */
		if (nextPlayerHasFewCards && !actionCards.isEmpty() && mProbGen.nextInt(100) < ProbConsts.USE_ACTION_CARD) {
			chosenCard = findActionCard(actionCards);
		}

		for (Card eight : eights) { // If there is an eight with a matching suit, we can combine the two
			if (eight.suit() == chosenCard.suit()) {
				placeCard(game, eight);
				Interface.wait(1000);
				break;
			}
		}

		return chosenCard;
	}

	private Card findActionCard(List<Card> actionCards) {
		assert !actionCards.isEmpty();
		Card chosenCard;
		List<Card> sixes = (new Hand(actionCards)).filterByRank(Rank.SIX);
		List<Card> sevens = (new Hand(actionCards)).filterByRank(Rank.SEVEN);
		List<Card> aces = (new Hand(actionCards)).filterByRank(Rank.ACE);

		// In this scenario we would prefer using up a six, seven or Ace, in this order
		boolean chooseSix = (!sixes.isEmpty() && (mProbGen.nextInt(100) < ProbConsts.USE_SIX)) ||
							(!sixes.isEmpty() && sevens.isEmpty());
		boolean chooseSeven = (!sevens.isEmpty() && (mProbGen.nextInt(100) < ProbConsts.USE_SEVEN)) ||
							  (!sevens.isEmpty() && aces.isEmpty());
		if (chooseSix) {
			chosenCard = sixes.get(0); // Suit (if there are more sixes) does not matter
		} else if (chooseSeven) {
			chosenCard = sevens.get(0);
		} else {
			// actionCards is not null, so it must contain an Ace
			chosenCard = aces.get(0);
		}
		return chosenCard;
	}


	private Card findQueen(Game game, boolean alreadyTookCard,
						   List<Card> queens) {

		Card chosenCard = null; // Returning null means passing

		if (queens.size() > 1) {
			// There are multiple Queens, we can use one up, preferably a non-Spade one
			for (Card queen : queens) {
				if (queen.suit() != Suit.SPADES) chosenCard = queen;
			}
		} else { // Only one queen
				/* If we already took a card, we would use up our single queen.
				If not, we would most likely  take another card and if that's unsuitable,
				use up the queen. */
			Card queen = queens.get(0);
			if ((alreadyTookCard && (queen.suit() != Suit.SPADES)) || (mProbGen.nextInt(100) < ProbConsts.USE_QUEEN)) {
				// Probably wouldn't use up the Queen of Spades right away
				chosenCard = queen;
			} else if (alreadyTookCard) {
				chosenCard = null; // Pass
			} else {
				// Take a new card and reevaluate
				takeNewCard(game);
				Interface.dots(200);
				chooseCard(game, super.hand(), true);
			}
		}
		return chosenCard;
	}


	private Card findEight(Game game, boolean alreadyTookCard,
						   List<Card> eights, Suit suit) {

		Card chosenCard = null; // Returning null means passing

		Card eightMatchingSuit = null;
		for (Card card : eights) {
			if (card.suit() == suit) eightMatchingSuit = card;
		}

		// If no eight has the right suit, we must take a new card
		if (eightMatchingSuit == null) {
			if (!alreadyTookCard) {
				takeNewCard(game);
				Interface.dots(200);
				chosenCard = chooseCard(game, super.hand(), true);
			}
		} else { // There is an eight with a matching suit
			if (eights.size() > 1) { // The player has several eights which can be chained

				chosenCard = chainEights(game, eights, eightMatchingSuit);
			} else { // The player has one eight but no follow-up card
				/* In general it is a risky idea to use up an Eight
				 if we cannot chain it with anything. Most of the time
		 		we would take another card. */
				if (ProbConsts.USE_EIGHT_WO_FOLLOWUP < mProbGen.nextInt(100)) {
					if (!alreadyTookCard) {
						takeNewCard(game);
						Interface.dots(200);
						chosenCard = chooseCard(game, super.hand(), true);
					}
				} else { // Play the eight without a follow-up card in hand
					// The player plays the eight after all, and prays that they don't have to pick up the whole deck
					Card eight = eights.get(0);
					chosenCard = playEightWithoutFollowUp(game, eight);
				}
			}
		}
		return chosenCard;
	}

	private Card playEightWithoutFollowUp(Game game, Card eight) {
		Card chosenCard;
		Card singleEight = eight;
		placeCard(game, singleEight);
		Interface.wait(1000);
		Card takenCard;
		do { // Infinite loop: breaks when the taken card is not an Eight
			boolean suitable;
			do {
				takenCard = takeNewCard(game);
				suitable = takenCard.rank() == Rank.QUEEN ||
						takenCard.rank() == Rank.EIGHT ||
						takenCard.suit() == singleEight.suit();
			} while (!suitable);
			if (takenCard.rank() == Rank.EIGHT) {
				// A new eight is placed on the stack, which might be covered with a card already in hand
				singleEight = takenCard;
				placeCard(game, singleEight);
				Interface.wait(1000);
				takenCard = chooseCard(game, super.hand(), true);
				if (takenCard != null) break; // Found a suitable card in hand
			} else break;
		} while (true);
		chosenCard = takenCard;
		return chosenCard;
	}

	private Card chainEights(Game game, List<Card> eights, Card eightMatchingSuit) {
		Card chosenCard;
		eights.remove(eightMatchingSuit);
		// eights now contains all Eights which do not match the face card

		// Create a temporary hand containing every card except the eights
		Hand tempHand = new Hand();
		List<Card> cardsInHand = super.hand().getCards();
		for (Card card : cardsInHand) {
			if (card.rank() != Rank.EIGHT) tempHand.addCard(card);
		}

				/* For each different eight we find the cards that we would choose
				if that eight was on the top of the played stack (that is, find the
				best cards for the corresponding suits in the hand.
				 */
		List<Card> topValueCards = new ArrayList<>();
		for (Card eight : eights) {
			PlayedStack tempStack = new PlayedStack();
			tempStack.addCard(eight);
			Card tempChosenCard = chooseCard(new Game(game.getDeck(), tempStack), tempHand, true);
			if (tempChosenCard != null) {
				topValueCards.add(tempChosenCard);
			}
		}
		if (!topValueCards.isEmpty()) { // Found a suitable card
			chosenCard = Collections.max(topValueCards, Card.VALUE_COMPARATOR);

			List<Card> eightChain = new LinkedList<>();
			Card terminatingEight = null; // The last eight in the chain, its suit must match that of chosenCard
			eightChain.add(eightMatchingSuit);
			for (Card eight : eights) {
				if (eight.suit() == chosenCard.suit()) terminatingEight = eight;
				else eightChain.add(eight);
			}
			eightChain.add(terminatingEight);

			for (Card eight : eightChain) {
				placeCard(game, eight);
				Interface.wait(1000);
			}

		} else { // Only eights, but no suitable follow-up cards
			if (ProbConsts.USE_EIGHT_CHAIN_WO_FOLLOWUP < mProbGen.nextInt(100)) {
				chosenCard = null; // Pass
			} else { // Play all the eights, then take cards from the deck until we can cover them
				placeCard(game, eightMatchingSuit);
				Interface.wait(1000);

				for (int i = 0; i < eights.size() - 1; i++) {
					Card eight = eights.get(i);
					placeCard(game, eight);
					Interface.wait(1000);
				}
				chosenCard = playEightWithoutFollowUp(game, eights.get(eights.size() - 1));
			}
		}
		return chosenCard;
	}

	/**
	 * Request a suit after playing a Queen.
	 */
	@Override
	public Suit requestSuit(Game game) {
		Interface.wait(1000);
		List<Card> cards = super.hand().getCards();
		Card highestValue = Collections.max(cards, Card.VALUE_COMPARATOR);

		Interface.displayWithMessage(game, name() + " requests " + highestValue.suit().toString() + ".");
		return highestValue.suit();
	}

	private class ProbConsts {
		public static final int USE_QUEEN = 15;
		public static final int USE_EIGHT_WO_FOLLOWUP = 15;
		public static final int USE_EIGHT_CHAIN_WO_FOLLOWUP = 25;
		public static final int USE_ACTION_CARD = 95;
		public static final int USE_SIX = 80;
		public static final int USE_SEVEN = 90;
	}


}
