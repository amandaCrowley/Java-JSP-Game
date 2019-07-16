package mainPackage;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.servlet.ServletContext;

/*
 * Amanda Crowley - c3137540
 * Assignment 2 SENG2050
 * The DealGame class stores all of the game's logic and attributes.
 * This includes methods used to manipulate the briefcases, saved game data and banker offers etc.
 * 
 */

public class DealGame implements java.io.Serializable{

	private static final long serialVersionUID = 1L;	

	private List<Briefcase> briefcases = new ArrayList<Briefcase>();
	private final double[] amounts = new double[] { 0.5, 1, 10, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000 };
	private int briefcaseCount = 0; //Number briefcases opened
	private int roundNumber = 1;
	private boolean gameOver = false;
	private String userName = "";
	private boolean offerRound = false;
	private String offerAmount = ""; //used to display offer on deal page
	private double largestAmount = 0;

	public DealGame() { 
		newGameBriefcases();
	}

	/*Simple getters and setters*/
	public List<Briefcase> getBriefcases() {
		return briefcases;
	}

	public void setOfferRound(boolean offer) {
		offerRound = offer;
	}
	
	public boolean getOfferRound() {
		return offerRound;
	}

	public void setUserName(String name) {
		userName = name;
	}

	public String getUserName() {
		return userName;
	}

	public double[] getAmounts() {
		return amounts;
	}

	public void setGameOver(boolean gameFinish) {
		gameOver = gameFinish;
	}

	public boolean isGameOver() {
		return gameOver;
	}
	
	public String getofferAmount() {
		return offerAmount;
	}
	
	public void setofferAmount(String amount) {
		offerAmount = amount;
	}
	
	public double getlargestAmount() {
		return largestAmount;
	}
	
	/*
	 * Set largestAmount as the highest value contained in an unopened briefcase left in the game
	 * */
	public void setlargestAmount(double amt) {
		double largestCurrentAmount = 0;

		if(briefcases != null) {
			for(Briefcase b : briefcases) {
				if(b.getAmount() > largestCurrentAmount && !b.isBriefcaseOpen()) { //Check amount is higher than previous amount, also briefcase must be not opened
					largestCurrentAmount = b.getAmount();
				}
			}
		}
		largestAmount = largestCurrentAmount;
	}
	
	/* Determines whether the player is in an offer round on not depending on how many briefcases have been opened in the game so far
	 * If the required number have been opened for the next round the round number is increased and the true is returned
	 * Once 11 briefcases have been opened the game has ended and the last remaining briefcase contains the amount the player has won 
	 * Returns a boolean
	 */ 
	public boolean isOfferRound() {
		if(briefcaseCount == 4) { //end round 1
			roundNumber++;
			return true;
		}else if(briefcaseCount == 7) { //end round 2
			roundNumber++;
			return true;
		}else if(briefcaseCount == 9) { //end round 3
			roundNumber++;
			return true;
		}else if(briefcaseCount == 10) { //end round 4
			roundNumber++;
			return true;
		}else if(briefcaseCount == 11) {//end round 5
			setGameOver(true); //All required briefcases have been opened
			return true;
		}
		return false;
	}

	
	 /* Assign the 12 briefcases random values for a new game
	 * Used at the start of each new game
	 * The values used set $ amounts which are stored within the amounts list and are assigned randomly
	 */
	public void newGameBriefcases() {
		LinkedList<Double> briefcaseValues = new LinkedList<Double>();
		for (int i = 0; i < amounts.length; i++) {
			briefcaseValues.add(amounts[i]); //add briefcase $ amounts to briefcaseValues list
		}

		//Now add the random briefcases to the new game
		for(int i = 0; i < 12; i++) { //game starts with 12 briefcases
			Random ran = new Random();
			int randomIndex = ran.nextInt(briefcaseValues.size());	 //Randomly choose a number (using the size of the amounts array, possible nums= 1-12)

			Briefcase b = new Briefcase();
			b.setCaseID(i);
			b.setAmount(briefcaseValues.get(randomIndex));

			briefcases.add(b); //Add briefcase to game
			briefcaseValues.remove(randomIndex); 	  //remove that amount from the array so it can't be used again for the next briefcase (if there is one)
		}

	}

	 
	 /* Calculate the monetary value of the banker's offer (Calculated at the end of each round)
	 * The amount offered is based on the following formula: the total amount of the money in the remaining cases divided by the number of cases remaining.
	 * Returns a formatted string representing the amount of money the banker is offering the player
	 */
	public void processOffer() {
		int briefcasesUnopened = 12;
		double offerAmount = 0;

		for(int i = 0; i < briefcases.size(); i ++) {
			if(!briefcases.get(i).isBriefcaseOpen()) {
				offerAmount = offerAmount + briefcases.get(i).getAmount();
			}
		}
		briefcasesUnopened = briefcasesUnopened - briefcaseCount;
		offerAmount = (offerAmount / briefcasesUnopened); 

		NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(Locale.US); //Currency formatting to use for final rounded amount
		double roundedAmt = ((double) (long) (offerAmount * 20 + 0.5)) / 20; //Round to the nearest 5c as offer amounts are currency values
		
		setofferAmount(usdCostFormat.format(roundedAmt));
		//return usdCostFormat.format(roundedAmt);
	}

	
	/* Open a briefcase - Change briefcase open value to true
	 * The briefcase opened is based on the amount passed into the method (amounts are unique)
	 * Briefcases are opened when they are clicked on by the player on the dealPage
	 */
	public void openBriefcase(double amount) {
		for(int i = 0; i < briefcases.size(); i++){
			if(briefcases.get(i).getAmount() == amount) {
				briefcases.get(i).setBriefcaseOpen(true);			//Change briefcase open value to true
			}
		}
		briefcaseCount++; //increment briefcaseCount to indicate a briefcase has been selected
	}

