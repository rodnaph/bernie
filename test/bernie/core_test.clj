
(ns bernie.core-test
  (:require [clojure.test :refer :all]
            [bernie.core :refer :all]))

(deftest parsing-nulls
  (testing "null is parsed as nil"
    (is (= nil (unserialize "N;")))))

(deftest parsing-booleans
  (testing "booleans are parsed as true and false"
    (is (= true (unserialize "b:1;")))
    (is (= false (unserialize "b:0;")))))

(deftest parsing-integers
  (testing "serialized integers are parsed as longs"
    (is (= 123 (unserialize "i:123;")))))

(deftest parsing-floats
  (testing "floats are parsed to doubles"
    (is (= 123.45 (unserialize "d:123.45;")))
    (is (= 456.78 (unserialize "d:456.78;")))))

(deftest parsing-strings
  (testing "strings are parsed to strings"
    (is (= "foobar" (unserialize "s:6:\"foobar\";")))))

(run-tests)

