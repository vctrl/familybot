package space.yaroslav.familybot.common.utils

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.AbsSender

fun AbsSender.send(
    update: Update,
    text: String,
    replyMessageId: Int? = null,
    enableHtml: Boolean = false,
    replyToUpdate: Boolean = false,
    customization: (SendMessage) -> SendMessage = { it -> it }
): Message {
    val messageObj = SendMessage(update.chatId(), text).enableHtml(enableHtml)
    if (replyMessageId != null) {
        messageObj.replyToMessageId = replyMessageId
    }
    if (replyToUpdate) {
        messageObj.replyToMessageId = update.message.messageId
    }

    return this.execute(customization(messageObj))
}