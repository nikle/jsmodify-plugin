package com.crawljax.plugins.jsmodify.executionTracer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.Symbol;

import com.crawljax.plugins.jsmodify.JSASTModifier;
import com.crawljax.util.Helper;

/**
 * This class is used to visit all JS nodes. When a node matches a certain condition, this class
 * will add instrumentation code near this code.
 * 
 */
public abstract class AstInstrumenter extends JSASTModifier {


	/**
	 * List with regular expressions that should not be instrumented.
	 */
	private List<String> excludeList = new ArrayList<String>();
	String jsFileNameToAttach;

	private boolean domModifications = false;

	/**
	 * Construct without patterns.
	 */
	public AstInstrumenter(String jsFileNameToAttach) {
		super();
		this.jsFileNameToAttach=jsFileNameToAttach;
		excludeList = new ArrayList<String>();
	}

	/**
	 * Constructor with patterns.
	 * 
	 * @param excludes
	 *            List with variable patterns to exclude.
	 */
	public AstInstrumenter(List<String> excludes) {
		excludeList = excludes;
	}

	/**
	 * Return an AST of the variable logging functions.
	 * 
	 * @return The AstNode which contains functions.
	 */
	private AstNode jsLoggingFunctions() {
		String code;

		File js = new File(this.getClass().getResource(jsFileNameToAttach).getFile());
		code = Helper.getContent(js);
		return parse(code);
	}

	@Override
	public abstract AstNode createNodeInFunction(FunctionNode function, int lineNo);
	
	@Override
	public abstract AstNode createNode(AstNode...node);

	/**
	 * Check if we should instrument this variable by matching it against the exclude variable
	 * regexps.
	 * 
	 * @param name
	 *            Name of the variable.
	 * @return True if we should add instrumentation code.
	 */
	protected boolean shouldInstrument(String name) {
		if (name == null) {
			return false;
		}

		/* is this an excluded variable? */
		for (String regex : excludeList) {
			if (name.matches(regex)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns all variables in scope.
	 * 
	 * @param func
	 *            The function.
	 * @return All variables in scope.
	 */
	protected String[] getVariablesNamesInScope(Scope scope) {
		TreeSet<String> result = new TreeSet<String>();
        


		do {
			/* get the symboltable for the current scope */
			Map<String, Symbol> t = scope.getSymbolTable();
		
			if (t != null) {
				for (String key : t.keySet()) {
					/* read the symbol */
					Symbol symbol = t.get(key);
					/* only add variables and function parameters */
					if (symbol.getDeclType() == Token.LP || symbol.getDeclType() == Token.VAR) {
						result.add(symbol.getName());
							
					}
				}
			}

			/* get next scope (upwards) */
			scope = scope.getEnclosingScope();
		} while (scope != null);

		/* return the result as a String array */
		return result.toArray(new String[0]);
	}

	@Override
	public void finish(AstRoot node) {
		/* add initialization code for the function and logging array */
		node.addChildToFront(jsLoggingFunctions());
	}

	@Override
	public abstract void start();

	@Override
	public abstract AstNode createPointNode(String objectAndFunction, int lineNo);

	protected boolean shouldInstrumentDOMModifications() {
		return domModifications;
	}

	
	public void instrumentDOMModifications() {
		domModifications = true;
	}

	
}
