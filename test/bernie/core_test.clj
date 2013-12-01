
(ns bernie.core-test
  (:require [clojure.test :refer :all]
            [bernie.core :refer :all]))

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
    (is (= "foobar" (unserialize "s:6:\"foobar\";")))))
    (is (= "foo;:bar:;" (unserialize "s:10:\"foo;:bar:;\";")))

(deftest parsing-arrays
  (testing "arrays are parsed to vectors with contents"
    (is (= [1 2] (unserialize "a:2:{i:0;i:1;i:1;i:2;}i:2;")))
    (is (= [[2]] (unserialize "a:1:{i:0;a:1:{i:0;i:2;}}")))))

(run-tests)

