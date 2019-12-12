Ext.define('Desktop.view.customer.CustomerForm', {
	extend: 'Ext.window.Window',
	xtype: 'customers',
	iconCls: 'x-fa fa-edit',
	controller: 'customer',
	viewModel: { 
		type: 'customers' 
	},
	referenceHolder: true,
	layout: 'column',
	scrollable: true,
	width: 800,
	height: 360,
	resizable: false,
	renderTo: Ext.getBody(),
	title: 'Customer Form',
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
					fieldLabel: 'Customer ID',
					reference: 'customerId',
					name: 'customerId',
					columnWidth: .33,
					hidden : true,
					margin: '10 15 0 0',
					hideTrigger: true,
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'numberfield',
					fieldLabel: 'Customer Ver',
					name: 'customerVer',
					reference: 'customerVer',
					columnWidth: .33,
					hidden : true,
					margin: '11 15 0 0',
					hideTrigger: true,
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'datefield',
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
					xtype: 'textfield',
					fieldLabel: 'Customer Number',
					reference: 'customerNumber',
					name: 'customerNumber',
					columnWidth: .50,
					margin: '11 15 0 0',
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'textfield',
					fieldLabel: 'Contact Number',
					reference: 'contactNumber',
					name: 'contactNumber',
					columnWidth: .50,
					margin: '10 15 0 0',
					labelStyle: 'font-weight:bold'
				},
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
					fieldLabel: 'Customer Order No',
					reference: 'customerOrderNo',
					name: 'customerOrderNo',
					columnWidth: .50,
					margin: '10 15 0 0',
					labelStyle: 'font-weight:bold'
				},
				{
					xtype: 'textarea',
					fieldLabel: 'Customer Address',
					reference: 'customerAddress',
					name: 'customerAddress',
					columnWidth: .50,
					margin: '10 15 0 0',
					labelStyle: 'font-weight:bold',
					height: 90
				}
			]
		},		
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
					reference: 'customerUpdtBtn',
					listeners: {
						click :  'onCustomerUpdate',
					}
				},
				{
					xtype: 'button',
					text: 'Save',
					hidden: true,
					reference: 'customerSaveBtn',
					listeners: {
						click :  'onCustomerUpdate',
					}
				},
				'->'
			]
		}
	]
});