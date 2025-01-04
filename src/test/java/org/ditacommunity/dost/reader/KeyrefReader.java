package org.ditacommunity.dost.reader;


import org.dita.dost.exception.DITAOTException;
import org.dita.dost.reader.AbstractReader;
import static net.sf.saxon.s9api.streams.Predicates.isElement;
import static net.sf.saxon.s9api.streams.Steps.child;
import static net.sf.saxon.s9api.streams.Steps.precedingSibling;
import static net.sf.saxon.type.BuiltInAtomicType.STRING;
import static org.dita.dost.module.filter.MapBranchFilterModule.BRANCH_COPY_TO;
import static org.dita.dost.util.Constants.*;
import static org.dita.dost.util.KeyScope.ROOT_ID;
import static org.dita.dost.util.URLUtils.toURI;
import static org.dita.dost.util.XMLUtils.rootElement;

import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import net.sf.saxon.event.PipelineConfiguration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.expr.parser.Loc;
import net.sf.saxon.om.*;
import net.sf.saxon.s9api.XdmDestination;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;
import net.sf.saxon.serialize.SerializationProperties;
import net.sf.saxon.trans.UncheckedXPathException;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Untyped;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.log.MessageBean;
import org.dita.dost.log.MessageUtils;
import org.dita.dost.util.Job;
import org.dita.dost.util.KeyDef;
import org.dita.dost.util.KeyScope;
import org.dita.dost.util.XMLUtils;


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
	    // TODO: use KeyScope implementation that retains order
//	    final KeyScope keyScope = readScopes(doc);
//	    final KeyScope keyScopeWithChildren = cascadeChildKeys(keyScope);
//	    // TODO: determine effective key definitions here
//	    final KeyScope keyScopeWithParents = inheritParentKeys(keyScopeWithChildren);
//	    rootScope = resolveIntermediate(keyScopeWithParents);
	  }


}
