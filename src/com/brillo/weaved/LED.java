package com.brillo.weaved;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
	
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.api.services.clouddevices.model.Command;
import com.google.api.services.clouddevices.model.JsonObject;

public class LED {
LED(){
	
}

protected JSONObject LEDeventHandler(Map<String, Object> map,String devId) throws SQLException, ParseException
{
	System.out.println("in ledeventhandler");
	Connection myConn=DriverManager.getConnection("jdbc:mysql://localhost:3306/users_details","root","arnav");
	Statement mystmt = myConn.createStatement();
	System.out.println("SELECT Conditions,Actions FROM USERSQL WHERE DevID=\""+devId+"\"");
	ResultSet myRes=mystmt.executeQuery("SELECT Conditions,Actions FROM USERSQL WHERE DevID=\""+devId+"\"");
	while(myRes.next()){
		System.out.println("in while");
		Object ob= map.get("_ledflasher");
		@SuppressWarnings("unchecked")
		Map<String,Object> subMap= (Map<String,Object>)ob;
		if(subMap.get("_leds").toString().contentEquals(myRes.getString("Conditions"))){
			System.out.println("inside if");
			JSONParser jsonParser = new JSONParser();
			JSONObject obj = (JSONObject)jsonParser.parse(myRes.getString("Actions"));
			return obj;
		}
	}
	System.out.println("out");
	return null;
	}
protected void LEDactionHandler(JSONObject actions ) throws IOException, SQLException{
	Map<String, Object> parameters = new HashMap<String, Object>();
	String name=null;
	String devId = (String)actions.get("Id");
	System.out.println("just outside ifaction  "+(String)actions.get("Command")+"   "+((String)actions.get("Command")).equalsIgnoreCase("set"));
	if(((String)actions.get("Command")).equalsIgnoreCase("set")){
		parameters.put("_led",Integer.parseInt((String)actions.get("LED")));
		parameters.put("_on",Boolean.parseBoolean("state"));
		 name = "_ledflasher._set";
	}else if(((String)actions.get("Command")).equalsIgnoreCase("toggle")){
		parameters.put("_led",(String)actions.get("LED"));
		 name = "_ledflasher._toggle";
	}else if(((String)actions.get("Command")).equalsIgnoreCase("UpdateDeviceInfo")){
		parameters.put("description",(String)actions.get("LED"));
		 name = "base.updateDeviceInfo";
	}
	Command command = new Command()
		      .setName(name) 
		      .setParameters(parameters)  
		      .setDeviceId(devId); 
	CloudDevicesSample cloud = new CloudDevicesSample();
	cloud.run();
	//System.out.println("Sent command to the device:\n" +command);
	cloud.sendCommand(command);
//	command = apiClient.commands().insert(command).execute();
	
}

}
