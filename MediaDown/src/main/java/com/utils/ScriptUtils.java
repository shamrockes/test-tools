package com.utils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptUtils {

	public static String JavaScriptOperation(String str) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("javascript");
		String result = null;
		try {
			engine.eval("function FonHen_JieMa(u){" +
					"    var tArr = u.split(\"*\");" +
					"    var str = '';" +
					"    for(var i=1,n=tArr.length;i<n;i++){" +
					"        str += String.fromCharCode(tArr[i]);" +
					"    }" +
					"    return str;" +
					"}");
			if (engine instanceof Invocable) {
				Invocable in = (Invocable) engine;
				result = (String) in.invokeFunction("FonHen_JieMa", str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
