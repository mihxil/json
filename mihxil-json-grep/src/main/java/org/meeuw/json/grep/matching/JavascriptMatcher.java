package org.meeuw.json.grep.matching;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.util.Predicate;
import org.meeuw.util.Predicates;
import org.mozilla.javascript.*;

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
        //ScriptContext context = engine.getContext();
        Context context = Context.enter();
        ScriptableObject scope = context.initStandardObjects();
        Scriptable that = context.newObject(scope);
        Function fct = context.compileFunction(scope, script, "script", 1, null);

        NativeObject nobj = new NativeObject();
        if (event.getNode() != null) {
            for (Map.Entry<String, Object> entry : event.getNode().entrySet()) {
                nobj.defineProperty(entry.getKey(), entry.getValue(), NativeObject.READONLY);
            }
        }

        Object result = fct.call(
                context, scope, that, new Object[]{nobj});
        if (! (result instanceof Boolean)) {
            throw new IllegalArgumentException(
                    "" + NativeJSON.stringify(context, scope, result, null, null) +
                            " is not a boolean. Called on " + NativeJSON.stringify(context, scope, nobj, null, null));
        } else {
            return (Boolean) result;
        }
    }

    @Override
    public Predicate<Path> needsKeyCollection() {
        return Predicates.alwaysFalse();
    }

    @Override
    public Predicate<Path> needsObjectCollection() {
        return Predicates.alwaysTrue();
    }

    @Override
    public String toString() {
        return " " + script;
    }
}
