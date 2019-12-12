Ext.define('Desktop.view.customer.Customer', {
	extend: 'Ext.grid.Panel',
	xtype: 'customers',
	title: 'Customer',
	header:{
		titlePosition: 0,
		items:[
			{
				xtype: 'textfield',
				reference: 'CustomerGridFilter',
				fieldLabel: 'Filter',
				padding: 3,
				left: '6px',
				width: 200,
				labelWidth: 30,
				margin: '0 680 0 0',
				listeners: {
					change: 'onCustomerGridFilter'
				},
			},
			{
				xtype:'button',
				iconCls: 'x-fa fa-plus',
				text: 'Add Customer',
				style: 'background-color: grey;',
				margin: '0 10 0 0',
				listeners:{
					//click: 'onAddNewCusomer'
				}
			},	
		]
	},
	controller: 'customer',
	viewModel: { type: 'customers' },
	bind: 'customerStore',
   // showing on UI
	scrollable: true,
	columns: [
		{
			dataIndex: 'customerId',
			text: 'Customer ID',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 150,
			hidden : true,
			align: 'center'
		}, 
		{
			dataIndex: 'customerVer',
			text: 'Customer Version',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 160,
			hidden : true,
			align: 'center'
		}, 
		{
			dataIndex: 'customerNumber',
			text: 'Customer Number',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 150,
			align: 'center'
		}, 
		{
			dataIndex: 'contactNumber',
			text: 'Contact Number',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 150,
			align: 'center'
		}, 
		{
			dataIndex: 'customerOrderNo',
			text: 'Customer Order No',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 180,
			align: 'center'
		}, 
		{
			dataIndex: 'customerAddress',
			text: "<span style = 'margin-left: 200px'>Customer Address</span>",
			sortable: true,
			filter: {
				type: 'string'
			},
			//width : 155,
			flex : 1,
			align: 'left'
		},
		{
			dataIndex: 'docId',
			text: 'Doc ID',
			hidden: true,
			sortable: true,
			filter: {
				type: 'string'
			},
			align: 'center'
		},
		{
			xtype: 'datecolumn',
			dataIndex: 'modifiedOn',
			text: 'Modified On',
			format: 'Y-m-d H:i:s A',
			sortable: true,
			hidden: true,
			filter: {
				type: 'date'
			},
			width : 150
		}  
	],
	stripeRows : true,
	columnLines: true,
	features: [
		{
			ftype: 'grouping',
			groupHeaderTpl: '{name} ({children.length})',
			startCollapsed: true
		}
	],
	plugins: [
		{
			ptype: 'gridfilters'
		}
	],
	viewConfig : {
		enableTextSelection : true
	},
	listeners: {
		itemdblclick:  'onDoubleClick',
		afterrender: 'onCustPanelShow' ,
	}
});