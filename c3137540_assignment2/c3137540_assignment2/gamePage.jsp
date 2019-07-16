<%@ page import="mainPackage.DealGame"%>
<%@ page import="mainPackage.Briefcase"%>
<%@ page import="java.util.List" %>

<!-- 
Amanda Crowley - c3137540
Assignment 2 SENG2050
This is the main display page of the application.
It contains an included jsp page (amountTable.jsp) which displays a table containing the 12 possible values that could be in the briefcases.
It also contains a table of image links which represent the 12 briefcases in the game.
Once an image link is clicked the request is processed by the GameController servlet and the user is re-directed appropriately.
(i.e. if an offer round then to the dealPage, otherwise back to this page to open more briefcases)
 -->
 
<!DOCTYPE html>
<jsp:useBean id="game" class="mainPackage.DealGame" scope="session"/> <!-- Scope is set to session as the game state needs to persist over multiple requests for a single user-->
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Deal or No Deal</title>
	<script src="${pageContext.request.contextPath}/js/validate.js"></script>
	<style>
		<%@include file="mainCss.css"%>
	</style>
</head>
<body>
	<div class="wrapper">
 		<%	//This securityCode is sent via the request object when a briefcase is clicked to open it and reveal its value
 			//It is used to ensure user cannot refresh the page and change the game state - This value is invalidated after a successful match (i.e. request security code object matches with session security object)
 			session.setAttribute("securityCode", DealGame.generateSecurityCode());
		%> 

		<p>Welcome <jsp:getProperty name="game" property="userName" /></p>
		
		<div id="leftcolumn">
			<jsp:include page="WEB-INF/includes/amountTable.jsp"/> <!-- include table representing briefcase $ values -->
		</div>
		
		<!-- Save game button -->
		<div id="rightContent">
			<form action="${pageContext.request.contextPath}/GameController" onsubmit="return confirm('<%=session.getAttribute("saveMessage")%>');" method="get">
					<input type="submit" name="saveGame" value="Save Game" /> 
			</form>
		</div>
		
		<!-- Main content section, contains 12 briefcase image links -->
		<div id="content">
		<h2>Pick your briefcase:</h2>
		<p>Briefcases left to pick in this round: <%=game.getBriefcasesLeftInRound()%></p>
			<table class="briefcaseTable">
			<!-- Loop through all 12 briefcases in the briefcase list -->
			<%
				List<Briefcase> briefcaseList = game.getBriefcases();
				for (int i = 0; i < 12; i++){
					//Display the briefcases, 3 rows of 4 images
					if((i == 0) || (i == 4) || (i == 8)){%>
						<tr>
					<%} 
						//Check if the briefcase has been opened, if it has display the opened image, otherwise displayed the closed briefcase image
						if(briefcaseList.get(i).isBriefcaseOpen()){%>
							<td><img src="images/briefcaseOpen.gif" alt="Open Briefcase"/><%=i+1%></td>
						<%}else{ //When the closed briefcase image is clicked the briefcase ID and security code are sent to the game controller for processing%>
 			        		<td>
 			        			<a href="GameController?id=<%=briefcaseList.get(i).getCaseID()%>&secCode=${sessionScope.securityCode}"><img src="images/suitcase.gif" alt="Briefcase"/></a>
 			        			<%=i+1%>
 			        		</td>
			        	<%}%>
					<%if((i == 3) || (i == 7) || (i == 11)){%>
						</tr>
					<%}%>
				<%}%>
			</table>
		</div>
	</div>
</body>
</html>