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
 * @version 1.4
 * @since 2016-03-30
 */

public class GemLicenseFinder implements Search{
	final String VERSION_BASE_URL = "https://rubygems.org/api/v1/versions/";
	final String SEARCH_BASE_URL = "https://rubygems.org/api/v1/search.json?query=";
	final String FORMAT_TYPE = ".json";
	final String ENCODING = "UTF-8";
	final String SEPERATOR = "\\s*,\\s*";
	final String KEYWORD_TARGET = "licenses";
	final String NULL_KEYWORD_FORMAT = '"' + KEYWORD_TARGET + '"' + " : N/A";
	final String NULL_LICENSE_CHECK = "null";
	final String PRINT_SEPERATOR = "--------------------------";
	
	private String searchParam;
	private List<String> resultSet;
	
	//Initialize GemLicenseFinder object with String searchParameter and new List<String> resultSet
	public GemLicenseFinder (String searchParameter) {
		searchParam = searchParameter;
		resultSet = new ArrayList<String>();
	}
	
	//Initialize GemLicenseFinder object with String searchParameter and List<String> resultSet
	public GemLicenseFinder (String searchParameter, List<String> resultSet) {
		searchParam = searchParameter;
		this.resultSet = resultSet; 
	}
	
	/**
	 * @return List<String> resultSet
	 */
	public List<String> getResultSet() {
		return resultSet;
	}
	
	/**
	 * @param List<String> resultSet
	 */
	public void setResultSet(List<String> resultSet) {
		this.resultSet = resultSet;
	}
	
	/**
	 * @param String searchParameter
	 */
	@Override
	public void setSearchParam(String searchParameter) {
		searchParam = 	searchParameter;	
	}
	
	/**
	 * @return List<String> resultSet
	 */
	@Override
	public String getSearchParam() {
		return searchParam;
	}
	

	/**
	 * Checks if Gem is valid by searching RubyGems.org API for active gems
	 * 
	 * @return true if gem is active
	 * 
	 */
	public boolean isValid() {

		URL url = null;
		String finalUrl = SEARCH_BASE_URL + searchParam;
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
		if (result.contains(searchParam)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Queries RubyGems.org API for version information on gem. Parses through
	 * result set to identify License data.
	 * 
	 * @return List of license(s) based on gemName
	 */
	public void getLicenses() {
		//List<String> resultSet = new ArrayList<String>();
		String finalUrl = VERSION_BASE_URL + this.searchParam + FORMAT_TYPE;
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
	}	

	/**
	 * Format and print (append) License information to file with time stamp.
	 */
	public void printResults() {
		BufferedWriter writer = null;
		Date currentDateTime = new Date();
		String timeStamp = currentDateTime.toString();
		try {
			File output = new File("output.txt");
			writer = new BufferedWriter(new FileWriter(output, true));
			writer.write(timeStamp);
			writer.newLine();
			writer.write("Ruby Gem: " + searchParam);
			writer.newLine();
			for (String str : resultSet) {
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
