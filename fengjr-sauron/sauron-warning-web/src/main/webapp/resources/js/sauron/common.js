$(document).ready(
		function() {

			function writeHtml() {

				$.getJSON('/common/getAppName/1', function(data) {

					if (data.code == 0) {
						var sel = $("#selectAppName");
						sel.empty();
						$.each(data.attach, function(i, item) {
							$("<option value='" + item + "'>" + item + "</option>").appendTo(sel);
							if(i == 0 ){
								initMethodByAppName(item);
							}
						});
					}
				});
				
				
				function initMethodByAppName(appName_selected) {

					$.getJSON('/common/getMethodByAppName', {
						appName : appName_selected
					}, function(data) {

						if (data.code == 0) {
							var sel = $("#selectMethodName");
							sel.empty();
							$.each(data.attach, function(i, item) {
								$("<option value='" + item + "'>" + item + "</option>").appendTo(sel);
							});
						}
					});
				}

				$('#selectAppName').change(
						function() {
							var appName_selected = $(this).children('option:selected').val();
							initMethodByAppName(appName_selected)
						});
			}
			writeHtml();
		}
);
