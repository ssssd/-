package com.example.core.ai

import com.example.core.database.ScheduleEntity
import com.example.core.database.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

data class AICoachMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedSchedules: List<ScheduleEntity> = emptyList(),
    val suggestedSubtasks: List<String> = emptyList()
)

enum class MessageSender {
    USER,
    AI
}

class AICoachService {

    suspend fun getCoachingResponse(
        userPrompt: String,
        tasks: List<TaskEntity>,
        schedules: List<ScheduleEntity>,
        screenTimeMinutes: Int,
        allowTasks: Boolean,
        allowSchedule: Boolean
    ): AICoachMessage = withContext(Dispatchers.Default) {
        val lower = userPrompt.lowercase()
        val now = System.currentTimeMillis()
        val oneHour = 3600000L

        when {
            lower.contains("论文") || lower.contains("thesis") || lower.contains("拆解") || lower.contains("break down") -> {
                val subtasks = listOf(
                    "阶段 1：文献背景综述与大纲梳理 (45分钟)",
                    "阶段 2：核心实验方法与数据图表导出 (60分钟)",
                    "阶段 3：实验结果对比分析与结论撰写 (50分钟)",
                    "阶段 4：格式排版与参考文献交叉核验 (30分钟)"
                )
                val reply = "我已经为你将「毕业论文撰写」拆解为 4 个清晰的深度专注阶段，并建议每阶段间隙休息 10 分钟。你可以直接将这些步骤添加至子任务清单或日程中！"
                AICoachMessage(
                    sender = MessageSender.AI,
                    text = reply,
                    suggestedSubtasks = subtasks
                )
            }
            lower.contains("安排") || lower.contains("计划") || lower.contains("schedule") || lower.contains("plan") -> {
                val plan1 = ScheduleEntity(
                    id = UUID.randomUUID().toString(),
                    title = "深度专注：核心学习与任务推进",
                    category = "Study",
                    startTime = now + 1 * oneHour,
                    endTime = now + 3 * oneHour,
                    colorHex = "#6366F1"
                )
                val plan2 = ScheduleEntity(
                    id = UUID.randomUUID().toString(),
                    title = "高效运动与体能恢复",
                    category = "Health",
                    startTime = now + 4 * oneHour,
                    endTime = now + 5 * oneHour,
                    colorHex = "#10B981"
                )
                val plan3 = ScheduleEntity(
                    id = UUID.randomUUID().toString(),
                    title = "阅读精读与知识内化",
                    category = "Reading",
                    startTime = now + 6 * oneHour,
                    endTime = now + 7 * oneHour,
                    colorHex = "#F59E0B"
                )

                val reply = "根据你的需求与工作节奏，我为你生成了科学的交替时间表：\n\n• 14:00 - 16:00: 核心学习/代码推进 (2h 深度专注)\n• 17:00 - 18:00: 健身/有氧运动 (激活多巴胺)\n• 19:00 - 20:00: 轻松阅读与词汇复习 (1h 适度专注)\n\n点击下方卡片可一键保存至日程！"
                AICoachMessage(
                    sender = MessageSender.AI,
                    text = reply,
                    suggestedSchedules = listOf(plan1, plan2, plan3)
                )
            }
            lower.contains("分心") || lower.contains("拖延") || lower.contains("distract") || lower.contains("procrastinat") -> {
                val reply = "检测到分心焦虑是正常的心理惯性。建议采取「5分钟微启动法则」：\n\n1. 打开专注计时器，设置仅仅 15 分钟番茄钟；\n2. 开启「雨声」或「森林」白噪音阻隔环境噪音；\n3. 启动防退出严格模式，将手机扣放在桌面上。\n只要跨过前 5 分钟，大脑就会自然进入心流状态。"
                AICoachMessage(sender = MessageSender.AI, text = reply)
            }
            lower.contains("总结") || lower.contains("summary") || lower.contains("report") -> {
                val pendingCount = tasks.count { it.status != "COMPLETED" }
                val completedCount = tasks.count { it.status == "COMPLETED" }
                val reply = "📊 今日效率复盘：\n• 任务达成：已完成 $completedCount 项，剩余 $pendingCount 项\n• 手机屏幕时间：${screenTimeMinutes / 60}小时 ${screenTimeMinutes % 60}分钟\n• 状态评级：A (效率处于顶峰前 15%)\n建议今晚 23:00 前放下手机，保证充沛睡眠迎接明天！"
                AICoachMessage(sender = MessageSender.AI, text = reply)
            }
            else -> {
                val reply = "我是你的 AI 专注教练。我可以为你提供：\n1. 复杂大任务的结构化拆解与番茄钟时间估算；\n2. 根据你的工作习惯智能生成全天日程；\n3. 克服拖延与手机分心的科学心理策略；\n4. 每日与每周专注数据分析报告。\n\n请告诉我你今天想攻克的目标！"
                AICoachMessage(sender = MessageSender.AI, text = reply)
            }
        }
    }
}
