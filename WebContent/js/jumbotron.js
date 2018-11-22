/**
 * 
 */

function jumbotronModifier(id){
	//HIDE PREVIOUS
	var currentArticle  = document.getElementsByClassName("selected-art")[0];
	currentArticle.setAttribute("style","position : fixed; visibility : hidden;")
	currentArticle.classList.remove("selected-art");
	//SHOW NEXT
	var newArticle = document.getElementById(id);
	newArticle.setAttribute("style", "position : static; visibility : visible;");
	newArticle.classList.add("selected-art");
}