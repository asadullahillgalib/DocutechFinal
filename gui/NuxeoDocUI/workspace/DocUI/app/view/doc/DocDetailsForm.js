Ext.define('Desktop.view.doc.DocDetailsForm', {
	extend: 'Ext.window.Window',
	xtype: 'docdetailsform',
	iconCls: 'x-fa fa-edit',
	controller: 'doc',
	viewModel: { 
		type: 'docs' 
	},
	referenceHolder: true,
	layout: 'column',
	scrollable: true,
	width: 800,
	height: 360,
	resizable: false,
	renderTo: Ext.getBody(),
	title: 'Doc Details Form',
	modal: true,
			
	fieldDefaults: {
		labelAlign: 'top',
		columnWidth: .25,
		padding: 5
	},
	items: [
		{
			xtype: 'fieldcontainer',
			columnWidth: 1,
			layout: {
				type: 'column',
				align: 'stretch'
			},
			padding: '0 0 0 10',
			items:[
				{ 
					xtype: 'numberfield',
					fieldLabel: 'Doc Details ID',
					name: 'docDetailsId',
					reference: 'docDetailsId',
					columnWidth: .25,
					margin: '0 15 0 0',
					hideTrigger: true,
					hidden : true,
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'numberfield',
					fieldLabel: 'Doc Details Version',
					name: 'docDetailsVer',
					reference: 'docDetailsVer',
					columnWidth: .25,
					margin: '0 15 0 0',
					hideTrigger: true,
					hidden : true,
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'numberfield',
					fieldLabel: 'Doc ID',
					name: 'docId',
					reference: 'docId',
					columnWidth: .25,
					margin: '0 15 0 0',
					hideTrigger: true,
					hidden : true,
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'numberfield',
					fieldLabel: 'Part No.',
					reference: 'partNo',
					name: 'partNo',
					columnWidth: .33,
					margin: '10 15 0 0',
					hideTrigger: true,
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'numberfield',
					fieldLabel: 'Item Quantity',
					name: 'itemQty',
					reference: 'itemQty',
					columnWidth: .33,
					margin: '11 15 0 0',
					hideTrigger: true,
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'textfield',
					fieldLabel: 'Reference No.',
					reference: 'referenceNo',
					name: 'referenceNo',
					columnWidth: .33,
					margin: '11 15 0 0',
					labelStyle: 'font-weight:bold'
				}
			]
		},
		{
			xtype: 'fieldcontainer',
			columnWidth: 1,
			layout: {
				type: 'column',
				align: 'stretch'
			},
			padding: '0 0 0 10',
			items:[
				{
					xtype: 'textfield',
					fieldLabel: 'Item Name',
					reference: 'itemName',
					name: 'itemName',
					columnWidth: .33,
					margin: '10 15 0 0',
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'textfield',
					fieldLabel: 'Item Code',
					reference: 'itemCode',
					name: 'itemCode',
					columnWidth: .33,
					margin: '10 15 0 0',
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'textfield',
					fieldLabel: 'Rent',
					reference: 'rent',
					name: 'rent',
					columnWidth: .33,
					margin: '10 15 0 0',
					labelStyle: 'font-weight:bold'
				}
			]
		},
		{
			xtype: 'fieldcontainer',
			columnWidth: 1,
			layout: {
				type: 'column',
				align: 'stretch'
			},
			padding: '0 0 0 10',
			items:[
				{
					xtype: 'textfield',
					fieldLabel: 'Pack',
					reference: 'pack',
					name: 'pack',
					columnWidth: .33,
					margin: '10 15 0 0',
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'numberfield',
					fieldLabel: 'Trade',
					hideTrigger: true,
					reference: 'trade',
					name: 'trade',
					columnWidth: .33,
					margin: '10 15 0 0',
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'numberfield',
					fieldLabel: 'Unit Price',
					hideTrigger: true,
					reference: 'unitPrice',
					name: 'unitPrice',
					columnWidth: .33,
					margin: '10 15 0 0',
					labelStyle: 'font-weight:bold'
				}
			]
		},
		{
			xtype: 'fieldcontainer',
			columnWidth: 1,
			layout: {
				type: 'column',
				align: 'stretch'
			},
			padding: '0 0 0 10',
			items:[
				{
					xtype: 'numberfield',
					fieldLabel: 'Total Price',
					hideTrigger: true,
					reference: 'totalPrice',
					name: 'totalPrice',
					columnWidth: .33,
					margin: '10 15 0 0',
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'numberfield',
					fieldLabel: 'Value of Goods',
					hideTrigger: true,
					reference: 'valueOfGoods',
					name: 'valueOfGoods',
					columnWidth: .33,
					margin: '4 15 0 0',
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'numberfield',
					fieldLabel: 'Net Value',
					hideTrigger: true,
					reference: 'netValue',
					name: 'netValue',
					columnWidth: .33,
					margin: '10 15 0 0',
					labelStyle: 'font-weight:bold'
				}
			]
		},
		{
			xtype: 'fieldcontainer',
			columnWidth: 1,
			layout: {
				type: 'column',
				align: 'stretch'
			},
			padding: '0 0 0 10',
			items:[
				{
					xtype: 'numberfield',
					fieldLabel: 'Insurance Premium',
					hideTrigger: true,
					reference: 'insurancePremium',
					name: 'insurancePremium',
					columnWidth: .33,
					margin: '5 15 0 0',
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'textfield',
					fieldLabel: 'Modified On',
					hideTrigger: true,
					name: 'modifiedOn',
					reference: 'modifiedOn',
					columnWidth: .25,
					margin: '10 15 0 0',
					hidden : true,
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'textarea',
					fieldLabel: 'Item Description',
					reference: 'itemDescription',
					name: 'itemDescription',
					columnWidth: .33,
					margin: '10 15 0 0',
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'textarea',
					fieldLabel: 'Property Address',
					reference: 'propertyAddress',
					name: 'propertyAddress',
					columnWidth: .33,
					margin: '10 15 0 0',
					labelStyle: 'font-weight:bold'
				}
			]
		}
	],
	dockedItems: [
		{
			xtype: 'toolbar',
			dock: 'bottom',
			ui: 'footer',
			items: [
				'->',
				{
					xtype: 'button',
					text: 'Cancel',
					handler: 'onWindowClose'
				},
				{
					xtype: 'button',
					text: 'Update',
					hidden: false,
					reference: 'docDetailsUpdtBtn',
					listeners: {
						click :  'docDetailsUpdate',
					}
				},
				{
					xtype: 'button',
					text: 'Save',
					hidden: true,
					reference: 'docDetailsSaveBtn',
					listeners: {
						click :  'docDetailsUpdate',
					}
				},
				'->'
			]
		}
	]
});