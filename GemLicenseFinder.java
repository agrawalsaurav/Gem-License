import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * GemLicenseFinder opens a stream to query information from RubyGems.org API
 * Gem metadata is filtered to locate License information and printed to file
 * "output.txt"
 * 
 * 
 * @author Saurav Agrawal
 * @version 1.3
 * @since 2016-03-30
 */

public class GemLicenseFinder {
	final String VERSION_BASE_URL = "https://rubygems.org/api/v1/versions/";
	final String SEARCH_BASE_URL = "https://rubygems.org/api/v1/search.json?query=";
	final String FORMAT_TYPE = ".json";
	final String ENCODING = "UTF-8";
	final String SEPERATOR = "\\s*,\\s*";
	final String KEYWORD_TARGET = "licenses";
	final String NULL_KEYWORD_FORMAT = '"' + KEYWORD_TARGET + '"' + " : N/A";
	final String NULL_LICENSE_CHECK = "null";
	final String PRINT_SEPERATOR = "--------------------------";

	/**
	 * Checks if Gem is valid by searching RubyGems.org API for active gems
	 * 
	 * @param gemName
	 * @return true if gem is active
	 * 
	 */
	public boolean isGem(String gemName) {

		URL url = null;
		String finalUrl = SEARCH_BASE_URL + gemName;
		try {
			url = new URL(finalUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		String result = "";
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				url.openStream(), ENCODING))) {
			for (String line; (line = reader.readLine()) != null;) {
				result = line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result.contains(gemName)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Queries RubyGems.org API for version information on gem. Parses through
	 * result set to identify License data.
	 * 
	 * @param gemName
	 * @return List of license(s) based on gemName
	 */
	public List<String> allLicenses(String gemName) {
		List<String> resultSet = new ArrayList<String>();
		String finalUrl = VERSION_BASE_URL + gemName + FORMAT_TYPE;
		// Form URL using name of gem
		URL url = null;
		try {
			url = new URL(finalUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		String result = "";
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				url.openStream(), ENCODING))) {
			for (String line; (line = reader.readLine()) != null;) {
				result = line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<String> info = Arrays.asList(result.split(SEPERATOR));
		Set<String> uniqueInfo = new HashSet<String>(info);

		for (String s : uniqueInfo) {
			if (s.contains(KEYWORD_TARGET)) {
				if (s.contains(NULL_LICENSE_CHECK)) {
					resultSet.add(NULL_KEYWORD_FORMAT);
				} else {
					resultSet.add(s);
				}
			}
		}
		return resultSet;
	}

	/**
	 * Format and print (append) License information to file with time stamp.
	 * 
	 * @param gemName
	 * @param licenses
	 */
	public void PrintLicenses(String gemName, List<String> licenses) {
		BufferedWriter writer = null;
		Date currentDateTime = new Date();
		String timeStamp = currentDateTime.toString();
		try {
			File output = new File("output.txt");
			writer = new BufferedWriter(new FileWriter(output, true));
			writer.write(timeStamp);
			writer.newLine();
			writer.write("Ruby Gem: " + gemName);
			writer.newLine();
			for (String str : licenses) {
				writer.write(str);
			}
			writer.newLine();
			writer.write(PRINT_SEPERATOR);
			writer.newLine();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
