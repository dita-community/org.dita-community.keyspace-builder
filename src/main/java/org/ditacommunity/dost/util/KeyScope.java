package org.ditacommunity.dost.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.saxon.s9api.XdmNode;

/**
 * Represents a constructed DITA key scope. A key scope consists of
 * zero or more key definitions. A key scope has one more key scope names.
 * The root keyscope defined by a DITA root map has the default name "#root" and
 * may have other scope names as well. A key scope is identified by its scope-defining
 * elements (a map element, a topicref element, or a mapref element). When a key scope is
 * defined by a mapref it has two scope-defining elements: the map reference and the referenced
 * map and may be found using either element as the lookup key. 
 * 
 * A key scope may have child key scopes. Note that key scope names need not be
 * unique among a set of sibling key scopes.
 * 
 * When accessing key scopes, they are listed in priority order (highest and earliest).
 * 
 * Key definition objects maintain the set of key-defining elements that specify the same
 * key name in the scope, listed in priority order. This allows the effective key definition
 * (and thus associated resource) to be determined dynamically at key resolution time, i.e.,
 * by applying DITAVAL filtering to the look up.
 * 
 * Key scope construction consists of three phases:
 * 
 * 1. The base key scopes are constructed from the scope- and key-defining elements in the
 *    input map.
 * 2. "Pull up": Keys from child scopes are "pulled up" into parent scopes, adding scope 
 *    qualifiers to each pulled-up key. These pulled-up keys have lower priority than than 
 *    same qualified key names defined in the parent scopes (meaning parent scopes can override
 *    scope-qualified key names pulled up from descendant scopes.
 * 3. "Push down": Keys from parent scopes are "pushed down" into child scopes. Pushed down keys
 *    have higher priority than keys in the child scope, meaning keys from parent scopes override
 *    keys with the same name (including any qualified keys) in child scopes.
 *    
 * After these three phases are performed, each scope has a copy of all qualified key names from
 * all descendant scopes and reflects any explicit overriding of keys from parent scopes.
 * 
 * Key resolution can be context-free, meaning the key is resolved without regard to map context,
 * normally using the root key scope. Note that in this case, unqualified key references that would
 * resolve in a child scope will not resolve in a parent scope if that scope does not override
 * the unqualified key name.
 * 
 * In most cases, key resolution includes the map context within which the key is resolved, which
 * determines the key scope the key is resolved in. In this case, unqualified (or incompletely qualified)
 * key references to keys defined in the scope will resolve in that scope unless those keys have
 * been overridden in an ancestor scope.
 */
public class KeyScope {


	private List<XdmNode> keyDefiners = new ArrayList<XdmNode>();
	private Map<String, KeyDef> keyDefinitions = new HashMap<String, KeyDef>();  

	public KeyScope(XdmNode keyDefiner, Map<XdmNode, KeyScope> keyScopesByDefiner) {
		// A key scope may have multiple key definers, but it must have at least one.
		this.keyDefiners .add(keyDefiner);
		keyScopesByDefiner.put(keyDefiner, this);		
	}

	/**
	 * Get the map of key definitions for the key space.
	 * @return
	 */
	public Map<String, KeyDef> getKeyDefinitions() {
		final HashMap<String, KeyDef> newMap = new HashMap<String, KeyDef>(this.keyDefinitions);
		return newMap;
	}

}
