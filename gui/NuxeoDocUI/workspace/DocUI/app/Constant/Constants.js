Ext.define('Desktop.constants.Constants', {
	alias: 'appConstants',
	alternateClassName: 'appConstants',
	requires: [
		'Desktop.constants.ActionType',
		'Desktop.constants.ContentType',
		'Desktop.constants.StatusType',
		'Desktop.constants.Type'
	],
	statics: {
		APP_NAME: 'DocuTech',
		APP_VER: '1.0.0.0',
		SERVER_URL: 'http://localhost:8080/docutech/jsonRequest',

		SOURCE: 'Docutech-client',
		DESTINATION: 'Nuxeo-server',
	}
});