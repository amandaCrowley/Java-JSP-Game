<!-- 
Amanda Crowley - c3137540
Assignment 2 SENG2050
This page is the first page in the deal or no deal application - It contains a simple form and message area
Once the form is submitted the userName text box is checked for a value using an external javaScript file called validate.js
The form then passes the form information on to the servlet called GameController for processing (i.e. checks form input and appropriately redirect player)
 -->
 
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<style>
		<%@include file="mainCss.css"%>
	</style>
	<script src="${pageContext.request.contextPath}/js/validate.js"></script>
	<title>Welcome to Deal or No Deal</title>
</head>
<body>
	<h1>Welcome to Deal or No Deal</h1>
	
	<!-- Game form, user must enter a user name to continue, validated with JavaScript-->
	<form action="${pageContext.request.contextPath}/GameController" name="gameForm" onsubmit="return validateForm()" method="get">
		<label>User name: </label>
		<input type="text" name="userName"> 
	    <label id="optionLabel">Choose an option: </label>
	    <input type="submit" name="newGame" value="New Game"/>
	    <input id="savedGame" type="submit" name="savedGame" value="Load saved game" onclick="saveButton();"/>
	</form>
	
	<!-- Displays a message if a saved game with a matching user name can't be found or when a game is successfully saved -->
	<p class="redText">
		<% if(request.getAttribute("errorMessage") != null){ 
			  	out.println(request.getAttribute("errorMessage"));
		}
		%>
	</p>
</body>
</html>