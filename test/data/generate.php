<?php

class Test {
    public $public = 1;
    protected $protected = 2;
    private $private = 3;
}

function put($file, $data)
{
    file_put_contents(
        sprintf('%s/%s', __DIR__, $file),
        serialize($data)
    );
}

put('null.ser', null);
put('boolean.ser', true);
put('integer.ser', 12345);
put('decimal.ser', 123.45);
put('string.ser', "foobar");
put('array.ser', array(1, 2, 3));
put('object.ser', new Test());

