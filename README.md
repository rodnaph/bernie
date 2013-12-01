
# Bernie, PHP Serialization Parser

A library to parse data serialized by PHP into Clojure data structures.

## Usage

The namespace provides a single function, _unserialize_, which takes a string
of serialized data and returns EDN.

```clojure
(ns my.namespace
  (:require [bernie.core :refer [unserialize]]))

(unserialize "i:123;") ; => 123
```

If the data supplied is not valid then an exception will most likely be thrown.

## Arrays

When serializing, PHP makes no distinction between numerically indexed and
associative arrays (as it's internal implementation doesn't care). In EDN
though we do care as these will end up as vectors or hashmaps respectively.

Bernie takes the approach of checking an array's keys, and if they are all
numeric it'll create a vector, otherwise you'll get back a hashmap.

## Custom Serialization

Bernie does not support the 'C' custom serialization format, as the
implementing code will not be available.  And serialized data containing
this information will throw an exception.

