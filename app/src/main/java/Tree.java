import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.util.Properties;

public class Tree {

  private static String[] parseArgs(String[] args) {
    if (args.length == 0) return new String[] {"."};
    // TODO: real argument parsing
    return args;
  }

  final static public String VERSION = version();

  /** Get the program version on startup. */
  private static String version() {
    Properties p = new Properties();
    try (Reader r =
        new InputStreamReader(MethodHandles.lookup().lookupClass().getResourceAsStream("/version.properties"))) {
      p.load(r);
    } catch (IOException e) {
      System.err.println("Error reading version.properties");
      e.printStackTrace();
    }
    return p.getProperty("version");
  }

  public static void main(String[] args) {
    String[] dirs = parseArgs(args);
    System.out.println("Tree version " + VERSION);
  }
}

enum Flags {
  HELP, VERSION, VERBOSE, QUIET, DEBUG,
}
