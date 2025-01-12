package org.ditacommunity.dost.reader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	    // Get the document node for input map.
	    XdmNode mapNode = getImmutableNode(mapUri);
	
	    KeyrefReader reader = new KeyrefReader();
		reader.setLogger(logger);
		reader.setXmlUtils(xmlUtils);
		// Reader the input map to construct the key space:
		reader.read(mapUri, mapNode);
		KeyScope rootScope = reader.getKeyDefinition();
		
		assertNotNull("Expected a root key scope", rootScope);
		
		// Check that the scope is accessible by its scope names:
		
		String expectedScopeName = KeyrefReader.ROOT_SCOPE_DEFAULT_NAME;
		List<KeyScope> candScopes = rootScope.getScopesByName(expectedScopeName);
		assertNotNull("Expected a list", candScopes);
		assertTrue("Expected one scope", candScopes.size() == 1);
		
		expectedScopeName = "small-map";
		candScopes = rootScope.getScopesByName(expectedScopeName);
		assertNotNull("Expected a list", candScopes);
		assertTrue("Expected one scope for scope name \"" + expectedScopeName + "\"", candScopes.size() == 1);
		
		Map<String, KeyDef> keydefs = rootScope.getKeyDefinitions();
		assertNotNull("Expected a key definitions map", keydefs);
		assertTrue("Expected at least one key definition", keydefs.keySet().size() > 0);
		int expectedCount = 2620; // Keys in the root scope
		assertTrue("Expected " + expectedCount + ", got " + keydefs.size(), expectedCount == keydefs.size());
		
		Set<String> scopeNames = rootScope.getScopeNames();
		assertNotNull("Expected a set of scope names", scopeNames);
		assertTrue("Expected 2 scope names for root scope, got " + scopeNames.size(), scopeNames.size() == 2);
		assertTrue("Expected #ROOT in scope names", scopeNames.contains(KeyrefReader.ROOT_SCOPE_DEFAULT_NAME));
		assertTrue("Expected \"" + expectedScopeName + "\" in scope names", scopeNames.contains(expectedScopeName));
		
		String keyName = "solutions-gallery";
		
		KeyDef cand = rootScope.getKeyDefinition(keyName);
		assertNotNull("Expected keydef for key \"" + keyName + "\"", cand);
		URI href = cand.getHref();
		assertNotNull("Expected href value", href);

		// Verify that the key definer is a element that defines the key:
		String expectedXtrc = "topicref:2;35:38";
		XdmNode definer = cand.getEffectiveKeyDefiner();
		assertNotNull("Expected key definer", definer);
		String xtrcValue = definer.attribute("xtrc").trim();
		String keysValue = definer.attribute("keys").trim();
		// Verify we got the effective definer:
		assertTrue("@xtrc value \"" + xtrcValue + "\" doesn't match expected \"" + expectedXtrc + "\"", expectedXtrc.equals(xtrcValue));
		assertTrue("@keys value \"" + keysValue + "\" doesn't match expected \"" + keyName + "\"", keyName.equals(keysValue));
		
		// Get list of child scopes:
		
		List<KeyScope> childScopes = rootScope.getChildScopes();
		assertNotNull("Expected a list of child scopes", childScopes);
		expectedCount = 19;
		int candCount = childScopes.size();
		assertTrue("Expected " + expectedCount + " scopes, got " + candCount, expectedCount == candCount);
		
		// Get scopes by scope name
		
		expectedScopeName = "image";
		List<KeyScope> imageScopes = rootScope.getScopesByName(expectedScopeName);
		assertNotNull("Expected a list for scope name \"" + expectedScopeName + "\"", imageScopes);
		expectedCount = 2;
		assertTrue("Expected " + expectedCount + " scopes for scope name \"" + expectedScopeName + "\", got " + imageScopes.size(), imageScopes.size() == expectedCount);
		KeyScope imageScope = imageScopes.get(0);
		expectedCount = 3348;
		candCount = imageScope.getKeyDefinitions().size();
		assertTrue("Expected " + expectedCount + " keydefs, got " + candCount, expectedCount == candCount );
		
		// Test result of pull-up phase: Keys from "image." keyspace should be in root key space
		// with "image." qualifier prepended:
		String expectedKeyname = "bus-1-1-meetings";		
		KeyDef candKeydef = imageScope.getKeyDefinition(expectedKeyname);
		assertNotNull("Expected keydef for key \"" + expectedKeyname + "\" in imageKeyscope", candKeydef);
		
		expectedKeyname = "image." + expectedKeyname;
		candKeydef = rootScope.getKeyDefinition(expectedKeyname);
		assertNotNull("Expected keydef for key \"" + expectedKeyname + "\" in rootKeyscope", candKeydef);
		
		// Test result of push-down phase: Scope-qualified keys pulled up from child scope should now also be
		// in the child scope:
		
		candKeydef = imageScope.getKeyDefinition(expectedKeyname);				
		assertNotNull("Expected keydef for key \"" + expectedKeyname + "\" in imageKeyscope", candKeydef);
		
		expectedCount = 1;
		int resultCount = candKeydef.getKeyDefiners().size();
		assertTrue("Expected " + expectedCount + " key definers, found " + resultCount, expectedCount == resultCount);

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