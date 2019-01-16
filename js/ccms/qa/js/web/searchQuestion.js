(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var obthis = this;
		$('#selectinput').select2(
				{
					 placeholder: "&nbsp;",
					 minimumInputLength: 1,
					 width:260,
					 ajax: { 
					 url: contextPath+"/action/ccms/qa/web/question/search/tags/count",
					 dataType: 'json',
					 data: function (term, page) {
					 return {
				     term: term, // search term
					 page_limit: 15
					 };
					 },
					 results: function (data, page) { // parse the results into the format expected by Select2.
					 // since we are using custom formatting functions we do not need to alter remote JSON data
					 return {results: data.result};
					 }
					 },
					 formatResult: movieFormatResult, // omitted for brevity, see the source of this page
					 formatSelection: movieFormatSelection, // omitted for brevity, see the source of this page
					 dropdownCssClass: "bigdrop", // apply css that makes the dropdown taller
					 escapeMarkup: function (m) { return m; } // we do not want to escape markup since we are displaying html in results
				});

			function movieFormatResult(movie) {
				   var title ="";
					   if(movie.type==1){
						   if(movie.avatarUrl==null){ 
							   title +=""+movie.title+"&nbsp;&nbsp;问题数："+movie.number;
					      }else{
					    	  //title +="<img style='height:20px;width:20px;' src='/image/"+movie.avatarUrl+"'>"+movie.title+"&nbsp;&nbsp;问题数："+movie.number;
							   title +=""+movie.title+"&nbsp;&nbsp;问题数："+movie.number;
					      }
						  
					   };
					   if(movie.type==2){
						   title +=""+movie.title+"&nbsp;&nbsp答案："+movie.number;
					   }

			    	 return title;
			}

			function movieFormatSelection(movie) {
			    return "";
			}
			$("#selectinput").on("select2-selecting", function(e) {
				if(e.object.type==1){
					//window.location.href=contextPath+"/action/ccms/qa/web/tag/tags?tqid=a&tag_name="+e.object.title+"";
					gotoPage("/action/ccms/qa/web/tag/tags?tqid=q&tag_name="+e.object.title+"&random="+Math.random());
				}
				if(e.object.type==2){
					//window.location.href=contextPath+"/action/ccms/qa/web/q_and_a?qid="+e.object.id;
					gotoPage("/action/ccms/qa/web/q_and_a?qid="+e.object.id+"&random="+Math.random());
				}
			});
	};
	$Q_init.prototype = $Q.fn;
	window["ccms"]["searchQuestion"] = $Q;
})();

$(document).ready(function() {
	ccms.searchQuestion().init();

});

		
	
