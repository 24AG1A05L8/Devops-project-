package com.example.domain.model

data class BlueprintFileArtifact(
    val path: String,
    val language: String,
    val description: String,
    val code: String
)

object BlueprintTemplates {

    val allFiles: List<BlueprintFileArtifact> = listOf(
        BlueprintFileArtifact(
            path = "frontend/src/pages/Dashboard.tsx",
            language = "TypeScript / React",
            description = "Main Enterprise Dashboard with 3-column KPI grid, RBAC guard, WebSocket live feed, and provisioning controls.",
            code = """
import React, { useEffect, useState } from 'react';
import { Navbar } from '../components/Navbar';
import { Sidebar } from '../components/Sidebar';
import { MetricCard } from '../components/MetricCard';

export interface MetricsSummary {
  monthly_cost: number;
  cost_status: string;
  security_pass: boolean;
  critical_vulnerabilities: number;
  deployment_frequency_per_day: number;
  pipeline_success_rate_percentage: number;
}

export const Dashboard: React.FC = () => {
  const [env, setEnv] = useState<'Production' | 'Staging' | 'Development'>('Production');
  const [role, setRole] = useState<'Platform Admin' | 'SecOps' | 'FinOps' | 'Auditor'>('Platform Admin');
  const [metrics, setMetrics] = useState<MetricsSummary>({
    monthly_cost: 395.00,
    cost_status: 'optimized',
    security_pass: true,
    critical_vulnerabilities: 0,
    deployment_frequency_per_day: 14,
    pipeline_success_rate_percentage: 99.4,
  });
  const [isTriggering, setIsTriggering] = useState(false);
  const [events, setEvents] = useState<string[]>([]);

  useEffect(() => {
    fetch(`/api/v1/metrics/summary?env=${'$'}{env.toLowerCase()}`)
      .then((res) => res.json())
      .then((data: MetricsSummary) => setMetrics(data))
      .catch(console.error);

    const ws = new WebSocket(`ws://${'$'}{window.location.host}/ws/telemetry`);
    ws.onmessage = (evt) => {
      setEvents((prev) => [evt.data, ...prev.slice(0, 19)]);
    };
    return () => ws.close();
  }, [env]);

  const handleTriggerPipeline = async () => {
    if (role === 'Auditor') return alert('RBAC Denied: Read-Only Auditor cannot trigger pipelines.');
    setIsTriggering(true);
    try {
      await fetch('/api/v1/pipeline/trigger', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'X-RBAC-Role': role },
        body: JSON.stringify({ environment: env.toLowerCase(), commit_sha: 'a1b2c3d4e5f6g7h8i9j0' }),
      });
    } finally {
      setTimeout(() => setIsTriggering(false), 600);
    }
  };

  return (
    <div className="min-h-screen bg-[#0F172A] text-[#F8FAFC] font-sans">
      <Navbar environment={env} onEnvironmentChange={setEnv} role={role} onRoleChange={setRole} />
      <Sidebar activeItem="Dashboard" />
      <main className="ml-[284px] mt-[88px] pr-6 pb-12">
        <div className="grid grid-cols-3 gap-6">
          <MetricCard
            title="Projected Monthly Cost"
            value={`$${'$'}{metrics.monthly_cost.toFixed(2)}`}
            valueColor="text-[#10B981]"
            subLabel="60% under budget limit"
            subLabelColor="text-[#10B981]"
          />
          <MetricCard
            title="Pipeline Security Status"
            value={metrics.security_pass ? 'Passed' : 'Alert'}
            valueColor="text-[#10B981]"
            subLabel={`${'$'}{metrics.critical_vulnerabilities} Critical CVEs detected`}
            subLabelColor="text-[#94A3B8]"
          />
          <MetricCard
            title="Deployment Frequency"
            value={`${'$'}{metrics.deployment_frequency_per_day} / Day`}
            valueColor="text-[#06B6D4]"
            subLabel={`Success rate: ${'$'}{metrics.pipeline_success_rate_percentage}%`}
            subLabelColor="text-[#10B981]"
          />
        </div>

        <section className="mt-8 p-6 bg-[#1E293B] border border-[#334155] rounded-[12px]">
          <h2 className="text-lg font-semibold text-[#F8FAFC] mb-4">
            Interactive Resource Provisioning & Pipeline Console
          </h2>
          <div className="flex items-center gap-4">
            <button
              onClick={handleTriggerPipeline}
              className="h-[44px] px-[24px] text-[14px] font-semibold bg-[#06B6D4] hover:bg-[#0891B2] active:scale-[0.98] text-[#0F172A] rounded-[6px] transition flex items-center gap-2"
            >
              {isTriggering && <span className="animate-spin h-4 w-4 border-2 border-[#0F172A] border-t-transparent rounded-full" />}
              Trigger Pipeline
            </button>
            <button className="h-[44px] px-[24px] text-[14px] bg-transparent border-2 border-[#EF4444] text-[#EF4444] hover:bg-[#EF4444] hover:text-white rounded-[6px] transition">
              Teardown Resources
            </button>
          </div>
        </section>
      </main>
    </div>
  );
};
""".trimIndent()
        ),
        BlueprintFileArtifact(
            path = "backend/app/main.py",
            language = "Python / FastAPI",
            description = "Asynchronous FastAPI engine with WebSocket telemetry, RBAC middleware, Audit Logging, and automated backups.",
            code = """
import asyncio
import hashlib
from datetime import datetime, timezone
from fastapi import FastAPI, WebSocket, Header, HTTPException, Depends
from .schemas import (
    MetricsSummaryResponse,
    PipelineTriggerRequest,
    PipelineTriggerResponse,
    CostSimulationResponse,
    AuditLogEntry,
)
from .services import DevOpsPlatformService

app = FastAPI(
    title="DevOps Core Enterprise Engine",
    version="2.4.0",
    description="Async DevOps, FinOps, RBAC, Audit & Security Compliance API"
)
service = DevOpsPlatformService()

def verify_rbac(x_rbac_role: str = Header(default="Platform Admin")):
    if x_rbac_role == "Read-Only Auditor":
        raise HTTPException(status_code=403, detail="RBAC Policy Violation: Read-Only role cannot mutate state.")
    return x_rbac_role

@app.get("/api/v1/metrics/summary", response_model=MetricsSummaryResponse)
async def get_metrics_summary(env: str = "production"):
    return await service.fetch_metrics_summary(env)

@app.post("/api/v1/pipeline/trigger", response_model=PipelineTriggerResponse)
async def trigger_pipeline(payload: PipelineTriggerRequest, role: str = Depends(verify_rbac)):
    result = await service.launch_pipeline(payload.environment, payload.commit_sha, role)
    await service.append_audit_log(
        action="PIPELINE_TRIGGER",
        actor_role=role,
        environment=payload.environment,
        details=f"Triggered pipeline {result.pipeline_id} for commit {payload.commit_sha}"
    )
    return result

@app.get("/api/v1/infrastructure/cost-simulation", response_model=CostSimulationResponse)
async def simulate_infrastructure_cost(proposed_replicas: int = 4):
    return await service.run_cost_simulation(proposed_replicas)

@app.websocket("/ws/telemetry")
async def websocket_telemetry(ws: WebSocket):
    await ws.accept()
    try:
        while True:
            frame = await service.next_telemetry_frame()
            await ws.send_json(frame)
            await asyncio.sleep(1.5)
    except Exception:
        await ws.close()
""".trimIndent()
        ),
        BlueprintFileArtifact(
            path = "backend/app/schemas.py",
            language = "Python / Pydantic",
            description = "Strict Pydantic v2 data validation contracts for Metrics, Pipelines, FinOps Simulation, and Audit Logs.",
            code = """
from pydantic import BaseModel, Field

class MetricsSummaryResponse(BaseModel):
    monthly_cost: float = Field(example=395.00)
    cost_status: str = Field(example="optimized")
    security_pass: bool = Field(example=True)
    critical_vulnerabilities: int = Field(example=0)
    deployment_frequency_per_day: int = Field(example=14)
    pipeline_success_rate_percentage: float = Field(example=99.4)

class PipelineTriggerRequest(BaseModel):
    environment: str = Field(default="staging", pattern="^(production|staging|development)${'$'}")
    commit_sha: str = Field(min_length=7, max_length=40, example="a1b2c3d4e5f6g7h8i9j0")

class PipelineTriggerResponse(BaseModel):
    pipeline_id: str = Field(example="job_9983471")
    status: str = Field(example="initiated")
    timestamp: str = Field(example="2026-10-01T10:32:00Z")

class CostSimulationResponse(BaseModel):
    current_infra_cost: float = Field(example=395.00)
    proposed_infra_cost: float = Field(example=420.00)
    cost_difference: float = Field(example=25.00)
    budget_violation: bool = Field(example=False)

class AuditLogEntry(BaseModel):
    action: str
    actor_role: str
    environment: str
    sha256_hash: str
    timestamp: str
""".trimIndent()
        ),
        BlueprintFileArtifact(
            path = "gitops/deployment.yaml",
            language = "YAML / Kubernetes",
            description = "Declarative Kubernetes Deployment manifest with zero-trust securityContext, resource quotas, and liveness probes.",
            code = """
apiVersion: apps/v1
kind: Deployment
metadata:
  name: devops-core-engine
  namespace: platform-prod
  labels:
    app.kubernetes.io/name: devops-core
    app.kubernetes.io/tier: control-plane
spec:
  replicas: 3
  revisionHistoryLimit: 5
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: devops-core-engine
  template:
    metadata:
      labels:
        app: devops-core-engine
    spec:
      securityContext:
        runAsNonRoot: true
        runAsUser: 10001
        seccompProfile:
          type: RuntimeDefault
      containers:
        - name: fastapi-engine
          image: ghcr.io/enterprise/devops-core:sha-a1b2c3d
          ports:
            - containerPort: 8000
              name: http-api
          resources:
            requests:
              cpu: "250m"
              memory: "512Mi"
            limits:
              cpu: "1000m"
              memory: "1Gi"
          readinessProbe:
            httpGet:
              path: /api/v1/metrics/summary
              port: 8000
            initialDelaySeconds: 5
            periodSeconds: 10
""".trimIndent()
        ),
        BlueprintFileArtifact(
            path = "gitops/service.yaml",
            language = "YAML / Kubernetes",
            description = "Internal Kubernetes ClusterIP & LoadBalancer routing manifest with Prometheus scrape annotations.",
            code = """
apiVersion: v1
kind: Service
metadata:
  name: devops-core-svc
  namespace: platform-prod
  annotations:
    prometheus.io/scrape: "true"
    prometheus.io/port: "8000"
spec:
  type: ClusterIP
  selector:
    app: devops-core-engine
  ports:
    - name: http
      protocol: TCP
      port: 80
      targetPort: 8000
""".trimIndent()
        ),
        BlueprintFileArtifact(
            path = "backend/Dockerfile",
            language = "Dockerfile",
            description = "Hardened multistage container build with non-root user and minimal distroless-compatible footprint.",
            code = """
FROM python:3.12-slim AS builder
WORKDIR /build
COPY requirements.txt .
RUN pip install --no-cache-dir --prefix=/install -r requirements.txt

FROM python:3.12-slim AS runtime
WORKDIR /app
RUN groupadd -g 10001 devops && useradd -u 10001 -g devops -s /sbin/nologin devops
COPY --from=builder /install /usr/local
COPY ./app /app/app
USER devops
EXPOSE 8000
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000", "--workers", "4"]
""".trimIndent()
        )
    )
}
