/**
 * This class is the view model for the Main view of the application.
 */
Ext.define('Desktop.DocViewModel', {
	extend: 'Ext.app.ViewModel',

	alias: 'viewmodel.docs',

	data: {
		selectedDoc: null,
		selectedDetails: null,
		docKey: null,
	},
});


