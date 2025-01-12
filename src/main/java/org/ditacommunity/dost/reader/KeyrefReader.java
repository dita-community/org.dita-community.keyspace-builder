package org.ditacommunity.dost.reader;


import static org.dita.dost.util.Constants.*;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;

import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.reader.AbstractReader;
import org.dita.dost.util.Job;
import org.dita.dost.util.XMLUtils;
import static org.dita.dost.util.XMLUtils.rootElement;
import static org.dita.dost.module.filter.MapBranchFilterModule.BRANCH_COPY_TO;
import static org.dita.dost.util.URLUtils.toURI;

import org.ditacommunity.dost.util.KeyDef;
import org.ditacommunity.dost.util.KeyScope;

import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;


public final class KeyrefReader implements AbstractReader {
	

	  private static final List<String> ATTS = List.of(
			    ATTRIBUTE_NAME_HREF,
			    ATTRIBUTE_NAME_AUDIENCE,
			    ATTRIBUTE_NAME_PLATFORM,
			    ATTRIBUTE_NAME_PRODUCT,
			    ATTRIBUTE_NAME_OTHERPROPS,
			    ATTRIBUTE_NAME_REV,
			    ATTRIBUTE_NAME_PROPS,
			    "linking",
			    ATTRIBUTE_NAME_TOC,
			    ATTRIBUTE_NAME_PRINT,
			    "search",
			    ATTRIBUTE_NAME_FORMAT,
			    ATTRIBUTE_NAME_SCOPE,
			    ATTRIBUTE_NAME_TYPE,
			    ATTRIBUTE_NAME_XML_LANG,
			    "dir",
			    "translate",
			    ATTRIBUTE_NAME_PROCESSING_ROLE,
			    ATTRIBUTE_NAME_CASCADE
			  );

	  public static final String ROOT_SCOPE_DEFAULT_NAME = "#ROOT";
	
	  private DITAOTLogger logger;
	  private Job job;
	  private DocumentBuilder builder;
	  private KeyScope rootScope;
	  private URI currentFile;
	  private XMLUtils xmlUtils;
	  // Mapping of scope-defining elements to key scopes.
	  private Map<XdmNode, KeyScope> keyscopesByDefiner = new HashMap<XdmNode, KeyScope>();

	  /**
	   * Constructor.
	   */
	  public KeyrefReader() {}

	  @Override
	  public void read(final File filename) {
	    throw new UnsupportedOperationException();
	  }

	  @Override
	  public void setLogger(final DITAOTLogger logger) {
	    this.logger = logger;
	  }

	  @Override
	  public void setJob(final Job job) {
	    this.job = job;
	  }

	  public void setXmlUtils(XMLUtils xmlUtils) {
	    this.xmlUtils = xmlUtils;
	    builder = xmlUtils.getDocumentBuilder();
	  }

	  /**
	   * Get key definitions for root scope. Each key definition Element has a distinct Document.
	   *
	   * @return root key scope
	   */
	  public KeyScope getKeyDefinition() {
	    return rootScope;
	  }

	  /**
	   * Read key definitions
	   *
	   * @param filename absolute URI to DITA map with key definitions
	   * @param doc      key definition DITA map (document node)
	   */
	  public void read(final URI filename, final XdmNode doc) {
		assert doc.getNodeKind() == XdmNodeKind.DOCUMENT;
		final XdmNode root = doc.select(rootElement()).asNode();
		  
	    currentFile = filename;
	    rootScope = null;
	    final KeyScope keyScope = readScopes(root);
	    final KeyScope keyScopeWithChildren = pullUpChildKeydefs(keyScope);
	    final KeyScope keyScopeWithParents = pushDownParentKeydefs(keyScopeWithChildren);
//	    rootScope = resolveIntermediate(keyScopeWithParents);
	    rootScope = keyScope;
	  }
	  

	/**
	 * Pull keydefs from child scopes in this scope, prepending the child scope's 
	 * key names.
	 * @param keyScope Key scope to child keydefs into.
	 * @return The updated key scope
	 */
	private KeyScope pullUpChildKeydefs(KeyScope keyScope) {
		for (KeyScope child : keyScope.getChildScopes()) {
			KeyScope childPulled = pullUpChildKeydefs(child);
			Set<String> scopeNames = childPulled.getScopeNames();
			Map<String, KeyDef> keydefs = childPulled.getKeyDefinitions();
			for (String keyName : keydefs.keySet()) {
				KeyDef keydef = keydefs.get(keyName);
				for (String scopeName : scopeNames) {
					KeyDef newKeyDef = new KeyDef(scopeName, keydef);
					keyScope.appendKeyDef(newKeyDef);
				}
			}
		}

		return keyScope;
	}

	/**
	 * Push keydefs from parents to children, prepending any key-definers to existing keys
	 * with the same name.
	 * @param keyScope Scope whose children will get keydefs pushed to it.
	 * @return Updated key scope.
	 */
	private KeyScope pushDownParentKeydefs(KeyScope keyScope) {
		Map<String, KeyDef> keydefs = keyScope.getKeyDefinitions();
		for (KeyScope child : keyScope.getChildScopes()) {
			for (String keyName : keydefs.keySet()) {
				KeyDef keydef = keydefs.get(keyName);
				child.prependKeyDef(keydef);
			}
			pushDownParentKeydefs(child);
			
		}
		return keyScope;
	}

