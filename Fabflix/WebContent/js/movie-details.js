$(document).ready(function() {
	console.log("OK2");

	var movieDetails = $("<div class='movie-details'></div>");
	$(document.body).append(movieDetails);
	movieDetails.hide();

	// show movie details on mouse over movie title
	$(document).on("mouseenter", ".movie-title", function(e) {
		var $target = $(e.target);
		var offset = $target.offset();

		var m_id = $target.attr('data-mid');
		$.ajax({
			url : "/Fabflix/MovieDetails",
			data : {
				movieid : m_id
			},
			success : function(data, textStatus, jqXHR) {
				movieDetails.empty();
				movieDetails.append(data);
				movieDetails.show();
				movieDetails.css({
					position : "absolute",
					top : offset.top + $target.height(),
					left : offset.left + $target.width(),
					backgroundColor : "white",
					border : "1px solid black"
				})
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert(textStatus);
			}
		});
	});

	// hide movie detail on click
	$(document).on("click", function(e) {
		var $target = $(e.target);

		if (!$target.hasClass(".movie-title")) {
			movieDetails.hide();
		}
	});
});