Ext.define('nazdaq.message.MessageDispatcher', {
	alias: 'nMessageDispatcher',
	alternateClassName: 'nMessageDispatcher',
	statics: {

		dispatch : function(request, dispatchType) {

			if (Ext.isEmpty(dispatchType)) {
				dispatchType = appConstants.DISPATCH_TYPE_AJAX;
			}

			if (dispatchType === appConstants.DISPATCH_TYPE_AJAX) {
				this.dispatchAjaxRequest(request);
			} 
			else {
				Ext.MessageBox.alert('Warn', 'Unknown dispatch type ' + dispatchType);
			}
		},

		dispatchAjaxRequest : function(appRequest) {

			if (!Ext.isEmpty(appRequest.message))
			{
				var urlDestination = appConstants.SERVER_URL;
								
				var payLoadString = appRequest.message.toString().replace("payLoad","payload");
	
				Ext.Ajax.request({

					url			: urlDestination,
					method		: appConstants.METHOD_POST,
					jsonData	: payLoadString,
					timeout 	: ajaxRequestTimeout,
					success: function(result, request) {
						if(result.responseText == "Logged out by System"){
							showAlertMessage('You are logged out from the system', 3000);
							setTimeout(function(){ 
								javascript: document.location.reload();
							}, 2000);
						}
						else{
							var response = Ext.JSON.decode(result.responseText);
						
							nMessageProcessor.processResponse(appRequest, response);
						}
						
					},

					failure: function(result, request) {
						Ext.MessageBox.alert('Server Error', 'Server Error');
					}
				});
			}
		}
	}
});