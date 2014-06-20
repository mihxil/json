package org.meeuw.json.grep.matching;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.util.Predicate;
import org.meeuw.util.Predicates;

/**
 * @author Michiel Meeuwissen
 * @since 0.6
 */
public class JavascriptMatcher extends ObjectMatcher {

    final String script;

    final ScriptEngine engine;

    public JavascriptMatcher(String script) {
        this.script = script;
        ScriptEngineManager mgr = new ScriptEngineManager();
        engine = mgr.getEngineByName("JavaScript");
    }

    @Override
    protected boolean matches(ParseEvent event) {
        ScriptContext context = engine.getContext();
        //ScriptableObject scope = context.initStandardObjects();

        context.setAttribute("_", event.getNode(), ScriptContext.ENGINE_SCOPE);
        try {
            return (Boolean) engine.eval(script, context);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Predicate<Path> needsKeyCollection() {
        return Predicates.alwaysFalse();
    }
}
