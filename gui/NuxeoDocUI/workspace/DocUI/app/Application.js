/**
 * The main application class. An instance of this class is created by app.js when it
 * calls Ext.application(). This is the ideal place to handle application launch and
 * initialization details.
 */
Ext.define('Desktop.Application', {
	extend: 'Ext.app.Application',

	name: 'Desktop',

	requires: [
		'Ext.data.ArrayStore',
		'Ext.util.Format',
		'Ext.grid.Panel',
		'Ext.grid.RowNumberer'
	],

	quickTips: false,
	platformConfig: {
		desktop: {
			quickTips: true
		}
	},

	stores: [
		
	],

	launch: function () {
		this.createGlobalStores();
	},

	onAppUpdate: function () {
		Ext.Msg.confirm('Application Update', 'This application has an update, reload?',
			function (choice) {
				if (choice === 'yes') {
					window.location.reload();
				}
			}
		);
	},

	createGlobalStores : function(){

		Ext.create('Ext.data.Store', {
			model: 'Desktop.model.Doc',
			storeId: 'docStore',
			groupField: 'documentDate'
		});

		Ext.create('Ext.data.Store', {
			model: 'Desktop.model.DocDetails',
			storeId: 'docDetailsStore',
		});

		Ext.create('Ext.data.Store', {
			model: 'Desktop.model.Customer',
			storeId: 'customerStore',
		});
	}
});
