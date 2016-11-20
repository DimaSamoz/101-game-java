package uk.ac.cam.ds709.one0one;

import java.util.*;

/**
 * The class representing a session of games.
 */
public class Session {

	// Fields
	private int mNumberOfPlayers;
	private String mPlayerName;
	private Player[] mPlayers;
	private List<Game> mGames;
	private Map<String, Integer> mScores = new HashMap<>();

	// Constructor
	public Session(String name, int numberOfPlayers) {
		mNumberOfPlayers = numberOfPlayers;
		mPlayerName = name;
		startNewSession();
	}


	// Methods

	public void startNewSession() {
		// Welcome message
		Interface.lines(1);
		System.out.print("  A new session is starting");
		Interface.dots(200);
		Interface.clearScreen();

		// Setting up the game data
		mPlayers = new Player[mNumberOfPlayers];
		// User starts
		mPlayers[mPlayers.length - 1] = new User(mPlayerName, true);
		setUpAIOpponents();
		mGames = new LinkedList<>();
		for (Player player : mPlayers) {
			mScores.put(player.name(), player.score());
		}

		showHighScores();
		Interface.lines(5);
		Interface.wait(2000);
		newGame();
	}

	/**
	 * Creates AI opponents of the user.
	 * The number of AI opponents is one less than the number of mPlayers.
	 * The names are taken from a random generated, fixed array.
	 */
	private void setUpAIOpponents() {
		String[] namesArray = {"Alexander", "Christopher", "Tommie", "Alec", "Rolando",
				"Kirby", "Millard", "Mohamed", "Jeffery", "Josh", "Edwin", "Rolf",
				"Fermin", "Delmer", "Merrill", "Daryl", "Cameron", "Garrett", "Ferdinand", "Joe"
		};
		List<String> names = new ArrayList<>(Arrays.asList(namesArray));

		Random rGen = new Random(1);

		for (int i = 0; i < mNumberOfPlayers - 1; i++) {
			// Ensures that all names are different
			String name;
			do {
				name = names.remove(rGen.nextInt(mNumberOfPlayers));
			} while (name.equals(mPlayerName));
			mPlayers[i] = new AI(name);
		}

		// Consolidate the order of the players by setting the previousPlayer and nextPlayer dependencies
		for (int i = 1; i < mPlayers.length - 1; i++) {
			mPlayers[i].setPreviousPlayer(mPlayers[i - 1]);
			mPlayers[i].setNextPlayer(mPlayers[i + 1]);
		}
		mPlayers[0].setPreviousPlayer(mPlayers[mPlayers.length - 1]);
		mPlayers[0].setNextPlayer(mPlayers[1]);
		mPlayers[mPlayers.length - 1].setPreviousPlayer(mPlayers[mPlayers.length - 2]);
		mPlayers[mPlayers.length - 1].setNextPlayer(mPlayers[0]);
	}

	/**
	 * Starts a new game.
	 */
	public void newGame() {
		// The next game number is one more than the number of played games
		int gameNumber = mGames.size() + 1;
		mGames.add(new Game(mPlayers, gameNumber, mPlayers[mPlayers.length - 1]));
	}

	/**
	 * Prints the high score table to the console.
	 * Displays a tabulated list of player names and scores in the console.
	 */
	private void showHighScores() {

		// Sort the scores map by value - courtesy of Carter Page at
		// http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
		List<Map.Entry<String, Integer>> list =
				new LinkedList<>(mScores.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<String, Integer> sortedScores = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> entry : list) {
			sortedScores.put(entry.getKey(), entry.getValue());
		}

		Object[] names = sortedScores.keySet().toArray();
		Object[] scores = sortedScores.values().toArray();


		System.out.println("\tHigh scores");
		Interface.tabulate(names, scores);
	}


}
