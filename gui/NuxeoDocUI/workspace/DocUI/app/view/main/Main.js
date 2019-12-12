/**
 * This class is the main view for the application. It is specified in app.js as the
 * "mainView" property. That setting automatically applies the "viewport"
 * plugin causing this view to become the body element (i.e., the viewport).
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('Desktop.view.main.Main', {
	extend: 'Ext.tab.Panel',
	xtype: 'app-main',

	requires: [
		'Ext.plugin.Viewport',
		'Ext.window.MessageBox',

		'Desktop.view.main.MainController',
		'Desktop.view.main.MainModel',
	],

	controller: 'main',
	viewModel: 'main',

	ui: 'navigation',

	tabBarHeaderPosition: 1,
	titleRotation: 0,
	tabRotation: 0,

	header: {
		layout: {
			align: 'stretchmax'
		},
		title: {
			bind: {
				text: '{name}'
			},
			flex: 0
		},
	},

	tabBar: {
		flex: 1,
		layout: {
			align: 'stretch',
			overflowHandler: 'none'
		}
	},

	responsiveConfig: {
		tall: {
			headerPosition: 'top'
		},
		wide: {
			headerPosition: 'left'
		}
	},

	defaults: {
		bodyPadding: 20,
		tabConfig: {
			responsiveConfig: {
				wide: {
					iconAlign: 'left',
					textAlign: 'center'
				},
				tall: {
					iconAlign: 'top',
					textAlign: 'center',
					width: 120
				}
			}
		}
	},

	items: [
		{
			title: 'Docs',
			//iconCls: 'x-fa fa-file',
			items: [
				{
					xtype: 'docs'
				}
			],
			layout: 'fit'
		},
		{
			title: 'Customers',
			hidden: true,
			//iconCls: 'x-fa fa-users',
			items: [
				{
					xtype: 'customers',
				}
			],
			layout: 'fit'
		},
	]
});
