# org.dita-community.keyspace-builder

Key space builder for Open Toolkit.

This plug-in provides an alternative keyspace constructor that better handles large key spaces.

The base keyspace constructor uses a map/reduce approach, which is simple and works fine for smaller maps but fails with memory and time issues on lorger maps with 100s of 1000s of keys.

This plug-in uses a multi-phase approach that minimizes memory and processing time requirements. It is based on a Python key space constructor developed by ServiceNow.

## DITA OT key space construction notes

The DOST key space construction is performed by the KeyrefModule, which uses the KeyrefReader to construct the keyscopes, which are then merged by key scope ID in teh KeyrefModule.

The basic processing flow is:

1. `KeyrefModule` gets the list of map files to process and iteratest over them using `Collection.stream()`. Each map file is mapped to a `KeyScope` by using `KeyrefReader` to read the map and construct a complete set of key scopes from the input map.
1. The list of `KeyScope`s is reduced to a single key scope (with child scopes) by using `Collection.reduce()` with the the `KeyScope.merge()` method, which merges key scopes with the same ID. The result is a single `KeyScope` reflecting the full set of key scopes in the key space rooted at the input root map.

The `KeyrefReader` does the actual key scope construction work:

1. The top-level `read()` method performs three operations in sequence for a single input DITA map:
    1. Constructs the base key scopes from the input map, reflecting just the key definitions within the key scope.
    1. Performs the "pull up" phase using the `cascadeChildKeys()` method, which pulls keys from child key scopes into the parent key scope, adding the child's key scope prefixes to the pulled-up keys.
    1. Performs the "push down" phase using the `inheritParentKeys()` method, which pushes keys from the parent scope to its child scopes such that the pushed keys override the same key definitions in the child.
    1. Resolves any key definitions that themselves point to keys, ensuring that all key definitions in the key space that are resolvable are resolved to a resource.
    
The `KeyScope` object is essentially a map of key definitions by key name and a list of child scopes.

Note that this approach to key space representation is not sufficient for all use cases because it does not capture the key-space-defining element, which is needed in order to go from a map context to the key scope that applies to that context, which is needed in order to resolve incompletely-qualified key references.

It is also incorrect to merge key scopes with the same scope name, as two different scopes may have the same name but define different bindings for the same key name. Incompletely-qualified key references within these scopes should resolve to the key as define in the scope.

For scope-qualified references, the key definition from the first scope with the scope name that defines the key name should be used. But this will be the case regardless of the scope context used for the resolution because the pull-up/push-down process results in all fully-qualified key definitions being available in every child scope and those pushed down take precedence, meaning key definitions from earlier scopes will always override the same qualified key name from later scopes.

The keyscopes are then used to process the topics referenced from the map by setting the key scope on a `KeyrefPaser` \[sic\] object, which then uses the key scope to resolve key references and rewrite them to reflect the key's resource (string value or referenced URI).

The key scope used for a given topic's `ResolveTask` is determined by the key scope values on topicrefs, where the key scope value is used to select child scopes of the current scope that have the key scope name(s).

Note that a better solution would be to use the key-scope-defining element as the lookup key for the key scope as this approach, coupled with the merge processing discussed above, could result in an incorrect key resolution.
