import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Tree {

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

  public static void main(String[] args) throws ParseException {
    CommandLineParser parser = new DefaultParser();
    Options options = new Options();
    options.addOption("h", "help", false, "Print this help");
    options.addOption("v", "version", false, "Print version");
    options.addOption(Option.builder("L").longOpt("level").hasArg().desc("Set the level").type(Integer.class).build());
    CommandLine cmd = parser.parse(options, args);
    if (cmd.hasOption("help")) {
      new HelpFormatter().printHelp("tree", options, true);
      return;
    }
    if (cmd.hasOption("version")) {
      System.out.println(Tree.VERSION);
      return;
    }

    int level = cmd.hasOption("level") ? cmd.getParsedOptionValue("level") : 0;
    List<String> dirs = cmd.getArgList().isEmpty() ? List.of(".") : cmd.getArgList();
    for (String dir : dirs) {
      Tree.tree(dir, level);
    }
  }

  /**
   * Recursively print the tree of a directory.
   */
  public static void tree(String dir, int level) {
    System.out.println(dir);
  }
}
