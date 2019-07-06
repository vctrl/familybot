package space.yaroslav.familybot.route.executors.command

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.telegram.telegrambots.api.methods.send.SendSticker
import org.telegram.telegrambots.api.methods.stickers.GetStickerSet
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.AbsSender
import space.yaroslav.familybot.common.CommandByUser
import space.yaroslav.familybot.common.User
import space.yaroslav.familybot.common.utils.send
import space.yaroslav.familybot.common.utils.toChat
import space.yaroslav.familybot.common.utils.toUser
import space.yaroslav.familybot.repos.ifaces.CommandHistoryRepository
import space.yaroslav.familybot.route.models.Command
import space.yaroslav.familybot.route.services.dictionary.Dictionary
import java.time.LocalDate
import java.time.ZoneOffset

@Component
class MoodStickerCommandExecutor(
    val historyRepository: CommandHistoryRepository,
    val dictionary: Dictionary
) : CommandExecutor {
    override fun command() = Command.WHATS_MOOD_TODAY

    override fun execute(update: Update): (AbsSender) -> Unit {

        if (isInvokedToday(update.toUser())) {
            return {}
        }

        return {
            runBlocking {
                val sticker = async { it.execute(GetStickerSet("youaretoday")).stickers.random().fileId }
                it.send(update, "Какой ты сегодня?")
                delay(1000)
                it.sendSticker(
                    SendSticker()
                        .setSticker(sticker.await())
                        .setChatId(update.toChat().id)
                )
            }
        }
    }

    private fun isInvokedToday(user: User): Boolean {
        val commandsFromUserToday = historyRepository.get(
            user,
            from = LocalDate.now().atTime(0, 0).toInstant(ZoneOffset.UTC)
        ).map(CommandByUser::command)
        return commandsFromUserToday.any { it == command() }
    }
}
