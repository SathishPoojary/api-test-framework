<script type="text/javascript">
function takeAction(e) {
	e.stopPropagation(); 
	var node =e.srcElement == undefined ? e.target : e.srcElement;
	var id = node.getAttribute("id"); 
	if (id != null && id.indexOf("Id_") > -1) {
		if (node.innerHTML == "-"){
			node.innerHTML = "+"; 
			document.getElementById("ExpandCollapse" +id).style.display = "none";
		} else if (node.innerHTML == "+"){
			node.innerHTML = "-"; document.getElementById("ExpandCollapse" +id).style.display = "block"; 
		} 
	} 
} 

function showDetails(id){ 
	var displayed = document.getElementById(id).style.display;
	if(displayed == "inline-block") document.getElementById(id).style.display = "none"; 
	else document.getElementById(id).style.display = "inline-block";
} 

function stopProp(e){
	e.stopPropagation();
} 

function showFrequency(){
   if (document.getElementById("FrequencyTable").style.display == "table" ) {
       document.getElementById("FrequencyTable").style.display="none";

   } else {
      document.getElementById("FrequencyTable").style.display="table";
   }		
} 
</script>