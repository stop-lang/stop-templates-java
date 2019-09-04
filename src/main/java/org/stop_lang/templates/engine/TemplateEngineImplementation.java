package org.stop_lang.templates.engine;

import org.stop_lang.stop.models.StateInstance;

import java.util.Collection;

public interface TemplateEngineImplementation<T> {
    T executeTemplate(StateInstance stateInstance, Collection<T> children) throws TemplateEngineException;
    T renderTemplateChildren(Collection<T> children);
}
