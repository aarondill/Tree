import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class TreeTest {
  @Test
  void version() throws IOException {
    Reader gradelProperties = new InputStreamReader(getClass().getResourceAsStream("/version.properties"));
    Properties properties = new Properties();
    properties.load(gradelProperties);
    String version = properties.getProperty("version");
    assertTrue(version.equals(Tree.VERSION));
  }
}
