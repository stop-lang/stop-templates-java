package org.stop_lang.templates.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.stop_lang.stop.models.State;
import org.stop_lang.stop.models.StateInstance;
import org.stop_lang.stop.Stop;
import org.stop_lang.templates.Template;
import org.stop_lang.templates.engine.*;

import java.util.*;

public class TemplateEngineTest {

    @Test
    public void test() throws Exception{
        Stop stop = new Stop("./examples/templates.stop");
        State templateStop = stop.getStates().get("Posts");
        Template template = new Template(stop, templateStop, "./examples/posts.st");

        Assertions.assertNotNull(template);

        Map<String, Object> props = new HashMap<>();
        props.put("fuck", "test");
        props.put("boolTest", true);

        Collection a = new ArrayList();
        Map<String, Object> post = new HashMap<>();
        post.put("title", "test post");
        post.put("body", "test body");
        post.put("description", "wow");
        List tags = new ArrayList();
        tags.add("fuck");
        tags.add("the");
        tags.add("noise");
        post.put("tags", tags);
        Map<String, Object> userProps = new HashMap<>();
        userProps.put("name", "Kyle");
        List<String> aliases = new ArrayList<>();
        aliases.add("fuckdong");
//        userProps.put("aliases", aliases);
        State userState = stop.getStates().get("User");
        StateInstance user = new StateInstance(userState, userProps);
        post.put("user", user);
        props.put("realUser", user);
        post.put("audio_url", "test.mp3");
        State postState = stop.getStates().get("Podcast");
        StateInstance postInstance = new StateInstance(postState, post);
        a.add(postInstance);
        props.put("posts", a);

        TemplateEngineImplementation<String> i = new TemplateEngineImplementation<String>() {
            @Override
            public String executeTemplate(StateInstance stateInstance, Collection<String> children) throws TemplateEngineException {
                StringBuilder sb = new StringBuilder();
                sb.append("<" + stateInstance.getState().getName()+" " + stateInstance.getProperties().toString()+">\n");
                for(String child : children){
                    sb.append(child + "\n");
                }
                sb.append("</" + stateInstance.getState().getName()+">\n");
                return sb.toString();
            }

            @Override
            public String renderTemplateChildren(Collection<String> children) {
                StringBuilder sb = new StringBuilder();
                for (String childString : children) {
                    sb.append(childString);
                }

                return sb.toString();
            }
        };

        StateInstance templateStateInstance = new StateInstance(templateStop, props);
        TemplateEngine<String> templateEngine = new TemplateEngine<String>(i, template);
        String output = templateEngine.render(templateStateInstance);

        System.out.println(output);
    }
}
