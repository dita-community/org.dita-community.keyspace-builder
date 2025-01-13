package org.ditacommunity.dost.util;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.List;

import org.ditacommunity.dost.KeyspaceTester;
import org.junit.Test;

import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.streams.Steps;
import net.sf.saxon.s9api.streams.XdmStream;

public class TestKeyDef extends KeyspaceTester {

	@Test
	public void test() throws Exception {
		
	    URI mapUri = getClass().getClassLoader().getResource("org/ditacommunity/dost/resources/small-map/small-map.ditamap").toURI();
	    // Get the document node for input map.
	    XdmNode mapNode = getImmutableNode(mapUri);
	    // Just getting an element, isn't necessarily an actual key defining element.
	    XdmNode keyDefiner = mapNode.getOutermostElement();
	    List<XdmNode> definers = mapNode.select(Steps.descendant("keydef")).asList();
	    XdmNode definer_01 = definers.get(0);
	    XdmNode definer_02 = definers.get(1);
		
		String keyName = definer_01.attribute("keys"); // Assuming it's a single token
		KeyDef keydef = new KeyDef(keyName, keyDefiner);
		String candKeyName = keydef.getKeyName();
		assertTrue("Expected \"" + keyName + "\", got \"" + candKeyName + "\"", keyName.equals(candKeyName));
		XdmNode candDefiner = keydef.getKeyDefiners().get(0);
		assertTrue("Expected key definer to be keyDefiner", keyDefiner.equals(candDefiner));
	}

}
