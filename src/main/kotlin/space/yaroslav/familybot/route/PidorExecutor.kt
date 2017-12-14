package space.yaroslav.familybot.route

import org.springframework.stereotype.Component
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.AbsSender
import space.yaroslav.familybot.common.Pidor
import space.yaroslav.familybot.common.isToday
import space.yaroslav.familybot.common.toChat
import space.yaroslav.familybot.repos.CommonRepository
import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

@Component
class PidorExecutor(val repository: CommonRepository) : Executor {
    override fun execute(update: Update):  (AbsSender) -> Unit {
        val chat = update.message.chat.toChat()
        val pidor = repository
                .getPidorsByChat(chat).find { it.date.isToday() }
        val message = pidor
                ?.let { SendMessage(update.message.chatId, "Сегодняшний пидор уже обнаружен: @${it.user.nickname}") }
        if (message != null) {
            return { it.execute(message) }
        } else {
            val users = repository.getUsers(chat)
            val id = ThreadLocalRandom.current().nextInt(0, users.size)
            val nextPidor = users[id]
            repository.addPidor(Pidor(1, nextPidor, LocalDateTime.now()))
            return { it.execute(SendMessage(update.message.chatId, "Пидор это @${nextPidor.nickname}")) }
        }


    }

    override fun canExecute(message: Message): Boolean {
        return message.text?.contains("/pidor")?:false
    }
}