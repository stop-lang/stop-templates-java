package org.stop_lang.templates;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.stop_lang.stop.Stop;
import org.stop_lang.stop.models.Property;
import org.stop_lang.stop.models.State;
import org.stop_lang.templates.models.StateTemplateNode;
import org.stop_lang.templates.models.StateTemplateNodeParameterValue;
import org.stop_lang.templates.models.TemplateNode;
import org.stop_lang.templates.parser.StopTemplatesLexer;
import org.stop_lang.templates.parser.StopTemplatesParser;
import org.stop_lang.templates.validation.DefPhase;
import org.stop_lang.templates.validation.RefPhase;
import org.stop_lang.templates.validation.StopTemplateValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Template {
    private State templateState;
    private TemplateNode root;

    public Template(Stop stop, State templateState, String filename) throws IOException, StopTemplateValidationException {
        CharStream input = CharStreams.fromFileName(filename);
        validate(stop, templateState, input);
    }

    public Template(Stop stop, State templateState, CharStream input) throws StopTemplateValidationException {
        validate(stop, templateState, input);
    }

    public State getTemplateState(){
        return templateState;
    }

    public TemplateNode getRoot(){
        return root;
    }

    private void validate(Stop stop, State templateState, CharStream input) throws StopTemplateValidationException {
        this.templateState = templateState;

        StopTemplatesLexer l = new StopTemplatesLexer(input);
        TokenStream tokens = new CommonTokenStream(l);
        StopTemplatesParser parser = new StopTemplatesParser(tokens);

        ParserRuleContext tree = parser.file();

        if (tree.exception != null){
            throw new StopTemplateValidationException(tree.exception.getMessage());
        }

        ParseTreeWalker walker = new ParseTreeWalker();

        DefPhase def = new DefPhase();
        walker.walk(def, tree);

        handleErrors(def.errors);

        StateTemplateNode root = new StateTemplateNode(templateState);
        for(Map.Entry<String, Property> propertyEntry : templateState.getProperties().entrySet()){
            root.addParameter(propertyEntry.getKey(), new StateTemplateNodeParameterValue(StateTemplateNodeParameterValue.Type.ID, propertyEntry.getKey()));
        }

        RefPhase ref = new RefPhase(stop, templateState, root, def.globals, def.scopes);
        walker.walk(ref, tree);

        handleErrors(ref.errors);

        this.root = root;
    }

    private void handleErrors(List<Exception> exceptions) throws StopTemplateValidationException {
        if (!exceptions.isEmpty()) {
            List<String> exceptionMessages = new ArrayList<>();
            for (Exception exception : exceptions){
                exceptionMessages.add(exception.getMessage());
            }
            throw new StopTemplateValidationException(String.join(", ", exceptionMessages));
        }
    }
}
