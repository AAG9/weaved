import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.clouddevices.CloudDevices;
import com.google.api.services.clouddevices.CloudDevices.Devices;
import com.google.api.services.clouddevices.model.CloudDeviceChannel;
import com.google.api.services.clouddevices.model.Command;
import com.google.api.services.clouddevices.model.CommandDefNew;
import com.google.api.services.clouddevices.model.Device;
import com.google.api.services.clouddevices.model.DevicesGetStateHistoryResponse;
import com.google.api.services.clouddevices.model.DevicesListResponse;
import com.google.api.services.clouddevices.model.RegistrationTicket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;


public class CloudDevicesSample {

  // See https://developers.google.com/weave/v1/dev-guides/getting-started/authorizing#setup
  // on how to set up your project and obtain client ID, client secret and API key.
 private static final String CLIENT_ID = "593065169550-8o2r95tnqqtqmfgsgv5qn1e1u16hdr0h.apps.googleusercontent.com";
  private static final String CLIENT_SECRET = "NtViFQ_4_urrOvv_I1Cex6d2";
  private static final String API_KEY = "AIzaSyA1mndDPfSWoFy2vYxJZy9DUukvVK9bSS4";
  private static final String AUTH_SCOPE = "https://www.googleapis.com/auth/weave.app";
  CloudDevices apiClient;
  // Redirect URL for client side installed apps.
  private static final String REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob";

  private static final File CREDENTIALS_CACHE_FILE = new File("credentials_cache.json");

  // Command definitions of a new device if we need to create it.
  private static final String COMMAND_DEFS = "{" +
      "    \"storage\": {" +
      "     \"list\": {" +
      "       \"parameters\": {" +
      "        \"path\": {" +
      "          \"type\": \"string\"," +
      "          \"isRequired\": true" +
      "        }," +
      "        \"continuationToken\": {" +
      "          \"type\": \"string\"" +
      "        }," +
      "        \"entryCount\": {" +
      "          \"type\": \"integer\"" +
      "        }" +
      "       }" +
      "      }" +
      "     }," +
      "     \"_blinkLed\": {" +
      "     }" +
      "    }";

  public static void main(String[] args) throws IOException, SQLException {
    new CloudDevicesSample().run();
  }

  private final NetHttpTransport httpTransport = new NetHttpTransport();
  private final JacksonFactory jsonFactory = new JacksonFactory();

  public List<Device> run() throws IOException, SQLException {
  
    try {
      apiClient = getApiClient();
    } catch (IOException e) { throw new RuntimeException("Could not get API client", e); }

    DevicesListResponse devicesListResponse;
    try {
      // Listing devices, request to devices.list API method, returns a list of devices
      // available to user. More details about the method:
      // https://developers.google.com/weave/v1/reference/cloud-api/devices/list
      devicesListResponse = apiClient.devices().list().execute();
    } catch (IOException e) { throw new RuntimeException("Could not list devices", e); }
    List<Device> devices = devicesListResponse.getDevices();
    Device device;
    String action= null;
    if (devices == null || devices.isEmpty()) {
      System.out.println("No devices, creating one.");
      try {
        device = createDevice(apiClient);
        System.out.println("Created new device: " + device.getId());
      } catch (IOException e) {
        throw new RuntimeException("Could not create new device", e);
      }
    } else {
      //device = devices.get(0);
/*for(Device dev : devices){
        System.out.println("Available device: " + dev.getId());
        
        // More about commands and command definitions:
        // https://developers.google.com/weave/v1/dev-guides/getting-started/commands-intro
		 if(dev.getId().equals("f3d59e25-0f96-dc2d-711f-971051345a06")) {
   System.out.println("Sending a new command to the device");
   Map<String, Object> parameters = new HashMap<String, Object>();
   String str = new String("standby");
   parameters.put("_led", 1);
   parameters.put("_on", false);
   Command command = new Command()
      .setName("_ledflasher._set")  // Command name to execute.
      .setParameters(parameters)  // Required command parameter.
      .setDeviceId(dev.getId()); 
   System.out.println("the command prepared was:");
   	System.out.println(command);// Device to send the command to.
   // Calling commands.insert method to send command to the device, more details about the method:
   // https://developers.google.com/weave/v1/reference/cloud-api/commands/insert
   try {
     command = apiClient.commands().insert(command).execute();
     System.out.println(command);
     action=command.toString();
   } catch (IOException e) { throw new RuntimeException("Could not insert command", e); }
   	sendCommand(command);

   // The state of the command will be "queued". In normal situation a client may request
   // command again via commands.get API method to get command execution results, but our fake
   // device does not actually receive any commands, so it will never be executed.

		 }
     
      }
*/    	/*CloudDeviceChannel channel= new CloudDeviceChannel();
    	System.out.println(channel.getGcmRegistrationId());
    	System.out.println(channel.getSupportedType());*/
    	//System.out.println(channel.);
    
   
	
	   
	

		
    	/*Map<String, Object> parameters = new HashMap<String, Object>();
        String str = new String("standby");
        parameters.put("_led", 1);
        parameters.put("_on",true);
        System.out.println(dev.getId());
        if(dev.getId().equals("ba4f4402-551a-41da-6695-0fc95de7ab1d"))
     sendCommand(dev, "_ledflasher._set", parameters);*/
        //System.out.println(dev.getCommandDefs());
       // Map<String, Map<String, CommandDefNew>> obj= dev.getCommandDefs();
    /*   for(Object key : obj.keySet()){
    	   System.out.println((String)key);
    	   Map<String, CommandDefNew> obj2 = obj.get(key);
    	   for(Object key2:obj2.keySet()){
    		   System.out.println("    "+(String)key2);
    		   CommandDefNew obj3 = obj2.get(key2);
    		   System.out.println("     Parameters");
    		   Map<String,Object> strs= (Map<String, Object>) obj3.get("parameters");
    		   for(String stro : strs.keySet())
    			   System.out.println("           "+stro);
    	   }
       }*/
       
        //System.out.println(dev.getId());
     /*   CloudDeviceChannel channel= new CloudDeviceChannel();
        // System.out.println("current Status of the device "+ jsonFactory.toPrettyString(dev.getState())); 
        System.out.println( dev.getChannel());
        System.out.println(dev.getState());
                System.out.println(channel.getGcmRegistrationId());
    	System.out.println(channel.getSupportedType());*/
    }

 /*  System.out.println("The action to be stored :"+action);
  Connection myConn=DriverManager.getConnection("jdbc:mysql://localhost:3306/users_details","root","arnav");
	Statement mystmt = myConn.createStatement();
	mystmt.executeUpdate("INSERT INTO USERDETAILS(email,conditions,events,actions) VALUES(\"arnav@g.com\", \""+"s"+"\",\""+"str"+"\",\'"+action+"\')");
*/	
	//System.out.println("INSERT INTO Ameya(email,conditions,events,actions) VALUES(\"arnav@g.com\", \""+s+"\",\""+str+"\",\""+action+"\")");
   
    return devices;
  }
   

