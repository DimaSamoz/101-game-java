package uk.ac.cam.ds709.one0one;

public class Main {

    public static void main(String[] args) {

        // Welcome text
        Interface.clearScreen();
		Interface.lines(5);
		System.out.println("\t Welcome to the game 101!");
		Interface.lines(10);
		System.out.print("  Please enter your name:  ");
//		String name = System.console().readLine();
		String name = "Dima";

		System.out.print("  Please enter the number of players (2-9):  ");
//		int numberOfPlayers = Integer.parseInt(System.console().readLine());
		int numberOfPlayers = 4;

		Session session = new Session(name, numberOfPlayers);

    }
}
