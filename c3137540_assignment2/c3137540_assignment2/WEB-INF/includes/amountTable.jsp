<!-- 
Amanda Crowley - c3137540
Assignment 2 SENG2050
Included jsp page for gamePage.jsp and dealPage.jsp
Displays a table containing the 12 possible values that could be in the briefcases.
Css changes the colour of the table cell depending on the open status of the briefcase.
 -->

<jsp:useBean id="game" class="mainPackage.DealGame" scope="session"/> <!-- Scope is set to session as the game state needs to persist over multiple requests for a single user-->
<table>
	<tr>
		<th>Briefcase Values:</th>
	</tr>
	<!-- Display all 12 possible briefcase values -->
	<% 
		double[] amounts = game.getAmounts();
		for (Double amt : amounts) {
	%>
	<tr>
		<%if(game.isAmountOpened(amt)){%>
			<td class="openedValue">$<%out.println(amt);%></td>
		<%}else{%>
			<td>$<%out.println(amt);%></td>
		<%}%>
	</tr>
	<%}%>
</table>