function validateForm() {
	var userName = document.forms["gameForm"]["userName"].value;
	
	if (userName == null || userName == "") { //checks that the user name field contains a value
		alert("User name is required");
		return false;
	}
}


function saveButton() {
	 var result = confirm("Please note, you may only load a saved game once. Continue?");

     if(result == true ){
        return true;
     }
}