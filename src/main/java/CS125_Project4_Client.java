/* CS 125 - Intro to Computer Science II
 * File Name: CS125_Project4_Client.java
 * Project 6 - Due 4/1/2020
 * Instructor: Dr. Dan Grissom
 * 
 * Name: Joshua Lee
 * Description: This project allows the user to enter type of food they want to eat, their starting address, and the distance they're willing
 * to travel. When the user hits search, the program will return a list of restaurants that serve their desired food type. They can see the
 * rating of each restaurant out of 5, and can obtain more details about a selected restaurant, as well as directions on how to get to the
 * restaurant from their starting address by clicking on the corresponding buttons.
 */

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	***NOTE: Before you begin this project, you must complete the following steps:
// 
// 	Create a new Maven Project and import library as dependency
// 		1) In Project Explorer, right click in white space and select "New->Project..."
//		2) Expand the "Maven" folder, select "Maven Project" and click "Next >"
//		3) Make sure both "Create a simple project" AND "Use default Workspace location" are CHECKED and click "Next >"
//		4) Fill out the following fields (and then click "Finish")
//			Group Id:) com.hellodrdan
//			Artifact Id:) Module_06_Threads_GUIs_And_APIs
//			Name:) Module_06_Threads_GUIs_And_APIs
//		5) Navigate in a browser to: https://mvnrepository.com/artifact/com.mashape.unirest/unirest-java
//			a) In the table, click on the highest "Version"
//			b) In the Tab view, click on the "Maven" tab and take note of the groupId, artifactId and version
//		6) In Eclipse, expand the "Module_06_Threads_GUIs_And_APIs" project, right click on the pom.xml file and click "Maven->Add Dependency"
//			a) Fill in the groupId, artifactId and version from step 5b above and click "OK"
//		7) Double click on the pom.xml file to ensure the data was added as a dependency (you'll see it!)
//			a) With pom.xml opened and focused on in Eclipse:
//				i) Click the green "Run As..." (play) button/icon and select "Maven install"
//				ii) Confirm in the Console window that it says "BUILD SUCCESS"
//				iii) If not working, some useful troubleshooting pages may be:
// 					- https://stackoverflow.com/questions/19655184/no-compiler-is-provided-in-this-environment-perhaps-you-are-running-on-a-jre-ra
// 					- https://stackoverflow.com/questions/23310474/hibernate-commons-annotations-4-0-1-final-jar-invalid-loc-header-bad-signature
//		8) Create new (or import existing) Java files into the "src/main/java" folder
//		9) For more info on using the Unirest library, see: http://kong.github.io/unirest-java/
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Toolkit;

public class CS125_Project4_Client extends JFrame {

	///////////////////////////////////////////////////////////////////
	// Instance variables/components
	private JTextField txtStartAddress = new JTextField();
	private JTextField txtFoodSearch = new JTextField();
	private JTextField txtRadius = new JTextField();
	private JList<Place> lstResults;
	private JTextArea txtDetails;
	private JButton btnDirections;
	private JButton btnSearch;
	private JButton btnDetails;


	///////////////////////////////////////////////////////////////////
	// DefaultListModel is bound to lstResults
	private DefaultListModel<Place> dlmResults = new DefaultListModel<Place>();

	///////////////////////////////////////////////////////////////////
	// APIs SECTION
	///////////////////////////////////////////////////////////////////
	private final static String mapsGeocodeBaseUrl = "https://maps.googleapis.com/maps/api/geocode/json?";
	private final static String placesNearbySearchBaseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	private final static String placesDetailBaseUrl = "https://maps.googleapis.com/maps/api/place/details/json?";
	private final static String mapsDirectionBaseUrl = "https://maps.googleapis.com/maps/api/directions/json?"; 

	// Once you obtain a key, include it here instead of "YOUR_KEY_HERE":
	private final static String googleApiKey = "AIzaSyB2Xz2pGWe_dtscTy2Z5TRicB8KZGYwQMU"; // Get your own key: https://developers.google.com

