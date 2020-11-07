package org.asciidoctor;

import org.apache.commons.io.IOUtils;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.asciidoctor.log.LogRecord;
import org.asciidoctor.log.Severity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ClasspathIncludeProcessor extends IncludeProcessor {

    public static final String CLASSPATH_PREFIX = "classpath:";

    @Override
    public boolean handles(String target) {
        return isClasspathTarget(target);
    }

    @Override
    public void process(Document document,
                        PreprocessorReader reader,
                        String target,
                        Map<String, Object> attributes) {
        try {
            log(new LogRecord(
                Severity.DEBUG,
                "reader.dir= " + reader.getDir() + ", reader.file= " + reader.getFile()
            ));

            final String sanitizedTarget = cleanPrefix(target);
            final URL resourceURL = isClasspathTarget(reader.getDir())
                ? findResource(cleanPrefix(reader.getDir()) + "/" + sanitizedTarget)
                : findResource(sanitizedTarget);

            if (resourceURL != null) {
                try (InputStream resourceAsStream = resourceURL.openStream()) {
                    // Alternatively, pass `resourceURL.toString()` to both file and path to get full path of parent source in `reader.getDir()` and  `reader.getFile()`
                    reader.push_include(
                        IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8),
                        target,
                        resourceURL.toString(),
                        1,
                        attributes);
                }
            }
        } catch (IOException e) {
            log(new LogRecord(Severity.ERROR, "Could not include target: " + target));
        }
    }

    private String cleanPrefix(String target) {
        return target.substring(CLASSPATH_PREFIX.length());
    }

    private URL findResource(String target) {
        return this.getClass().getClassLoader().getResource(target);
    }

    private boolean isClasspathTarget(String dir) {
        return dir.startsWith(CLASSPATH_PREFIX);
    }

}
