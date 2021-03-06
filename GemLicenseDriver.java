import java.util.List;

/**
 * GemLicenseDriver should be called from the command line and takes a single
 * argument, which is the name of a Ruby Gem package. The program queries the
 * RubyGems.org web API and returns the license(s) of the given gem. The program
 * prints the license information to an output file : "output.txt"
 *
 * @author Saurav Agrawal
 * @version 1.3
 * @since 2016-03-30
 */

public class GemLicenseDriver {

	public static void main(String[] args) {
		List<String> gemLicenseList;
		GemLicenseFinder gemLicense = new GemLicenseFinder();
		// Parse command line argument for gem name
		String gemName = "";
		if (args.length == 1) {
			gemName = args[0];
			if (gemLicense.isGem(gemName)) {
				gemLicenseList = gemLicense.allLicenses(gemName);
				gemLicense.PrintLicenses(gemName, gemLicenseList);
			} else {
				System.out
						.println("There was an error. Invalid Gem entered. Please try again.");
			}
		} else {
			System.out.println("Illegal Number of Arguments");
		}
	}
}
