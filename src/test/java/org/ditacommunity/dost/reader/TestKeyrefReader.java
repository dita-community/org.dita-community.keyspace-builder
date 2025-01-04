package org.ditacommunity.dost.reader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.util.XMLUtils;
import org.ditacommunity.dost.util.KeyDef;
import org.ditacommunity.dost.util.KeyScope;
import org.junit.Test;
import org.slf4j.Marker;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

public class TestKeyrefReader {

	final XMLUtils xmlUtils = new XMLUtils();

	@Test
	public void test() throws Exception {
		
	    URI mapUri = getClass().getClassLoader().getResource("org/ditacommunity/dost/resources/small-map/small-map.ditamap").toURI();
	    XdmNode mapNode = getImmutableNode(mapUri);
	
	    KeyrefReader reader = new KeyrefReader();
		reader.setLogger(logger);
		reader.setXmlUtils(xmlUtils);
		// Reader the input map to construct the key space:
		reader.read(mapUri, mapNode);
		KeyScope rootScope = reader.getKeyDefinition();
		assertNotNull("Expected a root key scope", rootScope);
		Map<String, KeyDef> keydefs = rootScope.getKeyDefinitions();
		assertNotNull("Expected a key definitions map", keydefs);
		assertTrue("Expected at least one key definition", keydefs.keySet().size() > 0);

	}

    DITAOTLogger logger =
    	      new DITAOTLogger() {
  				
  				@Override
  				public void warn(Marker marker, String format, Object arg1, Object arg2) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void warn(Marker marker, String msg, Throwable t) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void warn(Marker marker, String format, Object... arguments) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void warn(Marker marker, String format, Object arg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void warn(String format, Object arg1, Object arg2) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void warn(Marker marker, String msg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void warn(String msg, Throwable t) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void warn(String format, Object... arguments) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void warn(String format, Object arg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void warn(String msg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void trace(Marker marker, String format, Object arg1, Object arg2) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void trace(Marker marker, String msg, Throwable t) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void trace(Marker marker, String format, Object... argArray) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void trace(Marker marker, String format, Object arg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void trace(String format, Object arg1, Object arg2) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void trace(Marker marker, String msg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void trace(String msg, Throwable t) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void trace(String format, Object... arguments) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void trace(String format, Object arg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void trace(String msg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public boolean isWarnEnabled(Marker marker) {
  					// TODO Auto-generated method stub
  					return false;
  				}
  				
  				@Override
  				public boolean isWarnEnabled() {
  					// TODO Auto-generated method stub
  					return false;
  				}
  				
  				@Override
  				public boolean isTraceEnabled(Marker marker) {
  					// TODO Auto-generated method stub
  					return false;
  				}
  				
  				@Override
  				public boolean isTraceEnabled() {
  					// TODO Auto-generated method stub
  					return false;
  				}
  				
  				@Override
  				public boolean isInfoEnabled(Marker marker) {
  					// TODO Auto-generated method stub
  					return false;
  				}
  				
  				@Override
  				public boolean isInfoEnabled() {
  					// TODO Auto-generated method stub
  					return false;
  				}
  				
  				@Override
  				public boolean isErrorEnabled(Marker marker) {
  					// TODO Auto-generated method stub
  					return false;
  				}
  				
  				@Override
  				public boolean isErrorEnabled() {
  					// TODO Auto-generated method stub
  					return false;
  				}
  				
  				@Override
  				public boolean isDebugEnabled(Marker marker) {
  					// TODO Auto-generated method stub
  					return false;
  				}
  				
  				@Override
  				public boolean isDebugEnabled() {
  					// TODO Auto-generated method stub
  					return false;
  				}
  				
  				@Override
  				public void info(Marker marker, String format, Object arg1, Object arg2) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void info(Marker marker, String msg, Throwable t) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void info(Marker marker, String format, Object... arguments) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void info(Marker marker, String format, Object arg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void info(String format, Object arg1, Object arg2) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void info(Marker marker, String msg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void info(String msg, Throwable t) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void info(String format, Object... arguments) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void info(String format, Object arg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void info(String msg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public String getName() {
  					// TODO Auto-generated method stub
  					return null;
  				}
  				
  				@Override
  				public void error(Marker marker, String format, Object arg1, Object arg2) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void error(Marker marker, String msg, Throwable t) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void error(Marker marker, String format, Object... arguments) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void error(Marker marker, String format, Object arg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void error(String format, Object arg1, Object arg2) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void error(Marker marker, String msg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void error(String msg, Throwable t) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void error(String format, Object... arguments) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void error(String format, Object arg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void error(String msg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void debug(Marker marker, String format, Object arg1, Object arg2) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void debug(Marker marker, String msg, Throwable t) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void debug(Marker marker, String format, Object... arguments) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void debug(Marker marker, String format, Object arg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void debug(String format, Object arg1, Object arg2) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void debug(Marker marker, String msg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void debug(String msg, Throwable t) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void debug(String format, Object... arguments) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void debug(String format, Object arg) {
  					// TODO Auto-generated method stub
  					
  				}
  				
  				@Override
  				public void debug(String msg) {
  					// TODO Auto-generated method stub
  					
  				}
  	
    };

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
	};

}