	/* Retrieve the number of briefcases left within the round that the player is up to
	 * The number left is calculated by using the number of briefcases that should be open at the end of the round minus how many are currently open
	 * Returns an int
	 */
	public int getBriefcasesLeftInRound() {
		int briefcasesLeft = 0;
		//Number of briefcases that should be open by the end of each round
		final int openBriefcasesRound1 = 4;
		final int openBriefcasesRound2 = 7;
		final int openBriefcasesRound3 = 9;
		final int openBriefcasesRound4 = 10;
		final int openBriefcasesRound5 = 11;

		switch(roundNumber) {
			case 1: briefcasesLeft = openBriefcasesRound1 - briefcaseCount;
				break;
			case 2:briefcasesLeft = openBriefcasesRound2 - briefcaseCount;
				break;
			case 3:briefcasesLeft = openBriefcasesRound3 - briefcaseCount;
				break;
			case 4:briefcasesLeft = openBriefcasesRound4 - briefcaseCount;
				break;
			case 5:briefcasesLeft = openBriefcasesRound5 - briefcaseCount;
				break;	
		}
		return briefcasesLeft;
	}


	/* Boolean check which determines if a briefcase amount has been opened by the player
	 * Used for display purposes on gamePage (left hand table)
	 * Note: Could not use the amount property of a briefcase to display this as the amounts would be ordered the same on the page as they would be in the table 
	 * on the side of the page, allowing the player to cheat
	 * Returns a boolean
	 */
	public boolean isAmountOpened(double amount) {
		boolean opened = false;
		for(int i = 0; i < briefcases.size(); i++){
			if(briefcases.get(i).getAmount() == amount && briefcases.get(i).isBriefcaseOpen()) {
				opened = true;
			}
		}
		return opened;
	}

	/* 
	 * Generate a 6 character random alpha numeric string to be used as the security code for the gamePage and dealPage
	 * The characters are chosen from capital letters and numbers only
	 * Returns a String*/
	 
	public static String generateSecurityCode(){
		final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		int stringLength =6;
		StringBuilder builder = new StringBuilder();

		while (stringLength-- != 0) {
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}


	 /* Writes a DealGame object to saved game file if addNewGame is true
	 * Otherwise removes a DealGame object from the file (Re-writes the file without that game)
	 * 
	 * The file is read to retrieve all saved DealGame objects into a list
	 * Then check the list for an existing game stored by the current user, and if found remove from the list
	 * If a new game, add to the list then convert the list of saved games to an array and save to the file (Does not store properly as an arrayList)  
	 * Returns a boolean depending on save success */
	 
	public boolean saveGameSuccess(DealGame game, ServletContext context, boolean addNewGame){
		boolean success = false;
		FileOutputStream fout = null;
		ObjectOutputStream oos = null;
		List<DealGame> saves = new ArrayList<DealGame>();

		try {
			saves = loadSavedGames(context); //retrieve saved games 

			//Check for matching userName in a currently stored game - if there is a match remove it from the list
			for(int i = 0; i < saves.size(); i ++) {
				if(saves.get(i).getUserName().equals(game.getUserName())) {
					saves.remove(i);	//Remove existing game
				}
			}
			if(addNewGame) {
				saves.add(game); //add new game data onto the end of the List
			}
			DealGame[] gameArray = saves.toArray(new DealGame[saves.size()]); //Change arrayList to Array to save to file

			fout = new FileOutputStream(context.getRealPath("/savedGames.ser"));
			oos = new ObjectOutputStream(fout);
			oos.writeObject(gameArray); //write game array to file (overwrites all saved games)

			success = true;

		}catch (Exception ex) {

			ex.printStackTrace();
		} finally {
			//Close file and object output streams
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}

	/*
	 * Read all saved DealGame Objects from saved game file - savedGames.ser
	 * The DealGame objects are read from the file as an array of DealGame objects then converted to an arrayList
	 * returns a List of DealGame objects
	 * */
	public List<DealGame> loadSavedGames(ServletContext context){
		DealGame[] savedGames = null;
		List<DealGame> saves = new ArrayList<DealGame>();

		FileInputStream fileIn = null;
		ObjectInputStream objectIn = null;

		try {
			fileIn = new FileInputStream(context.getRealPath("/savedGames.ser"));
			objectIn = new ObjectInputStream(fileIn);

			savedGames = (DealGame[]) objectIn.readObject();
			saves = new ArrayList<DealGame>(Arrays.asList(savedGames)); //Add the array to an arrayList + convert

		}catch(InvalidClassException ex) {
			ex.printStackTrace();
		}catch (EOFException ex) { 
			return saves; //return the empty game object - the class will hit this exception when the application is first run and the saved game file contains no games
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
		finally {
			//Close file and object output streams
			if (fileIn != null) {
				try {
					fileIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (objectIn != null) {
				try {
					objectIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return saves;
	}	

	/*
	 * Find a saved game with a matching userName
	 * Call method to load the stored DealGame objects (in savedGames.ser) into the DealGame storedSavedGames List
	 * Search through the list and if a match is found return the matching DealGame object
	 * Returns a DealGame object
	 * */ 
	public DealGame findSavedGame(String userName, ServletContext context){
		DealGame savedGame = new DealGame();
		List<DealGame> storedSavedGames = loadSavedGames(context);

		for(DealGame g : storedSavedGames) {
			if(g.getUserName().equals(userName)) {
				savedGame = g;
			}
		}
		return savedGame;
	}
}
