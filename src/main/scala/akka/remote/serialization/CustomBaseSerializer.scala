package akka.remote.serialization

import akka.actor.ExtendedActorSystem
import akka.remote.serialization.JavaTransformerUtility.{createInstanceOfClassFromProtoClass, createInstanceOfProtoClassFromClass}
import akka.serialization.BaseSerializer
import com.knoldus.protobuf.cluster.{ObjectSerializer, ProtobufSerializable, ThrowableHolder}

class CustomBaseSerializer(val system: ExtendedActorSystem) extends BaseSerializer
{
    private val throwableSupport = new ThrowableSupport(system)
    override def toBinary(o : AnyRef) : Array[Byte] = {
        o match {
            case message : ProtobufSerializable => {
                println(s">>>>>>>>>>>>>>>>>>> Serialize $message Message <<<<<<<<<<<<<<<<<<<< ")
                val holder = message.asInstanceOf[ThrowableHolder]
                holder.cause.map { ex =>
                    val cause : Array[Byte] = throwableSupport.serializeThrowable(ex)
                    val anyRef : AnyRef = createInstanceOfProtoClassFromClass(message.getClass.getName, message.getClass, message, cause)
                    ScalaTransformerUtility.invokeToByteArrayMethod(anyRef.getClass, anyRef)
                }.getOrElse {
                    val anyRef : AnyRef = createInstanceOfProtoClassFromClass(message.getClass.getName, message.getClass, message, null)
                    ScalaTransformerUtility.invokeToByteArrayMethod(anyRef.getClass, anyRef)
                }
            }
            case _ => Array.empty
        }
    }

    override def includeManifest : Boolean = true

    override def fromBinary(bytes : Array[Byte], manifest : Option[Class[_]]) : AnyRef = {
        manifest match {
            case Some(clazz) if classOf[ProtobufSerializable].isAssignableFrom(clazz) => {
                println(s">>>>>>>>>>>>>>>>>>> De-Serialize ${clazz.getName} Message <<<<<<<<<<<<<<<<<<<< ")
                val data : AnyRef = ScalaTransformerUtility.invokeParseFromMethod(clazz, bytes)
                createInstanceOfClassFromProtoClass(data.getClass.getName, data.getClass, data, system)
            }
            case _ => throw new ClassNotFoundException("Invalid class type for De-Serialize")
        }
    }
}
