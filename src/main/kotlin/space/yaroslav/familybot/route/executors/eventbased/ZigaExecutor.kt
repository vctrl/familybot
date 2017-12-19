package space.yaroslav.familybot.route.executors.eventbased

import org.springframework.stereotype.Component
import org.telegram.telegrambots.api.methods.send.SendSticker
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.AbsSender
import space.yaroslav.familybot.route.executors.Executor
import space.yaroslav.familybot.route.models.Priority

@Component
class ZigaExecutor : Executor {
    override fun priority(): Priority {
        return Priority.MEDIUM
    }

    private val zigaLeftId = "CAADAgADNQAD_8Q9CN0CpZZUdZygAg"
    private val zigaRightId = "CAADAgADNgAD_8Q9CIQDztfj3HHcAg"

    override fun execute(update: Update): (AbsSender) -> Unit {
        return {
            it.sendSticker(SendSticker()
                    .setChatId(update.message.chatId)
                    .setReplyToMessageId(update.message.messageId)
                    .setSticker(if (update.message.sticker?.fileId == zigaRightId) {
                        zigaLeftId
                    } else {
                        zigaRightId
                    })
            )
        }
    }

    override fun canExecute(message: Message): Boolean {
        val fileId = message.sticker?.fileId
        return fileId == zigaLeftId || fileId == zigaRightId
    }
}