	///////////////////////////////////////////////////////////////////
	// MAIN
	///////////////////////////////////////////////////////////////////
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		// Your program should always output your name and the project number.
		// DO NOT DELETE OR COMMENT OUT. Replace with relevant info.
		System.out.println("Joshua Lee");
		System.out.println("Project 4");
		System.out.println("");

		// Launch GUI
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	// Checked exception...throws above exceptions
		CS125_Project4_Client frame = new CS125_Project4_Client();
		frame.setVisible(true);
	}

	///////////////////////////////////////////////////////////////////
	// Constructor - calls initComponents and createEvents
	///////////////////////////////////////////////////////////////////
	public CS125_Project4_Client() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(CS125_Project4_Client.class.getResource("/resources/food-and-restaurant.png")));
		setTitle("Joshua Lee's Food Search Program");
		initComponents();
		createEvents();
	}

	///////////////////////////////////////////////////////////////////
	// All component initializations done here in this method
	///////////////////////////////////////////////////////////////////
	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(1050, 200, 700, 465);

		// TODO 1: Use WindowBuilder to layout controls (most
		// auto-generated WindowBuilder code will go here)
		JLabel lblStartAddress = new JLabel("Start Address:");
		txtStartAddress.setColumns(10);
		JLabel lblRadius = new JLabel("Radius (miles):");
		txtRadius.setColumns(10);
		JLabel lblFoodSearch = new JLabel("Food Search:");
		txtFoodSearch.setColumns(10);
		btnSearch = new JButton("Search");
		JLabel lblResults = new JLabel("Results:");
		JScrollPane scrResults = new JScrollPane();
		btnDetails = new JButton("Details >");
		btnDirections = new JButton("Directions >");
		JScrollPane scrDetails = new JScrollPane();

		JLabel lblDetails = new JLabel("Details/Directions:");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(btnSearch)
								.addGroup(groupLayout.createSequentialGroup()
										.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
												.addComponent(lblFoodSearch)
												.addComponent(lblRadius)
												.addComponent(lblStartAddress)
												.addComponent(lblResults))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addComponent(scrResults, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)
												.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
														.addComponent(txtRadius, Alignment.LEADING)
														.addComponent(txtStartAddress, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
														.addComponent(txtFoodSearch, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)))))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnDetails, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnDirections, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(scrDetails, GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
								.addComponent(lblDetails))
						.addContainerGap())
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblStartAddress)
										.addComponent(txtStartAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(lblDetails))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(groupLayout.createSequentialGroup()
										.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
												.addComponent(lblRadius)
												.addComponent(txtRadius, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
												.addComponent(lblFoodSearch)
												.addComponent(txtFoodSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnSearch)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addComponent(lblResults)
												.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
														.addComponent(scrResults, GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
														.addGroup(groupLayout.createSequentialGroup()
																.addComponent(btnDetails)
																.addGap(7)
																.addComponent(btnDirections)
																.addGap(0, 244, Short.MAX_VALUE)))))
								.addComponent(scrDetails, GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
						.addContainerGap())
				);

		txtDetails = new JTextArea();
		txtDetails.setEditable(false);
		scrDetails.setViewportView(txtDetails);

		lstResults = new JList<Place>(dlmResults);
		scrResults.setViewportView(lstResults);
		getContentPane().setLayout(groupLayout);
	}

	///////////////////////////////////////////////////////////////////
	// All event handlers placed here in this method
	///////////////////////////////////////////////////////////////////
	private void createEvents() {
		///////////////////////////////////////////////////////////////
		// Calls Google Maps (Geocode) and Google Places APIs to return
		// a list of places with the search radius from the starting
		// address
		// TODO 2: Uncomment, read/understand when you have the btnSearch
		// button in the JFrame
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// Convert address to latitude/longitude
				String address = txtStartAddress.getText();
				HttpRequest geocodeRequest = generateGeocodeRequestUrl(address);
				System.out.println("DEBUG PRINT: " + geocodeRequest.getUrl());
				String latLong = getAndParseGeocodeResponse(geocodeRequest);

				// Making web call to get nearby places search results from user input
				double meters = Double.parseDouble(txtRadius.getText()) * 1609.34;
				HttpRequest nearbyPlacesRequest = generateNearbyPlacesRequestUrl(latLong, txtFoodSearch.getText(), (int)meters);
				System.out.println("DEBUG PRINT: " + nearbyPlacesRequest.getUrl());
				ArrayList<Place> places = getAndParseNearbyPlacesResponse(nearbyPlacesRequest);

				// Populate JList with our results
				dlmResults.clear();
				for (Place p : places)
					dlmResults.addElement(p);
			}
		});



		///////////////////////////////////////////////////////////////
		// Calls Google Places API to return some place details for
		// the selected places (in the list) 
		// TODO 7: Uncomment and read/understand when you have the btnDetails
		// button in the JFrame
		btnDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Get URL for place details
				HttpRequest placeDetailRequest = generatePlaceDetailsRequestUrl((Place)lstResults.getSelectedValue());
				System.out.println("DEBUG PRINT: " + placeDetailRequest.getUrl());

				// Get and Format the response and place in text area
				String formattedPlaceDetails = getAndParsePlaceDetailResponse(placeDetailRequest);
				txtDetails.setText(formattedPlaceDetails);
			}
		});


		///////////////////////////////////////////////////////////////
		// Calls Google Maps API to return some directions to
		// the selected places (in the list, from the starting address)
		// TODO 10: Uncomment and read/understand when you have the btnDirections
		// button in the JFrame
		btnDirections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Get URL for place details
				HttpRequest mapDirectionRequest = generateDirectionsRequestUrl(txtStartAddress.getText(), (Place)lstResults.getSelectedValue());
				System.out.println("DEBUG PRINT: " + mapDirectionRequest.getUrl());

				// Get and Format the response and place in text area
				String formattedDirectionDetails = getAndParseDirectionsResponse(mapDirectionRequest);
				txtDetails.setText(formattedDirectionDetails);
			}
		});

	}

	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// The following four methods generate the URL request strings for    //
	// the four API calls we need to make.								  //
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////	
	////////////////////////////////////////////////////////////////////////
	// Generates a URL request for the Google Maps Geocode web-services API
	// according to the following web-page: https://developers.google.com/maps/documentation/geocoding/start
	// The purpose of this call is to get a JSON response with latitude and
	// longitude for the supplied address.
	////////////////////////////////////////////////////////////////////////
	public static HttpRequest generateGeocodeRequestUrl(String address)
	{
		// Start with the base url
		HttpRequest request = Unirest.get(mapsGeocodeBaseUrl);
		// TODO 3: Finish forming URL
		request = request.queryString("address", address);
		request = request.queryString("key", googleApiKey);

		return request;
	}

	////////////////////////////////////////////////////////////////////////
	// Generates a URL request for the Google Places web-services API
	// according to the following web-page: https://developers.google.com/places/web-service/
	////////////////////////////////////////////////////////////////////////
	public static HttpRequest generateNearbyPlacesRequestUrl(String latLong, String query, int meters)
	{
		// Start with the base url
		HttpRequest request = Unirest.get(placesNearbySearchBaseUrl);
		// TODO 5: Finish forming URL
		
		//Adds the location parameter to String request
		request = request.queryString("location", latLong);
		
		//Adds the radius (in meters) parameter to String request
		request = request.queryString("radius", meters);
		
		//Adds the keyword parameter to String request
		request = request.queryString("keyword", query);
		
		//Adds the API key to String request
		request = request.queryString("key", googleApiKey);

		return request;
	}

	////////////////////////////////////////////////////////////////////////
	// Generates a URL request for the Google Places web-services API
	// according to the following web-page: https://developers.google.com/places/web-service/
	////////////////////////////////////////////////////////////////////////
	public static HttpRequest generatePlaceDetailsRequestUrl(Place place)
	{
		// Start with the base url
		HttpRequest request = Unirest.get(placesDetailBaseUrl);
		// TODO 8: Finish forming URL
		
		//Adds the API key to String request
		request = request.queryString("key", googleApiKey);
		
		//Adds the unique place ID to String request
		request = request.queryString("place_id", place.getPlacesId());

		return request;
	}

	////////////////////////////////////////////////////////////////////////
	// Generates a URL request for the Google Maps directions web-services API
	// according to the following web-page: https://developers.google.com/maps/documentation/directions/
	////////////////////////////////////////////////////////////////////////
	public static HttpRequest generateDirectionsRequestUrl(String origin, Place destPlace)
	{
		// Start with the base url
		HttpRequest request = Unirest.get(mapsDirectionBaseUrl);
		// TODO 11: Finish forming URL
		
		//Adds the starting address to String request
		request = request.queryString("origin", origin);
		
		//Adds the ending address to String request
		request = request.queryString("destination", destPlace.getAddress());
		
		//Adds the API key to String request
		request = request.queryString("key", googleApiKey);

		return request;
	}

	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// The following four methods use the JSONObject and JSONArray		  //
	// classes defined in the org.json JAR file you imported to parse	  //
	// through each of the four responses from the four URL requests.	  //
	// Each method's header describes what kind of data it is returning.  //
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Return the lat,long pair from the given geocode response
	// Ex: 34.5029,39.40201
	////////////////////////////////////////////////////////////////////////
	private static String getAndParseGeocodeResponse(HttpRequest request)
	{
		// TODO 4: Convert response string to JSON & dive down into JSON response to get lat/long
		double lat = 0;
		double lng = 0;
		try {
			// Get the main response
			JSONObject objResponse = request.asJson().getBody().getObject();
			//System.out.println("RESPONSE OBJECT:\n" + objResponse);

			//pass the results array response to arrResults
			JSONArray arrResults = objResponse.getJSONArray("results");
			
			//Get the first Result object in the array
			JSONObject objResult = arrResults.getJSONObject(0);
			
			//Get the geometry field from objResult
			JSONObject objGeo = objResult.getJSONObject("geometry");
			
			//GEt the location field from objResult
			JSONObject objLoc = objGeo.getJSONObject("location");

			//If "lat" field is present in objLoc, set lat to "lat" field
			if (objLoc.has("lat"))
				lat = objLoc.getDouble("lat");
			
			//If "lng" field is present in objLoc, set lng to "ngt" field
			if (objLoc.has("lng"))
				lng = objLoc.getDouble("lng");
		} catch (UnirestException e) {
			System.out.println("API ERROR: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("UNKNOWN ERROR: " + e.getMessage());
		}

		//return formatted String
		return lat + "," + lng;
	}

	////////////////////////////////////////////////////////////////////////
	// Returns an arrayList of places given the nearby places results/response
	// This method needs to parse out the relevant info for a place that
	// we need (see the instance variables in Place.java) and create an object
	// for each place in the response that is passed in as a parameter (each
	// response is added to a new ArrayList, which is returned).
	////////////////////////////////////////////////////////////////////////
	private static ArrayList<Place> getAndParseNearbyPlacesResponse(HttpRequest request)
	{
		// Init places arrayList
		ArrayList<Place> places = new ArrayList<Place>();

		// TODO 6: Dive down into response string; when you find the actual
		// nearby places results, iteration through each one, creating a
		// Place object and adding to the places array.
		try {
			// Get the main response
			JSONObject objResponse = request.asJson().getBody().getObject();
			//System.out.println("RESPONSE OBJECT:\n" + objResponse);

			//pass the results array response to arrResults
			JSONArray arrResults = objResponse.getJSONArray("results");
			for (int i = 0; i < arrResults.length(); i++) {
				//Get the next business
				JSONObject objPlace = arrResults.getJSONObject(i);
				//System.out.printf("\t%s) %s\n", (i + 1), objPlace);

				//Pull the name field from objPlace
				String name = objPlace.getString("name");

				//Pull the address field from objPlace
				String address = objPlace.getString("vicinity");

				//Pull the rating field from objPlace
				double rating = objPlace.getDouble("rating");

				//Pull the place ID field from objPlace
				String placeId = objPlace.getString("place_id");

				//Create new Place object w/ overloaded constructor and add to places ArrayList
				Place restaurant = new Place(name, rating, address, placeId);
				places.add(restaurant);
			}

		} catch (UnirestException e) {
			System.out.println("API ERROR: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("UNKNOWN ERROR: " + e.getMessage());
		}

		return places;
	}

	////////////////////////////////////////////////////////////////////////
	// Return a formatted directions string given the directions response.
	// Ex of formatted return String: 
	// START ADDRESS:
	//    901 E Alosta Ave, Azusa, CA 91702, USA
	//
	// END ADDRESS:
	//    1377 E Gladstone St, Glendora, CA 91740, USA
	//
	// DISTANCE (TIME):
	//    4.5 mi (10 mins)
	//
	// STEP-BY-STEP NAVIGATION:
	//    1: Head south toward E Alosta Ave/Historic Rte 66 W
	//    2: Turn right onto E Alosta Ave/Historic Rte 66 W
	//    3: Turn left at the 1st cross street onto N Citrus Ave
	//    4: Take the Interstate 210 ramp to San Bernardino
	//    5: Merge onto I-210 E
	//    6: Take exit 43 for Sunflower Ave
	//    7: Turn right onto S Sunflower Ave
	//    8: Turn left onto E Gladstone St
	//    9: Turn left at N Shellman Ave
	//    10: Turn right
	//    11: Turn right
	//    12: Turn left
	////////////////////////////////////////////////////////////////////////
	private static String getAndParseDirectionsResponse(HttpRequest request) {

		// TODO 12: Convert response string to JSON & dive down into JSON response
		// to get all the relevant data	
		String startAddress = "N/A";
		String endAddress = "N/A";
		String distAndTime = "N/A";
		String navigation = "N/A";
		try {
			// Get the main response
			JSONObject objResponse = request.asJson().getBody().getObject();
			//System.out.println("RESPONSE OBJECT:\n" + objResponse);
			
			//pass the routes array response to routes
			JSONArray arrRoute = objResponse.getJSONArray("routes");
			
			//Get the first route object in arrRoute array
			JSONObject routeObj = arrRoute.getJSONObject(0);
			JSONArray legs = routeObj.getJSONArray("legs");
			
			//Gets the first step object in legs array
			JSONObject stepObj = legs.getJSONObject(0);
			
			//Get start and end address fields from stepObj
			startAddress = "START ADDRESS:\n\t" + stepObj.getString("start_address") + "\n";
			endAddress = "END ADDRESS:\n\t" + stepObj.getString("end_address") + "\n";

			//Get distance and duration fields from stepObj
			JSONObject disObj = stepObj.getJSONObject("distance");
            JSONObject durObj = stepObj.getJSONObject("duration");
            distAndTime = "DISTANCE (TIME):\n\t" + disObj.getString("text") + " (" + durObj.getString("text") + ")\n";
            
            //Pass the steps array to arrSteps
            JSONArray arrSteps = stepObj.getJSONArray("steps");
            
            //Format navigation String
            navigation = "STEP-BY-STEP NAVIGATION:\n\t";
            
            //Iterate through every index in arrSteps
            for (int i = 0; i < arrSteps.length(); i++) {
            	
            	//Get the step object from arrSteps index
            	JSONObject step = arrSteps.getJSONObject(i);
            	
            	//Get the html_instructions string from step object
            	String instruction = step.getString("html_instructions");
            	
            	//removes all html tags from instruction string
            	instruction = instruction.replaceAll("\\<.*?\\>", "");
            	
            	//removes extra spaces
            	instruction = instruction.replaceAll("(?!^)([A-Z])", " $1");
            	for (int j = 1; j < instruction.length(); j++) {
            		if (instruction.charAt(j) == ' ')
            			if (instruction.charAt(j - 1) == ' ')
            				instruction = instruction.trim().replaceAll(" +", " ");
            	}
            	
            	//append instruction to navigation string
                navigation += "" + (i + 1) + ": " + instruction + "\n\t";
            }
		} catch (UnirestException e) {
			System.out.println("API ERROR: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("UNKNOWN ERROR: " + e.getMessage());
		}

		//return formatted strings
		return startAddress + endAddress + distAndTime + navigation;
	}

	////////////////////////////////////////////////////////////////////////
	// Returns a formatted place details string given the details response.
	// Ex. of formatted details return string:
	// ADDRESS:
	//	     1377 East Gladstone Street #104, Glendora
	//
	// PHONE:
	//	     (909) 305-0505
	//
	// WEB:
	//	     https://www.mooyah.com/locations/glendora-ca-268/
	//
	// HOURS:
	//	     N/A     Monday: 11:00 AM - 9:00 PM
	//	     Tuesday: 11:00 AM - 9:00 PM
	//	     Wednesday: 11:00 AM - 9:00 PM
	//	     Thursday: 11:00 AM - 9:00 PM
	//	     Friday: 11:00 AM - 10:00 PM
	//	     Saturday: 11:00 AM - 10:00 PM
	//	     Sunday: 11:00 AM - 9:00 PM
	////////////////////////////////////////////////////////////////////////
	private static String getAndParsePlaceDetailResponse(HttpRequest request) {
		// Create variables
		String phone = "N/A";
		//String hours = "N/A"; (NOT USED)
		String web = "N/A";
		String address = "N/A";
		String dayHours = "N/A";

		// TODO 9: Convert response string to JSON & dive down into JSON response
		// to get all the relevant data
		try {
			// Get the main response
			JSONObject objResponse = request.asJson().getBody().getObject();
			//System.out.println("RESPONSE OBJECT:\n" + objResponse);
			
			//Obtain the JSON object response
			JSONObject objResult = objResponse.getJSONObject("result");
			
			//Obtain the phone number if the object has one
			if (objResult.has("formatted_phone_number"))
				phone = "PHONE:\n\t" + objResult.getString("formatted_phone_number") + "\n";
			else
				phone = "PHONE:\n\tN/A\n";
			
			//Obtain the website if the object has one
			if (objResult.has("website"))
				web = "WEB:\n\t" + objResult.getString("website") + "\n";
			else
				web = "WEB\n\tN/A\n";
			
			//Obtain the address if the object has one
			if (objResult.has("formatted_address"))
				address = "ADDRESS:\n\t" + objResult.getString("formatted_address") + "\n";
			else
				address = "ADDRESS:\n\tN/A\n";
			
			//Obtain the open days and hours if the object has them
			if (objResult.has("opening_hours")) {
				if (objResult.getJSONObject("opening_hours").has("weekday_text")) {
					dayHours = "HOURS:\n";
					JSONArray arrWorkHours = objResult.getJSONObject("opening_hours").getJSONArray("weekday_text");
					for (int i = 0; i < arrWorkHours.length(); i++) {
						dayHours += "\t" + arrWorkHours.getString(i) + "\n";
					}
				}
			}
			else {
				dayHours = "HOURS:\n\tN/A\n";
			}
		} catch (UnirestException e) {
			System.out.println("API ERROR: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("UNKNOWN ERROR: " + e.getMessage());
		}

		//return the formatted strings
		return address + phone + web + dayHours;
	}
}