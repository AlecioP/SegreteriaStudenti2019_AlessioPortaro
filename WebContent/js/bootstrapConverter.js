/**
 * 
 */
window.onload = function() {
    
	var bodyChilds = (document.getElementsByTagName("body"))[0].children;
	for(var i=0;i<bodyChilds.length;i++)
        recurisiveClassChanger(bodyChilds[i]);
}

function recurisiveClassChanger(node){
    
	var myClasses = node.classList;
    
    for(var i=0;i<myClasses.length;i++){
        var toReplace = /myCol-(\d*)/i;
        if(toReplace.test((myClasses[i]).toString())){
            var matched = myClasses[i].toString();
            node.classList.remove(matched);
            var replaced = matched.replace(toReplace,"col-xl-$1 col-lg-$1 col-md-$1 col-sm-$1 col-$1");
            var splits = replaced.split(" ");
            for(var s=0;s<splits.length;s++)
                node.classList.add(splits[s]);
        }
    }
    var childs = node.children;
    for(var j=0;j<childs.length;j++)
        recurisiveClassChanger(childs[j]);
}