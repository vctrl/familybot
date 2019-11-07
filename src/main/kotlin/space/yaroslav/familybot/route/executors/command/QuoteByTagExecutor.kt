package space.yaroslav.familybot.route.executors.command

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender
import space.yaroslav.familybot.common.utils.send
import space.yaroslav.familybot.repos.ifaces.QuoteRepository
import space.yaroslav.familybot.route.models.Command
import space.yaroslav.familybot.telegram.BotConfig

const val QUOTE_MESSAGE = "Тег?"

@Component
class QuoteByTagExecutor(val quoteRepository: QuoteRepository, config: BotConfig) : CommandExecutor(config) {
    override fun command(): Command {
        return Command.QUOTE_BY_TAG
    }

    override fun execute(update: Update): suspend (AbsSender) -> Unit {
        return {
            val rows = quoteRepository
                .getTags()
                .map { tag -> InlineKeyboardButton().setText(tag.capitalize()).setCallbackData(tag) }
                .chunked(3)
            it.send(update, QUOTE_MESSAGE, replyToUpdate = true, customization = customization(rows))
        }
    }

    private fun customization(rows: List<List<InlineKeyboardButton>>): (SendMessage) -> SendMessage {
        return { message: SendMessage ->
            message.setReplyMarkup(InlineKeyboardMarkup().setKeyboard(rows))
        }
    }
}
