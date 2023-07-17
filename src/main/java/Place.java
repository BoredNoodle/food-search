/* CS 125 - Intro to Computer Science II
 * File Name: Place.java
 * Project 6 - Due 4/1/2020
 * Instructor: Dr. Dan Grissom
 * 
 * Name: Joshua Lee
 * Description: Insert your meaningful description for Project 4.
 */
public class Place {
	//////////////////////////////////////////////
	// Instance variables
	private String name;
	private double rating;
	private String address;
	private String placesId;
	
	//////////////////////////////////////////////
	// Overloaded constructor
	public Place(String placeName, double placeRating, String placeAddress, String placeId) {
		super();
		name = placeName;
		rating = placeRating;
		address = placeAddress;
		placesId = placeId;
	}

	//////////////////////////////////////////////
	// Getters
	public String getName() { return name; }
	public double getRating() { return rating; }
	public String getAddress() { return address; }
	public String getPlacesId() { return placesId; }

	//////////////////////////////////////////////
	// toString override
	public String toString() {
		if (rating > 0)
			return name + " (" + rating + "/5.0)";
		else
			return name + " (NO RATINGS)";
	}
}
