package org.stop_lang.templates.test;

import org.junit.jupiter.api.Test;
import org.stop_lang.stop.Stop;
import org.stop_lang.stop.models.State;
import org.stop_lang.templates.Template;

public class ParserTest {
    @Test
    void parserTest() throws Exception {
        Stop stop = new Stop("./examples/templates.stop");
        State templateStop = stop.getStates().get("Posts");
        Template template = new Template(stop, templateStop, "./examples/posts.st");
    }
}