/**
 * 
 */
var GLOBAL_NUM_COMP;

function addFormElements(){
	var numeroElementi = document.getElementsByClassName("el-num")[0].value;
	GLOBAL_NUM_COMP = numeroElementi;
    
    var container = document.getElementsByClassName("el-data-container")[0];
    
    var htmlContent = " ";
    
    for(var i =0;i<numeroElementi;i++){
    	htmlContent = htmlContent+"<div class=\"row\"><span> Reddito Elemento "+(i+1)+"</span><input class=\"reddito\"type=\"text\"><span class=\"ps\" ></span><span> Patrimonio Elemento "+(i+1)+"</span><input class=\"patrimonio\"type=\"text\"></div>"
    }
    
    container.innerHTML=htmlContent;
}

function calcolaISEEU(){
	var ISEE;
    var ISE;
    var ISEEU;
    var p;
    var R = 0;
    var Patrimonio=0;
    /*ISE = R + [(PM + PI) × 0,20]
    ISEE = ISE / p
    In cui:
    R = Reddito complessivo del nucleo familiare
    PM = Patrimonio Mobiliare
    PI = Patrimonio Immobiliare
    p = parametro della scala di equivalenza*/
    switch(GLOBAL_NUM_COMP){
    	case 1 :{
    		p = 1.00;
    		break;
    	}
    	case 2 :{
    		p = 1.57;
    		break;
    	}
    	case 3 :{
    		p = 2.04;
    		break;
    	}
    	case 4 :{
    		p = 2.46;
    		break;
    	}
    	case 5 :{
    		p = 2.85;
    		break;
    	}
    	default : {
    		p = 2.85 + (0.35*(GLOBAL_NUM_COMP-5));
    	}
    }
    
    var redditi  = document.getElementsByClassName("reddito");
    var patrimoni = document.getElementsByClassName("patrimonio");
    
    for(var i=0;i<redditi.length;i++){
        R+=parseFloat(redditi[i].value);
        Patrimonio+=parseFloat(patrimoni[i].value);
    }
    //alert("Patrimonio : "+Patrimonio);
    //alert("Reddito : "+R);
    //alert("Parametro di equivalenza : "+p);
    ISE = R + ((Patrimonio)*(0.20));
    //alert("ISE : "+ISE);
    ISEE = ISE/p;
    document.getElementsByClassName("result-div")[0].innerHTML = "<div class=\"row\"><span>ISEE \: </span><span class=\"result\">"+ISEE+"</span></div>";
}
