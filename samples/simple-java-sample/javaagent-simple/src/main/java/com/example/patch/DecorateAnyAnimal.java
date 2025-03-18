package com.example.patch;

import tech.ytsaurus.spyt.patch.annotations.*;

@Decorate
@OriginClass("com.example.AnyAnimal")
public class DecorateAnyAnimal {

    @DecoratedMethod
    public void sayAny(String any) {
        before(any);
        __sayAny("Decorated: " + any);
        System.out.println("after: " + any);
    }

    public void __sayAny(String any) {}

    @AddMethod
    private void before(String any) {
        System.out.println("before: " + any);
    }

}
