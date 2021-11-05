package space.yaroslav.familybot.services.misc

import org.springframework.stereotype.Component
import space.yaroslav.familybot.common.extensions.key
import space.yaroslav.familybot.common.extensions.prettyFormat
import space.yaroslav.familybot.models.router.ExecutorContext
import space.yaroslav.familybot.models.telegram.Chat
import space.yaroslav.familybot.models.telegram.User
import space.yaroslav.familybot.services.settings.Ban
import space.yaroslav.familybot.services.settings.EasyKey
import space.yaroslav.familybot.services.settings.EasyKeyValueService
import java.time.Duration
import java.time.Instant

@Component
class BanService(
    private val easyKeyValueService: EasyKeyValueService
) {

    fun getUserBan(context: ExecutorContext): String? {
        return findBanByKey(context.userKey)
    }

    fun getChatBan(context: ExecutorContext): String? {
        return findBanByKey(context.chatKey)
    }

    fun banUser(user: User, description: String, isForever: Boolean = false) {
        banByKey(user.key(), description, calculateDuration(isForever))
    }

    fun banChat(chat: Chat, description: String, isForever: Boolean = false) {
        banByKey(chat.key(), description, calculateDuration(isForever))
    }

    fun findBanByKey(easyKey: EasyKey): String? {
        return easyKeyValueService.get(
            Ban,
            easyKey
        )
    }

    fun removeBan(easyKey: EasyKey) {
        easyKeyValueService.remove(Ban, easyKey)
    }

    private fun calculateDuration(isForever: Boolean): Duration {
        return if (isForever) Duration.ofDays(9999) else Duration.ofDays(7)
    }

    private fun banByKey(easyKey: EasyKey, description: String, duration: Duration) {
        val until = Instant.now().plusSeconds(duration.seconds)
        easyKeyValueService.put(
            Ban,
            easyKey,
            "Бан нахуй по причине \"$description\" до ${until.prettyFormat()}",
            duration = duration
        )
    }
}
