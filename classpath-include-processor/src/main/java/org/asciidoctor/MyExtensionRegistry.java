package org.asciidoctor;

public class MyExtensionRegistry implements org.asciidoctor.jruby.extension.spi.ExtensionRegistry {

    @Override
    public void register(Asciidoctor asciidoctor) {
        asciidoctor
            .javaExtensionRegistry()
            .includeProcessor(ClasspathIncludeProcessor.class);
    }

}
