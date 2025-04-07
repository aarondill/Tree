import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public class Tree {

  private static String[] parseArgs(String[] args) {
    if (args.length == 0) return new String[] {"."};
    // TODO: real argument parsing
    return args;
  }

  final static public String VERSION = version();

  private static String version() {
    Reader r = (new InputStreamReader(Tree.class.getResourceAsStream("/version.properties")));
    Properties p = new Properties();
    try {
      p.load(r);
    } catch (IOException e) {
      System.err.println("Error reading version.properties");
      e.printStackTrace();
    }
    p.list(System.out);
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
