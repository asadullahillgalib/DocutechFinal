Ext.define('Ext.view.customer.CustomerController',{
	extend: 'Ext.app.ViewController',
	alias: 'controller.customer',
	requires: [ 
		'Desktop.view.customer.Customer',
		'Desktop.CustomerViewModel'
	],
	onSuccess: function(request, response) {
		var me = this;

		if(request.component == 'onCustPanelShow'){
			var items = response.payload[1].payload;
			var customerStore = Ext.data.StoreManager.lookup('customerStore');

			customerStore.removeAll();
			customerStore.add(items);
		}
		
	 },

	 onCustPanelShow : function(){

		var header = {
			userName    : 100000,
			userModKey  : 100000,
			userId      : 100000
		};

		var payload = {
			reference: 'onCustPanelShow',
			userModKey: 100000,
		};

		this.sendRequest(appActionType.ACTION_TYPE_SELECT_CUSTOMER,  appContentType.CONTENT_TYPE_DOC, payload, null);
	},
	sendRequest: function(actionName, contentType, payload, header) {

		var component = null;

		if (Ext.isEmpty(payload)) {
			payload = new Array();
		}
		else if(!Ext.isEmpty(payload.reference)){
			component = payload.reference;
		}
		else if(!Ext.isEmpty(payload[0]) && payload[0].reference != 'undefined'){
			component = payload[0].reference;
		} 

		var request = {
			actionName      : actionName,
			contentType     : contentType,
			requestId       : null,
			requestType     : null,
			header          : header,
			body            : payload,
			message         : null,
			dispatchType    : null,
			sender          : this,
			component       : component,
			onSuccess       : this.onSuccess,
			onError         : this.onError
		};

		var requestId = nMessageProcessor.sendRequest(request); 
	},
	onCustomerGridFilter : function (component, newValue, oldValue, eOpts) {
		var store = Ext.data.StoreManager.lookup('customerStore');
		store.clearFilter();

		if (newValue) {

			var matcher = new RegExp(Ext.String.escapeRegex(newValue), "i");

			store.filter({
				filterFn: function(record) {
					return	matcher.test(record.get('customerId')) ||
							matcher.test(record.get('customerVer')) ||
							matcher.test(record.get('customerNumber')) ||
							matcher.test(record.get('contactNumber')) ||
							matcher.test(record.get('customerOrderNo')) ||
							matcher.test(record.get('customerAddress')) ||
							matcher.test(record.get('docId')) ||
							matcher.test(record.get('modifiedOn'));
				}
			});
		}
		component.focus();
	},
	//onCustomerUpdate
	onDoubleClick: function (dataview, record, item, index, e) {
		var me = this;
		var data = record.data;
		var win = Ext.create('Desktop.view.customer.CustomerForm');

		win.lookupReference('customerId').setValue(data.customerId);
		win.lookupReference('customerVer').setValue(data.customerVer);
		win.lookupReference('customerNumber').setValue(data.customerNumber);
		win.lookupReference('contactNumber').setValue(data.contactNumber);
		win.lookupReference('customerOrderNo').setValue(data.customerOrderNo);
		win.lookupReference('customerAddress').setValue(data.customerAddress);
		win.lookupReference('docId').setValue(data.docId);
		win.lookupReference('modifiedOn').setValue(data.modifiedOn);
		win.show();
	},

	onWindowClose: function () {
		this.view.destroy();
	},

	onCustomerUpdate: function(button, eOpts) {
		var me = this, btnRef = button.reference;

		var customerId 	    = me.lookupReference('customerId').getValue();
		var customerVer 	= me.lookupReference('customerVer').getValue();
		var customerNumber  = me.lookupReference('customerNumber').getValue();
		var contactNumber 	= me.lookupReference('contactNumber').getValue();
		var customerOrderNo = me.lookupReference('customerOrderNo').getValue();
		var customerAddress = me.lookupReference('customerAddress').getValue();
		var docId 	  		= me.lookupReference('docId').getValue();

		if(Ext.isEmpty(customerNumber)){
			customerNumber = null;
		}
		if(Ext.isEmpty(contactNumber)){
			contactNumber = null;
		}
		if(Ext.isEmpty(customerOrderNo)){
			customerOrderNo = null;
		}
		if(Ext.isEmpty(customerAddress)){
			customerAddress = null;
		}

		var header = {
			userName 	: 100000,
			userModKey 	: 100000,
			userId 		: 100000
		};

		var payload = {
			userModKey		: 100000,
			customerId      : customerId,
			customerVer     : customerVer,
			customerNumber  : customerNumber,
			contactNumber   : contactNumber,
			customerOrderNo : customerOrderNo,
			customerAddress : customerAddress,
			docId           : docId,
			reference 	    : 'onCustomerUpdate',
		};
		
		if(btnRef == 'customerUpdtBtn'){
			Ext.MessageBox.confirm('Confirm', 'Are you sure?', function(btn) {

				if (btn == 'yes') {
					me.sendRequest(appActionType.ACTION_TYPE_UPDATE_CUSTOMER, appContentType.CONTENT_TYPE_DOC, payload, null);			
				}
			});

		}
	},


});