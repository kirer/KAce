package com.kace.notification.infrastructure.messaging

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

/**
 * RabbitMQ客户端实现类
 */
class RabbitMQClientImpl(
    host: String,
    port: Int,
    username: String,
    password: String,
    private val virtualHost: String = "/"
) : RabbitMQClient {
    
    private val logger = LoggerFactory.getLogger(RabbitMQClientImpl::class.java)
    private val connectionFactory: ConnectionFactory
    private var connection: Connection? = null
    private val channels = mutableMapOf<String, Channel>()
    
    init {
        connectionFactory = ConnectionFactory().apply {
            this.host = host
            this.port = port
            this.username = username
            this.password = password
            this.virtualHost = this@RabbitMQClientImpl.virtualHost
        }
        
        try {
            connection = connectionFactory.newConnection()
            logger.info("RabbitMQ连接已建立: $host:$port")
        } catch (e: Exception) {
            logger.error("RabbitMQ连接失败: ${e.message}")
            throw e
        }
    }
    
    /**
     * 获取或创建Channel
     */
    private fun getChannel(name: String): Channel {
        return channels.getOrPut(name) {
            connection?.createChannel() ?: throw IllegalStateException("RabbitMQ连接未建立")
        }
    }
    
    override fun publish(exchange: String, routingKey: String, message: String) {
        try {
            val channel = getChannel("publisher")
            channel.basicPublish(
                exchange,
                routingKey,
                null,
                message.toByteArray(StandardCharsets.UTF_8)
            )
        } catch (e: Exception) {
            logger.error("消息发布失败: ${e.message}")
            throw e
        }
    }
    
    override fun consume(queue: String, autoAck: Boolean, handler: (String) -> Boolean) {
        try {
            val channelName = "consumer-$queue"
            val channel = getChannel(channelName)
            
            val deliverCallback = DeliverCallback { consumerTag, delivery ->
                val message = String(delivery.body, StandardCharsets.UTF_8)
                logger.debug("收到消息: $message")
                
                val success = try {
                    handler(message)
                } catch (e: Exception) {
                    logger.error("消息处理失败: ${e.message}")
                    false
                }
                
                if (!autoAck) {
                    if (success) {
                        channel.basicAck(delivery.envelope.deliveryTag, false)
                    } else {
                        channel.basicNack(delivery.envelope.deliveryTag, false, true)
                    }
                }
            }
            
            channel.basicConsume(queue, autoAck, deliverCallback) { consumerTag -> 
                logger.info("消费者已取消: $consumerTag")
            }
            
        } catch (e: Exception) {
            logger.error("消息消费失败: ${e.message}")
            throw e
        }
    }
    
    override fun ack(deliveryTag: Long, multiple: Boolean) {
        try {
            getChannel("consumer").basicAck(deliveryTag, multiple)
        } catch (e: Exception) {
            logger.error("消息确认失败: ${e.message}")
            throw e
        }
    }
    
    override fun nack(deliveryTag: Long, requeue: Boolean) {
        try {
            getChannel("consumer").basicNack(deliveryTag, false, requeue)
        } catch (e: Exception) {
            logger.error("消息拒绝失败: ${e.message}")
            throw e
        }
    }
    
    /**
     * 关闭连接
     */
    fun close() {
        try {
            channels.values.forEach { it.close() }
            connection?.close()
            logger.info("RabbitMQ连接已关闭")
        } catch (e: Exception) {
            logger.error("RabbitMQ连接关闭失败: ${e.message}")
        }
    }
} 