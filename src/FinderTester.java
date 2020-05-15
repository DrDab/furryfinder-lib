import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

import furrylib.FinderUtils;
import furrylib.Furry;

@SuppressWarnings("all")
public class FinderTester 
{
	public static final double LATITUDE = 47.6062;
	public static final double LONGITUDE = -122.3321;
	public static final double SEARCH_RADIUS = 20.0;
	public static final String CITY_NAME = "Seattle";
	
	public static void main(String[] args) throws IOException
	{
		ArrayList<Furry> test = FinderUtils.getFurryList(FinderUtils.getJSONData());
		System.out.println(test.size() + " total furries indexed.");
		double latitude;
		double longitude;
		String cityName;
		double searchRadius;
		boolean useGPX = false;
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter a latitude in degrees. (-1 to skip)");
		latitude = Double.parseDouble(sc.nextLine());
		if (latitude == -1)
		{
			latitude = LATITUDE;
			longitude = LONGITUDE;
			searchRadius = SEARCH_RADIUS;
			cityName = CITY_NAME;
		}
		else
		{
			System.out.println("Please enter a longitude in degrees.");
			longitude = Double.parseDouble(sc.nextLine());
			System.out.println("Please enter a city name.");
			cityName = sc.nextLine();
			System.out.println("Please enter a search radius.");
			searchRadius = Double.parseDouble(sc.nextLine());
		}
		System.out.println("If you would like to generate a Garmin GPX file, please type \"gpx\".");
		useGPX = sc.nextLine().contains("gpx");
		ArrayList<Furry> sorted = FinderUtils.getFurryListWithinSearchRadius(test, latitude, longitude, searchRadius);
		if (useGPX)
		{
			PrintWriter pw = new PrintWriter(new FileWriter("Waypoints FurryMap.gpx"));
			TimeZone tz = TimeZone.getTimeZone("UTC");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
			df.setTimeZone(tz);
			String iso8601Cur = df.format(new Date());
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" xmlns:wptx1=\"http://www.garmin.com/xmlschemas/WaypointExtension/v1\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" creator=\"fenix\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www8.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackStatsExtension/v1 http://www8.garmin.com/xmlschemas/TrackStatsExtension.xsd http://www.garmin.com/xmlschemas/WaypointExtension/v1 http://www8.garmin.com/xmlschemas/WaypointExtensionv1.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\"><metadata><link href=\"http://www.garmin.com\"><text>Garmin International</text></link><time>" + iso8601Cur + "</time></metadata>");
			for(Furry e : sorted)
			{
				double curLat = e.getLatitude();
				double curLon = e.getLongitude();
				String userName = e.getUserName();
				pw.println("<wpt lat=\"" + curLat + "\" lon=\"" + curLon + "\"><ele>0.0</ele><time>2018-08-02T20:43:22Z</time><name>" + userName + "</name><sym>Flag, Blue</sym></wpt>");
			}
			pw.println("</gpx>");
			pw.close();
			System.out.println("GPX file generated.");
		}
		else
		{
			System.out.println("Furries within " + searchRadius + " miles of " + cityName + ":");
			if (sorted.size() == 0)
			{
				System.out.println("No furries nearby!");
			}
			else
			{
				Furry closestFurry = sorted.get(0);
				for(Furry e : sorted)
				{
					double curLat = e.getLatitude();
					double curLon = e.getLongitude();
					double dist = e.distanceFromCoords(latitude, longitude);
					System.out.println("===========================");
					System.out.println("ID#: " + e.getID());
					System.out.println(e.getLatitude() + "," + e.getLongitude());
					System.out.println("Distance: " + dist + " miles");
					System.out.println("Username: " + e.getUserName());
					System.out.println("Description: " + e.getDescription());
					System.out.println("Profile:" + e.getProfile());
					System.out.println("Profile Picture: " + e.getProfilePictureURL());
					System.out.println("===========================");
				}
				System.out.println("The furry nearest to your location is: ");
				try
				{
					DecimalFormat df = new DecimalFormat("#.##");
					System.out.println(closestFurry.getUserName());
					System.out.println(closestFurry.getDescription());
					System.out.println(closestFurry.getLatitude() + "," + closestFurry.getLongitude());
					System.out.println("with distance " + df.format(closestFurry.distanceFromCoords(latitude, longitude)) + " miles from your location.");
					System.out.println("Compass direction to your current location: " + df.format(closestFurry.angleFromCoords(latitude, longitude)) + " degrees");
				}
				catch (NullPointerException e)
				{
					System.out.println("There are no furries in your search radius.");
				}
				System.out.println("\n\nFound " + sorted.size() + " furries total within " + searchRadius + " miles of " + cityName);
			}
		}
	}		
}
