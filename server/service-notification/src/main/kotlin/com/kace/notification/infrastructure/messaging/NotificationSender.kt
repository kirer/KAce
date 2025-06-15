package com.kace.notification.infrastructure.messaging

import com.kace.notification.domain.model.Notification

/**
 * 通知发送器接口
 */
interface NotificationSender {
    /**
     * 发送通知
     * 
     * @param notification 要发送的通知
     * @return 发送是否成功
     */
    fun send(notification: Notification): Boolean
} 