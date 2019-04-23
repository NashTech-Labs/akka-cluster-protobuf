package com.knoldus.protobuf.cluster

import com.knoldus.protobuf.cluster.ReflectionUtility.PROTO_SUFFIX
import scalapb.{GeneratedEnumCompanion, GeneratedMessageCompanion}

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

object ReflectionScalaUtility
{
    private val runtimeMirror: Mirror = universe.runtimeMirror(getClass.getClassLoader)

    def invokeToByteArrayMethod(clazz : Class[_], obj: AnyRef) : Array[Byte] = {
        println(" >>>>>>>>>>>>>>>>> In invokeToByteArrayMethod Method <<<<<<<<<<<<<<<<<<<<")

        val instanceMirror : universe.InstanceMirror = runtimeMirror.reflect(obj)
        val methodSymbol : universe.MethodSymbol = instanceMirror.symbol.typeSignature.member(TermName("toByteArray")).asMethod
        instanceMirror.reflectMethod(methodSymbol).apply().asInstanceOf[Array[Byte]]
    }

    def invokeParseFromMethod(clazz : Class[_], bytes : Array[Byte]) : AnyRef = {
        println(" >>>>>>>>>>>>>>>>> In invokeParseFromMethod Method <<<<<<<<<<<<<<<<<<<<")

        val obj = loadProtoCompanionObject(clazz)
        val generatedMessageCompanion = obj.instance.asInstanceOf[GeneratedMessageCompanion[_]]
        generatedMessageCompanion.parseFrom(bytes).asInstanceOf[AnyRef]
    }

    def convertEnumerationValueToGeneratedEnumValue(clazz : Class[_], id : Int) : AnyRef = {
        println(" >>>>>>>>>>>>>>>>> In convertEnumerationValueToGeneratedEnumValue Method <<<<<<<<<<<<<<<<<<<<")

        val obj = loadProtoCompanionObject(clazz)
        val generatedEnumCompanion = obj.instance.asInstanceOf[GeneratedEnumCompanion[_]]
        generatedEnumCompanion.fromValue(id).asInstanceOf[AnyRef]
    }

    def convertGeneratedEnumValueToEnumerationValue(clazz : Class[_], name: String): Enumeration#Value = {
        println(" >>>>>>>>>>>>>>>>> In convertGeneratedEnumValueToEnumerationValue Method <<<<<<<<<<<<<<<<<<<<")

        val classSymbol : universe.ClassSymbol = runtimeMirror.classSymbol(clazz)
        val methodSymbol : universe.MethodSymbol = classSymbol.toType.member(TermName("withName")).asMethod
        val moduleSymbol = classSymbol.companion.asModule
        val moduleMirror : universe.ModuleMirror = runtimeMirror.reflectModule(moduleSymbol)
        val instanceMirror = runtimeMirror.reflect(moduleMirror.instance)
        instanceMirror.reflectMethod(methodSymbol)(name).asInstanceOf[Enumeration#Value]
    }

    def findEnumerationOuterType(clazz : Class[_], enumeration: Enumeration#Value) : Class[_ <: AnyRef] = {
        println(" >>>>>>>>>>>>>>>>> In findEnumerationOuterType Method <<<<<<<<<<<<<<<<<<<<")

        clazz.getMethod("scala$Enumeration$Val$$$outer").invoke(enumeration).getClass
    }

    def method(className: String) = {
        val classLoader = getClass.getClassLoader.loadClass(className)
        println("\n //////////////////////////// " +  classLoader.getClasses.mkString(" , "))

        val enumObject = getClass.getClassLoader.loadClass(className + "$").getField("MODULE$").get(null)
        println(" *********************************************  " + enumObject)

        val module : universe.ModuleSymbol = runtimeMirror.staticModule(className + "$")
        runtimeMirror.reflectModule(module).instance.asInstanceOf[Enumeration]
    }

    private def loadProtoCompanionObject(clazz : Class[_]) : universe.ModuleMirror = {
        val clazzName = clazz.getName
        val tempClazzName = if(clazzName.endsWith("$")) clazzName.substring(0, clazzName.length - 1) else clazzName
        val module = runtimeMirror.staticModule(tempClazzName + PROTO_SUFFIX)
        runtimeMirror.reflectModule(module)
    }
}
