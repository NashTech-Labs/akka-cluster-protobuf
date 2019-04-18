package com.knoldus.protobuf.cluster

import scala.reflect.runtime.universe

object ReflectionScalaUtility
{
    def invokeToByteArrayMethod(clazz : Class[_]) : Array[Byte] = {
        val runtimeMirror = universe.runtimeMirror(clazz.getClassLoader)
        val module = runtimeMirror.staticModule(clazz.getName)
        val obj = runtimeMirror.reflectModule(module)
        val someTrait : SomeTrait = obj.instance.asInstanceOf[SomeTrait]
        someTrait.toByteArray
    }

    def invokeParseFromMethod(clazz : Class[_], bytes : Array[Byte]) : AnyRef = {
        val runtimeMirror = universe.runtimeMirror(clazz.getClassLoader)
        val module = runtimeMirror.staticModule(clazz.getName)
        val obj = runtimeMirror.reflectModule(module)
        val someTrait : SomeTrait = obj.instance.asInstanceOf[SomeTrait]
        someTrait.parseFrom(bytes)
    }
}
