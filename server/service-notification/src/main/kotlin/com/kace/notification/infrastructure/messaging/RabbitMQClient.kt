package com.kace.notification.infrastructure.messaging

/**
 * RabbitMQ客户端接口
 */
interface RabbitMQClient {
    /**
     * 发布消息到指定的交换机和路由键
     * 
     * @param exchange 交换机名称
     * @param routingKey 路由键
     * @param message 消息内容
     */
    fun publish(exchange: String, routingKey: String, message: String)
    
    /**
     * 消费消息
     * 
     * @param queue 队列名称
     * @param autoAck 是否自动确认
     * @param handler 消息处理器
     */
    fun consume(queue: String, autoAck: Boolean = false, handler: (String) -> Boolean)
    
    /**
     * 确认消息
     * 
     * @param deliveryTag 投递标签
     * @param multiple 是否批量确认
     */
    fun ack(deliveryTag: Long, multiple: Boolean = false)
    
    /**
     * 拒绝消息
     * 
     * @param deliveryTag 投递标签
     * @param requeue 是否重新入队
     */
    fun nack(deliveryTag: Long, requeue: Boolean = true)
} 