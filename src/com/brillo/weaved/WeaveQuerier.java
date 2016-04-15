package com.brillo.weaved;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.clouddevices.CloudDevices.Devices;
import com.google.api.services.clouddevices.model.Device;

public class WeaveQuerier {
	public static void main(String[] args) throws IOException, SQLException, ParseException {
		 CloudDevicesSample cloud= new CloudDevicesSample();
		 List<Device>   devices = cloud.run();

for(Device dev:devices){
	System.out.println(dev.getId());
	System.out.println(dev.getUnknownKeys().get("name").toString());
	checkEvent(dev);
	
	}
	}
	public static void checkEvent(Device dev) throws SQLException, ParseException, IOException{

		if(dev.getUnknownKeys().get("name").toString().contentEquals("weave_daemon_ledflasher")){
			
			LED led = new LED();
			JSONObject action= led.LEDeventHandler(dev.getState(),dev.getId());
			System.out.println(action);
			if(action!=null){
			String actionDev=(String)action.get("Device");
			checkAction(actionDev,action);
			}
		}
		else if(dev.getUnknownKeys().get("name").toString().contentEquals("weave_daemon_speaker")){
			
		}
	}
	public static void checkAction(String actionDev,JSONObject action) throws IOException, SQLException{
		if(actionDev.contentEquals("LED")){
			LED led = new LED();
			led.LEDactionHandler(action);
			
		}
		else if(actionDev.contentEquals("Speaker")){
			
		}
	}

}

