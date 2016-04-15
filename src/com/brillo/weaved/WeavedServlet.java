package com.brillo.weaved;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.clouddevices.CloudDevices;
import com.google.api.services.clouddevices.model.Device;
import com.google.api.services.clouddevices.model.DevicesListResponse;

import com.mysql.jdbc.PreparedStatement;




/**
 * Servlet implementation class myServlet
 */

public class WeavedServlet extends HttpServlet {
  
	private static final long serialVersionUID = 1L;

	private final NetHttpTransport httpTransport = new NetHttpTransport();
	private static final JacksonFactory jsonFactory = new JacksonFactory();
	
	  
	
	
	// Redirect URL for client side installed apps.
	
	  // See https://developers.google.com/weave/v1/dev-guides/getting-started/authorizing#setup
	  // on how to set up your project and obtain client ID, client secret and API key.
	/* private static final String CLIENT_ID = "593065169550-8o2r95tnqqtqmfgsgv5qn1e1u16hdr0h.apps.googleusercontent.com";
	  private static final String CLIENT_SECRET = "NtViFQ_4_urrOvv_I1Cex6d2";*/
	  
	  //private static final String AUTH_SCOPE = "https://www.googleapis.com/auth/weave.app";

	  // Redirect URL for client side installed apps.
	  private static final String REDIRECT_URL = "http://localhost:8888/";
	  
	  
	  


	
	// See https://developers.google.com/weave/v1/dev-guides/getting-started/authorizing#setup
	  // on how to set up your project and obtain client ID, client secret and API key.
	 private static final String CLIENT_ID = "309435708548-fsvfu060n29531ufr5qqqgf7t2jhhvan.apps.googleusercontent.com";
	 private static final String CLIENT_SECRET = "oFWicANTZPjRLdyC-gcJZdw-";
	 private static final String API_KEY = "AIzaSyDaR-PSdRK0psv1ZyDcaY2n2UFpZWui2UE";

	 private static String Access_Token;
	 private static String Refresh_Token;
	 private static String Id_Token;
	 
	 private String userId= "111612014973784390146";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WeavedServlet() {
        super();
        // TODO Auto-generated constructor stubksonFactory;
    }

    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		//response.getWriter().println("in servlet");
		
		String authCode = request.getReader().readLine();
		//response.getWriter().println(authCode);
		System.out.println("Authcode:"+authCode);
		
		CloudDevices apiClient = getApiClient(authCode);
		try {
			SQLConnectorURLgenerator();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    DevicesListResponse devicesListResponse;
		devicesListResponse = apiClient.devices().list().execute();
		List<Device> devices = devicesListResponse.getDevices();
	    
		int i = 0;
	    for(Device dev : devices){
	        System.out.println("Available device "+ ++i+": " + dev.getId());
	    }
	    response.sendRedirect("http://localhost:8888");
	    
	}
	

	private CloudDevices getApiClient(String authCode) throws IOException {
	    // Try to load cached credentials.
	    GoogleCredential credential=null;
	    if (credential == null) {
	      System.out.println("Did not find cached credentials");
	     credential = authorize(authCode);
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
	 * @return 
	   */
	  private GoogleCredential authorize(String authCode) throws IOException {
		  String authorizationCode = authCode;
		  System.out.println("test");
		  // Use the authorization code to get an access token and a refresh token.
		  GoogleAuthorizationCodeTokenRequest request = new GoogleAuthorizationCodeTokenRequest(
	        httpTransport, JacksonFactory.getDefaultInstance(), CLIENT_ID, CLIENT_SECRET, authorizationCode,
	        REDIRECT_URL);
	    System.out.println(request);
	    GoogleTokenResponse response = request.execute();
	    System.out.println(response);
	    Access_Token=response.getAccessToken();
	    Refresh_Token=response.getRefreshToken();
	    Id_Token=response.getIdToken();
	    
	    System.out.println("Refresh TOken:"+response.getRefreshToken());
	    System.out.println("Id TOken:"+response.getIdToken());
	    // Use the access and refresh tokens to set up credentials.
	    GoogleCredential credential = new GoogleCredential.Builder()
	        .setJsonFactory(jsonFactory)
	        .setTransport(httpTransport)
	        .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
	        .build()
	        .setFromTokenResponse(response);
	    return credential;
	  }
	
	  
	  public static void SQLConnectorURLgenerator() throws ClassNotFoundException {
			String url;
			String user = null;
			String password = null;
			
		Class.forName("com.mysql.jdbc.Driver");
			url = "jdbc:mysql://127.0.0.1:3306/Credentials";
			user 		= "root";
			password 	= "root";			
		  getConnection(url, user, password);
		}
		
		private static void getConnection(String url, String user, String password){
			System.out.println(user+"    "+password);
			String type = "Unauthorized_user";
			try {
				Connection conn =  DriverManager.getConnection(url,user,password);
				
				String statement = "INSERT INTO Credentials ( client_id, client_secret, refresh_token, type ) VALUES(?,?,?,?) ";
		          PreparedStatement stmt =  (PreparedStatement) conn.prepareStatement(statement);
		          stmt.setString(1, CLIENT_ID);
		          stmt.setString(2, CLIENT_SECRET);
		          stmt.setString(3, Refresh_Token);
		          stmt.setString(4, type);
		          int success = 2;
		          success = stmt.executeUpdate();
		          if (success == 1) {
		            System.out.println("Success! Database written");
		          } else if (success == 0) {
		            System.out.println("Failure! Database not written");
		          }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	  
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
