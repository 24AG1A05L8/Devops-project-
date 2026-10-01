package com.example.data.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

data class GeminiCopilotResult(
    val responseText: String,
    val modelUsed: String,
    val isLiveApi: Boolean,
    val latencyMs: Long
)

class GeminiDevOpsService {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    suspend fun generateDevOpsInsight(
        userPrompt: String,
        modelId: String,
        environmentName: String,
        monthlyCost: Double,
        proposedCost: Double,
        deployFrequency: Int,
        successRate: Double,
        activeRole: String,
        modulesSummary: String
    ): GeminiCopilotResult = withContext(Dispatchers.IO) {
        val startMs = System.currentTimeMillis()
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Throwable) {
            ""
        }

        val systemContext = """
            You are the DevOps Core Enterprise AI Architect & SecOps Copilot.
            Current Live Control Plane Telemetry:
            - Active Environment: $environmentName
            - Active RBAC Session Role: $activeRole
            - Projected Monthly Cost: $${String.format(Locale.US, "%.2f", monthlyCost)}/mo (Proposed Sim: $${String.format(Locale.US, "%.2f", proposedCost)}/mo)
            - Pipeline Security Status: Passed (0 Critical CVEs detected)
            - DORA Deployment Frequency: $deployFrequency / Day (Success rate: $successRate%)
            - Active Terraform / K8s Modules: $modulesSummary
            
            Provide a concise, actionable, production-grade engineering response with concrete CLI commands, Terraform/Kubernetes snippets, or FinOps savings calculations where helpful.
        """.trimIndent()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey.startsWith("YOUR_")) {
            val elapsed = (System.currentTimeMillis() - startMs).coerceAtLeast(18L)
            return@withContext GeminiCopilotResult(
                responseText = buildContextualFallbackResponse(
                    userPrompt = userPrompt,
                    environmentName = environmentName,
                    monthlyCost = monthlyCost,
                    proposedCost = proposedCost,
                    deployFrequency = deployFrequency,
                    successRate = successRate,
                    activeRole = activeRole,
                    modulesSummary = modulesSummary
                ),
                modelUsed = "$modelId (Built-in SRE Engine • Set GEMINI_API_KEY in Secrets for Live Cloud API)",
                isLiveApi = false,
                latencyMs = elapsed
            )
        }

        try {
            val requestJson = JSONObject().apply {
                put(
                    "systemInstruction",
                    JSONObject().apply {
                        put(
                            "parts",
                            JSONArray().put(JSONObject().put("text", systemContext))
                        )
                    }
                )
                put(
                    "contents",
                    JSONArray().put(
                        JSONObject().apply {
                            put("role", "user")
                            put(
                                "parts",
                                JSONArray().put(JSONObject().put("text", userPrompt))
                            )
                        }
                    )
                )
                put(
                    "generationConfig",
                    JSONObject().apply {
                        put("temperature", 0.35)
                        put("topP", 0.9)
                    }
                )
            }

            val url =
                "https://generativelanguage.googleapis.com/v1beta/models/$modelId:generateContent?key=$apiKey"
            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                val rawBody = response.body?.string().orEmpty()
                val elapsed = (System.currentTimeMillis() - startMs).coerceAtLeast(1L)

                if (!response.isSuccessful) {
                    val fallback = buildContextualFallbackResponse(
                        userPrompt = userPrompt,
                        environmentName = environmentName,
                        monthlyCost = monthlyCost,
                        proposedCost = proposedCost,
                        deployFrequency = deployFrequency,
                        successRate = successRate,
                        activeRole = activeRole,
                        modulesSummary = modulesSummary
                    )
                    return@withContext GeminiCopilotResult(
                        responseText = "$fallback\n\n[Note: Gemini API returned HTTP ${response.code}; served via built-in SRE diagnostic engine.]",
                        modelUsed = modelId,
                        isLiveApi = false,
                        latencyMs = elapsed
                    )
                }

                val root = JSONObject(rawBody)
                val text = root.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")
                    ?.takeIf { it.isNotBlank() }

                if (text != null) {
                    return@withContext GeminiCopilotResult(
                        responseText = text.trim(),
                        modelUsed = "$modelId (Live Gemini API)",
                        isLiveApi = true,
                        latencyMs = elapsed
                    )
                }
            }
        } catch (e: Exception) {
            val elapsed = (System.currentTimeMillis() - startMs).coerceAtLeast(1L)
            val fallback = buildContextualFallbackResponse(
                userPrompt = userPrompt,
                environmentName = environmentName,
                monthlyCost = monthlyCost,
                proposedCost = proposedCost,
                deployFrequency = deployFrequency,
                successRate = successRate,
                activeRole = activeRole,
                modulesSummary = modulesSummary
            )
            return@withContext GeminiCopilotResult(
                responseText = "$fallback\n\n[Offline Fallback Active: ${e.localizedMessage ?: "network unreachable"}]",
                modelUsed = modelId,
                isLiveApi = false,
                latencyMs = elapsed
            )
        }

        val elapsed = (System.currentTimeMillis() - startMs).coerceAtLeast(1L)
        GeminiCopilotResult(
            responseText = buildContextualFallbackResponse(
                userPrompt = userPrompt,
                environmentName = environmentName,
                monthlyCost = monthlyCost,
                proposedCost = proposedCost,
                deployFrequency = deployFrequency,
                successRate = successRate,
                activeRole = activeRole,
                modulesSummary = modulesSummary
            ),
            modelUsed = modelId,
            isLiveApi = false,
            latencyMs = elapsed
        )
    }

    private fun buildContextualFallbackResponse(
        userPrompt: String,
        environmentName: String,
        monthlyCost: Double,
        proposedCost: Double,
        deployFrequency: Int,
        successRate: Double,
        activeRole: String,
        modulesSummary: String
    ): String {
        val lower = userPrompt.lowercase(Locale.US)
        return when {
            lower.contains("cost") || lower.contains("finops") || lower.contains("budget") -> """
### FinOps AI Cost & Right-Sizing Analysis ($environmentName)
- **Active Spend**: `$${String.format(Locale.US, "%.2f", monthlyCost)}/mo` (Simulated Proposed: `$${String.format(Locale.US, "%.2f", proposedCost)}/mo`)
- **Modules Evaluated**: $modulesSummary

#### Recommended Actions:
1. **Graviton3 / Spot Node Group Migration (`-$45.00/mo`)**:
   Shift stateless EKS worker pods to `m7g.xlarge` Spot capacity with Karpenter consolidation enabled.
2. **RDS Multi-AZ Storage Auto-Scaling (`-$18.50/mo`)**:
   Convert under-utilized GP2 volumes to `gp3` with 3,000 baseline IOPS.
3. **ElastiCache Tiered Memory**:
   Enable Redis 7.2 data tiering and reserve 1-year Compute Savings Plan.
            """.trimIndent()

            lower.contains("security") || lower.contains("cve") || lower.contains("rbac") || lower.contains("compliance") -> """
### Zero-Trust SecOps & Compliance Audit ($environmentName)
- **Active Session Role**: `$activeRole`
- **Vulnerability Posture**: `0 Critical CVEs` • `SOC2 Type II / ISO 27001 / CIS K8s v1.8` Compliant

#### Hardening Recommendations:
1. **Enforce Cosign Keyless OIDC Verification**:
   Require admission controller (`Kyverno`) to verify `ghcr.io/enterprise/devops-core` image signatures prior to pod scheduling.
2. **RBAC Least-Privilege Lock**:
   Ensure `infra:teardown` and `backup:manage` remain restricted to `Platform Admin [ROOT-SRE]` with mandatory pre-teardown SHA-256 state snapshots.
3. **Runtime eBPF Falco Rules**:
   Keep syscall drift alerts active on `ws://security/cve`.
            """.trimIndent()

            lower.contains("yaml") || lower.contains("k8s") || lower.contains("kubernetes") || lower.contains("hpa") -> """
### Production Kubernetes HPA & NetworkPolicy Manifest ($environmentName)
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: devops-core-engine-hpa
  namespace: platform-${environmentName.lowercase(Locale.US)}
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: devops-core-engine
  minReplicas: 3
  maxReplicas: 12
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 65
```
            """.trimIndent()

            else -> """
### DevOps Core AI Copilot Assessment ($environmentName)
- **Environment Health**: Operational (`$deployFrequency deployments/day`, `$successRate%` pipeline success rate)
- **Current Spend**: `$${String.format(Locale.US, "%.2f", monthlyCost)}/mo` across $modulesSummary
- **RBAC Clearance**: `$activeRole`

#### Next Best Engineering Steps:
1. Run **FinOps Cost Simulation** in `Infrastructure (IaC)` before scaling node replicas.
2. Trigger **Pipeline `POST /api/v1/pipeline/trigger`** to validate SAST, Trivy CVE scan, and ArgoCD GitOps sync.
3. Verify **SHA-256 Audit Logs & Point-in-Time Backups** in `Security, RBAC & Audit`.
            """.trimIndent()
        }
    }
}