  /**
   * Registers a new device making authenticated user the owner, check for more details:
   * https://developers.google.com/weave/v1/dev-guides/getting-started/register
   * @return the device just created
   */
  private Device createDevice(CloudDevices apiClient) throws IOException {
    GenericJson commandDefs =
        jsonFactory.createJsonParser(COMMAND_DEFS).parseAndClose(GenericJson.class);
    Device deviceDraft = new Device()
        .setDeviceKind("storage")
        .setSystemName("NAS 12418")
        .setDisplayName("Network Access Storage")
        .setChannel(new CloudDeviceChannel().setSupportedType("xmpp"))
        .set("commandDefs", commandDefs);
    RegistrationTicket ticket = apiClient.registrationTickets().insert(
        new RegistrationTicket()
            .setOauthClientId(CLIENT_ID)
            .setDeviceDraft(deviceDraft)
            .setUserEmail("me"))
        .execute();
    ticket = apiClient.registrationTickets().finalize(ticket.getId()).execute();
    return ticket.getDeviceDraft();
  }

  private CloudDevices getApiClient() throws IOException {
    // Try to load cached credentials.
    GoogleCredential credential = getCachedCredential();
    if (credential == null) {
      System.out.println("Did not find cached credentials");
      credential = authorize();
    }
    return new CloudDevices.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName("Weave Sample")
        .setServicePath("clouddevices/v1")
        .setGoogleClientRequestInitializer(new CommonGoogleClientRequestInitializer(API_KEY))
        .build();
  }

  /**
   * Goes through Google OAuth2 authorization flow. See more details:
   * https://developers.google.com/weave/v1/dev-guides/getting-started/authorizing
   */
  private GoogleCredential authorize() throws IOException {
    // Generate the URL to send the user to grant access.
    // There are also other flows that may be used for authorization:
    // https://developers.google.com/accounts/docs/OAuth2
    String authorizationUrl = new GoogleAuthorizationCodeRequestUrl(
        CLIENT_ID, REDIRECT_URL, Collections.singleton(AUTH_SCOPE)).build();
    // Direct user to the authorization URI.
    System.out.println("Go to the following link in your browser:");
    System.out.println(authorizationUrl);
    // Get authorization code from user.
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("What is the authorization code?");
    String authorizationCode = in.readLine();

    // Use the authorization code to get an access token and a refresh token.
    GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
        httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, authorizationCode,
        REDIRECT_URL).execute();
    cacheCredential(response.getRefreshToken());
    // Use the access and refresh tokens to set up credentials.
    GoogleCredential credential = new GoogleCredential.Builder()
        .setJsonFactory(jsonFactory)
        .setTransport(httpTransport)
        .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
        .build()
        .setFromTokenResponse(response);
    return credential;
  }

  private GoogleCredential getCachedCredential() {
    try {
      return GoogleCredential.fromStream(new FileInputStream(CREDENTIALS_CACHE_FILE));
    } catch (IOException e) {
      return null;
    }
  }

  private void cacheCredential(String refreshToken) {
    GenericJson json = new GenericJson();
    json.setFactory(jsonFactory);
    json.put("client_id", CLIENT_ID);
    json.put("client_secret", CLIENT_SECRET);
    json.put("refresh_token", refreshToken);
    json.put("type", "authorized_user");
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(CREDENTIALS_CACHE_FILE);
      out.write(json.toPrettyString().getBytes(Charset.defaultCharset()));
    } catch (IOException e) {
      System.err.println("Error caching credentials");
      e.printStackTrace();
    } finally {
      if (out != null) {
        try { out.close(); } catch (IOException e) { /* Ignore. */ }
      }
    }
  }
 protected void sendCommand(Command command){
	    System.out.println("Sending a new command to the device");
	    System.out.println(command);
	      try {
	      command = apiClient.commands().insert(command).execute();
	    } catch (IOException e) { throw new RuntimeException("Could not insert command", e); }

	    // The state of the command will be "queued". In normal situation a client may request
	    // command again via commands.get API method to get command execution results, but our fake
	    // device does not actually receive any commands, so it will never be executed.
	    try {
	      System.out.println("Sent command to the device:\n" + jsonFactory.toPrettyString(command));
	    } catch (Exception e) { throw new RuntimeException(e); }
 }
  
}