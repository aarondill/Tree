import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Properties;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TreeTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream(),
      errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out, originalErr = System.err;

  @BeforeEach
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @AfterEach
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  void version() throws IOException {
    Reader gradelProperties = new InputStreamReader(getClass().getResourceAsStream("/version.properties"));
    Properties properties = new Properties();
    properties.load(gradelProperties);
    String version = properties.getProperty("version");
    assertTrue(version.equals(Tree.VERSION));
  }

  /** Return the output of the *real* tree command. */
  private static String tree(String[] args) {
    String[] treeArgs = new String[args.length + 1];
    treeArgs[0] = "tree";
    System.arraycopy(args, 0, treeArgs, 1, args.length);
    Runtime rt = Runtime.getRuntime();
    try {
      Process proc = rt.exec(treeArgs);
      return new String(proc.getInputStream().readAllBytes());
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Create structure:
   *
   * <pre>
   *   tmp
   *   ├── a
   *   │   ├── b
   *   │   │   ├── c
   *   │   │   └── d
   *   │   └── e
   *   └── f
   * </pre>
   *
   * Then run tree with -L 2.
   */
  @Test
  void matches() {
    try (var dir = Tmp.dir(null)) {
      Path d = dir.getPath();
      String[] args = {"-L", "2", d.toAbsolutePath().toString()};
      Stream.of("a/b/c", "a/b/d", "a/e", "f").map(d::resolve).map(Path::toFile).forEach(File::mkdirs);
      String expected = tree(args).stripTrailing();
      Tree.main(args);
      String output = outContent.toString().stripTrailing();
      System.setOut(originalOut);

      System.out.println("EXPECTED:");
      System.out.println(expected);
      System.out.println("GOT:");
      System.out.println(output);

      String[] eLines = expected.split("\n"), oLines = output.split("\n");
      for (int i = 0; i < Math.max(eLines.length, oLines.length); i++) {
        if (i >= eLines.length) {
          System.out.println(oLines[i] + "\t<--EXTRA!");
          continue;
        } else if (i >= oLines.length) {
          System.out.println(eLines[i] + "\t<--MISSING!");
          continue;
        }
        String eLine = eLines[i].stripTrailing(), oLine = oLines[i].stripTrailing();
        System.out.println(eLine);
        if (!eLine.equals(oLine)) {
          System.out.println(oLine + "\t<--DIFF!");
        }
      }

      assertTrue(output.equals(expected));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

class Tmp implements AutoCloseable {
  private final Path file;

  private Tmp(Path file) {
    this.file = file;
  }

  public static Tmp dir(String prefix) throws IOException {
    return new Tmp(Files.createTempDirectory(prefix));
  }

  public static Tmp file(String prefix) throws IOException {
    return new Tmp(Files.createTempFile(prefix, null));
  }

  public Path getPath() {
    return file;
  }

  @Override
  public void close() throws IOException {
    try (var dirStream = Files.walk(file)) {
      dirStream.map(Path::toFile).sorted(Comparator.reverseOrder()).forEach(File::delete);
    }
  }
}
