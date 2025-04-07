import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
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

  public static void main(String[] args) {
    CommandLineParser parser = new DefaultParser();
    Options options = new Options();
    options.addOption("h", "help", false, "Print this help");
    options.addOption("v", "version", false, "Print version");
    options.addOption(Option.builder("L").longOpt("level").hasArg().desc("Set the level").type(Integer.class).build());
    CommandLine cmd;
    int level;
    try {
      cmd = parser.parse(options, args);
      level = cmd.hasOption("level") ? cmd.getParsedOptionValue("level") : 0;
    } catch (ParseException e) {
      System.err.println("Unexpected error: " + e.getMessage());
      return;
    }
    if (cmd.hasOption("help")) {
      new HelpFormatter().printHelp("tree", options, true);
      return;
    }
    if (cmd.hasOption("version")) {
      System.out.println(Tree.VERSION);
      return;
    }

    List<String> dirs = cmd.getArgList().isEmpty() ? List.of(".") : cmd.getArgList();
    for (String dir : dirs)
      Tree.tree(dir, level);
  }

  /**
   * Recursively print the tree of a directory.
   */
  public static void tree(String dir, int level) {
    Counter c = new Counter();
    tree(Path.of(dir), 0, level, true, "", c);
    System.out.println();
    System.out.println(c.dirs + " directories, " + c.files + " files");
  }

  private static List<Path> list(Path dir) {
    try (Stream<Path> s = Files.list(dir)) {
      return s.sorted((p1, p2) -> p1.getFileName().toString().compareTo(p2.getFileName().toString())).toList();
    } catch (IOException e) {
      return List.of();
    }
  }

  private static void tree(Path f, int depth, int maxLevel, boolean isLastChild, String prefix, Counter c) {
    // Print the fancy graphical tree (after first file)
    if (depth > 0) {
      System.out.print(prefix);
      if (isLastChild) System.out.print("└");
      else System.out.print("├");
      System.out.print("── ");
    }
    // Print the given name if depth is 0, else the file name only
    if (depth == 0) System.out.println(f);
    else System.out.println(f.getFileName());

    // Count dirs and files for final output
    if (f.toFile().isDirectory()) c.dirs++;
    else c.files++;

    // If we're at the max level, we're done
    if (maxLevel != 0 && depth >= maxLevel) return;
    // we're done, this is a file
    if (!f.toFile().isDirectory()) return;

    List<Path> dirs = list(f);
    for (int i = 0; i < dirs.size(); i++) {
      Path d = dirs.get(i);
      String nbsp = "\u00a0"; // nbsp (match tree output)
      String newPrefix = depth > 0 ? prefix.concat(isLastChild ? "    " : "│" + nbsp + nbsp + " ") : "";
      tree(d, depth + 1, maxLevel, i == dirs.size() - 1, newPrefix, c);
    }
  }

}

class Counter {
  int dirs, files;
}
