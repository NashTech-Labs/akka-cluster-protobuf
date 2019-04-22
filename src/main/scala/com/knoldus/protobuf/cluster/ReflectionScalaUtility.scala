package com.knoldus.protobuf.cluster

import com.knoldus.protobuf.cluster.ReflectionUtility.PROTO_SUFFIX
import scalapb.GeneratedMessageCompanion

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

object ReflectionScalaUtility
{
    def invokeToByteArrayMethod(clazz : Class[_], obj: AnyRef) : Array[Byte] = {
        println(" >>>>>>>>>>>>>>>>> In invokeToByteArrayMethod Method <<<<<<<<<<<<<<<<<<<<")
        val runtimeMirror = universe.runtimeMirror(clazz.getClassLoader)
        val instanceMirror : universe.InstanceMirror = runtimeMirror.reflect(obj)
        val methodSymbol : universe.MethodSymbol = instanceMirror.symbol.typeSignature.member(TermName("toByteArray")).asMethod
        instanceMirror.reflectMethod(methodSymbol).apply().asInstanceOf[Array[Byte]]
    }

    def invokeParseFromMethod(clazz : Class[_], bytes : Array[Byte]) : AnyRef = {
        println(" >>>>>>>>>>>>>>>>> In invokeParseFromMethod Method <<<<<<<<<<<<<<<<<<<<")
        val runtimeMirror = universe.runtimeMirror(clazz.getClassLoader)
        val module = runtimeMirror.staticModule(clazz.getName + PROTO_SUFFIX)
        val obj = runtimeMirror.reflectModule(module)
        val generatedMessageCompanion = obj.instance.asInstanceOf[GeneratedMessageCompanion[_]]
        generatedMessageCompanion.parseFrom(bytes).asInstanceOf[AnyRef]
    }
}
