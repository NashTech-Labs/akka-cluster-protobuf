package com.knoldus.protobuf.cluster

import scala.reflect.runtime.universe

object ReflectionScalaUtility
{
    def invokeToByteArrayMethod(clazz : Class[_], obj : AnyRef) : Array[Byte] = {
        val runtimeMirror = universe.runtimeMirror(clazz.getClassLoader)
        val module = runtimeMirror.staticModule(clazz.getName)
        val obj = runtimeMirror.reflectModule(module)
        val someTrait : SomeTrait = obj.instance.asInstanceOf[SomeTrait]
        someTrait.toByteArray
    }
}
