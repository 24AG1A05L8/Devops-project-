# 🚀 DevOps Core — Enterprise Cloud, FinOps, AI & Security Control Plane

**DevOps Core** is a production-grade Enterprise DevOps, FinOps, AI Copilot, and Cloud Security Control Plane built with **Kotlin & Jetpack Compose (Material 3 Enterprise Dark Mode)** alongside a full-stack **React + TypeScript + Tailwind CSS**, **Python FastAPI**, and **Kubernetes GitOps** reference architecture.

---

## ✨ Key Enterprise Capabilities

### 1. 📊 Executive FinOps & DORA KPI Dashboard (`GET /api/v1/metrics/summary`)
- **Projected Monthly Cost**: Real-time cloud spend aggregation (`$395.00/mo` optimized baseline — `60% under budget limit` of `$1,000.00/mo`).
- **Pipeline Security Posture**: Continuous SAST, SBOM, and Trivy container vulnerability verification (`Passed` — `0 Critical CVEs detected`).
- **Elite DORA Metrics**: Tracks daily deployment velocity (`14 / Day`) and pipeline reliability (`99.4%` success rate) across **Production**, **Staging**, and **Development** environments.

### 2. 🤖 Gemini AI DevSecOps Copilot (`gemini-3.5-flash` & `gemini-3.1-pro-preview`)
- **Context-Aware SRE & FinOps Advisor**: Analyzes live Room database IaC modules, cluster telemetry, and RBAC state to generate right-sizing plans, SOC2 security audits, and production Kubernetes HPA/NetworkPolicy YAML manifests.
- **Dual-Model Support**: Switch between `gemini-3.5-flash` (Fast SRE Diagnostics) and `gemini-3.1-pro-preview` (Deep Infrastructure & Security Reasoning).

### 3. 🌐 Real-World Live Cloud Probes, GitHub API & Hardware Telemetry
- **Live HTTP/HTTPS Latency Prober**: Executes real network probes against **GitHub Status API**, **Cloudflare Edge Trace**, **PyPI FastAPI Release Registry**, **Kubernetes OCI Registry**, and custom endpoints.
- **Live GitHub Repository Connector**: Queries `https://api.github.com/repos/{owner}/{repo}` to fetch real stars, default branches, and latest Commit SHAs with 1-tap synchronization into the CI/CD Pipeline.
- **Real Device Kernel & JVM Telemetry**: Streams actual Android CPU core count, system RAM utilization, JVM heap memory, and uptime into the live telemetry feed.

### 4. 🏗️ Terraform IaC & FinOps Cost Simulation (`GET /api/v1/infrastructure/cost-simulation`)
- **Pre-Deployment Cost Delta Analysis**: Simulates node replica scaling (`$395.00` current vs. `$420.00` proposed, `+$25.00` delta) and AWS Spot/Graviton savings before applying changes.
- **Safe Teardown Guardrails**: Automatically captures a point-in-time `SHA-256` state snapshot prior to terminating or draining resources.

### 5. 🛡️ Zero-Trust RBAC, SHA-256 Audit Trail & Automated Backups
- **Role-Based Access Control (RBAC)**: Enforces granular permissions across `Platform Admin [ROOT-SRE]`, `SecOps Engineer [SEC-OPS]`, `FinOps Analyst [FIN-OPS]`, and `Read-Only Auditor [AUDITOR]`.
- **Immutable Cryptographic Audit Logging**: Every state mutation and unauthorized RBAC attempt is persisted asynchronously in Room Database with a **SHA-256 integrity hash**.
- **Automated Point-in-Time Backups & 1-Click Restore**: Verified state snapshots with 1-click disaster recovery restoration.
- **Continuous Compliance Reporting**: Real-time verification across **SOC2 Type II**, **ISO 27001**, **CIS Kubernetes v1.8**, and **NIST 800-53**.
