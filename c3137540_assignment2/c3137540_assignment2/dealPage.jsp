<%@ page import="mainPackage.DealGame"%>

<!-- 
Amanda Crowley - c3137540
Assignment 2 SENG2050
This is the page where the player can choose to take the banker's offer or reject the deal, continuing the game.
It contains a simple form with 2 buttons, deal or no deal. Once the form is submitted the request is sent to the 
GameController servlet for processing and the user is re-directed appropriately. 
(i.e. If deal is selected then back to this page with the offer amount, otherwise back to the gamePage to open more briefcases)
 -->
 
<!DOCTYPE html>
<jsp:useBean id="game" class="mainPackage.DealGame" scope="session"/> <!-- Scope is set to session as the game state needs to persist over multiple requests for a single user-->

<html lang="en">
<head>
	<meta charset="UTF-8">
	<style>
		<%@include file="mainCss.css"%>
	</style>
	<script src="${pageContext.request.contextPath}/js/validate.js"></script>
	<title>Deal or No Deal</title>
</head>
<body>
	<div class="wrapper">
		<p>Welcome <jsp:getProperty name="game" property="userName" /></p>
			
		<div id="leftcolumn">
			<jsp:include page="WEB-INF/includes/amountTable.jsp"/> <!-- include table representing briefcase $ values -->
		</div>
		
		<!-- Save game button -->
		<% if(!game.isGameOver()){ %> <!-- Display save button unless the game is over -->
		<div id="rightContent">
			<form action="${pageContext.request.contextPath}/GameController" onsubmit="return confirm('<%=session.getAttribute("saveMessage")%>');" method="get">
					<input type="submit" name="saveGame" value="Save Game" /> 
			</form>
		</div>
		<%}%>
		
		<% if(!game.isGameOver()){ %> <!-- Display deal or no deal buttons unless the game is over -->
			<div id="dealContent">
				<h1>Pick an option</h1>
				<p class="redText">Banker's offer: <jsp:getProperty name="game" property="offerAmount" /></p>

				<!-- Deal or no deal buttons -->
				<form action="${pageContext.request.contextPath}/GameController" method="post">
					<input type="submit" name="deal" value="Deal" /> 
					<input type="submit" name="noDeal" value="No Deal" />
				</form>

				<jsp:setProperty property="largestAmount" name="game" value="0"/>
				<p>Largest amount unrevealed in the current stage: $<jsp:getProperty name="game" property="largestAmount" /></p>
			</div>
			<%}else{%>
				<div id="dealContent"> <!-- If the game is over display the amount won -->
					<h1>Congratulations!</h1>

					<p>You have won: <jsp:getProperty name="game" property="offerAmount" /></p>
					<a href="firstPage.jsp">Play again?</a>
					<%session.invalidate();%><!-- Once the game has finished and relevant game details have been displayed, remove the session -->
				</div>
			<%}%>
	</div>
</body>
</html>