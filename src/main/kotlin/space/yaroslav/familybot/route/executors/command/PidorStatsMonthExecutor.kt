package space.yaroslav.familybot.route.executors.command

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import space.yaroslav.familybot.common.Pidor
import space.yaroslav.familybot.common.utils.bold
import space.yaroslav.familybot.common.utils.formatTopList
import space.yaroslav.familybot.common.utils.send
import space.yaroslav.familybot.common.utils.toChat
import space.yaroslav.familybot.common.utils.toRussian
import space.yaroslav.familybot.repos.ifaces.CommonRepository
import space.yaroslav.familybot.route.executors.Configurable
import space.yaroslav.familybot.route.models.Command
import space.yaroslav.familybot.route.models.FunctionId
import space.yaroslav.familybot.route.models.Phrase
import space.yaroslav.familybot.route.services.dictionary.Dictionary
import space.yaroslav.familybot.telegram.BotConfig
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

@Component
class PidorStatsMonthExecutor(
    private val repository: CommonRepository,
    private val dictionary: Dictionary,
    config: BotConfig
) : CommandExecutor(config), Configurable {

    override fun getFunctionId(): FunctionId {
        return FunctionId.PIDOR
    }

    override fun command(): Command {
        return Command.STATS_MONTH
    }

    override fun execute(update: Update): suspend (AbsSender) -> Unit {
        val now = LocalDate.now()
        val pidorsByChat = repository.getPidorsByChat(
            update.message.chat.toChat(),
            startDate = LocalDateTime.of(LocalDate.of(now.year, now.month, 1), LocalTime.MIDNIGHT)
                .toInstant(ZoneOffset.UTC)
        )
            .map(Pidor::user)
            .formatTopList()
        val title = "${dictionary.get(Phrase.PIDOR_STAT_MONTH)} ${now.month.toRussian()}:\n".bold()
        return { it.send(update, title + pidorsByChat.joinToString("\n"), enableHtml = true) }
    }
}
