package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AiRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

public class DummyAgent(private val name: String, private val response: String) : Agent {
    override fun getName() = name
    override fun getType() = null
    override suspend fun processRequest(request: AiRequest) = AgentResponse(response, 1.0f)
}

public class GenesisAgentTest {
    @Test
    public fun testParticipateWithAgents_turnOrder() = runBlocking {
        public val auraService = mock<AuraAIService>()
        public val kaiService = mock<KaiAIService>()
        public val cascadeService = mock<CascadeAIService>()
        public val dummyAgent = DummyAgent("Dummy", "ok")
        whenever(auraService.processRequest(org.mockito.kotlin.any())).thenReturn(
            AgentResponse(
                "ok",
                1.0f
            )
        )
        whenever(kaiService.processRequest(org.mockito.kotlin.any())).thenReturn(
            AgentResponse(
                "ok",
                1.0f
            )
        )
        whenever(cascadeService.processRequest(org.mockito.kotlin.any())).thenReturn(
            AgentResponse(
                "ok",
                1.0f
            )
        )
        public val genesis = GenesisAgent(
            auraService = auraService,
            kaiService = kaiService,
            cascadeService = cascadeService
        )
        public val responses = genesis.participateWithAgents(
            emptyMap(),
            listOf(dummyAgent),
            "test",
            GenesisAgent.ConversationMode.TURN_ORDER
        )
        assertTrue(responses["Dummy"]?.content == "ok")
    }

    @Test
    public fun testAggregateAgentResponses() {
        public val auraService = mock<AuraAIService>()
        public val kaiService = mock<KaiAIService>()
        public val cascadeService = mock<CascadeAIService>()
        public val genesis = GenesisAgent(
            auraService = auraService,
            kaiService = kaiService,
            cascadeService = cascadeService
        )
        public val resp1 = mapOf("A" to AgentResponse("foo", 0.5f))
        public val resp2 = mapOf("A" to AgentResponse("bar", 0.9f))
        public val consensus = genesis.aggregateAgentResponses(listOf(resp1, resp2))
        assertTrue(consensus["A"]?.content == "bar")
    }
}
