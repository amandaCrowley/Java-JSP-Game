package mainPackage;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/*
 * Amanda Crowley - c3137540
 * Assignment 2 SENG2050
 * The GameController servlet processes all requests sent by the various JSP pages within this application.
 * It directs the flow of the application based on the choices made by the player and updates the session information as needed.
 * 
 * The user cannot cheat by clicking the refresh or back button within the application. 
 * A security code is used on the gamePage (request parameter) so that if it does not match, then the page 
 * has been refreshed and the player is redirected back to the relevant page.
 * The back button does not affect the state of the game, as the progress of the game is stored in the application's session and is checked by the servlet whenever a 
 * request is submitted (i.e. where the player is up to), redirecting the user back to the relevant page if necessary. (They can still click the back button which will 
 * take them back a page, but they cannot change any of their previous selections)
 */

@WebServlet("/GameController")
public class GameController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GameController() {
		super();

	}

	/*
	 * The form on firstPage.jsp implements the GET method (Once the form is submitted the application is directed to this method).
	 * Calls the relevant methods for processing/re-direct the user should any of the following occur:
	 * - New game button or Load saved game button clicked on firstPage form
	 * - A briefcase has been clicked on dealPage
	 * - The save game button has been clicked on gamePage or dealPage
	 * */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {	
			if (request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()) { //session expired
				request.setAttribute("errorMessage", "Session has expired. Your game has been saved.");
				redirect(request, response,"/firstPage.jsp");
			}else {
				HttpSession playerSession = request.getSession();
				DealGame game = (DealGame)playerSession.getAttribute("game"); //retrieve the session

				if (request.getParameter("newGame") != null) {	 //New game button selected
					newGameBtn(request, response, playerSession);
				}

				else if(request.getParameter("savedGame") != null){ //load saved game button selected
					loadGameBtn(request, response, playerSession);
				}
				else if(request.getParameter("id") != null) { //A suitcase has been clicked on the gamePage
					if (request.getParameter("secCode").equals(playerSession.getAttribute("securityCode"))) { //check page has not been refreshed
						playerSession.setAttribute("securityCode", "INVALID"); // security code has been matched - now disable it so it cant be used again
						processSeat(request, response, playerSession);

					}else {//page has been refreshed or back button has been clicked - redirect user	
						if(game.getOfferRound()) {
							game.processOffer();
							response.sendRedirect("dealPage.jsp");
						}else {
							response.sendRedirect("gamePage.jsp");
						}
					}
				}
				else if(request.getParameter("saveGame") != null){ //save game button clicked on gamePage or dealPage
					if(game.saveGameSuccess(game, getServletContext(), true)) { //add new game to file
						playerSession.invalidate(); //clear session attributes - once game is saved the current session ends
						request.setAttribute("errorMessage", "Your game has been saved"); //Add saved message
						redirect(request, response,"/firstPage.jsp");
					}
				}
			}
		}catch (NullPointerException ex){
			request.setAttribute("errorMessage", "Sorry your game has finished. Play again?"); 
			redirect(request, response,"/firstPage.jsp");
		}catch(Exception ex) {
			request.setAttribute("errorMessage", "Sorry there was an error: " + ex);
			redirect(request, response,"/firstPage.jsp");
		}
	}	

	/*
	 * A briefcase has been clicked on the gamePage - indicating a briefcase needs to be opened
	 * The id of the briefcase to be opened is passed in by the request object
	 * Retrieve all 12 briefcases, store them in a list, then find the briefcase with the matching id within the list
	 * Change the openBriefcase value of the selected briefcase in the session to true
	 * Then redirect the player
	 * */
	public void processSeat(HttpServletRequest request, HttpServletResponse response, HttpSession playerSession) throws ServletException, IOException {
		DealGame game = (DealGame)playerSession.getAttribute("game"); 
		if(game.getUserName().equals("")) { 
			request.setAttribute("errorMessage", "Sorry your game has finished. Play again?"); 
			redirect(request, response,"/firstPage.jsp");
		}else {
			List<Briefcase> briefcaseList = game.getBriefcases();			  
			Briefcase selectedBriefcase= briefcaseList.get(Integer.parseInt(request.getParameter("id"))); //Find the selected briefcase based on id passed in
			game.openBriefcase(selectedBriefcase.getAmount()); //method changes briefcaseOpen to true - selects the briefcase based on the briefcase's amount (amounts are unique)

			if(game.isOfferRound()) { //Redirect player depending on whether game is in an offer round or not
				game.setOfferRound(true);
				game.processOffer();
				response.sendRedirect("dealPage.jsp"); 
			}else {
				response.sendRedirect("gamePage.jsp");
			}
		}
	}

	/*
	 * The new game button has been selected on the firstPage
	 * Create a new DealGame object, set the userName of the game object then save the game into the current session
	 * */
	public void newGameBtn(HttpServletRequest request, HttpServletResponse response, HttpSession playerSession) throws ServletException, IOException {
		if(request.getParameter("userName") != null) { 		 //Check the user name is not empty		
			DealGame game = new DealGame();
			game.setUserName(request.getParameter("userName"));
			if((DealGame)playerSession.getAttribute("game") == null) { 
				playerSession.setAttribute("game", game);
				playerSession.setAttribute("saveMessage", saveGameMessage(request, response));
				
				response.sendRedirect("gamePage.jsp");
			}else { //Game is currently being played and a second browser is opened 
				request.setAttribute("errorMessage", "Sorry a game is currently being played. Please finish game in other browser window, or wait roughly 1 minute for game session to expire.");
				redirect(request, response,"/firstPage.jsp");
			}
		}
	}

	/*
	 * The load saved game button has been selected on the firstPage
	 * Call the method to find the stored game with a matching user name
	 * If the game is found, remove it from the savedGame.ser file and load it into the current session, then redirect the player
	 * Otherwise if a matching game has not been found return the player to the first page with an error
	 * */
	public void loadGameBtn(HttpServletRequest request, HttpServletResponse response, HttpSession playerSession) throws ServletException, IOException {
		String userName = request.getParameter("userName");

		DealGame storedGame = new DealGame();
		storedGame = storedGame.findSavedGame(userName, getServletContext());
		if(storedGame.getUserName().equals("")) { //Stored game not found, display error and redirect
			request.setAttribute("errorMessage", "Sorry no stored game found for user: " + userName);
			redirect(request, response,"/firstPage.jsp");
		}else {
			if((DealGame)playerSession.getAttribute("game") == null) { 
				storedGame.saveGameSuccess(storedGame, getServletContext(), false); //Once a game has been loaded - remove from the saved game file
				playerSession.setAttribute("game", storedGame); //Stored game found, add to session
				playerSession.setAttribute("saveMessage", saveGameMessage(request, response));
				
				//Redirect user depending on whether they were in an offer round or not
				if(storedGame.getOfferRound()) {
					storedGame.processOffer();
					response.sendRedirect("dealPage.jsp");
				}else {
					response.sendRedirect("gamePage.jsp");
				}
			}else { //Game is currently being played and a second browser is opened 
				request.setAttribute("errorMessage", "Sorry a game is currently being played. Please finish game in other browser window, or wait roughly 1 minute for game session to expire.");
				redirect(request, response,"/firstPage.jsp");
			}
		}
	}	

	/*
	 * The form on dealPage.jsp implements the POST method (Once the form is submitted the application is directed to this method).
	 * If deal has been selected on the form - retrieve the game object from the current session, change the object's game over 
	 * value to true then re-save object to session, then redirect the player.
	 * Otherwise (No-deal) re-direct the player to continue the game.
	 * */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()) { //session expired
				request.setAttribute("errorMessage", "Session has expired. Your game has been saved.");
				redirect(request, response,"/firstPage.jsp");
			}else {
				HttpSession playerSession = request.getSession();
				DealGame game = (DealGame) playerSession.getAttribute("game");
				if(game !=null) {
					
					playerSession.setAttribute("saveMessage", saveGameMessage(request, response));
						if(request.getParameter("deal") != null){ //If deal button is clicked - i.e. not empty
							game.setGameOver(true);
							playerSession.setAttribute("game", game);
							
							game.processOffer();
							response.sendRedirect("dealPage.jsp"); 
						}else{ //No deal - continue the game
							game.setOfferRound(false);
							response.sendRedirect("gamePage.jsp");
						}
				}else { //game is null
					request.setAttribute("errorMessage", "Sorry your game has finished. Play again?");
					redirect(request, response,"/firstPage.jsp");
				}
			}
		}catch (NullPointerException ex){
			request.setAttribute("errorMessage", "Sorry your game has finished. Play again?"); //Hits this error when selecting deal after session timeout
			redirect(request, response,"/firstPage.jsp");
		}catch(Exception ex) {
			request.setAttribute("errorMessage", "Sorry there was an error: " + ex);
			redirect(request, response,"/firstPage.jsp");
		}
	}

	/*
	 * Changes the save message string depending on whether the user has a saved game stored in the saved game file
	 * Calls method to check for a saved game with a matching user name
	 * This method is used to change the value of the session variable saveMessage, which is used to display a message when clicking the save button on the gamePage or dealpage
	 * Returns a String
	 * */
	public String saveGameMessage(HttpServletRequest request, HttpServletResponse response) {
		HttpSession playerSession = request.getSession();
		DealGame game = (DealGame) playerSession.getAttribute("game");
		
		DealGame storedGame = new DealGame();
		storedGame = storedGame.findSavedGame(game.getUserName(), getServletContext());
		
		String s = "Saving the game will end the current game. Save now?";
		
		if(!storedGame.getUserName().equals("")) {
			s = "Saving this game will overwrite your previously saved game and you will be redirected. Continue?";
		}
		return s;
	}

	/*
	 * Custom method to redirect the user
	 * Uses RequestDispatcher forward method to forward the request object to the JSP page that the player is re-directed to
	 * This method is used after adding an error string to the request object, then redirects the user to firstPage.jsp which displays the message
	 * */
	private void redirect(HttpServletRequest request, HttpServletResponse response, String pageName) throws ServletException, IOException {
		RequestDispatcher rd = getServletContext().getRequestDispatcher(pageName);
		rd.forward(request, response);
	}
}