	/**
	 * Construct the initial key scopes from the input root map.
	 * @param root Root DITA map element to construct the scopes from.
	 * @return Root key scope.
	 */
	private KeyScope readScopes(final XdmNode root) {
		KeyScope rootScope = new KeyScope(root);
		keyscopesByDefiner.put(root, rootScope);
		
		// Now process the key scopes recursively.
		// The rule about key precedence reflecting the breadth-first traversal
		// of the map tree adds some complexity here because there is no necessary
		// alignment between the map boundaries and scope boundaries: A map may be entirely
		// in a parent scope and not define its own scope (although it may contain scopes within it)
		// or it may define a new scope. A scope cannot start in one map and end in another map.
		// So for the purposes of determining key definition precedence, only the map tree within
		// a scope that defines keys directly-defined in that scope (and not in child scopes) is
		// relevant. Sibling scopes do not interfere with each other except to degree that two
		// sibling scopes with the same key scope name define the same key, in which case, the
		// definition in the first scope in document order will take precedence over the key
		// definition in second scope in the context of the parent scope into which those keys
		// are pulled up. But because the scopes are siblings, they necessarily also reflect
		// a breadth-first ordering. Grandchild scopes are not relevant here because they will
		// reflect a further qualification.
		//
		// Thus, breadth-first traversal of the map tree is only relevant for determining the
		// set of directly-defined keys within a given scope. 
		//
		// This code currently ignores that complexity.
		//
		// TODO: Implement breadth-first traversal of the map tree when determining the set of
		//       directly-defined keys in a key scope. This implies there are essentially
		//       two tree walks: one to find scope-definers and one to process the key definitions
		//       within a scope. I think the solution is:
		//
		//       * For a scope, construct the map tree, excluding any child scopes.
		//       * Walk the map tree breadth first, collecting key definitions from each map,
		//         ignoring any child maps (in the context of the resolved map with inline
		//         submaps).
		
		this.readKeyScope(rootScope);
		return rootScope;
	}

	// NOTE: These two versions of readKeyScope() allow the handleElement() method
	//       to handle elements other than the root map element.
	/**
	 * Process a key scope to add key definitions to it (possibly including
	 * the scope-defining element).
	 * @param scope The key scope to add keydefs to.
	 */
	private void readKeyScope(KeyScope scope) {
		XdmNode elem = scope.getScopeDefiner();
		// When a topicref is both a key scope definer and a key definition,
		// handleElement() will have sent it here, so we have to handle
		// any key definition here before processing our children.
		if (MAP_TOPICREF.matches(elem.attribute(ATTRIBUTE_NAME_CLASS)) &&
				elem.attribute(ATTRIBUTE_NAME_KEYS) != null) 
		{
			// Add keydef:
			readKeyDefinition(scope, elem);
		}
		processChildren(scope, elem);
	}

	/**
	 * Process the child nodes of an element.
	 * @param scope Key scope being constructed.
	 * @param elem Element to process the children of.
	 */
	private void processChildren(KeyScope scope, XdmNode elem) {
		for (XdmNode child : elem.children()) {
			handleElement(scope, child);
		}
	}

	/**
	 * Construct a new key scope and add key definitions to it.
	 * @param elem Scope-defining element.
	 */
	private void readKeyScope(KeyScope parentScope, XdmNode elem) {
		KeyScope scope = new KeyScope(elem);
		keyscopesByDefiner.put(elem, scope);
		readKeyScope(scope);		
		parentScope.addChildScope(scope);
	}

	/**
	 * Dispatch the appropriate processing based on the element type and attributes.
	 * @param scope The KeyScope being constructed.
	 * @param elem Element to be handled.
	 */
	private void handleElement(KeyScope scope, XdmNode elem) {
		String classValue = elem.attribute(ATTRIBUTE_NAME_CLASS);
		if (elem.attribute(ATTRIBUTE_NAME_KEYSCOPE) != null) {
			// This will also handle adding a key definition for the scope-definer
			// if it is also a key-defining element.
			readKeyScope(scope, elem);
		} else if (MAP_TOPICREF.matches(classValue) && elem.attribute(ATTRIBUTE_NAME_KEYS) != null) {
			readKeyDefinition(scope, elem);
			processChildren(scope, elem);
		} else if (SUBMAP.matches(elem) || MAP_TOPICREF.matches(classValue)) {
			processChildren(scope, elem);
		} else {
			// Ignore other elements
			// FIXME: Do we need to handle relationship tables?
		}
		
	}

	/**
	 * Construct a key definition and add it to the current scope.
	 * @param scope The scope the key definition is added to.
	 * @param elem The key-defining element.
	 */
	private void readKeyDefinition(KeyScope scope, XdmNode elem) {
	    final String keysValue = elem.attribute(ATTRIBUTE_NAME_KEYS);
	    if (keysValue != null) {
	      for (final String key : keysValue.trim().split("\\s+")) {
	    	KeyDef keyDef = scope.getKeyDefinition(key);
	        if (keyDef == null) {
	        	keyDef = new KeyDef(key);
				scope.appendKeyDef(keyDef);
	        }
			keyDef.appendKeyDefiner(elem);
	      }
	    }
		
		
	}



}
