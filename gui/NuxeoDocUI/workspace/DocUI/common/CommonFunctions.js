
//  -- var --
var isMessageBox = false;

// ----------

function replacer(key, value) {

	if (typeof value === 'number' && !isFinite(value)) {
		return String(value);
	}

	return value;
}

function showProcessMessage(message){
	Ext.MessageBox.show({
		title: 'Status',
		msg : message,
		progressText : message,
		zIndex: 99999,
		buttons : Ext.window.MessageBox.OK,
		width : 300,
		progress:true,
		floating:true,
		modal: true,
		wait:false,
		fn : function() {
			Ext.MessageBox.hide();
		}
	});
	Ext.defer(function() {
                    Ext.Msg.toFront()
                }, 20);

	isMessageBox = true;
}

function closeTab(form){
	var tabPanel = form.findParentByType('tabpanel');
	tabPanel.remove(tabPanel.getActiveTab());
}

function formatDateOnly(value){
	return value ? Ext.Date.dateFormat(value, 'M d, Y') : '';
}

function formatDate(value){
	return value ? Ext.Date.dateFormat(value, 'M d, Y') : '';
}

function formatDateTime(value){
	return value ? ((Ext.Date.dateFormat(value, 'Ymd') != '19700101') ? Ext.Date.dateFormat(value, 'M d, Y h:i:s a') : '') : '';
}

function containRoleAny(roles){
	var contain = false;
	var roleList = loginUser.roleList;
	for(var i = 0; i < roleList.length; i++){
		var data = roleList[i];
		if(roles.includes(roleList[i].name)){
			contain = true;
			break;
		}
	}
	return contain;
}

function containsAll(subset){
	if (0 === subset.length) {
	    return false;
	}
	var roleList = loginUser.roleList;
	var rolenames = [];
	for(var i = 0; i < roleList.length; i++){
		rolenames.push(roleList[i].name);
	}
	return subset.every(function (value) {
	    return (rolenames.indexOf(value) >= 0);
	  });
}

function between(x, min, max) {
  return x >= min && x <= max;
}

function showAlertMessage(messsage, duration){

	var alertBox = Ext.Msg.alert("Alert", messsage);

	setTimeout(function(){
		alertBox.hide();
	},
	duration);
}

function checkEmptyValue(value){

	var val = null;

	if(value != '' || value != null || !Ext.isEmpty(value)){ 
		val = value;
	}
	return val;
}
//this is because Mozilla does not show date time in grid
function formatGridDate(date){
	var newDate = null;
	if(!Ext.isEmpty(date)){
		var d2 = Date.parse(date.replace(/-/g, ' '));
		newDate = new Date(d2);
	}

	return newDate;
}

