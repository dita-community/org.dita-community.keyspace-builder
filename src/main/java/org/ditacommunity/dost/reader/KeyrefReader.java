package org.ditacommunity.dost.reader;


import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_AUDIENCE;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_CASCADE;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_FORMAT;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_HREF;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_OTHERPROPS;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_PLATFORM;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_PRINT;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_PROCESSING_ROLE;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_PRODUCT;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_PROPS;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_REV;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_SCOPE;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_TOC;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_TYPE;
import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_XML_LANG;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;

import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.reader.AbstractReader;
import org.dita.dost.util.Job;
import org.dita.dost.util.XMLUtils;
import org.ditacommunity.dost.util.KeyScope;

import net.sf.saxon.s9api.XdmNode;


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
	   * @param doc      key definition DITA map
	   */
	  public void read(final URI filename, final XdmNode doc) {
	    currentFile = filename;
	    rootScope = null;
	    final KeyScope keyScope = readScopes(doc, keyscopesByDefiner);
//	    final KeyScope keyScopeWithChildren = cascadeChildKeys(keyScope);
//	    // TODO: determine effective key definitions here
//	    final KeyScope keyScopeWithParents = inheritParentKeys(keyScopeWithChildren);
//	    rootScope = resolveIntermediate(keyScopeWithParents);
	    rootScope = keyScope;
	  }

	/**
	 * Construct the initial key scopes from the input root map.
	 * @param doc Root DITA map to construct the scopes from.
	 * @return Root key scope.
	 */
	private KeyScope readScopes(XdmNode doc, Map<XdmNode, KeyScope> keyScopesByDefiner) {
		KeyScope rootScope = new KeyScope(doc, keyScopesByDefiner);
		return rootScope;
	}


}
