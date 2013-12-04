
(ns bernie.core-test
  (:import com.pugh.bernie.UnserializeException)
  (:require [clojure.test :refer :all]
            [bernie.core :refer :all]))

(deftest parsing-serialized-data
  (testing "parsing data dumped directly from PHP serialize"
    (is (= nil (unserialize (slurp "test/data/null.ser"))))
    (is (= true (unserialize (slurp "test/data/boolean.ser"))))
    (is (= 12345 (unserialize (slurp "test/data/integer.ser"))))
    (is (= 123.45 (unserialize (slurp "test/data/decimal.ser"))))
    (is (= "foobar" (unserialize (slurp "test/data/string.ser"))))
    (is (= [1 2 3] (unserialize (slurp "test/data/array.ser"))))))

(deftest parsing-nulls
  (testing "null is parsed as nil"
    (is (= nil (unserialize "N;")))))

(deftest parsing-booleans
  (testing "booleans are parsed as true and false"
    (is (= true (unserialize "b:1;")))
    (is (= false (unserialize "b:0;")))
    (is (= false (unserialize "b:0;i:5;")))))

(deftest parsing-integers
  (testing "serialized integers are parsed as longs"
    (is (= 123 (unserialize "i:123;")))))

(deftest parsing-decimals
  (testing "decimals are parsed to doubles"
    (is (= 123.45 (unserialize "d:123.45;")))
    (is (= 456.78 (unserialize "d:456.78;")))))

(deftest parsing-strings
  (testing "strings are parsed to strings"
    (is (= "foobar" (unserialize "s:6:\"foobar\";")))
    (is (= "foobar" (unserialize "s:6:\"foobar\";i:2;")))
    (is (= "foo;:bar:;" (unserialize "s:10:\"foo;:bar:;\";")))
    (is (= "foobarfoobar" (unserialize "s:12:\"foobarfoobar\";")))))

(deftest parsing-arrays
  (testing "arrays are parsed to vectors with contents"
    ; vectors
    (is (= [1 2] (unserialize "a:2:{i:0;i:1;i:1;i:2;}i:2;")))
    (is (= [[2]] (unserialize "a:1:{i:0;a:1:{i:0;i:2;}}")))
    ; hashmaps
    (is (= {:foo [2]} (unserialize "a:1:{s:3:\"foo\";a:1:{i:0;i:2;}}")))
    (is (= {:foo "bar"} (unserialize "a:1:{s:3:\"foo\";s:3:\"bar\"}")))))

(deftest parsing-objects
  (testing "objects are parsed into maps of their properties"
    (is (= {:public 1 :protected 2 :private 3}
           (unserialize (slurp "test/data/object.ser"))))
    (is (= {:foo 1} (unserialize "O:12:\"FooBarFooBar\":1:{s:3:\"foo\";i:1;}")))))

(deftest parsing-custom
  (testing "custom serialization is assumed to be a serialized string"
    (is (= [[1]] (unserialize "a:1:{i:0;C:4:\"Test\":14:{a:1:{i:0;i:1;}}i:1;i:1;}")))))

(deftest parsing-references
  (testing "parsing references uses nil"
    (is (= [1 nil] (unserialize "a:2:{i:0;i:1;i:1;R:2;}")))))

(deftest parsing-bad-data
  (testing "bad data throws an exception"
    (is (thrown? UnserializeException (unserialize "INVALIDDATA")))
    (is (thrown? UnserializeException (unserialize "a:1:{}")))))

(run-tests)

