package org.ditacommunity.dost;

import java.io.IOException;
import java.net.URI;

import javax.xml.transform.stream.StreamSource;

import org.dita.dost.util.XMLUtils;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

public class KeyspaceTester {

	final protected XMLUtils xmlUtils = new XMLUtils();

	public KeyspaceTester() {
		super();
	}

	/**
	 * Copy of method from StreamStore for parsing a document to a node.
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public XdmNode getImmutableNode(final URI path) throws IOException {
	    try {
	      return xmlUtils.getProcessor().newDocumentBuilder().build(new StreamSource(path.toString()));
	    } catch (SaxonApiException e) {
	      throw new IOException(e);
	    }
	}

}