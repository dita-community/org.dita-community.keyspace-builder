/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2013 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */
package org.ditacommunity.dost.util;

import static org.dita.dost.module.filter.MapBranchFilterModule.BRANCH_COPY_TO;
import static org.dita.dost.util.Constants.*;
import static org.dita.dost.util.URLUtils.toURI;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.sf.saxon.s9api.XdmNode;

/**
 * Key definition.
 */
public class KeyDef {

	private String keyName;
	private List<XdmNode> keyDefiners = new ArrayList<XdmNode>();

	public KeyDef(String key) {
		this.keyName = key;
	}

	/**
	 * Construct a new KeyDef using the key definers in the 
	 * input keydef, prepending the qualifier to the key name
	 * of the input keydef, separated by ".".
	 * @param qualifier Qualifier to prepend to the key name (separated by ".").
	 * @param keydef Keydef to get the key definers from.
	 */
	public KeyDef(String qualifier, KeyDef keydef) {
		this.keyName = qualifier + "." + keydef.getKeyName();
		for (XdmNode keyDefiner : keydef.getKeyDefiners()) {
			if (!this.keyDefiners.contains(keyDefiner)) {
				this.keyDefiners.add(keyDefiner);
			}
		}
	}

	/**
	 * Create a new key definition, adding its first key-defining element.
	 * @param keyName The name of the key being defined
	 * @param keyDefiner The first key definer for the key.
	 */
	public KeyDef(String keyName, XdmNode keyDefiner) {
		this(keyName);
		this.appendKeyDefiner(keyDefiner);

	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder().append(keyName).append(EQUAL);
		
//		if (href != null) {
//			buf.append(href.toString());
//		}
//		if (scope != null) {
//			buf.append(LEFT_BRACKET).append(scope).append(RIGHT_BRACKET);
//		}
//		if (source != null) {
//			buf.append(LEFT_BRACKET).append(source.toString()).append(RIGHT_BRACKET);
//		}
		return buf.toString();
	}

	/**
	 * Get the name of the key defined by the key definition.
	 * @return The key name.
	 */
	public String getKeyName() {
		return this.keyName;
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((href == null) ? 0 : href.hashCode());
//		result = prime * result + ((keys == null) ? 0 : keys.hashCode());
//		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
//		result = prime * result + ((format == null) ? 0 : format.hashCode());
//		result = prime * result + ((source == null) ? 0 : source.hashCode());
//		return result;
//	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof KeyDef other)) {
			return false;
		}
//		if (href == null) {
//			if (other.href != null) {
//				return false;
//			}
//		} else if (!href.equals(other.href)) {
//			return false;
//		}
//		if (keys == null) {
//			if (other.keys != null) {
//				return false;
//			}
//		} else if (!keys.equals(other.keys)) {
//			return false;
//		}
//		if (scope == null) {
//			if (other.scope != null) {
//				return false;
//			}
//		} else if (!scope.equals(other.scope)) {
//			return false;
//		}
//		if (format == null) {
//			if (other.format != null) {
//				return false;
//			}
//		} else if (!format.equals(other.format)) {
//			return false;
//		}
//		if (source == null) {
//			if (other.source != null) {
//				return false;
//			}
//		} else if (!source.equals(other.source)) {
//			return false;
//		}
		return true;
	}

	/**
	 * Append a key definer to the list of key definers for the key definition.
	 * @param elem Key-defining element.
	 */
	public void appendKeyDefiner(XdmNode elem) {
		this.keyDefiners.add(elem);
		
	}
	
	/**
	 * Get the effective @href value for a key-defining element.
	 * @param elem A key-defining element 
	 * @return The URI for the @href. May be null.
	 */
	private URI getHrefFromKeyDefiner(XdmNode elem) {
		final URI href = toURI(
				elem.attribute(BRANCH_COPY_TO) != null
		      ? elem.attribute(BRANCH_COPY_TO)
		      : elem.attribute(ATTRIBUTE_NAME_COPY_TO) != null
		        ? elem.attribute(ATTRIBUTE_NAME_COPY_TO)
		        : elem.attribute(ATTRIBUTE_NAME_HREF)
		);
		return href;
	}

	/**
	 * Get the list of key definers for the key definition.
	 * @return List of key-defining elements. May be empty if the KeyDef hasn't yet been constructed.
	 */
	public List<XdmNode> getKeyDefiners() {
		return this.keyDefiners;
	}

	/**
	 * Append key definers to the existing key definers.
	 * @param newKeyDefiners Key definers to append. Appended key definers will have
	 *                       a lower priority than existing key definers.
	 */
	public void appendKeyDefiners(List<XdmNode> newKeyDefiners) {
		this.keyDefiners.addAll(newKeyDefiners);
	}

	/**
	 * Prepend the new key definers to the start of the list of key definers,
	 * making them higher priority.
	 * @param newKeyDefiners New key definers to prepend to the definer list
	 */
	public void prependKeyDefiners(List<XdmNode> newKeyDefiners) {
		List<XdmNode> newList = new ArrayList<XdmNode>(newKeyDefiners);
		for (XdmNode definer : this.keyDefiners) {
			if (!newKeyDefiners.contains(definer)) {
				newList.add(definer);
			}
		}
		this.keyDefiners = newList;
	}

	/**
	 * Get the HREF URI for the effective key definition.
	 * @return URI or null if there is no href.
	 */
	public URI getHref() {
		XdmNode keyDefiner = getEffectiveKeyDefiner();
		return this.getHrefFromKeyDefiner(keyDefiner);
	}

	/**
	 * Return the effective key definer for the key definition.
	 * This could be determined dynamically using filtering if
	 * the map is not filtered before key resolution is done.
	 * @return The effective key-defining element.
	 */
	public XdmNode getEffectiveKeyDefiner() {
		XdmNode keyDefiner = this.keyDefiners.get(0);
		return keyDefiner;
	}

}
