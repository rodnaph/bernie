
# Bernie, PHP Serialization Parser

A library to parse data serialized by PHP into Clojure data structures.

## Usage

The namespace provides a single function, _unserialize_, which takes a string
of serialized data and returns EDN.

```clojure
(ns my.namespace
  (:require [bernie.core :refer [unserialize]]))

(unserialize "i:123;") ; => 123
(unserialize "a:2:{i:0;s:3:"foo";i:1;d:456.78;}") ; => ["foo" 456.78]
(unserialize "a:1:{s:3:"foo";i:123}") ; => {:foo 123}
```

If the data supplied is not valid then an exception will most likely be thrown.

## Arrays

When serializing, PHP makes no distinction between numerically indexed and
associative arrays (as it's internal implementation doesn't care). In EDN
though we do care as these will end up as vectors or hashmaps respectively.

Bernie takes the approach of checking an array's keys, and if they are all
numeric it'll create a vector, otherwise you'll get back a hashmap.

## Custom Serialization

For custom serialization ('C') Bernie assumes the content is serialized data
itself.  This is because the usual way this is used is as follows:

```php
class Test implements \Serializable
{
    public function serialize()
    {
        return serialize(array(
            // properties
        ));
    }
}
```

## References

The reference type 'R' is currently not supported, and will return nil.

```clojure
(unserialize "R:1;") ; => nil
```

## Invalid Data

Any invalid data will throw a _com.pugh.bernie.UnserializeException_.

## Installation

Bernie is available via [Clojars](https://clojars.org/rodnaph/bernie).

