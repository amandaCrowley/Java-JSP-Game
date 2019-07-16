package mainPackage;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/*
 * Amanda Crowley - c3137540
 * Assignment 2 SENG2050
 * When a session times out the session destroyed method is called. This class implements this method in order to save an unfinished game 
 * to the savedGames.ser file in the event that a player's session times out. The session timeout duration is specified in the application's web.xml file.
 * It is set to 1 minute.
 */

@WebListener
public class SessionListener implements HttpSessionListener{

	/*
	 * This method will retrieve the game session object, if it's not null it will then check that the game has not finished and that the user name is not null.
	 * This indicates that the game has timed out before the player has finished playing their game.
	 * If this is the case the game object is saved to the saved games file.
	 * */
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession playerSession = event.getSession(); //Retrieve the session
		if(playerSession!=null)
		{
			DealGame game = (DealGame)playerSession.getAttribute("game"); //Retrieve the game saved in session
			if(game != null) {
				if(!game.isGameOver() && game.getUserName() != null) {
					game.saveGameSuccess(game, playerSession.getServletContext(), true); //Save game to file if the game has not finished the game i.e. player timeout + userName not blank (Player isn't on first page)	
				}
			}
		}	
	}

	public void sessionCreated(HttpSessionEvent event) {
	}
}
