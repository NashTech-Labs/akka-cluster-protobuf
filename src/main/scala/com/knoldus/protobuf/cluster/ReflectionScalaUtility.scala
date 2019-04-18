package com.knoldus.protobuf.cluster

import scalapb.GeneratedMessageCompanion

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

object ReflectionScalaUtility
{
    def invokeToByteArrayMethod(clazz : Class[_], anyRef: AnyRef) : Array[Byte] = {
        val runtimeMirror = universe.runtimeMirror(clazz.getClassLoader)
        val some : universe.InstanceMirror = runtimeMirror.reflect(anyRef)
        val value : universe.Symbol = some.symbol.typeSignature.member(TermName("toByteArray")).asMethod
        some.reflectMethod(value.asMethod).apply().asInstanceOf[Array[Byte]]
    }

    def invokeParseFromMethod(clazz : Class[_], bytes : Array[Byte]) : AnyRef = {
        val runtimeMirror = universe.runtimeMirror(clazz.getClassLoader)
        val module = runtimeMirror.staticModule(clazz.getName)
        val obj = runtimeMirror.reflectModule(module)
        val generatedMessageCompanion = obj.instance.asInstanceOf[GeneratedMessageCompanion[_]]
        generatedMessageCompanion.parseFrom(bytes).asInstanceOf[AnyRef]
    }
}
