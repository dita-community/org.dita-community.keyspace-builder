package org.ditacommunity.dost.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ditacommunity.dost.reader.KeyrefReader;

import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;

import static org.dita.dost.util.Constants.*;

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
	private Set<String> scopeNames = new HashSet<String>();
	private Map<String, List<KeyScope>> scopesByName = new HashMap<>();
	private List<KeyScope> childScopes = new ArrayList<KeyScope>();
	// NOTE: The root scope does not have a parent.
	private KeyScope parentScope = null;

	/**
	 * Construct a key scope ready to have key definitions added to it.
	 * @param scopeDefiner The scope-defining element: A map, topicref, or mapref (for peer scopes).
	 */
	public KeyScope(XdmNode scopeDefiner) {
		// A key scope may have multiple key definers, but it must have at least one.
		this.keyDefiners .add(scopeDefiner);	
		Set<String> scopeNames = new HashSet<String>();
		String scopeValue = scopeDefiner.attribute(ATTRIBUTE_NAME_KEYSCOPE);
		
		// If the scope is defined by the root map element, set the default name:
		XdmNode parent = scopeDefiner.getParent();
		if (scopeDefiner.getParent().getNodeKind() == XdmNodeKind.DOCUMENT) {
			scopeNames.add(KeyrefReader.ROOT_SCOPE_DEFAULT_NAME);
		}
		scopeNames.addAll(Arrays.asList(scopeValue.split("\\s+")));
		for (final String scope : scopeNames) {
			this.addScopeName(scope);
			List<KeyScope> scopes = this.scopesByName.get(scope);
			if (scopes == null) {
				scopes = new ArrayList<KeyScope>();
				this.scopesByName.put(scope, scopes);
			}			
			scopes.add(this);
		}

	}

	/**
	 * Get the parent scope, if any. The root scope does not have a parent.
	 * @return Parent scope, or null if this is the root scope.
	 */
	public KeyScope getParent() {
		return this.parentScope;
	}

	/**
	 * Add a scope name to the scope. A scope may have any number
	 * of names. The names of a scope do not need to be unique across
	 * the scopes defined by a root map.
	 * @param name The scope name.
	 */
	public void addScopeName(String name) {
		
		this.scopeNames.add(name);		
	}

	/**
	 * Get the map of key definitions for the key space.
	 * @return
	 */
	public Map<String, KeyDef> getKeyDefinitions() {
		final HashMap<String, KeyDef> newMap = new HashMap<String, KeyDef>(this.keyDefinitions);
		return newMap;
	}

	/**
	 * Gets the first (or only) scope-defining element for this key scope.
	 * @return Scope-defining element.
	 */
	public XdmNode getScopeDefiner() {
		return this.keyDefiners.get(0);
	}

	/**
	 * Get the key definition with the specified key, if any
	 * @param key The key of the key definition to get
	 * @return The key definition for the key, or null.
	 */
	public KeyDef getKeyDefinition(String key) {
		return this.keyDefinitions.get(key);
	}

	/**
	 * Add a key definition to the scope. If there is already
	 * a key definition then any key definers in the keydef are
	 * appended to the key definers.
	 * @param keyDef
	 */
	public void appendKeyDef(KeyDef keyDef) {
		String keyName = keyDef.getKeyName();
		if (this.keyDefinitions.containsKey(keyName)) {
			KeyDef existing = this.getKeyDefinition(keyName);
			existing.appendKeyDefiners(keyDef.getKeyDefiners());
		} else {
			this.keyDefinitions.put(keyName, keyDef);
		}
	}

	/**
	 * Get the child (or self) scopes with the specified scope name
	 * @param scopeName The scope name to look for.
	 * @return List, possibly empty, of scopes with the specified name.
	 */
	public List<KeyScope> getScopesByName(String scopeName) {
		List<KeyScope> scopes = this.scopesByName.get(scopeName);
		List<KeyScope> result = null;
		if (scopes != null) {
			result = Collections.unmodifiableList(scopes);
		}
		return result;
	}

	/**
	 * Gets the set of scope names for the scope. A scope must have at least one scope name.
	 * @return Set of at least one scope name.
	 */
	public Set<String> getScopeNames() {
		Set<String> result = new HashSet<String>(this.scopeNames);
		return result;
	}

	/**
	 * Add a key scope as a child scope
	 * @param scope Child scope to add
	 */
	public void addChildScope(KeyScope scope) {
		this.childScopes.add(scope);
		scope.setParent(this);
		for (String scopeName : scope.getScopeNames()) {
			List<KeyScope> scopes = this.scopesByName.get(scopeName);
			if (scopes == null) {				
				scopes = new ArrayList<KeyScope>();
				this.scopesByName.put(scopeName, scopes);
			}
			scopes.add(scope);
		}
		
	}

	private void setParent(KeyScope keyScope) {
		this.parentScope = keyScope;		
	}
	
	@Override
	public String toString() {
		int hash = this.getScopeDefiner().hashCode();
		StringBuffer buf = new StringBuffer("KeyScope ");
		buf.append("(Definer <")
		.append(this.getScopeDefiner().getNodeName())
		.append(">")
		.append(" @").append(hash).append(") ");
		for (String scopeName : this.getScopeNames()) {
			buf.append("[").append(scopeName).append("]");
		}
		return buf.toString();
	}

	/**
	 * Get list of child scopes, if any.
	 * @return List, possibly empty, of child scopes in priority (document) order.
	 */
	public List<KeyScope> getChildScopes() {
		List<KeyScope> result = new ArrayList<KeyScope>(this.childScopes);
 		return result;
	}

}
