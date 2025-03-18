package com.example.patch;

import tech.ytsaurus.spyt.patch.annotations.OriginClass;

@OriginClass("com.example.Dog")
public class PatchedDog {
    public void woof() {
        System.out.println("MEOW");
    }
}
