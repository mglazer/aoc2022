import java.nio.charset.StandardCharsets
import java.nio.file.Files

class Utils {
    companion object {
        fun read(file: String): String {
            return ClassLoader.getSystemClassLoader().getResourceAsStream(file).readAllBytes()
                .toString(StandardCharsets.UTF_8);
        }

        fun readLines(file: String): List<String> {
            return read(file).split("\n");
        }
    }
}