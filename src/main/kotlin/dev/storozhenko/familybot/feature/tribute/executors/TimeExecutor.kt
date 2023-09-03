package dev.storozhenko.familybot.feature.tribute.executors

import dev.storozhenko.familybot.common.extensions.DateConstants
import dev.storozhenko.familybot.common.extensions.PluralizedWordsProvider
import dev.storozhenko.familybot.common.extensions.bold
import dev.storozhenko.familybot.common.extensions.code
import dev.storozhenko.familybot.common.extensions.pluralize
import dev.storozhenko.familybot.common.extensions.send
import dev.storozhenko.familybot.core.executors.CommandExecutor
import dev.storozhenko.familybot.core.models.telegram.Command
import dev.storozhenko.familybot.core.routers.models.ExecutorContext
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class TimeExecutor : CommandExecutor() {

    private val timeFormatter =
        DateTimeFormatter.ofPattern("HH:mm")

    companion object {
        private val times = mapOf(
            "Время в Лондоне:          " to "Europe/London",
            "Время в Москве:           " to "Europe/Moscow",
            "Время в Ульяновске:       " to "Europe/Samara",
            "Время в Ташкенте:         " to "Asia/Tashkent",
            "Время в Аргентине:        " to "America/Argentina/Buenos_Aires",
        )
            .map { (prefix, zone) -> prefix.code() to ZoneId.of(zone) }
            .toMap()

        fun getMortgageDate(start: Instant, end: Instant): String {
            val startLocalDate = start.atZone(ZoneId.systemDefault()).toLocalDate()
            val endLocalDate = end.atZone(ZoneId.systemDefault()).toLocalDate()
            val period = Period.between(startLocalDate, endLocalDate)

            val duration = Duration.between(start, end)
            val hours = duration.toHoursPart()
            val minutes = duration.toMinutesPart()

            val years = period.years
            val months = period.months
            val days = period.days

            val yearProvider = PluralizedWordsProvider({ "год" }, { "года" }, { "лет" })
            val monthProvider = PluralizedWordsProvider({ "месяц" }, { "месяца" }, { "месяцев" })
            val dayProvider = PluralizedWordsProvider({ "день" }, { "дня" }, { "дней" })
            val hourProvider = PluralizedWordsProvider({ "час" }, { "часа" }, { "часов" })
            val minuteProvider = PluralizedWordsProvider({ "минута" }, { "минуты" }, { "минут" })


            val parts = mutableListOf<String>()
            if (years > 0) parts.add("$years ${pluralize(years.toLong(), yearProvider)}")
            if (months > 0) parts.add("$months ${pluralize(months.toLong(), monthProvider)}")
            if (days > 0) parts.add("$days ${pluralize(days.toLong(), dayProvider)}")
            if (hours > 0) parts.add("$hours ${pluralize(hours, hourProvider)}")
            if (minutes > 0) parts.add("$minutes ${pluralize(minutes, minuteProvider)}")

            return "Время в Ипотечной Кабале: ".code() + parts.joinToString(", ").bold()
        }
    }

    override fun command() = Command.TIME

    override suspend fun execute(context: ExecutorContext) {
        val now = Instant.now()
        val result = times.map { (prefix, zone) -> prefix to now.atZone(zone) }
            .sortedBy { (_, time) -> time }
            .joinToString(separator = "\n") { (prefix, time) -> prefix + time.format(timeFormatter).bold() }
        context.sender.send(
            context,
            "$result\n${getMortgageDate(DateConstants.VITYA_MORTGAGE_DATE, now)}",
            replyToUpdate = true,
            enableHtml = true
        )
    }


}
