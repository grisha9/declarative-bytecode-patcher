package com.example.patch;

import tech.ytsaurus.spyt.patch.annotations.OriginClass;
import tech.ytsaurus.spyt.patch.annotations.Subclass;

@OriginClass("com.example.Cat")
@Subclass
public class SubclassCat {
    public void meow() {
        System.out.println("WOOF");
    }
}
