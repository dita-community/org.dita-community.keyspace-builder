# org.dita-community.keyspace-builder

Key space builder for Open Toolkit.

This plug-in provides an alternative keyspace constructor that better handles large key spaces.

The base keyspace constructor uses a map/reduce approach, which is simple and works fine for smaller maps but fails with memory and time issues on lorger maps with 100s of 1000s of keys.

This plug-in uses a multi-phase approach that minimizes memory and processing time requirements. It is based on a Python key space constructor developed by ServiceNow.
