Ext.define('Ext.view.doc.Controller',{
	extend: 'Ext.app.ViewController',
	alias: 'controller.doc',

	requires: [ 
		'Desktop.view.doc.DocWindow',
		'Desktop.DocViewModel'
	],
	onSuccess: function(request, response) {
		var me = this;

		if(request.component == 'onPanelShow'){
			var items = response.payload[1].payload;
			//var docStore = request.sender.getView().store;
			var docStore = Ext.data.StoreManager.lookup('docStore');

			docStore.removeAll();
			docStore.add(items);
		}
		else if(request.component == 'getDocDetailsGrid'){
			var items = response.payload[1].payload;
			//var docStore = request.sender.getView().store;
			var docDetailsStore = Ext.data.StoreManager.lookup('docDetailsStore');

			docDetailsStore.removeAll();
			docDetailsStore.add(items);
		}
		else if(request.component == 'docDetailsUpdate'){

			var body = request.body;

			var msg = null;
			if(Ext.isEmpty(body.docDetailsId)){
				msg = 'Doc details added successfully';
			}
			else{
				msg = 'Doc details updated successfully';
			}

			var alertBox = Ext.Msg.alert("Success", msg);
			setTimeout(function(){
				alertBox.hide();
			}, 4000);

			request.sender.getDocDetailsGrid(body.docId);
			request.sender.getView().destroy();
		}
		else if(request.component == 'docUpdate'){
			var body = request.body;

			var msg = null;
			if(Ext.isEmpty(body.docId)){
				msg = 'Doc added successfully';
			}
			else{
				msg = 'Doc updated successfully';
			}

			var alertBox = Ext.Msg.alert("Success", msg);
			setTimeout(function(){
				alertBox.hide();
			}, 3000);

			request.sender.onPanelShow();
			request.sender.getView().destroy();
		}
	 },
	// Send all request to server
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

	onPanelShow : function(){

		var header = {
			userName    : 100000,
			userModKey  : 100000,
			userId      : 100000
		};

		var payload = {
			reference: 'onPanelShow',
			userModKey: 100000,
		};

		this.sendRequest(appActionType.ACTION_TYPE_SELECT,  appContentType.CONTENT_TYPE_DOC, payload, null);
	},


	onDoubleClick: function (dataview, record, item, index, e) {
		var me = this;
		var data = record.data;
		var win = Ext.create('Desktop.view.doc.DocWindow');

		win.lookupReference('docId').setValue(data.docId);
		win.lookupReference('docVer').setValue(data.docVer);
	   // win.lookupReference('modifiedOn').setValue(data.modifiedOn);
		win.lookupReference('supplierNumber').setValue(data.supplierNumber);
		win.lookupReference('deliveryNoteNo').setValue(data.deliveryNoteNo);
		win.lookupReference('receiptNo').setValue(data.receiptNo);
		win.lookupReference('documentNumber').setValue(data.documentNumber);
		win.lookupReference('documentDate').setValue(this.formatDate(data.documentDate));
		win.lookupReference('recipientOfInvoice').setValue(data.recipientOfInvoice);
		win.lookupReference('vatNumber').setValue(data.vatNumber);
		win.lookupReference('despatchDate').setValue(this.formatDate(data.despatchDate));
		win.lookupReference('invoiceNumber').setValue(data.invoiceNumber);
		win.lookupReference('currency').setValue(data.currency);
		win.lookupReference('accountNumber').setValue(data.accountNumber);
		win.lookupReference('voucherNumber').setValue(data.voucherNumber);
		win.lookupReference('faxNumber').setValue(data.faxNumber);
		win.lookupReference('associationNumber').setValue(data.associationNumber);
		win.lookupReference('companyAddress').setValue(data.companyAddress);
		win.lookupReference('invoiceTo').setValue(data.invoiceTo);
		win.lookupReference('deliverTo').setValue(data.deliverTo);
		win.lookupReference('orderId').setValue(data.orderId);
		win.lookupReference('deliveryNote').setValue(data.deliveryNote);
		win.lookupReference('deliveryDetails').setValue(data.deliveryDetails);
		win.lookupReference('paymentDetails').setValue(data.paymentDetails);
		win.lookupReference('vat').setValue(data.vat);
		win.lookupReference('vatRate').setValue(data.vatRate);
		win.lookupReference('vatPayable').setValue(data.vatPayable);
		win.lookupReference('invoiceAmount').setValue(data.invoiceAmount);
		win.lookupReference('totalBeforeVat').setValue(data.totalBeforeVat);
		win.lookupReference('totalAmountDue').setValue(data.totalAmountDue);
		win.lookupReference('discount').setValue(data.discount);
		win.lookupReference('netInvoiceTotal').setValue(data.netInvoiceTotal);

		win.show();

		this.getDocDetailsGrid(data.docId);
	},

	getDocDetailsGrid: function(docId){
		var header = {
			userName    : 100000,
			userModKey  : 100000,
			userId      : 100000
		};

		var payload = {
			reference: 'getDocDetailsGrid',
			userModKey: 100000,
			docId : docId
		};

		this.sendRequest(appActionType.ACTION_TYPE_SELECT_DOC_DETAILS,  appContentType.CONTENT_TYPE_DOC_DETAILS, payload, null);
	},

	formatDate: function(date){

		if(!Ext.isEmpty(date)){
			return new Date(date);
		}
		else{
			return null;
		}

	},

	docUpdate: function(button, eOpts) {
		var me = this, btnRef = button.reference;

		var docId 	            = me.lookupReference('docId').getValue();
		var docVer 	           	= me.lookupReference('docVer').getValue();
		var supplierNumber     	= me.lookupReference('supplierNumber').getValue();
		var deliveryNoteNo 	   	= me.lookupReference('deliveryNoteNo').getValue();
		var receiptNo          	= me.lookupReference('receiptNo').getValue();
		var documentNumber     	= me.lookupReference('documentNumber').getValue();
		var documentDate 	   	= me.lookupReference('documentDate').getValue();
		var recipientOfInvoice 	= me.lookupReference('recipientOfInvoice').getValue();
		var vatNumber          	= me.lookupReference('vatNumber').getValue();
		var despatchDate 	   	= me.lookupReference('despatchDate').getValue();
		var invoiceNumber      	= me.lookupReference('invoiceNumber').getValue();
		var currency           	= me.lookupReference('currency').getValue();
		var accountNumber      	= me.lookupReference('accountNumber').getValue();
		var voucherNumber      	= me.lookupReference('voucherNumber').getValue();
		var faxNumber          	= me.lookupReference('faxNumber').getValue();
		var associationNumber 	= me.lookupReference('associationNumber').getValue();
		var netInvoiceTotal    	= me.lookupReference('netInvoiceTotal').getValue();
		var orderId            	= me.lookupReference('orderId').getValue();
		var vat 	           	= me.lookupReference('vat').getValue();
		var vatRate 			= me.lookupReference('vatRate').getValue();
		var vatPayable 		   	= me.lookupReference('vatPayable').getValue();
		var invoiceAmount 	   	= me.lookupReference('invoiceAmount').getValue();
		var paymentDetails     	= me.lookupReference('paymentDetails').getValue();
		var totalBeforeVat     	= me.lookupReference('totalBeforeVat').getValue();
		var totalAmountDue     	= me.lookupReference('totalAmountDue').getValue();
		var discount 	       	= me.lookupReference('discount').getValue();
		//var modifiedOn         	= me.lookupReference('modifiedOn').getValue();
		var deliveryNote 	   	= me.lookupReference('deliveryNote').getValue();
		var deliveryDetails    	= me.lookupReference('deliveryDetails').getValue();
		var paymentDetails     	= me.lookupReference('paymentDetails').getValue();
		//var docDetailsId 	       = me.lookupReference('docDetailsId').getValue();
		//var docDetailsVer          = me.lookupReference('docDetailsVer').getValue();
		var companyAddress      = me.lookupReference('companyAddress').getValue();
		var invoiceTo 	        = me.lookupReference('invoiceTo').getValue();
		var deliverTo           = me.lookupReference('deliverTo').getValue();

		if(!Ext.isEmpty(documentDate)){
			documentDate = Ext.Date.format(documentDate, "Ymd")
		}
		else{
			documentDate = null;
		}

		if(!Ext.isEmpty(despatchDate)){
			despatchDate = Ext.Date.format(despatchDate, "Ymd")
		}
		else{
			despatchDate = null;
		}
		

		var header = {
			userName 	: 100000,
			userModKey 	: 100000,
			userId 		: 100000
		};

		var payload = {
			userModKey			: 100000,
			//docId               : docId,
			//docVer              : docVer,
			//modifiedOn          : modifiedOn,
			supplierNumber      : supplierNumber,
			deliveryNoteNo      : deliveryNoteNo,
			receiptNo           : receiptNo,
			documentNumber      : documentNumber,
			documentDate        : documentDate,
			recipientOfInvoice  : recipientOfInvoice,
			vatNumber           : vatNumber,
			despatchDate        : despatchDate,
			invoiceNumber       : invoiceNumber,
			currency            : currency,
			accountNumber       : accountNumber,
			voucherNumber       : voucherNumber,
			faxNumber           : faxNumber,
			associationNumber   : associationNumber,
			netInvoiceTotal     : netInvoiceTotal,
			orderId             : orderId,
			vat                 : vat,
			vatRate             : vatRate,
			vatPayable          : vatPayable,
			invoiceAmount       : invoiceAmount,
			paymentDetails      : paymentDetails,
			totalBeforeVat      : totalBeforeVat,
			totalAmountDue      : totalAmountDue,
			discount            : discount,
			invoiceAmount       : invoiceAmount,
			//modifiedOn          : modifiedOn,
			deliveryNote        : deliveryNote,
			deliveryDetails     : deliveryDetails,
			paymentDetails      : paymentDetails,
			companyAddress      : companyAddress,
			invoiceTo           : invoiceTo,
			deliverTo           : deliverTo,
			reference 	        : 'docUpdate',
		};


		if(btnRef == 'documentUpdateBtn'){
			payload.docId = docId;
			payload.docVer = docVer;

			Ext.MessageBox.confirm('Confirm', 'Are you sure?', function(btn) {

				if (btn == 'yes') {
					me.sendRequest(appActionType.ACTION_TYPE_UPDATE, appContentType.CONTENT_TYPE_DOC, payload, null);			
				}
			});

		}
		else if (btnRef == 'documentSaveBtn') {
			me.sendRequest(appActionType.ACTION_TYPE_NEW, appContentType.CONTENT_TYPE_DOC, payload, null);
		}
	},

	onWindowClose: function () {
		this.view.destroy();
	},

	onDocDetailsDblClk: function (dataview, record, item, index, e) {
		var me = this;
		var data = record.data;
		var win = Ext.create('Desktop.view.doc.DocDetailsForm');

		win.lookupReference('docDetailsId').setValue(data.docDetailsId);
		win.lookupReference('docDetailsVer').setValue(data.docDetailsVer);
		win.lookupReference('docId').setValue(data.docId);
		win.lookupReference('partNo').setValue(data.partNo);
		win.lookupReference('itemQty').setValue(data.itemQty);
		win.lookupReference('referenceNo').setValue(data.referenceNo);
		win.lookupReference('itemName').setValue(data.itemName);
		win.lookupReference('itemCode').setValue(data.itemCode);
		win.lookupReference('rent').setValue(data.rent);
		win.lookupReference('pack').setValue(data.pack);
		win.lookupReference('itemDescription').setValue(data.itemDescription);
		win.lookupReference('propertyAddress').setValue(data.propertyAddress);
		win.lookupReference('trade').setValue(data.trade);
		win.lookupReference('unitPrice').setValue(data.unitPrice);
		win.lookupReference('totalPrice').setValue(data.totalPrice);
		win.lookupReference('netValue').setValue(data.netValue);
		win.lookupReference('valueOfGoods').setValue(data.valueOfGoods);
		win.lookupReference('insurancePremium').setValue(data.insurancePremium);
		win.lookupReference('modifiedOn').setValue(data.modifiedOn);
		win.show();

		this.getDocDetailsGrid(data.docId);
	},
	onFormReset: function(button, e, eOpts){
		var me = this;

		me.lookupReference('docId').reset();
		me.lookupReference('docVer').reset();
		me.lookupReference('modifiedOn').reset();
		me.lookupReference('supplierNumber').reset();
		me.lookupReference('deliveryNoteNo').reset();
		me.lookupReference('receiptNo').reset();
		me.lookupReference('documentNumber').reset();
		me.lookupReference('documentDate').reset();
		me.lookupReference('recipientOfInvoice').reset();
		me.lookupReference('vatNumber').reset();
		me.lookupReference('despatchDate').reset();
		me.lookupReference('invoiceNumber').reset();
		me.lookupReference('currency').reset();
		me.lookupReference('accountNumber').reset();
		me.lookupReference('voucherNumber').reset();
		me.lookupReference('faxNumber').reset();
		me.lookupReference('associationNumber').reset();
		me.lookupReference('netInvoiceTotal').reset();
		me.lookupReference('orderId').reset();
		me.lookupReference('vat').reset();
		me.lookupReference('vatRate').reset();
		me.lookupReference('vatPayable').reset();
		me.lookupReference('invoiceAmount').reset();
		me.lookupReference('paymentDetails').reset();
		me.lookupReference('totalBeforeVat').reset();
		me.lookupReference('discount').reset();
		me.lookupReference('totalAmountDue').reset();
		me.lookupReference('modifiedOn').reset();
		me.lookupReference('deliveryNote').reset();
		me.lookupReference('deliveryDetails').reset();
		me.lookupReference('companyAddress').reset();
		me.lookupReference('invoiceTo').reset();
		me.lookupReference('deliverTo').reset();
	},

	docDetailsUpdate: function(button, e, eOpts){
		var btnRef = button.reference, me  = this;

		var docDetailsId 	    = me.lookupReference('docDetailsId').getValue();
		var docDetailsVer 	    = me.lookupReference('docDetailsVer').getValue();
		var docId         	    = me.lookupReference('docId').getValue();
		var partNo     		    = me.lookupReference('partNo').getValue();
		var itemQty 	   	    = me.lookupReference('itemQty').getValue();
		var unitPrice           = me.lookupReference('unitPrice').getValue();
		var referenceNo         = me.lookupReference('referenceNo').getValue();
		var itemName 	        = me.lookupReference('itemName').getValue();
		var itemCode 	        = me.lookupReference('itemCode').getValue();
		var rent          	    = me.lookupReference('rent').getValue();
		var pack 	     	    = me.lookupReference('pack').getValue();
		var itemDescription     = me.lookupReference('itemDescription').getValue();
		var propertyAddress     = me.lookupReference('propertyAddress').getValue();
		var trade      			= me.lookupReference('trade').getValue();
		var totalPrice      	= me.lookupReference('totalPrice').getValue();
		var netValue          	= me.lookupReference('netValue').getValue();
		var valueOfGoods 	    = me.lookupReference('valueOfGoods').getValue();
		var insurancePremium    = me.lookupReference('insurancePremium').getValue();

		if(Ext.isEmpty(partNo)){
			partNo = null;
		}
		if(Ext.isEmpty(itemQty)){
			itemQty = null;
		}
		if(Ext.isEmpty(unitPrice)){
			unitPrice = null;
		}
		if(Ext.isEmpty(totalPrice)){
			totalPrice = null;
		}
		if(Ext.isEmpty(netValue)){
			netValue = null;
		}
		if(Ext.isEmpty(valueOfGoods)){
			valueOfGoods = null;
		}
		if(Ext.isEmpty(insurancePremium)){
			insurancePremium = null;
		}

		if(Ext.isEmpty(referenceNo)){
			referenceNo = null;
		}
		if(Ext.isEmpty(itemName)){
			itemName = null;
		}
		if(Ext.isEmpty(itemCode)){
			itemCode = null;
		}
		if(Ext.isEmpty(rent)){
			rent = null;
		}
		if(Ext.isEmpty(pack)){
			pack = null;
		}
		if(Ext.isEmpty(itemDescription)){
			itemDescription = null;
		}
		if(Ext.isEmpty(propertyAddress)){
			propertyAddress = null;
		}
		if(Ext.isEmpty(trade)){
			trade = null;
		}

		var header = {
			userName 	: 100000,
			userModKey 	: 100000,
			userId 		: 100000
		};
		var payload = {
			docId           : docId,
			partNo          : partNo,
			itemQty         : itemQty,
			referenceNo     : referenceNo,
			itemName        : itemName,
			itemCode        : itemCode,
			rent            : rent,
			pack            : pack,
			itemDescription : itemDescription,
			propertyAddress : propertyAddress,
			trade           : trade,
			unitPrice       : unitPrice,
			totalPrice 		: totalPrice,
			netValue        : netValue,
			valueOfGoods    : valueOfGoods,
			insurancePremium: insurancePremium,
			reference: 'docDetailsUpdate'
		};

		if(btnRef == 'docDetailsUpdtBtn'){
			payload.docDetailsId = docDetailsId;
			payload.docDetailsVer = docDetailsVer;

			Ext.MessageBox.confirm('Confirm', 'Are you sure?', function(btn) {

				if (btn == 'yes') {
					me.sendRequest(appActionType.ACTION_TYPE_UPDATE_DOC_DETAILS, appContentType.CONTENT_TYPE_DOC_DETAILS, payload, null);			
				}
			});
		}
		else if(btnRef == 'docDetailsSaveBtn'){
			me.sendRequest(appActionType.ACTION_TYPE_NEW_DOC_DETAILS, appContentType.CONTENT_TYPE_DOC_DETAILS, payload, null);
		}
	},

	onAddNewDocDetails: function(button, e, eOpts){
		var win = Ext.create('Desktop.view.doc.DocDetailsForm');
		
		var docId =  this.lookupReference('docId').getValue();
		win.lookupReference('docId').setValue(docId);
		win.lookupReference('docDetailsUpdtBtn').setHidden(true);
		win.lookupReference('docDetailsSaveBtn').setHidden(false);

		win.show();
	},
	onAddNewDoc: function(button, e, eOpts){
		var win   = Ext.create('Desktop.view.doc.DocWindow');
		
		win.lookupReference('userGridPanel').setHidden(true);
		win.lookupReference('documentUpdateBtn').setHidden(true);
		win.lookupReference('documentSaveBtn').setHidden(false);
		win.lookupReference('formReset').setHidden(false);

		win.show();
	},

	onDocGridFilter : function (component, newValue, oldValue, eOpts) {
		var store = Ext.data.StoreManager.lookup('docStore');
		store.clearFilter();

		if (newValue) {

			var matcher = new RegExp(Ext.String.escapeRegex(newValue), "i");

			store.filter({
				filterFn: function(record) {
					return 	matcher.test(record.get('docId')) ||
							matcher.test(record.get('docVer')) ||
							matcher.test(record.get('modifiedOn')) ||
							matcher.test(record.get('supplierNumber')) ||
							matcher.test(record.get('deliveryNoteNo')) ||
							matcher.test(record.get('receiptNo')) ||	
							matcher.test(record.get('documentNumber')) ||
							matcher.test(record.get('documentDate')) ||
							matcher.test(record.get('recipientOfInvoice')) ||
							matcher.test(record.get('vatNumber')) ||
							matcher.test(record.get('despatchDate')) ||
							matcher.test(record.get('invoiceNumber')) ||
							matcher.test(record.get('currency'))||
							matcher.test(record.get('accountNumber')) ||
							matcher.test(record.get('voucherNumber')) ||
							matcher.test(record.get('faxNumber')) ||
							matcher.test(record.get('associationNumber')) ||
							matcher.test(record.get('netInvoiceTotal')) ||
							matcher.test(record.get('orderId')) ||
							matcher.test(record.get('vat')) ||
							matcher.test(record.get('vatRate')) ||
							matcher.test(record.get('vatPayable')) ||
							matcher.test(record.get('invoiceAmount')) ||
							matcher.test(record.get('paymentDetails'))||
							matcher.test(record.get('totalBeforeVat')) ||
							matcher.test(record.get('totalAmountDue')) ||
							matcher.test(record.get('discount')) ||
							matcher.test(record.get('deliveryNote'))||
							matcher.test(record.get('deliveryDetails')) ||
							matcher.test(record.get('paymentDetails')) ||
							matcher.test(record.get('companyAddress')) ||
							matcher.test(record.get('invoiceTo')) ||
							matcher.test(record.get('deliverTo')); 
				}
			});
		}
		component.focus();
	},
	onDocDetailsGridFilter : function (component, newValue, oldValue, eOpts) {
		var store = Ext.data.StoreManager.lookup('docDetailsStore');
		store.clearFilter();

		if (newValue) {

			var matcher = new RegExp(Ext.String.escapeRegex(newValue), "i");

			store.filter({
				filterFn: function(record) {
					return	matcher.test(record.get('docDetailsId')) ||
							matcher.test(record.get('docDetailsVer')) ||
							matcher.test(record.get('docId')) ||
							matcher.test(record.get('partNo')) ||
							matcher.test(record.get('itemQty')) ||
							matcher.test(record.get('referenceNo')) ||
							matcher.test(record.get('itemName')) ||
							matcher.test(record.get('itemCode')) ||	
							matcher.test(record.get('rent')) ||
							matcher.test(record.get('pack')) ||
							matcher.test(record.get('itemDescription')) ||
							matcher.test(record.get('propertyAddress')) ||
							matcher.test(record.get('trade')) ||
							matcher.test(record.get('unitPrice')) ||
							matcher.test(record.get('totalPrice'))||
							matcher.test(record.get('netValue')) ||
							matcher.test(record.get('valueOfGoods')) ||
							matcher.test(record.get('insurancePremium'))||
							matcher.test(record.get('modifiedOn'));
				}
			});
		}
		component.focus();
	},
	
});