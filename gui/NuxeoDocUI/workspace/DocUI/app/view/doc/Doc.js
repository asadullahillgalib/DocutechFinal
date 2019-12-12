Ext.define('Desktop.view.doc.Doc', {
	extend: 'Ext.grid.Panel',
	xtype: 'docs',
	id: 'docgrid',
	reference : 'documentGrid',
	title: 'Docs',
	header:{
		titlePosition: 0,
		items:[
			{
				xtype: 'textfield',
				reference: 'docGridFilter',
				fieldLabel: 'Filter',
				padding: 3,
				left: '6px',
				width: 200,
				labelWidth: 30,
				margin: '0 750 0 0',
				listeners: {
					change: 'onDocGridFilter'
				},
			},
			{
				xtype:'button',
				iconCls: 'x-fa fa-plus',
				text: 'Add Doc',
				style: 'background-color: grey;',
				margin: '0 10 0 0',
				listeners:{
					click: 'onAddNewDoc'
				}
			},	
		]
	},
	controller: 'doc',
	viewModel: { type: 'docs' },
	bind: 'docStore',
   // showing on UI
	scrollable: true,
	columns: [
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
			dataIndex: 'docVer',
			text: 'Doc Version',
			hidden: true,
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 110,
			align: 'center'
		}, 
		{
			dataIndex: 'supplierNumber',
			text: 'Supplier Number',
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 150,
			align: 'center'
		}, 
		{
			dataIndex: 'deliveryNoteNo',
			text: 'Delivery Note No.',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 160,
			align: 'center'
		}, 
		{
			dataIndex: 'receiptNo',
			text: 'Receipt No.',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 100,
			align: 'center'
		}, 
		{
			dataIndex: 'documentNumber',
			text: 'Document Number',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 150,
			align: 'center'
		}, 
		{
			xtype: 'datecolumn',
			dataIndex: 'documentDate',
			text: 'Document Date',
			sortable: true,
			format: 'Y-m-d',
			filter: {
				type: 'date'
			},
			width : 125,
			align: 'center'
		}, 
		{
			dataIndex: 'recipientOfInvoice',
			text: 'Recipient of Invoice',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 155,
			align: 'center'
		}, 
		{
			dataIndex: 'vatNumber',
			text: 'VAT Number',
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 100,
			align: 'center'
		}, 
		{
			dataIndex: 'invoiceNumber',
			text: 'Invoice Number',
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 135,
			align: 'center' 
		}, 
		{
			xtype: 'datecolumn',
			dataIndex: 'despatchDate',
			text: 'Despatch Date',
			format: 'Y-m-d',
			sortable: true,
			filter: {
				type: 'date'
			},
			width : 130,
			align: 'center'
		}, 
		{
			dataIndex: 'currency',
			text: 'Currency',
			sortable: true,
			filter: {
				type: 'list'
			},
			width : 80,
			align: 'center'
		}, 
		{
			dataIndex: 'accountNumber',
			text: 'Account Number',
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 140,
			align: 'center'
		}, 
		{
			dataIndex: 'voucherNumber',
			text: 'Voucher Number',
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 130,
			align: 'center'
		}, 
		{
			dataIndex: 'faxNumber',
			text: 'Fax Number', 
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 110,
			align: 'center'
		}, 
		{
			dataIndex: 'associationNumber',
			text: 'Association Number',
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 170,
			align: 'center'
		}, 
		{
			dataIndex: 'companyAddress',
			text: 'Company Address',
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 150,
			align: 'center'
		}, 
		{
			dataIndex: 'invoiceTo',
			text: 'Invoice To',
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 100,
			align: 'center'
		}, 
		{
			dataIndex: 'deliverTo',
			text: 'Deliver To',
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 100,
			align: 'center'
		}, 
		{
			dataIndex: 'orderId',
			text: 'Order ID',
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 90,
			align: 'center'
		}, 
		{
			dataIndex: 'deliveryNote',
			text: 'Delivery Note', 
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 130,
			align: 'center'
		}, 
		{
			dataIndex: 'deliveryDetails',
			text: 'Delivery Details',
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 160,
			align: 'center'
		}, 
		{
			dataIndex: 'paymentDetails',
			text: 'Payment Details',
			sortable: true,
			filter: {
				type: 'string'
			},
			width : 150,
			align: 'center'
		}, 
		{
			dataIndex: 'vat',
			text: 'VAT',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 150,
			align: 'right'
		}, 
		{
			dataIndex: 'vatRate',
			text: 'VAT Rate',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 105,
			align: 'right'
		}, 
		{
			dataIndex: 'vatPayable',
			text: 'VAT Payable',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 130,
			align: 'right'
		}, 
		{
			dataIndex: 'invoiceAmount',
			text: 'Invoice Amount',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 140,
			align: 'right'
		}, 
		{
			dataIndex: 'totalBeforeVat',
			text: 'Total Before VAT',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 160,
			align: 'right'
		}, 
		{
			dataIndex: 'totalAmountDue',
			text: 'Total Amount Due',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 150,
			align: 'right'
		}, 
		{
			dataIndex: 'discount',
			text: 'Discount',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 100,
			align: 'right'
		}, 
		{
			dataIndex: 'netInvoiceTotal',
			text: 'Net Invoice Total',
			sortable: true,
			filter: {
				type: 'number'
			},
			width : 160,
			align: 'right'
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
			groupHeaderTpl: 'Document Date: {name} ({children.length})',
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
		afterrender: 'onPanelShow' ,
	}
});